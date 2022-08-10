package org.bijenkorf.imageservice.service;

import lombok.extern.slf4j.Slf4j;
import org.bijenkorf.imageservice.exception.AwsException;
import org.bijenkorf.imageservice.exception.GenericException;
import org.bijenkorf.imageservice.exception.ImageNotFoundException;
import org.bijenkorf.imageservice.model.PredefinedType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StorageService {

    private static final int MAX_RETRIES = 1;
    private static final int RETRY_DURATION = 200;

    @Value("${application.bucket.name}")
    private String bucketName;

    @Value("${application.sourceRoot}")
    private String sourceRoot;

    private ImageResizerService imageResizerService;
    private S3Client s3Client;

    public StorageService(final ImageResizerService imageResizerService,
                          final S3Client s3Client) {
        this.imageResizerService = imageResizerService;
        this.s3Client = s3Client;
    }

    public byte[] resizeAndUpload(final PredefinedType predefinedTypeName,
                                  final String fileName) {
        try {
            byte[] bytes =
                    imageResizerService.resizeFile(getFileFromSource(fileName)
                            , predefinedTypeName);
            String objectKey = generateObjectKey(fileName, predefinedTypeName);
            if (!doesObjectExistsInS3(objectKey)) {
                uploadFile(bytes, objectKey);
            }
            return downloadFile(objectKey);
        } catch (IOException exception) {
            log.error(exception.getMessage());
            log.info("The requested source image {} does not exist", fileName);
            throw new ImageNotFoundException("The requested source image does " +
                    "not exist");
        } catch (InterruptedException exception) {
            throw new GenericException(exception.getMessage());
        }
    }

    public void uploadFile(final byte[] file, final String fileName) throws InterruptedException {
        for (int i = 0; i <= MAX_RETRIES; i++) {
            try {

                PutObjectRequest putOb = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build();
                s3Client.putObject(putOb, RequestBody.fromBytes(file));
                log.info("Success Uploading file {}", fileName);
                return;
            } catch (S3Exception exception) {
                Thread.sleep(RETRY_DURATION);
                log.warn("Upload failed for {} retrying after {} ", fileName,
                        RETRY_DURATION);
                if (i == MAX_RETRIES) {
                    log.error("Upload failed after {} retries", MAX_RETRIES);
                    throw new AwsException("There is a problem writing the" +
                            "new (resized) image to the S3" +
                            "storage");
                }
            } catch (Exception exception) {
                throw new GenericException(exception.getMessage());
            }
        }
    }

    public byte[] downloadFile(final String fileName) {
        log.info("Downloading file {}", fileName);
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(fileName)
                    .bucket(bucketName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes =
                    s3Client.getObjectAsBytes(objectRequest);
            return objectBytes.asByteArray();
        } catch (S3Exception exception) {
            log.info("Error while downloading the file");
            throw new AwsException("There is a problem while downloading the " +
                    "file");
        }
    }

    public void deleteImage(final PredefinedType predefinedTypeName,
                            final String reference) {
        if (predefinedTypeName.equals(PredefinedType.original)) {
            List<PredefinedType> predefinedTypes =
                    PredefinedType.getAllPredefinedTypes();
            final List<String> objectKeysToDelete = predefinedTypes
                    .stream()
                    .map(predefinedType -> generateObjectKey(reference, predefinedType))
                    .filter(this::doesObjectExistsInS3)
                    .collect(Collectors.toList());
            objectKeysToDelete.forEach(this::deleteFileFromS3);
        } else {
            this.deleteFileFromS3(generateObjectKey(reference, predefinedTypeName));
        }
    }

    private void deleteFileFromS3(final String fileName) {
        log.info("Deleting {}", fileName);
        try {
            ArrayList<ObjectIdentifier> toDelete = new ArrayList<>();
            toDelete.add(ObjectIdentifier.builder()
                    .key(fileName)
                    .build());
            DeleteObjectsRequest dor = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder()
                            .objects(toDelete).build())
                    .build();
            s3Client.deleteObjects(dor);
        } catch (S3Exception exception) {
            log.error("There was a problem deleting the file {}", fileName);
            throw new AwsException(exception.getMessage());
        }
    }

    private boolean doesObjectExistsInS3(final String objectKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException exception) {
            return false;
        }
    }

    private String generateObjectKey(final String fileName,
                                     final PredefinedType predefinedType) {
        String fileNameWithoutExtension = fileName.toLowerCase(Locale.ROOT).split("\\.")[0];
        String replacedFileName = fileName.replace("/", "_");
        String separator = "/";
        StringBuilder finalName =
                new StringBuilder(predefinedType.name()).append(separator);
        if (fileNameWithoutExtension.length() > 4) {
            finalName.append(replacedFileName, 0, 4)
                    .append(separator);
        }
        if (fileNameWithoutExtension.length() > 8) {
            finalName.append(replacedFileName, 4, 8).append(separator);
        }
        return finalName
                .append(replacedFileName)
                .toString();
    }

    private byte[] getFileFromSource(final String fileName) throws IOException {
        if (sourceRoot.equals("classpath")) {
            Resource resource =
                    new ClassPathResource(sourceRoot + ":" + fileName);
            return FileCopyUtils.copyToByteArray(resource.getInputStream());
        } else
            return new byte[0];
    }
}
