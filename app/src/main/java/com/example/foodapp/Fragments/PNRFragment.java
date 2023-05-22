package com.example.foodapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foodapp.R;

public class PNRFragment extends Fragment {
    private View pnr_view;
    WebView web;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pnr_view=inflater.inflate(R.layout.fragment_pnr,container,false);

        web=pnr_view.findViewById(R.id.web);
        web.setWebViewClient(new myweb());
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl("https://indianrailways.info/");

        return  pnr_view;

    }
    public class myweb extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
