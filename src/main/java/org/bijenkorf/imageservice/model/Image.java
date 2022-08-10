package org.bijenkorf.imageservice.model;

public abstract class Image {
    private int height;
    private int width;
    private int quality;
    private ScaleType scaleType;
    private String fillColor;
    private ImageType imageType;
    private String sourceName;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getQuality() {
        return quality;
    }

    public ScaleType getScaleType() {
        return scaleType;
    }

    public String getFillColor() {
        return fillColor;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public void setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String toString() {
        return "Image{" +
                "height=" + height +
                ", width=" + width +
                ", quality=" + quality +
                ", scaleType=" + scaleType +
                ", fillColor='" + fillColor + '\'' +
                ", imageType=" + imageType +
                ", sourceName='" + sourceName + '\'' +
                '}';
    }
}
