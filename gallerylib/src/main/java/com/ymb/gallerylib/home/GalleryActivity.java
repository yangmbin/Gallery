package com.ymb.gallerylib.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.andremion.counterfab.CounterFab;
import com.ymb.gallerylib.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements GalleryFragment.Callbacks, View.OnClickListener {

    private static final String EXTRA_MAX_SELECTION = GalleryActivity.class.getPackage().getName() + ".extra.MAX_SELECTION";
    private static final String EXTRA_MEDIA_TYPE_FILTER = GalleryActivity.class.getPackage().getName() + ".extra.MEDIA_TYPE_FILTER";
    private static final String EXTRA_SELECTION = GalleryActivity.class.getPackage().getName() + ".extra.SELECTION";
    private static final int DEFAULT_MAX_SELECTION = 1;

    public static void startActivity(@NonNull Activity activity, int requestCode,
                                     @IntRange(from = 0) int maxSelection,
                                     List<Uri> selection,
                                     String... mediaTypeFilter) {
        Intent intent = buildIntent(activity, maxSelection, selection, mediaTypeFilter);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivity(@NonNull Fragment fragment, int requestCode,
                                     @IntRange(from = 0) int maxSelection,
                                     List<Uri> selection,
                                     String... mediaTypeFilter) {
        Intent intent = buildIntent(fragment.getContext(), maxSelection, selection, mediaTypeFilter);
        fragment.startActivityForResult(intent, requestCode);
    }

    @NonNull
    private static Intent buildIntent(@NonNull Context context, @IntRange(from = 0) int maxSelection, List<Uri> selection, String[] mediaTypeFilter) {
        Intent intent = new Intent(context, GalleryActivity.class);
        if (maxSelection > 0) {
            intent.putExtra(EXTRA_MAX_SELECTION, maxSelection);
        }
        if (selection != null) {
            intent.putExtra(EXTRA_SELECTION, new LinkedList<>(selection));
        }
        if (mediaTypeFilter != null && mediaTypeFilter.length > 0) {
            intent.putExtra(EXTRA_MEDIA_TYPE_FILTER, mediaTypeFilter);
        }
        return intent;
    }

    public static List<Uri> getSelection(Intent data) {
        return data.getParcelableArrayListExtra(EXTRA_SELECTION);
    }

    private GalleryFragment mFragment;
    private ViewGroup mContentView;
    private CounterFab mFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mContentView = (ViewGroup) findViewById(R.id.coordinator_layout);

        mFab = (CounterFab) findViewById(R.id.fab_done);
        mFab.setOnClickListener(this);

        mFragment = (GalleryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_gallery);
        mFragment.setMaxSelection(getIntent().getIntExtra(EXTRA_MAX_SELECTION, DEFAULT_MAX_SELECTION));
        if (getIntent().hasExtra(EXTRA_SELECTION)) {
            mFragment.setSelection((List<Uri>) getIntent().getSerializableExtra(EXTRA_SELECTION));
        }
        if (getIntent().hasExtra(EXTRA_MEDIA_TYPE_FILTER)) {
            mFragment.setMediaTypeFilter(getIntent().getStringArrayExtra(EXTRA_MEDIA_TYPE_FILTER));
        }

        mFragment.loadBuckets();

    }

    @Override
    public void onClick(View v) {
        Intent data = new Intent();
        data.putExtra(EXTRA_SELECTION, (ArrayList<Uri>) mFragment.getSelection());
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBucketClick(String label) {
        // label为相册名称
    }

    @Override
    public void onMediaClick(@NonNull View imageView, @NonNull View checkView, long bucketId, int position) {
        // 点击图片
    }

    @Override
    public void onSelectionUpdated(int count) {
        mFab.setCount(count);
    }

    @Override
    public void onMaxSelectionReached() {
        Snackbar.make(mContentView, R.string.activity_gallery_max_selection_reached, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onWillExceedMaxSelection() {
        Snackbar.make(mContentView, R.string.activity_gallery_will_exceed_max_selection, Snackbar.LENGTH_SHORT).show();
    }

}
