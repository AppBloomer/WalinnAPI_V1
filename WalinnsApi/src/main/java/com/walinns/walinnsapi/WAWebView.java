package com.walinns.walinnsapi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.WebView;

/**
 * Created by walinnsinnovation on 06/06/18.
 */

public class WAWebView  extends WebView {
    private  OnScrollChangedCallback mOnScrollChangedCallback;

    public WAWebView(Context context) {
        super(context);
    }
    public WAWebView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WAWebView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        System.out.println("Observe scroll :" + oldl +"....new"+ oldt);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        System.out.println("Observe scroll screenState :" + screenState);

        super.onScreenStateChanged(screenState);
    }
    public  WAWebView.OnScrollChangedCallback getOnScrollChangedCallback()
    {
        System.out.println("Observe scroll screenState :" + "callback");

        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback)
    {
        System.out.println("Observe scroll screenState :" + "Change call back");

        mOnScrollChangedCallback = onScrollChangedCallback;
    }


    public static interface OnScrollChangedCallback
    {
        public void onScroll(int l, int t);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("Key down dispatch to "+ "............"+event.getKeyCode());
        return super.onKeyDown(keyCode, event);
    }
}
