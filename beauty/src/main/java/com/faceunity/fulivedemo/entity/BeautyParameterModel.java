package com.faceunity.fulivedemo.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.faceunity.entity.Filter;
import com.faceunity.fulivedemo.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 美颜参数SharedPreferences记录,目前仅以保存数据，可改造为以SharedPreferences保存数据
 * Created by tujh on 2018/3/7.
 */
public class BeautyParameterModel {

    public transient final String STR_FILTER_LEVEL = "FilterLevel_";

    public Map<String, Float> sFilterLevel = new HashMap<>(16);

    //默认选中粉嫩滤镜
    public String selectedFilter = FilterEnum.fennen.filterName();
    private transient Filter sFilter;

    /**
     * key: name，value: level
     */
    public Map<String, Float> sBatchMakeupLevel = new HashMap<>(8);

    // 美颜默认参数
    public float sSkinDetect = 1.0f;//精准磨皮
    public float sHeavyBlur = 0.0f;//美肤类型
    public float sHeavyBlurLevel = 0.7f;//磨皮
    public float sBlurLevel = 0.7f;//磨皮
    public float sColorLevel = 0.3f;//美白
    public float sRedLevel = 0.3f;//红润
    public float sEyeBright = 0.0f;//亮眼
    public float sToothWhiten = 0.0f;//美牙

    // 脸型默认参数
    public float sCheekThinning = 0f;//瘦脸
    public float sCheekV = 0.5f;//V脸
    public float sCheekNarrow = 0f;//窄脸
    public float sCheekSmall = 0f;//小脸
    public float sEyeEnlarging = 0.4f;//大眼
    public float sIntensityChin = 0.3f;//下巴
    public float sIntensityForehead = 0.3f;//额头
    public float sIntensityNose = 0.5f;//瘦鼻
    public float sIntensityMouth = 0.4f;//嘴形

    private transient final SparseArray<Float> FACE_SHAPE_DEFAULT_PARAMS = new SparseArray<>(16);

    public static BeautyParameterModel beautyParameterModel;

    public static BeautyParameterModel get(Context context) {
        if (beautyParameterModel == null) {
            synchronized (BeautyParameterModel.class) {
                if (beautyParameterModel == null) {

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String saveData = sharedPreferences.getString("BeautyParameter", null);

                    if (TextUtils.isEmpty(saveData)) {
                        beautyParameterModel = new BeautyParameterModel();
                    } else {
                        beautyParameterModel = JSON.parseObject(saveData, BeautyParameterModel.class);
                    }
                }
            }
        }
        return beautyParameterModel;
    }

    public BeautyParameterModel() {
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_thinning, sCheekThinning);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_narrow, sCheekNarrow);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_small, sCheekSmall);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_v, sCheekV);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_eye_enlarge, sEyeEnlarging);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_chin, sIntensityChin);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_forehead, sIntensityForehead);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_nose, sIntensityNose);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_mouth, sIntensityMouth);
        sFilter = Filter.create(selectedFilter);
    }

    /**
     * 保存美颜参数
     *
     * @param context
     */
    public final void save(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String saveData = JSON.toJSONString(this);
        sharedPreferences.edit().putString("BeautyParameter", saveData).apply();
    }

    public final void setsFilter(Filter filter) {
        sFilter = filter;
        selectedFilter = filter.filterName();
    }

    public Filter getsFilter() {
        return sFilter;
    }

    /**
     * 美颜效果是否打开
     *
     * @param checkId
     * @return
     */
    public boolean isOpen(int checkId) {
        if (checkId == R.id.beauty_box_skin_detect) {
            return sSkinDetect == 1;
        } else if (checkId == R.id.beauty_box_heavy_blur) {
            return sHeavyBlur == 1 ? sHeavyBlurLevel > 0 : sBlurLevel > 0;
        } else if (checkId == R.id.beauty_box_blur_level) {
            return sHeavyBlurLevel > 0;
        } else if (checkId == R.id.beauty_box_color_level) {
            return sColorLevel > 0;
        } else if (checkId == R.id.beauty_box_red_level) {
            return sRedLevel > 0;
        } else if (checkId == R.id.beauty_box_eye_bright) {
            return sEyeBright > 0;
        } else if (checkId == R.id.beauty_box_tooth_whiten) {
            return sToothWhiten != 0;
        } else if (checkId == R.id.beauty_box_eye_enlarge) {
            return sEyeEnlarging > 0;
        } else if (checkId == R.id.beauty_box_cheek_thinning) {
            return sCheekThinning > 0;
        } else if (checkId == R.id.beauty_box_cheek_narrow) {
            return sCheekNarrow > 0;
        } else if (checkId == R.id.beauty_box_cheek_v) {
            return sCheekV > 0;
        } else if (checkId == R.id.beauty_box_cheek_small) {
            return sCheekSmall > 0;
        } else if (checkId == R.id.beauty_box_intensity_chin) {
            return sIntensityChin != 0.5;
        } else if (checkId == R.id.beauty_box_intensity_forehead) {
            return sIntensityForehead != 0.5;
        } else if (checkId == R.id.beauty_box_intensity_nose) {
            return sIntensityNose > 0;
        } else if (checkId == R.id.beauty_box_intensity_mouth) {
            return sIntensityMouth != 0.5;
        }
        return true;
    }

    /**
     * 获取美颜的参数值
     *
     * @param checkId
     * @return
     */
    public float getValue(int checkId) {
        if (checkId == R.id.beauty_box_skin_detect) {
            return sSkinDetect;
        } else if (checkId == R.id.beauty_box_heavy_blur) {
            return sHeavyBlur == 1 ? sHeavyBlurLevel : sBlurLevel;
        } else if (checkId == R.id.beauty_box_blur_level) {
            return sHeavyBlurLevel;
        } else if (checkId == R.id.beauty_box_color_level) {
            return sColorLevel;
        } else if (checkId == R.id.beauty_box_red_level) {
            return sRedLevel;
        } else if (checkId == R.id.beauty_box_eye_bright) {
            return sEyeBright;
        } else if (checkId == R.id.beauty_box_tooth_whiten) {
            return sToothWhiten;
        } else if (checkId == R.id.beauty_box_eye_enlarge) {
            return sEyeEnlarging;
        } else if (checkId == R.id.beauty_box_cheek_thinning) {
            return sCheekThinning;
        } else if (checkId == R.id.beauty_box_cheek_narrow) {
            return sCheekNarrow;
        } else if (checkId == R.id.beauty_box_cheek_v) {
            return sCheekV;
        } else if (checkId == R.id.beauty_box_cheek_small) {
            return sCheekSmall;
        } else if (checkId == R.id.beauty_box_intensity_chin) {
            return sIntensityChin;
        } else if (checkId == R.id.beauty_box_intensity_forehead) {
            return sIntensityForehead;
        } else if (checkId == R.id.beauty_box_intensity_nose) {
            return sIntensityNose;
        } else if (checkId == R.id.beauty_box_intensity_mouth) {
            return sIntensityMouth;
        }
        return 0;
    }

    /**
     * 设置美颜的参数值
     *
     * @param checkId
     * @param value
     */
    public void setValue(int checkId, float value) {
        if (checkId == R.id.beauty_box_skin_detect) {
            sSkinDetect = value;
        } else if (checkId == R.id.beauty_box_heavy_blur) {
            if (sHeavyBlur == 1) {
                sHeavyBlurLevel = value;
            } else {
                sBlurLevel = value;
            }
        } else if (checkId == R.id.beauty_box_blur_level) {
            sHeavyBlurLevel = value;
        } else if (checkId == R.id.beauty_box_color_level) {
            sColorLevel = value;
        } else if (checkId == R.id.beauty_box_red_level) {
            sRedLevel = value;
        } else if (checkId == R.id.beauty_box_eye_bright) {
            sEyeBright = value;
        } else if (checkId == R.id.beauty_box_tooth_whiten) {
            sToothWhiten = value;
        } else if (checkId == R.id.beauty_box_eye_enlarge) {
            sEyeEnlarging = value;
        } else if (checkId == R.id.beauty_box_cheek_thinning) {
            sCheekThinning = value;
        } else if (checkId == R.id.beauty_box_cheek_v) {
            sCheekV = value;
        } else if (checkId == R.id.beauty_box_cheek_narrow) {
            sCheekNarrow = value;
        } else if (checkId == R.id.beauty_box_cheek_small) {
            sCheekSmall = value;
        } else if (checkId == R.id.beauty_box_intensity_chin) {
            sIntensityChin = value;
        } else if (checkId == R.id.beauty_box_intensity_forehead) {
            sIntensityForehead = value;
        } else if (checkId == R.id.beauty_box_intensity_nose) {
            sIntensityNose = value;
        } else if (checkId == R.id.beauty_box_intensity_mouth) {
            sIntensityMouth = value;
        }
    }

    /**
     * 默认的美型参数是否被修改过
     *
     * @return
     */
    public boolean checkIfFaceShapeChanged() {
        if (Float.compare(sCheekNarrow, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_narrow)) != 0) {
            return true;
        }
        if (Float.compare(sCheekSmall, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_small)) != 0) {
            return true;
        }
        if (Float.compare(sCheekV, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_v)) != 0) {
            return true;
        }
        if (Float.compare(sCheekThinning, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_thinning)) != 0) {
            return true;
        }
        if (Float.compare(sEyeEnlarging, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_enlarge)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityNose, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_nose)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityChin, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_chin)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityMouth, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_mouth)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityForehead, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_forehead)) != 0) {
            return true;
        }
        return false;
    }

    /**
     * 恢复美型的默认值
     */
    public void recoverToDefValue() {
        sCheekNarrow = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_narrow);
        sCheekSmall = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_small);
        sCheekV = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_v);
        sCheekThinning = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_thinning);
        sEyeEnlarging = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_enlarge);
        sIntensityNose = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_nose);
        sIntensityMouth = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_mouth);
        sIntensityForehead = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_forehead);
        sIntensityChin = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_chin);
    }

}