package com.ymb.gallerylib.home;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.ymb.gallerylib.R;
import com.ymb.gallerylib.data.MediaLoader;
import com.ymb.gallerylib.util.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment implements MediaLoader.Callbacks, com.ymb.gallerylib.home.GalleryAdapter.Callbacks {

    public interface Callbacks {

        void onBucketClick(String label);

        void onMediaClick(@NonNull View imageView, View checkView, long bucketId, int position);

        void onSelectionUpdated(int count);

        void onMaxSelectionReached();

        void onWillExceedMaxSelection();
    }

    private final MediaLoader mMediaLoader;
    private final GalleryAdapter mAdapter;
    private View mEmptyView;
    private GridLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private Callbacks mCallbacks;
    private boolean mShouldHandleBackPressed;

    public GalleryFragment() {
        mMediaLoader = new MediaLoader();
        mAdapter = new GalleryAdapter();
        mAdapter.setCallbacks(this);
    }

    public void setMediaTypeFilter(@NonNull String[] mediaTypes) {
        mMediaLoader.setMediaTypes(mediaTypes);
    }

    public void setMaxSelection(@IntRange(from = 0) int maxSelection) {
        mAdapter.setMaxSelection(maxSelection);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof Callbacks)) {
            throw new IllegalArgumentException(context.getClass().getSimpleName() + " must implement " + Callbacks.class.getName());
        }
        mCallbacks = (Callbacks) context;
        if (!(context instanceof FragmentActivity)) {
            throw new IllegalArgumentException(context.getClass().getSimpleName() + " must inherit from " + FragmentActivity.class.getName());
        }
        mMediaLoader.onAttach((FragmentActivity) context, this);
    }

    @Override
    public void onBucketLoadFinished(@Nullable Cursor data) {
        mAdapter.swapData(GalleryAdapter.VIEW_TYPE_BUCKET, data);
        updateEmptyState();
    }

    @Override
    public void onMediaLoadFinished(@Nullable Cursor data) {
        mAdapter.swapData(GalleryAdapter.VIEW_TYPE_MEDIA, data);
        updateEmptyState();
    }

    private void updateEmptyState() {
        mRecyclerView.setVisibility(mAdapter.getItemCount() > 0 ? View.VISIBLE : View.INVISIBLE);
        mEmptyView.setVisibility(mAdapter.getItemCount() > 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        mMediaLoader.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        mEmptyView = view.findViewById(android.R.id.empty);

        mLayoutManager = new GridLayoutManager(getContext(), 1);
        mAdapter.setLayoutManager(mLayoutManager);

        final int spacing = getResources().getDimensionPixelSize(R.dimen.gallery_item_offset);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(spacing));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                int size = getResources().getDimensionPixelSize(R.dimen.gallery_item_size);
                int width = mRecyclerView.getMeasuredWidth();
                int columnCount = width / (size + spacing);
                mLayoutManager.setSpanCount(columnCount);
                return false;
            }
        });

        if (savedInstanceState != null) {
            updateEmptyState();
        }

        return view;
    }

    @Override
    public void onBucketClick(long bucketId, String label) {
        mMediaLoader.loadByBucket(bucketId);
        mCallbacks.onBucketClick(label);
        mShouldHandleBackPressed = true;
    }

    @Override
    public void onMediaClick(View imageView, View checkView, long bucketId, int position) {
        mCallbacks.onMediaClick(imageView, checkView, bucketId, position);
    }

    @Override
    public void onSelectionUpdated(int count) {
        mCallbacks.onSelectionUpdated(count);
    }

    @Override
    public void onMaxSelectionReached() {
        mCallbacks.onMaxSelectionReached();
    }

    @Override
    public void onWillExceedMaxSelection() {
        mCallbacks.onWillExceedMaxSelection();
    }

    public boolean onBackPressed() {
        if (mShouldHandleBackPressed) {
            loadBuckets();
            return true;
        }
        return false;
    }

    public void loadBuckets() {
        mMediaLoader.loadBuckets();
        mShouldHandleBackPressed = false;
    }

    public List<Uri> getSelection() {
        return new ArrayList<>(mAdapter.getSelection());
    }

    public void setSelection(@NonNull List<Uri> selection) {
        mAdapter.setSelection(selection);
    }

}
