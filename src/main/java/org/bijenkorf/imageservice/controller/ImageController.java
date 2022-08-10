package org.bijenkorf.imageservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.bijenkorf.imageservice.exception.ImageNotFoundException;
import org.bijenkorf.imageservice.model.PredefinedType;
import org.bijenkorf.imageservice.service.StorageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images/")
@Slf4j
public class ImageController {

    private final StorageService storageService;

    public ImageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("show/{predefined-type-name}/{dummy-seo-name}")
    public ResponseEntity<ByteArrayResource> showImage(@PathVariable(
            "predefined-type-name") final String predefinedTypeName,
                                   @PathVariable(value = "dummy-seo-name", required = false)
                                           final String dummySeoName,
                                   @RequestParam final String reference){
       PredefinedType type;
       try{
            type = PredefinedType.valueOf(predefinedTypeName);
       }catch (IllegalArgumentException exception){
            log.info("The requested predefined image type {} does not exist",
                    predefinedTypeName);
            throw new ImageNotFoundException("The requested predefined " +
                    "image type does not exist.");
        }
        byte[] data = storageService.resizeAndUpload(type,
                reference);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + reference + "\"")
                .body(resource);
    }

    @DeleteMapping("/flush/{predefined-type-name}")
    public ResponseEntity<String> flushImage(@PathVariable(
            "predefined-type-name") final PredefinedType predefinedTypeName,  @RequestParam final String reference){
        storageService.deleteImage(predefinedTypeName, reference);
        return  ResponseEntity.noContent().build();
    }


}
