package com.example.tvbrowser;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    private WebView webView;
    private CheckBox darkModeCheckBox;
    private Button clearCookiesButton;
    private Button clearHistoryButton;
    private Button saveSettingsButton;
    private EditText homeUrlInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        darkModeCheckBox = findViewById(R.id.dark_mode_checkbox);
        clearCookiesButton = findViewById(R.id.clear_cookies_button);
        clearHistoryButton = findViewById(R.id.clear_history_button);
        saveSettingsButton = findViewById(R.id.save_settings_button);
        homeUrlInput = findViewById(R.id.home_url_input);

        // Load current settings
        loadSettings();

        // Initialize WebView
        webView = new WebView(this);

        // Clear cookies action
        clearCookiesButton.setOnClickListener(v -> {
            clearWebViewCookies();
            Toast.makeText(SettingsActivity.this, "Cookies Cleared", Toast.LENGTH_SHORT).show();
        });

        // Clear browsing history action
        clearHistoryButton.setOnClickListener(v -> {
            clearWebViewHistory();
            Toast.makeText(SettingsActivity.this, "History Cleared", Toast.LENGTH_SHORT).show();
        });

        // Save settings action
        saveSettingsButton.setOnClickListener(v -> {
            boolean isDarkMode = darkModeCheckBox.isChecked();
            saveSettings(isDarkMode);
            Toast.makeText(SettingsActivity.this, "Settings Saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // Load saved settings
    private void loadSettings() {
        SharedPreferences preferences = getSharedPreferences("browser_settings", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("darkMode", false);
        darkModeCheckBox.setChecked(isDarkMode);

        String homeUrl = preferences.getString("homeUrl", "https://lite.duckduckgo.com");
        homeUrlInput.setText(homeUrl);
    }

    // Save settings to SharedPreferences
    private void saveSettings(boolean isDarkMode) {
        SharedPreferences preferences = getSharedPreferences("browser_settings", MODE_PRIVATE);
        preferences.edit()
                .putBoolean("darkMode", isDarkMode)
                .putString("homeUrl", homeUrlInput.getText().toString())
                .apply();

        updateWebViewTheme(isDarkMode);
    }

    // Apply the dark mode setting to the WebView
    private void updateWebViewTheme(boolean isDarkMode) {
        WebSettings settings = webView.getSettings();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (isDarkMode) {
                settings.setForceDark(WebSettings.FORCE_DARK_ON);  // API >= 29
            } else {
                settings.setForceDark(WebSettings.FORCE_DARK_OFF);
            }
        } else {
            // For older devices (API < 29), apply manual CSS for dark mode
            String darkModeCSS = "body { background-color: #121212; color: white; }";
            if (isDarkMode) {
                webView.evaluateJavascript("document.body.style.backgroundColor='#121212'; document.body.style.color='white';", null);
            } else {
                webView.evaluateJavascript("document.body.style.backgroundColor=''; document.body.style.color='';", null);
            }
        }
    }

    // Clear all WebView cookies
    private void clearWebViewCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        cookieManager.flush();
    }

    // Clear WebView browsing history
    private void clearWebViewHistory() {
        webView.clearHistory();
    }
}
