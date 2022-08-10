package org.bijenkorf.imageservice.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.predefinedtype.original")
public class OriginalImage extends Image{

    public PredefinedType getType(){
        return  PredefinedType.original;
    }

}
