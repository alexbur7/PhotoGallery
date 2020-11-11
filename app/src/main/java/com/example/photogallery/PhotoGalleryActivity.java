package com.example.photogallery;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class PhotoGalleryActivity extends SingleFragmentGallery{

    @Override
    Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}