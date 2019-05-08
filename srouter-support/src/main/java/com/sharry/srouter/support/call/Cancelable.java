package com.sharry.srouter.support.call;

/**
 * The cancelable associated with {@link ICall}
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2019-05-08 18:32
 */
public class Cancelable {

    private boolean isCanceled = false;

    Cancelable() {
    }

    public void cancel() {
        isCanceled = true;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

}
