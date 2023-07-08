

package com.whu.eyerecongize.util;

/**
 * 时延工具类
 *
 * @since 2021-12-08
 */
public class CostTimeUtils {
    private volatile static CostTimeUtils costTimeUtils;

    long startTime = System.currentTimeMillis();

    private CostTimeUtils() {
    }

    public static CostTimeUtils getInstance() {
        if (costTimeUtils == null) {
            synchronized (CostTimeUtils.class) {
                if (costTimeUtils == null) {
                    costTimeUtils = new CostTimeUtils();
                }
            }
        }
        return costTimeUtils;
    }

    public void setStartTime(long time) {
        startTime = time;
    }

    public long getStartTime() {
        return startTime;
    }

}
