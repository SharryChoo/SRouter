package com.sharry.srouter.support.call;

/**
 * The ICancelable implementor.
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2019-05-08 18:32
 */
class RealCancelable implements ICancelable {

    private boolean isCanceled;

    @Override
    public void cancel() {
        isCanceled = true;
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

}
