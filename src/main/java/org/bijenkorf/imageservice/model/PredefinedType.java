package org.bijenkorf.imageservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;

public enum PredefinedType {

//    ORIGINAL("original"),
//    THUMBNAIL("thumbnail");

    original, thumbnail;

//    private String key;
//
//    PredefinedType(String key) {
//        this.key = key;
//    }
//
//    @JsonCreator
//    public static PredefinedType fromString(String key) {
//        return key == null
//                ? null
//                : PredefinedType.valueOf(key.toUpperCase());
//    }
//
//    @JsonValue
//    public String getKey() {
//        return key;
//    }

    public static List<PredefinedType> getAllPredefinedTypes(){
        return Arrays.asList(original, thumbnail);
    }

}
