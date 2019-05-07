package com.sharry.srouter.support.data;

import android.app.Activity;
import android.content.Intent;

/**
 * The data parcel of {@link Activity#onActivityResult(int, int, Intent)}
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 3:03 PM
 */
public class ActivityResult {

    public final int requestCode;
    public final int resultCode;
    public final Intent data;
    private ActivityResult(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    /**
     * Create an instance of ActivityResult.
     */
    public static ActivityResult create(int requestCode, int resultCode, Intent data) {
        return new ActivityResult(requestCode, resultCode, data);
    }

}