package com.sharry.srouter.support;

/**
 * This interface provider cancel when {@link ICall} posted.
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2019-05-08 18:32
 */
public interface ICancelable {

    ICancelable DEFAULT = new ICancelable() {
        @Override
        public void cancel() {

        }

        @Override
        public boolean isCanceled() {
            return false;
        }
    };

    /**
     * Cancel the {@link ICall}.
     */
    void cancel();

    /**
     * Returns true if this call has been canceled.
     *
     * @return true if this call has been canceled.
     */
    boolean isCanceled();

}
