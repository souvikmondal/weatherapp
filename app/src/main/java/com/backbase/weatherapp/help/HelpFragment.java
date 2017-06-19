package com.backbase.weatherapp.help;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.backbase.weatherapp.R;

/**
 * Created by Souvik on 19/06/17.
 */

public class HelpFragment extends Fragment {

    private WebView webView;

    public HelpFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_help, null);

        webView = (WebView) view.findViewById(R.id.webview);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initWebView();
    }

    private void initWebView() {
        webView.loadUrl("file:///android_asset/help/help.html");
    }

}
