package com.codejustice.global;

import com.codejustice.eyeconizefamily.R;

import java.util.HashMap;
import java.util.Map;

public class tempProfile {
    public static Map<Long, Integer> profile;
    static {
        profile = new HashMap<>();
        profile.put(0L, R.drawable.old_man_2);
        profile.put(1L, R.drawable.old_man_3);
        profile.put(2L, R.drawable.old_man_1);
        profile.put(3L, R.drawable.old_man_4);
        profile.put(4L, R.drawable.old_woman_1);
        profile.put(123456L, R.drawable.dingzhen);
    }

}
