package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 滑动浏览图片
 */
public class SlidePhotoActivity extends BaseActivity {

    @BindView(R.id.content_vp)
    ViewPager contentVp;

    @BindView(R.id.index_tv)
    TextView indexTv;

    private int currentPosition;

    public static void start(Context context, ArrayList<String> list, int selected) {
        if (list == null || list.size() == 0) {
            return;
        }
        Intent starter = new Intent(context, SlidePhotoActivity.class);
        starter.putStringArrayListExtra("list", list);
        starter.putExtra("selected", selected);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_slide_photo);
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        initVp();
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @OnClick(R.id.finish_btn)
    public void onClick() {
        finish();
    }

    private void initVp() {

        final List<String> urls = getIntent().getStringArrayListExtra("list");

        final int dataCount = urls.size();

        if (dataCount == 0) {
            return;
        }

        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return dataCount;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PhotoView imageView = createPhotoView(urls.get(position));
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        };

        contentVp.setAdapter(pagerAdapter);

        //设置滑动监听事件
        contentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                indexTv.setText(String.format("%s / %s", position + 1, dataCount));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        currentPosition = getIntent().getIntExtra("selected", 0);
        indexTv.setText(String.format("%s / %s", currentPosition + 1, dataCount));
        contentVp.setCurrentItem(currentPosition);
    }

    private PhotoView createPhotoView(String url) {

        PhotoView photoView = new PhotoView(mContext);

        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

        Glide.with(this)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .listener(new RequestListener<Drawable>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        attacher.update();
                        return false;
                    }

                }).into(photoView);

        attacher.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                finish();
            }
        });

        return photoView;
    }
}
