package cn.coolbhu.snailgo.fragments.main;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import cn.coolbhu.snailgo.R;

public class HomeMainFragment extends Fragment {

    private static final String SERVER_URL = "http://www.coolbhu.cn";

    //view
    public WebView mWebView = null;
    public ProgressBar mProgressBar = null;

    public static HomeMainFragment newInstance() {
        HomeMainFragment fragment = new HomeMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWebView = (WebView) view.findViewById(R.id.home_web_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);


        mWebView.loadUrl(SERVER_URL);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }


        });

        //设置进度条
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {

                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {

                    mProgressBar.setVisibility(View.VISIBLE);

                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        //支持js
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        //优先用缓存
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }


}
