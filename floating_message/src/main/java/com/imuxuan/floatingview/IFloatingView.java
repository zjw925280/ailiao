package com.imuxuan.floatingview;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Yunpeng Li on 2018/3/15.
 */

public interface IFloatingView {

    FloatingView attach(Activity activity);

    FloatingView detach(Activity activity);

    EnFloatingView getView();

    FloatingView icon(@DrawableRes int resId);

    FloatingView layoutParams(ViewGroup.LayoutParams params);

    FloatingView listener(MagnetViewListener magnetViewListener);

}
