package com.lovechatapp.chat.fragment;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.constant.Constant;

public class WebFragment extends BaseFragment {

    WebView mWebView;
    ProgressBar mProgressBar;
    TextView mRetryTv;

    public static WebFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(Constant.URL, url);
        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.GONE);
            }
            if (mRetryTv != null && mRetryTv.getVisibility() == View.VISIBLE) {
                if (mRetryTv.getTag() != null && view.getUrl().equals(mRetryTv.getTag().toString())) {
                    mRetryTv.setVisibility(View.VISIBLE);
                } else {
                    mRetryTv.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            if (mRetryTv != null) {
                mRetryTv.setTag(null);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (request.isForMainFrame()) {
                    if (mRetryTv != null) {
                        mRetryTv.setTag(view.getUrl());
                        mRetryTv.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mRetryTv != null) {
                    mRetryTv.setTag(view.getUrl());
                    mRetryTv.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient = new WebChromeClient() {
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定", null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (mProgressBar != null) {
                mProgressBar.setProgress(newProgress);
            }
        }
    };

    /**
     * JS调用android的方法
     */
    @JavascriptInterface //仍然必不可少
    public void getClient(String str) {
        Log.i("ansen", "html调用客户端:" + str);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放资源
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        if (webChromeClient != null) {
            webChromeClient = null;
        }
        if (webViewClient != null) {
            webViewClient = null;
        }
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_web;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        mRetryTv = view.findViewById(R.id.retry_tv);
        mRetryTv.setVisibility(View.GONE);
        mRetryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.reload();
            }
        });

        mWebView = view.findViewById(R.id.content_wb);
        mProgressBar = view.findViewById(R.id.progressbar);

        //添加js监听 这样html就能调用客户端
        mWebView.addJavascriptInterface(this, "android");
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(webViewClient);
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setAppCacheEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);//不使用缓存，只从网络获取数据.
        webSetting.setDisplayZoomControls(false);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(getActivity().getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(getActivity().getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(getActivity().getDir("geolocation", 0).getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
    }

    @Override
    protected void onFirstVisible() {
        String url = getArguments().getString(Constant.URL);
        mWebView.loadUrl(url);//加载url
    }
}