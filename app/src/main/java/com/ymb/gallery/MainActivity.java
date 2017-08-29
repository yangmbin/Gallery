package com.ymb.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ymb.gallerylib.Louvre;
import com.ymb.gallerylib.home.GalleryActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button mGallery;
    private TextView mUrls;
    private static final int LOUVRE_REQUEST_CODE = 0;
    private List<Uri> mSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGallery = (Button) findViewById(R.id.btn_gallery);
        mGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Louvre.init(MainActivity.this)
                        .setRequestCode(LOUVRE_REQUEST_CODE)
                        .setMaxSelection(2)
                        .setSelection(mSelection)
                        .setMediaTypeFilter(new String[] {Louvre.IMAGE_TYPE_BMP, Louvre.IMAGE_TYPE_JPEG, Louvre.IMAGE_TYPE_PNG})
                        .open();

            }
        });
        mUrls = (TextView) findViewById(R.id.urls);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && requestCode == LOUVRE_REQUEST_CODE) {
            mSelection = GalleryActivity.getSelection(data);
            if (mSelection == null)
                return;
            String result = "";
            for (int i = 0; i < mSelection.size(); i++) {
                result = result + mSelection.get(i) + "\n";
            }
            mUrls.setText(result);
        }
    }
}
