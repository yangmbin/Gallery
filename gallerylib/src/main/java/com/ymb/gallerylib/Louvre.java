package com.ymb.gallerylib;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;

import com.ymb.gallerylib.home.GalleryActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class Louvre {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static final String IMAGE_TYPE_BMP = "image/bmp";
    public static final String IMAGE_TYPE_JPEG = "image/jpeg";
    public static final String IMAGE_TYPE_PNG = "image/png";
    public static final String[] IMAGE_TYPES = {IMAGE_TYPE_BMP, IMAGE_TYPE_JPEG, IMAGE_TYPE_PNG};

    @StringDef({IMAGE_TYPE_BMP, IMAGE_TYPE_JPEG, IMAGE_TYPE_PNG})
    @Retention(RetentionPolicy.SOURCE)
    @interface MediaType {
    }

    private Activity mActivity;
    private Fragment mFragment;
    private int mRequestCode;
    private int mMaxSelection;
    private List<Uri> mSelection;
    private String[] mMediaTypeFilter;

    private Louvre(@NonNull Activity activity) {
        mActivity = activity;
        mRequestCode = -1;
    }

    private Louvre(@NonNull Fragment fragment) {
        mFragment = fragment;
        mRequestCode = -1;
    }

    public static Louvre init(@NonNull Activity activity) {
        return new Louvre(activity);
    }

    public static Louvre init(@NonNull Fragment fragment) {
        return new Louvre(fragment);
    }

    public Louvre setRequestCode(int requestCode) {
        mRequestCode = requestCode;
        return this;
    }

    public Louvre setMaxSelection(@IntRange(from = 0) int maxSelection) {
        mMaxSelection = maxSelection;
        return this;
    }

    public Louvre setSelection(@NonNull List<Uri> selection) {
        mSelection = selection;
        return this;
    }

    public Louvre setMediaTypeFilter(@MediaType @NonNull String... mediaTypeFilter) {
        mMediaTypeFilter = mediaTypeFilter;
        return this;
    }

    public void open() {
        if (mRequestCode == -1) {
            throw new IllegalArgumentException("You need to define a request code in setRequestCode(int) method");
        }
        if (mActivity != null) {
            GalleryActivity.startActivity(mActivity, mRequestCode, mMaxSelection, mSelection, mMediaTypeFilter);
        } else {
            GalleryActivity.startActivity(mFragment, mRequestCode, mMaxSelection, mSelection, mMediaTypeFilter);
        }
    }

}
