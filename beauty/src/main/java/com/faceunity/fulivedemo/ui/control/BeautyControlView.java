package com.faceunity.fulivedemo.ui.control;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.faceunity.OnFUControlListener;
import com.faceunity.entity.FaceMakeup;
import com.faceunity.entity.Filter;
import com.faceunity.entity.MakeupItem;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.BeautyParameterModel;
import com.faceunity.fulivedemo.entity.FaceMakeupEnum;
import com.faceunity.fulivedemo.entity.FilterEnum;
import com.faceunity.fulivedemo.ui.BeautyBox;
import com.faceunity.fulivedemo.ui.BeautyBoxGroup;
import com.faceunity.fulivedemo.ui.CheckGroup;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.VHSpaceItemDecoration;
import com.faceunity.fulivedemo.ui.dialog.BaseDialogFragment;
import com.faceunity.fulivedemo.ui.dialog.ConfirmDialogFragment;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;

import java.util.List;

/**
 * Created by tujh on 2017/8/15.
 */
public class BeautyControlView extends FrameLayout {

    private static final String TAG = "BeautyControlView";

    private Context mContext;

    private OnFUControlListener mOnFUControlListener;
    private RecyclerView mRvMakeupItems;

    public void setOnFUControlListener(@NonNull OnFUControlListener onFUControlListener) {
        mOnFUControlListener = onFUControlListener;
    }

    private CheckGroup mBottomCheckGroup;

    private HorizontalScrollView mSkinBeautySelect;
    private BeautyBoxGroup mSkinBeautyBoxGroup;
    private BeautyBox mBoxSkinDetect;
    private BeautyBox mBoxHeavyBlur;
    private BeautyBox mBoxBlurLevel;
    private BeautyBox mBoxEyeBright;
    private BeautyBox mBoxToothWhiten;
    private BeautyBoxGroup mFaceShapeBeautyBoxGroup;
    private FrameLayout mFlFaceShapeItems;
    private ImageView mIvRecoverFaceShpe;
    private TextView mTvRecoverFaceShpe;

    private RecyclerView mFilterRecyclerView;
    private FilterRecyclerAdapter mFilterRecyclerAdapter;
    private List<Filter> mFilters;

    private DiscreteSeekBar mBeautySeekBar;
    private FaceMakeupAdapter mFaceMakeupAdapter;
    private RelativeLayout mFaceShapeLayout;
    private boolean isShown;
    private boolean mFirstShowLightMakeup = false;

    public BeautyControlView(Context context) {
        this(context, null);
    }

    public BeautyControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private int mFilterPositionSelect = -1;

    public BeautyControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        BeautyParameterModel.get(mContext);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mFilters = FilterEnum.getFiltersByFilterType();
        LayoutInflater.from(context).inflate(R.layout.layout_beauty_control, this);
        initView();
    }

    private void initView() {
        initViewBottomRadio();
        initViewSkinBeauty();
        initViewFaceShape();
        initViewFilterRecycler();
        initMakeupView();
        initViewTop();
    }

    public void onResume() {
        updateViewSkinBeauty();
        updateViewFaceShape();
        hideBottomLayoutAnimator();
    }

    private void initMakeupView() {
        mRvMakeupItems = findViewById(R.id.rv_face_makeup);
        mRvMakeupItems.setHasFixedSize(true);
        ((SimpleItemAnimator) mRvMakeupItems.getItemAnimator()).setSupportsChangeAnimations(false);
        mRvMakeupItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mRvMakeupItems.addItemDecoration(new VHSpaceItemDecoration(0, getResources().getDimensionPixelSize(R.dimen.x15)));

        List<FaceMakeup> faceMakeups = FaceMakeupEnum.getBeautyFaceMakeup();
        mFaceMakeupAdapter = new FaceMakeupAdapter(faceMakeups);
        OnFaceMakeupClickListener onMpItemClickListener = new OnFaceMakeupClickListener();
        mFaceMakeupAdapter.setOnItemClickListener(onMpItemClickListener);
        mRvMakeupItems.setAdapter(mFaceMakeupAdapter);

        String selectedFilter = BeautyParameterModel.get(getContext()).selectedFilter;
        int faceIndex = 0;
        for (FaceMakeup faceMakeup : faceMakeups) {
            if (faceMakeup.getFilter().equals(selectedFilter)) {
                mFaceMakeupAdapter.setItemSelected(faceIndex);
                break;
            }
            faceIndex++;
        }
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    private void initViewBottomRadio() {
        mBottomCheckGroup = findViewById(R.id.beauty_radio_group);
        mBottomCheckGroup.setOnCheckedChangeListener(new CheckGroup.OnCheckedChangeListener() {
            int checkedidOld = View.NO_ID;

            @Override
            public void onCheckedChanged(CheckGroup group, int checkedId) {
                clickViewBottomRadio(checkedId);
                if ((checkedId == View.NO_ID || checkedId == checkedidOld) && checkedidOld != View.NO_ID) {
                    int endHeight = (int) getResources().getDimension(R.dimen.x98);
                    int startHeight = getHeight();
                    changeBottomLayoutAnimator(startHeight, endHeight);
                    isShown = false;
                } else if (checkedId != View.NO_ID && checkedidOld == View.NO_ID) {
                    int startHeight = (int) getResources().getDimension(R.dimen.x98);
                    int endHeight = (int) getResources().getDimension(R.dimen.x366);
                    changeBottomLayoutAnimator(startHeight, endHeight);
                    isShown = true;
                }
                checkedidOld = checkedId;
            }
        });
    }

    private void updateViewSkinBeauty() {
        mBoxSkinDetect.setVisibility(VISIBLE);
        mBoxHeavyBlur.setVisibility(VISIBLE);
        mBoxBlurLevel.setVisibility(GONE);
        mBoxEyeBright.setVisibility(VISIBLE);
        mBoxToothWhiten.setVisibility(VISIBLE);
        if (mOnFUControlListener != null) {
            mOnFUControlListener.onHeavyBlurSelected(BeautyParameterModel.get(mContext).sHeavyBlur);
        }
        onChangeFaceBeautyLevel(R.id.beauty_box_skin_detect);
        if (BeautyParameterModel.get(mContext).sHeavyBlur == 0) {
            onChangeFaceBeautyLevel(R.id.beauty_box_heavy_blur);
        } else {
            onChangeFaceBeautyLevel(R.id.beauty_box_blur_level);
        }
        onChangeFaceBeautyLevel(R.id.beauty_box_color_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_red_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_bright);
        onChangeFaceBeautyLevel(R.id.beauty_box_tooth_whiten);
    }

    private void initViewSkinBeauty() {
        mSkinBeautySelect = findViewById(R.id.skin_beauty_select_block);

        mSkinBeautyBoxGroup = findViewById(R.id.beauty_group_skin_beauty);
        mSkinBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
                mFaceShapeLayout.setVisibility(GONE);
                mBeautySeekBar.setVisibility(GONE);
                if (checkedId != R.id.beauty_box_skin_detect) {
                    seekToSeekBar(checkedId);
                    onChangeFaceBeautyLevel(checkedId);
                }
            }
        });
        mBoxSkinDetect = findViewById(R.id.beauty_box_skin_detect);
        mBoxSkinDetect.setOnOpenChangeListener(new BeautyBox.OnOpenChangeListener() {
            @Override
            public void onOpenChanged(BeautyBox beautyBox, boolean isOpen) {
                BeautyParameterModel.get(mContext).sSkinDetect = isOpen ? 1 : 0;
                setDescriptionShowStr(BeautyParameterModel.get(mContext).sSkinDetect == 0 ? R.string.beauty_box_skin_detect_close : R.string.beauty_box_skin_detect_open);
                onChangeFaceBeautyLevel(R.id.beauty_box_skin_detect);
            }
        });
        mBoxHeavyBlur = findViewById(R.id.beauty_box_heavy_blur);
        mBoxHeavyBlur.setOnDoubleChangeListener(new BeautyBox.OnDoubleChangeListener() {
            @Override
            public void onDoubleChanged(BeautyBox beautyBox, boolean isDouble) {
                BeautyParameterModel.get(mContext).sHeavyBlur = isDouble ? 1 : 0;
                setDescriptionShowStr(BeautyParameterModel.get(mContext).sHeavyBlur == 0 ? R.string.beauty_box_heavy_blur_normal_text : R.string.beauty_box_heavy_blur_double_text);
                seekToSeekBar(R.id.beauty_box_heavy_blur);
                onChangeFaceBeautyLevel(R.id.beauty_box_heavy_blur);
                if (mOnFUControlListener != null) {
                    mOnFUControlListener.onHeavyBlurSelected(BeautyParameterModel.get(mContext).sHeavyBlur);
                }
            }
        });
        mBoxBlurLevel = findViewById(R.id.beauty_box_blur_level);
        mBoxEyeBright = findViewById(R.id.beauty_box_eye_bright);
        mBoxToothWhiten = findViewById(R.id.beauty_box_tooth_whiten);
    }

    private void updateViewFaceShape() {
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_enlarge);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_thinning);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_v);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_narrow);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_small);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_chin);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_forehead);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_nose);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_mouth);
    }

    private void initViewFilterRecycler() {
        int filterIndex = 0;
        String selectedFilter = BeautyParameterModel.get(getContext()).selectedFilter;
        for (Filter mFilter : mFilters) {
            if (mFilter.filterName().equals(selectedFilter)) {
                mFilterPositionSelect = filterIndex;
                break;
            }
            filterIndex++;
        }
        mFilterRecyclerView = findViewById(R.id.filter_recycle_view);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mFilterRecyclerView.setAdapter(mFilterRecyclerAdapter = new FilterRecyclerAdapter());
    }

    private void initViewFaceShape() {
        mFlFaceShapeItems = findViewById(R.id.fl_face_shape_items);
        mIvRecoverFaceShpe = findViewById(R.id.iv_recover_face_shape);
        mIvRecoverFaceShpe.setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(mContext.getString(R.string.dialog_reset_avatar_model), new BaseDialogFragment.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        // recover params
                        setRecoverFaceShapeEnable(false);
                        BeautyParameterModel.get(mContext).recoverToDefValue();
                        updateViewFaceShape();
                        int checkedId = mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId();
                        seekToSeekBar(checkedId);
                        setRecoverFaceShapeEnable(false);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                confirmDialogFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "ConfirmDialogFragmentReset");
            }
        });
        mTvRecoverFaceShpe = findViewById(R.id.tv_recover_face_shape);
        mFaceShapeBeautyBoxGroup = findViewById(R.id.beauty_group_face_shape);
        mFaceShapeBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
                mFaceShapeLayout.setVisibility(GONE);
                mBeautySeekBar.setVisibility(GONE);
                seekToSeekBar(checkedId);
                onChangeFaceBeautyLevel(checkedId);
            }
        });
        checkFaceShapeChanged();
    }

    private void setRecoverFaceShapeEnable(boolean enable) {
        if (enable) {
            mIvRecoverFaceShpe.setAlpha(1f);
            mTvRecoverFaceShpe.setAlpha(1f);
        } else {
            mIvRecoverFaceShpe.setAlpha(0.6f);
            mTvRecoverFaceShpe.setAlpha(0.6f);
        }
        mIvRecoverFaceShpe.setEnabled(enable);
        mTvRecoverFaceShpe.setEnabled(enable);
    }

    private void onChangeFaceBeautyLevel(int viewId) {
        if (viewId == View.NO_ID) {
            return;
        }
        ((BeautyBox) findViewById(viewId)).setOpen(BeautyParameterModel.get(mContext).isOpen(viewId));
        if (mOnFUControlListener == null) {
            return;
        }
        if (viewId == R.id.beauty_box_skin_detect) {
            mOnFUControlListener.onSkinDetectSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_heavy_blur) {
            mOnFUControlListener.onBlurLevelSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_blur_level) {
            mOnFUControlListener.onBlurLevelSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_color_level) {
            mOnFUControlListener.onColorLevelSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_red_level) {
            mOnFUControlListener.onRedLevelSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_eye_bright) {
            mOnFUControlListener.onEyeBrightSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_tooth_whiten) {
            mOnFUControlListener.onToothWhitenSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_eye_enlarge) {
            mOnFUControlListener.onEyeEnlargeSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_cheek_thinning) {
            mOnFUControlListener.onCheekThinningSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_cheek_narrow) {
            mOnFUControlListener.onCheekNarrowSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_cheek_v) {
            mOnFUControlListener.onCheekVSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_cheek_small) {
            mOnFUControlListener.onCheekSmallSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_intensity_chin) {
            mOnFUControlListener.onIntensityChinSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_intensity_forehead) {
            mOnFUControlListener.onIntensityForeheadSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_intensity_nose) {
            mOnFUControlListener.onIntensityNoseSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        } else if (viewId == R.id.beauty_box_intensity_mouth) {
            mOnFUControlListener.onIntensityMouthSelected(BeautyParameterModel.get(mContext).getValue(viewId));
        }
    }

    private void initViewTop() {
        mFaceShapeLayout = findViewById(R.id.face_shape_radio_layout);
        mBeautySeekBar = findViewById(R.id.beauty_seek_bar);
        mBeautySeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar SeekBar, int value, boolean fromUser) {

                if (!fromUser) {
                    return;
                }

                float valueF = 1.0f * (value - SeekBar.getMin()) / 100;
                int checkedCheckBoxId = mBottomCheckGroup.getCheckedCheckBoxId();

                //美肤
                if (checkedCheckBoxId == R.id.beauty_radio_skin_beauty) {
                    BeautyParameterModel.get(mContext).setValue(mSkinBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                    onChangeFaceBeautyLevel(mSkinBeautyBoxGroup.getCheckedBeautyBoxId());
                }

                //美型
                else if (checkedCheckBoxId == R.id.beauty_radio_face_shape) {
                    BeautyParameterModel.get(mContext).setValue(mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                    onChangeFaceBeautyLevel(mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId());
                    checkFaceShapeChanged();
                }

                //滤镜
                else if (checkedCheckBoxId == R.id.beauty_radio_filter) {
                    mFilterRecyclerAdapter.setFilterLevels(valueF);
                }

                //质感美颜
                else if (checkedCheckBoxId == R.id.beauty_radio_face_beauty) {
                    // 整体妆容调节
                    float level = 1.0f * value / 100;
                    FaceMakeup faceMakeup = mFaceMakeupAdapter.getSelectedItems().valueAt(0);
                    String name = getResources().getString(faceMakeup.getNameId());
                    BeautyParameterModel.get(mContext).sBatchMakeupLevel.put(name, level);
                    List<MakeupItem> makeupItems = faceMakeup.getMakeupItems();
                    /* 数学公式
                     * 0.4        0.7
                     * strength  level
                     * --> strength = 0.4 * level / 0.7
                     *   if level = 1.0, then strength = 0.57
                     *   if level = 0.2, then strength = 0.11
                     *   so, float strength = item.defaultLevel * level / DEFAULT_BATCH_MAKEUP_LEVEL
                     * */
                    if (makeupItems != null) {
                        for (MakeupItem makeupItem : makeupItems) {
                            float lev = makeupItem.getDefaultLevel() * level / FaceMakeupEnum.MAKEUP_OVERALL_LEVEL.get(faceMakeup.getNameId());
                            makeupItem.setLevel(lev);
                        }
                    }
                    mOnFUControlListener.onLightMakeupOverallLevelChanged(level);
                    mOnFUControlListener.onFilterLevelSelected(level);
                }
            }
        });
    }

    private void checkFaceShapeChanged() {
        if (BeautyParameterModel.get(mContext).checkIfFaceShapeChanged()) {
            setRecoverFaceShapeEnable(true);
        } else {
            setRecoverFaceShapeEnable(false);
        }
    }

    /**
     * 点击底部 tab
     *
     * @param viewId
     */
    private void clickViewBottomRadio(int viewId) {
        mSkinBeautySelect.setVisibility(GONE);
        mFlFaceShapeItems.setVisibility(GONE);
        mFilterRecyclerView.setVisibility(GONE);
        mRvMakeupItems.setVisibility(GONE);

        mFaceShapeLayout.setVisibility(GONE);
        mBeautySeekBar.setVisibility(GONE);
        if (viewId == R.id.beauty_radio_skin_beauty) {
            mSkinBeautySelect.setVisibility(VISIBLE);
            int id = mSkinBeautyBoxGroup.getCheckedBeautyBoxId();
            if (id != R.id.beauty_box_skin_detect) {
                seekToSeekBar(id);
            }
        } else if (viewId == R.id.beauty_radio_face_shape) {
            mFlFaceShapeItems.setVisibility(VISIBLE);
            int id = mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId();
            seekToSeekBar(id);
        } else if (viewId == R.id.beauty_radio_filter) {
            mFilterRecyclerView.setVisibility(VISIBLE);
            mFilterRecyclerAdapter.setFilterProgress();
        } else if (viewId == R.id.beauty_radio_face_beauty) {
            mRvMakeupItems.setVisibility(VISIBLE);
            mBeautySeekBar.setVisibility(INVISIBLE);
            // 首次选中第一个桃花妆
            if (mFirstShowLightMakeup) {
                mFirstShowLightMakeup = false;
                mFaceMakeupAdapter.setItemSelected(1);
                new OnFaceMakeupClickListener().onItemClick(mFaceMakeupAdapter, null, 1);
            }
            FaceMakeup faceMakeup = mFaceMakeupAdapter.getSelectedItems().valueAt(0);
            if (faceMakeup != null) {
                String name = getResources().getString(faceMakeup.getNameId());
                Float level = BeautyParameterModel.get(mContext).sBatchMakeupLevel.get(name);
                if (level == null) {
                    level = FaceMakeupEnum.MAKEUP_OVERALL_LEVEL.get(faceMakeup.getNameId());
                    BeautyParameterModel.get(mContext).sBatchMakeupLevel.put(name, level);
                }
                if (level != null) {
                    seekToSeekBar(level);
                }
            }
        }
    }

    private void seekToSeekBar(float value) {
        seekToSeekBar(value, 0, 100);
    }

    private void seekToSeekBar(float value, int min, int max) {
        mBeautySeekBar.setVisibility(VISIBLE);
        mBeautySeekBar.setMin(min);
        mBeautySeekBar.setMax(max);
        mBeautySeekBar.setProgress((int) (value * (max - min) + min));
    }

    private void seekToSeekBar(int checkedId) {
        if (checkedId == View.NO_ID) {
            return;
        }

        float value = BeautyParameterModel.get(mContext).getValue(checkedId);
        int min = 0;
        int max = 100;
        if (checkedId == R.id.beauty_box_intensity_chin || checkedId == R.id.beauty_box_intensity_forehead || checkedId == R.id.beauty_box_intensity_mouth) {
            min = -50;
            max = 50;
        }
        seekToSeekBar(value, min, max);
    }

    private void changeBottomLayoutAnimator(final int startHeight, final int endHeight) {
        if (mBottomLayoutAnimator != null && mBottomLayoutAnimator.isRunning()) {
            mBottomLayoutAnimator.end();
        }
        mBottomLayoutAnimator = ValueAnimator.ofInt(startHeight, endHeight).setDuration(150);
        mBottomLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = getLayoutParams();
                if (params == null) {
                    return;
                }
                params.height = height;
                setLayoutParams(params);
                if (mOnBottomAnimatorChangeListener != null) {
                    float showRate = 1.0f * (height - startHeight) / (endHeight - startHeight);
                    mOnBottomAnimatorChangeListener.onBottomAnimatorChangeListener(startHeight > endHeight ? 1 - showRate : showRate);
                }
            }
        });
        mBottomLayoutAnimator.start();
    }

    public interface OnBottomAnimatorChangeListener {
        void onBottomAnimatorChangeListener(float showRate);
    }

    public void setOnBottomAnimatorChangeListener(OnBottomAnimatorChangeListener onBottomAnimatorChangeListener) {
        mOnBottomAnimatorChangeListener = onBottomAnimatorChangeListener;
    }

    private OnBottomAnimatorChangeListener mOnBottomAnimatorChangeListener;

    private ValueAnimator mBottomLayoutAnimator;

    private void setDescriptionShowStr(int str) {
        if (mOnDescriptionShowListener != null) {
            mOnDescriptionShowListener.onDescriptionShowListener(str);
        }
    }

    public void hideBottomLayoutAnimator() {
        mBottomCheckGroup.check(View.NO_ID);
    }

    public final void setCheck() {
        CompoundButton button = (CompoundButton) mBottomCheckGroup.getChildAt(0);
        button.setChecked(true);
    }

    public interface OnDescriptionShowListener {
        void onDescriptionShowListener(int str);
    }

    public void setOnDescriptionShowListener(OnDescriptionShowListener onDescriptionShowListener) {
        mOnDescriptionShowListener = onDescriptionShowListener;
    }

    private OnDescriptionShowListener mOnDescriptionShowListener;

    public void setFilterLevel(String filterName, float faceBeautyFilterLevel) {
        BeautyParameterModel.get(mContext).sFilterLevel.put(BeautyParameterModel.get(mContext).STR_FILTER_LEVEL + filterName, faceBeautyFilterLevel);
        if (mOnFUControlListener != null) {
            mOnFUControlListener.onFilterLevelSelected(faceBeautyFilterLevel);
        }
    }

    public float getFilterLevel(String filterName) {
        String key = BeautyParameterModel.get(mContext).STR_FILTER_LEVEL + filterName;
        Float level = BeautyParameterModel.get(mContext).sFilterLevel.get(key);
        if (level == null) {
            level = Filter.DEFAULT_FILTER_LEVEL;
            BeautyParameterModel.get(mContext).sFilterLevel.put(key, level);
        }
        setFilterLevel(filterName, level);
        return level;
    }

    class FilterRecyclerAdapter extends RecyclerView.Adapter<FilterRecyclerAdapter.HomeRecyclerHolder> {

        @Override
        public HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new HomeRecyclerHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_beauty_control_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(HomeRecyclerHolder holder, @SuppressLint("RecyclerView") final int position) {
            final List<Filter> filters = mFilters;
            holder.filterImg.setImageResource(filters.get(position).resId());
            holder.filterName.setText(filters.get(position).description());
            if (mFilterPositionSelect == position) {
                mFaceMakeupAdapter.clearSingleItemSelected();
                holder.filterImg.setBackgroundResource(R.drawable.control_filter_select);
            } else {
                holder.filterImg.setBackgroundResource(0);
            }
            holder.itemView.setOnClickListener(new OnMultiClickListener() {
                @Override
                protected void onMultiClick(View v) {
                    mFilterPositionSelect = position;
                    mBeautySeekBar.setVisibility(position == 0 ? INVISIBLE : VISIBLE);
                    setFilterProgress();
                    notifyDataSetChanged();
                    if (mOnFUControlListener != null) {
                        BeautyParameterModel.get(mContext).setsFilter(filters.get(mFilterPositionSelect));
                        mOnFUControlListener.onFilterNameSelected(BeautyParameterModel.get(mContext).getsFilter().filterName());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFilters.size();
        }

        public void setFilterLevels(float filterLevels) {
            if (mFilterPositionSelect >= 0) {
                setFilterLevel(mFilters.get(mFilterPositionSelect).filterName(), filterLevels);
            }
        }

        public void setFilter(Filter filter) {
            mFilterPositionSelect = mFilters.indexOf(filter);
        }

        public int indexOf(Filter filter) {
            for (int i = 0; i < mFilters.size(); i++) {
                if (filter.filterName().equals(mFilters.get(i).filterName())) {
                    return i;
                }
            }
            return -1;
        }

        public void setFilterProgress() {
            if (mFilterPositionSelect >= 0) {
                seekToSeekBar(getFilterLevel(mFilters.get(mFilterPositionSelect).filterName()));
            }
        }

        class HomeRecyclerHolder extends RecyclerView.ViewHolder {

            ImageView filterImg;
            TextView filterName;

            public HomeRecyclerHolder(View itemView) {
                super(itemView);
                filterImg = (ImageView) itemView.findViewById(R.id.control_recycler_img);
                filterName = (TextView) itemView.findViewById(R.id.control_recycler_text);
            }
        }
    }

    // ----------- 新添加的美妆组合，也叫质感美颜

    // 美妆列表点击事件
    private class OnFaceMakeupClickListener implements BaseRecyclerAdapter.OnItemClickListener<FaceMakeup> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<FaceMakeup> adapter, View view, int position) {

            Filter selectedFilter;
            float filterLevel = 0;

            FaceMakeup faceMakeup = adapter.getItem(position);

            if (TextUtils.isEmpty(faceMakeup.getFilter())) {

                // 卸妆
                mBeautySeekBar.setVisibility(View.INVISIBLE);

                selectedFilter = mFilters.get(0);

            } else {

                // 效果妆容
                mBeautySeekBar.setVisibility(View.VISIBLE);

                String name = getResources().getString(faceMakeup.getNameId());
                Float level = BeautyParameterModel.get(mContext).sBatchMakeupLevel.get(name);
                boolean used = true;
                if (level == null) {
                    used = false;
                    level = FaceMakeupEnum.MAKEUP_OVERALL_LEVEL.get(faceMakeup.getNameId());
                    BeautyParameterModel.get(mContext).sBatchMakeupLevel.put(name, level);
                }
                seekToSeekBar(level);

                mOnFUControlListener.onLightMakeupOverallLevelChanged(level);

                Pair<Filter, Float> filterFloatPair = FaceMakeupEnum.MAKEUP_FILTERS.get(faceMakeup.getNameId());
                selectedFilter = filterFloatPair.first;

                filterLevel = used ? level : filterFloatPair.second;
            }

            mFilterPositionSelect = -1;
            mFilterRecyclerAdapter.notifyDataSetChanged();

            mOnFUControlListener.onLightMakeupBatchSelected(faceMakeup.getMakeupItems());
            mOnFUControlListener.onFilterNameSelected(selectedFilter.filterName());
            BeautyParameterModel.get(mContext).setsFilter(selectedFilter);
            setFilterLevel(selectedFilter.filterName(), filterLevel);
        }
    }

    // 妆容组合适配器
    private class FaceMakeupAdapter extends BaseRecyclerAdapter<FaceMakeup> {

        FaceMakeupAdapter(@NonNull List<FaceMakeup> data) {
            super(data, R.layout.layout_rv_makeup);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, FaceMakeup item) {
            viewHolder.setText(R.id.tv_makeup, getResources().getString(item.getNameId()))
                    .setImageResource(R.id.iv_makeup, item.getIconId());
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, FaceMakeup data, boolean selected) {
            ((TextView) viewHolder.getViewById(R.id.tv_makeup)).setTextColor(selected ?
                    getResources().getColor(R.color.main_color) : getResources().getColor(R.color.colorWhite));
            viewHolder.setBackground(R.id.iv_makeup, selected ? R.drawable.control_filter_select : 0);
        }
    }

}