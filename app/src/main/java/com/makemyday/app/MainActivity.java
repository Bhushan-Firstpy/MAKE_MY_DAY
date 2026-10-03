package com.makemyday.app;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_REQUEST = 1001;
    private static final String CHANNEL_ID = "daily_inspiration";

    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();

        webView = new WebView(this);
        setContentView(webView);

        webView.addJavascriptInterface(new NotificationBridge(this), "AndroidNotificationBridge");

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request != null ? request.getUrl() : null;
                return uri != null && !isAllowedUrl(uri.toString());
            }

            @Override
            @Deprecated
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return !isAllowedUrl(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String quoteText = getIntent() != null ? getIntent().getStringExtra(NotificationHelper.EXTRA_QUOTE_TEXT) : null;
                if (quoteText != null && !quoteText.trim().isEmpty()) {
                    String script = "if (typeof window.mmdOpenQuoteFromNotification === 'function') { window.mmdOpenQuoteFromNotification(" + quoteText.replace("\\", "\\\\").replace("\"", "\\\"") + "); }";
                    view.evaluateJavascript(script, null);
                }
            }
        });

        try {
            webView.loadUrl("file:///android_asset/index.html");
        } catch (Exception e) {
            Toast.makeText(this, "Unable to start MAKE MY DAY", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && webView != null) {
            String quoteText = intent.getStringExtra(NotificationHelper.EXTRA_QUOTE_TEXT);
            if (quoteText != null && !quoteText.trim().isEmpty()) {
                String script = "if (typeof window.mmdOpenQuoteFromNotification === 'function') { window.mmdOpenQuoteFromNotification(" + quoteText.replace("\\", "\\\\").replace("\"", "\\\"") + "); }";
                webView.post(() -> webView.evaluateJavascript(script, null));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode,
        String[] permissions,
        int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationPreferencesModel preferences = NotificationPreferenceStore.load(this);
                preferences.setNotificationsEnabled(true);
                NotificationPreferenceStore.save(this, preferences);
                NotificationScheduler.schedule(this, preferences);
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(
                    this,
                    "Notification permission is off. You can enable it later in app settings.",
                    Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    public void ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.POST_NOTIFICATIONS },
                NOTIFICATION_PERMISSION_REQUEST
            );
        }
    }

    public void triggerTestNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ensureNotificationPermission();
            return;
        }

        NotificationPreferencesModel preferences = NotificationPreferenceStore.load(this);
        if (!preferences.isNotificationsEnabled()) {
            preferences.setNotificationsEnabled(true);
            NotificationPreferenceStore.save(this, preferences);
        }

        NotificationQuoteSelector selector = new NotificationQuoteSelector();
        NotificationQuote quote = selector.selectQuote(preferences);
        if (quote == null) {
            Toast.makeText(this, "No notification content available right now.", Toast.LENGTH_SHORT).show();
            return;
        }

        NotificationHelper.sendNotification(this, quote, true);
        NotificationHelper.updateRecentQuoteHistory(this, quote);
    }

    private boolean isAllowedUrl(String url) {
        if (url == null) {
            return false;
        }
        return url.startsWith("file:///android_asset/") || url.startsWith("about:blank");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Daily Inspiration",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("MAKE MY DAY daily inspirational notifications");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
