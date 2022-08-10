package org.bijenkorf.imageservice.service;

import org.bijenkorf.imageservice.model.ImageFactory;
import org.bijenkorf.imageservice.model.PredefinedType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ImageResizerService {

    @Autowired
    private ImageFactory imageFactory;

    public byte[] resizeFile(final byte[] file,
                           final PredefinedType predefinedType){
        return file;
    }
}
