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
import android.widget.ScrollView;

import cn.coolbhu.snailgo.R;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class HomeMainFragment extends Fragment {

    private static final String SERVER_URL = "http://www.coolbhu.cn";

    //view
    public WebView mWebView = null;
    public ProgressBar mProgressBar = null;
    private PtrClassicFrameLayout mPtrLayout = null;
    private ScrollView mScrollView = null;

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
        mPtrLayout = (PtrClassicFrameLayout) view.findViewById(R.id.ptr_classic_layout);
        mScrollView = (ScrollView) view.findViewById(R.id.scroll_layout);


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

                    mProgressBar.setVisibility(View.GONE);

                    if (mPtrLayout != null) {

                        mPtrLayout.refreshComplete();
                    }
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

        //下拉刷新
        initPtr();
    }

    //initPtr
    private void initPtr() {

        mPtrLayout.setLastUpdateTimeRelateObject(this);

        mPtrLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                //开始刷新
                if (mWebView != null) {

                    mWebView.clearCache(false);
                    mWebView.reload();
                }
            }
        });
    }
}
