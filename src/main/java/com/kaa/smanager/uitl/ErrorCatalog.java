package com.kaa.smanager.uitl;

// It is better to implement message catalog as database table.
// But application complexity allows to do it in such way.
public class ErrorCatalog {
    public static final String WRONG_ID = "Wrong id format!";
    public static final String WRONG_DURATION = "Wrong image duration format!";
    public static final String NO_IMAGE_URL = "No image URL found!";
    public static final String NO_PICTURE_SOURCE_ON_URL = "Image URL not contains any picture!";
    public static final String NO_SLIDESHOW_ID = "No slideshow id into added image!";
}
