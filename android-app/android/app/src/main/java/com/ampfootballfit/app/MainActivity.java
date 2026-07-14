package com.ampfootballfit.app;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Guards against the WebView caching bundled app assets across APK
        // updates — without this, reinstalling a new build over an old one
        // can silently keep showing the previous version's UI.
        if (getBridge() != null && getBridge().getWebView() != null) {
            getBridge().getWebView().clearCache(true);
        }
    }
}
