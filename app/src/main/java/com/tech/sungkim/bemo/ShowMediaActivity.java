package com.tech.sungkim.bemo;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.tech.sungkim.util.FileUtils;

import static android.R.attr.type;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.tech.sungkim.bemo.R.id.textView;

public class ShowMediaActivity extends AppCompatActivity {

    ImageView imageView;
    VideoView video_player_view;
    MediaController media_Controller;
    Uri mUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_media);
        imageView = (ImageView) findViewById(R.id.image_view);
        video_player_view = (VideoView) findViewById(R.id.video_view);
        String uriString = (String)getIntent().getExtras().get("uri");
        if(FileUtils.isLocal(uriString)) {
            mUri = Uri.parse(uriString);
            Log.d("tejasvi", mUri.toString());
            String type = getMimeType(mUri.toString());
            if (type.contains("image")) {
                imageView.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(uriString)
                        .fitCenter()
                        .into(imageView);
            }
        }else{
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(uriString)
                    .fitCenter()
                    .into(imageView);
        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

   /* public void getInit()
    {
        video_player_view = (VideoView) findViewById(R.id.video_view);
        video_player_view.setVisibility(View.VISIBLE);
        media_Controller = new MediaController(this);
        video_player_view.setMediaController(media_Controller);
        video_player_view.setVideoURI(mUri);
        video_player_view.start();
    }*/
}
