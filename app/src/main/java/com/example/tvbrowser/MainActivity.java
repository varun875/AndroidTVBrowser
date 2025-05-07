package com.example.tvbrowser;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    private WebView webView;
    private ImageView cursor;
    private int cursorX = 500, cursorY = 600;
    private final int moveStep = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout rootLayout = new FrameLayout(this);

        // URL bar layout
        LinearLayout urlBar = new LinearLayout(this);
        urlBar.setOrientation(LinearLayout.HORIZONTAL);
        urlBar.setBackgroundColor(Color.parseColor("#EEEEEE"));
        urlBar.setPadding(32, 24, 32, 24);
        urlBar.setElevation(8f);
        FrameLayout.LayoutParams urlBarParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        urlBarParams.topMargin = 0;
        urlBar.setLayoutParams(urlBarParams);

        EditText urlInput = new EditText(this);
        urlInput.setHint("Enter URL");
        urlInput.setText("https://lite.duckduckgo.com");
        urlInput.setTextColor(Color.BLACK);
        urlInput.setBackground(null);
        urlInput.setPadding(32, 24, 32, 24);
        urlInput.setTextSize(16);
        urlInput.setSingleLine(true);
        LinearLayout.LayoutParams urlInputParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
        );
        urlInput.setLayoutParams(urlInputParams);

        Button goButton = new Button(this);
        goButton.setText("Go");
        goButton.setPadding(32, 16, 32, 16);
        goButton.setOnClickListener(v -> {
            String url = urlInput.getText().toString().trim();
            if (!url.startsWith("http")) {
                url = "https://" + url;
            }
            webView.loadUrl(url);
        });

        urlBar.addView(urlInput);
        urlBar.addView(goButton);

        // WebView setup
        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLoadsImagesAutomatically(true);
        settings.setBlockNetworkImage(true); // Load text first

        // Enable focus & scroll support
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus();
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        FrameLayout.LayoutParams webViewParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        webView.setLayoutParams(webViewParams);
        webView.loadUrl(urlInput.getText().toString());

        // Progress bar
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                8
        );
        progressParams.topMargin = 148;
        progressBar.setLayoutParams(progressParams);

        // Spinner
        ProgressBar spinner = new ProgressBar(this);
        spinner.setIndeterminate(true);
        spinner.setVisibility(View.GONE);
        FrameLayout.LayoutParams spinnerParams = new FrameLayout.LayoutParams(
                80, 80
        );
        spinnerParams.topMargin = 24;
        spinnerParams.rightMargin = 24;
        spinnerParams.gravity = Gravity.END | Gravity.TOP;
        spinner.setLayoutParams(spinnerParams);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setProgress(100);
                    Animation fadeOut = new AlphaAnimation(1, 0);
                    fadeOut.setDuration(500);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override public void onAnimationStart(Animation animation) {}
                        @Override public void onAnimationEnd(Animation animation) {
                            progressBar.setVisibility(View.GONE);
                        }
                        @Override public void onAnimationRepeat(Animation animation) {}
                    });
                    progressBar.startAnimation(fadeOut);
                    spinner.setVisibility(View.GONE);
                }
            }
        });

        // Cursor
        cursor = new ImageView(this);
        cursor.setImageResource(R.drawable.cursor); // Add cursor.png to res/drawable
        FrameLayout.LayoutParams cursorParams = new FrameLayout.LayoutParams(48, 48);
        cursorParams.leftMargin = cursorX;
        cursorParams.topMargin = cursorY;
        cursor.setLayoutParams(cursorParams);

        // Add views
        rootLayout.addView(webView);
        rootLayout.addView(urlBar);
        rootLayout.addView(progressBar);
        rootLayout.addView(spinner);
        rootLayout.addView(cursor); // Cursor always on top

        setContentView(rootLayout);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cursor.getLayoutParams();

            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    cursorY = Math.max(150, cursorY - moveStep);
                    webView.pageUp(false);
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    cursorY += moveStep;
                    webView.pageDown(false);
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    cursorX = Math.max(0, cursorX - moveStep);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    cursorX += moveStep;
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    simulateClickInWebView();
                    return true;
                default:
                    return super.dispatchKeyEvent(event);
            }

            params.leftMargin = cursorX;
            params.topMargin = cursorY;
            cursor.setLayoutParams(params);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void simulateClickInWebView() {
        long now = System.currentTimeMillis();

        int[] webViewLocation = new int[2];
        webView.getLocationOnScreen(webViewLocation);

        float x = cursorX - webViewLocation[0];
        float y = cursorY - webViewLocation[1];

        MotionEvent down = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, x, y, 0);
        MotionEvent up = MotionEvent.obtain(now + 100, now + 100, MotionEvent.ACTION_UP, x, y, 0);

        webView.dispatchTouchEvent(down);
        webView.dispatchTouchEvent(up);

        down.recycle();
        up.recycle();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
