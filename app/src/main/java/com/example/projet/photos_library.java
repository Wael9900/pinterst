package com.example.projet;


import android.graphics.Bitmap;
import android.media.Image;

public class photos_library {
    Bitmap image;

    public photos_library(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }
}

