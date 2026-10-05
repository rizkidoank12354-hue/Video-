package com.example.videoplayer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * Minimal local video player. Uses only the Android platform APIs
 * (VideoView + MediaController), so there are no extra dependencies.
 *
 * - "Open video" button picks a file from device storage.
 * - Also registered to handle ACTION_VIEW for video/* from other apps.
 */
public class MainActivity extends Activity {

    private static final int REQ_PICK_VIDEO = 1;
    private static final String KEY_URI = "uri";
    private static final String KEY_POS = "pos";
    private static final String KEY_PLAYING = "playing";

    private VideoView videoView;
    private Button openButton;
    private Uri currentUri;
    private int resumePosition = 0;
    private boolean resumePlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFF000000);

        videoView = new VideoView(this);
        FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        root.addView(videoView, videoParams);

        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        videoView.setMediaController(controller);

        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, "Cannot play this video", Toast.LENGTH_SHORT).show();
            openButton.setVisibility(android.view.View.VISIBLE);
            return true;
        });
        videoView.setOnCompletionListener(mp -> openButton.setVisibility(android.view.View.VISIBLE));

        openButton = new Button(this);
        openButton.setText("Open video");
        openButton.setOnClickListener(v -> pickVideo());
        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        root.addView(openButton, buttonParams);

        setContentView(root);

        if (savedInstanceState != null) {
            String saved = savedInstanceState.getString(KEY_URI);
            if (saved != null) {
                resumePosition = savedInstanceState.getInt(KEY_POS, 0);
                resumePlaying = savedInstanceState.getBoolean(KEY_PLAYING, true);
                play(Uri.parse(saved));
                return;
            }
        }

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null) {
            play(intent.getData());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null) {
            resumePosition = 0;
            resumePlaying = true;
            play(intent.getData());
        }
    }

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        startActivityForResult(intent, REQ_PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_VIDEO && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            resumePosition = 0;
            resumePlaying = true;
            play(data.getData());
        }
    }

    private void play(Uri uri) {
        currentUri = uri;
        openButton.setVisibility(android.view.View.GONE);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> {
            if (resumePosition > 0) {
                videoView.seekTo(resumePosition);
            }
            if (resumePlaying) {
                videoView.start();
            }
            resumePosition = 0;
        });
        videoView.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentUri != null) {
            resumePosition = videoView.getCurrentPosition();
            resumePlaying = false;
            videoView.pause();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentUri != null) {
            outState.putString(KEY_URI, currentUri.toString());
            outState.putInt(KEY_POS, videoView.getCurrentPosition());
            outState.putBoolean(KEY_PLAYING, videoView.isPlaying());
        }
    }
}
