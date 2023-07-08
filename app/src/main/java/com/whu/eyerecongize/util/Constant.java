

package com.whu.eyerecongize.util;

import android.graphics.Color;

import com.whu.eyerecongize.R;


public class Constant {

    public static final int GET_DATA_SUCCESS = 100;

    public static final int GET_DATA_FAILED = 101;

    public static final String CAMERA_FACING = "facing";

    public static final String CLOUD_IMAGE_CLASSIFICATION = "Cloud Classification";
    public static final String CLOUD_LANDMARK_DETECTION = "Landmark";
    public static final String MODEL_TYPE = "model_type";

    public static final String ADD_PICTURE_TYPE = "picture_type";
    public static final String TYPE_TAKE_PHOTO = "take photo";
    public static final String TYPE_SELECT_IMAGE = "select image";

    public static final String DEFAULT_VERSION = "1.0.3.300";

    public static int[] IMAGES = null;

    public static int[] COLOR_TABLE = {
            Color.rgb(255, 0, 0),
            Color.rgb(255, 255, 0),
            Color.rgb(0, 255, 0),
            Color.rgb(0, 255, 255),
            Color.rgb(0, 0, 255),
            Color.rgb(255, 0, 255),
            Color.rgb(255, 0, 0)
    };

    /**
     * Number of the background image used in the background replacement.
     */
    public static final String VALUE_KEY = "index_value";
}
