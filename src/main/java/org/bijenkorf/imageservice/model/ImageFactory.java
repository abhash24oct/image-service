package org.bijenkorf.imageservice.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageFactory {

    @Autowired
    private OriginalImage image;

    @Autowired
    private ThumbnailImage thumbnailImage;

    public Image getImageInstance(final PredefinedType type){
        if(type.equals(PredefinedType.original)){
            return  image;
        }else {
            return thumbnailImage;
        }
    }
}
