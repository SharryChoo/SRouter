package com.sharry.sroutersupport.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.SparseArray;

import com.sharry.sroutersupport.facade.SRouter;
import com.sharry.sroutersupport.interceptors.IInterceptor;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * The request associated with a navigation.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public class NavigationRequest extends RouteMeta {

    public static final int NON_REQUEST_CODE = -1;

    public static NavigationRequest create(@NonNull String path) {
        return new NavigationRequest(path);
    }

    /**
     * Navigation path.
     */
    private final String path;

    /**
     * The datum for the route navigation.
     */
    private Bundle bundle;

    /**
     * The Flag for the route navigation.
     */
    private int flags = -1;

    /**
     * The requestCode for the requestCode.
     */
    private int requestCode = NON_REQUEST_CODE;

    /**
     * The jump activity options for the request.
     */
    private ActivityOptionsCompat activityOptions;

    /**
     * Navigation timeout, TimeUnit.Second.
     */
    private int timeout = 300;

    /**
     * if true, it will ignore interceptor.
     */
    private boolean isGreenChannel;

    /**
     * The interceptors will be process before {@link Warehouse#ROUTES_INTERCEPTORS}
     */
    private final List<IInterceptor> interceptors = new ArrayList<>();

    /**
     * The interceptors will be process after {@link Warehouse#ROUTES_INTERCEPTORS} and
     * before {@link com.sharry.sroutersupport.interceptors.NavigationInterceptor}
     */
    private final List<IInterceptor> navigationInterceptors = new ArrayList<>();

    private NavigationRequest(String path) {
        this.path = path;
        bundle = new Bundle();
    }

    /**
     * BE ATTENTION TO THIS METHOD WAS <P>SET, NOT ADD!</P>
     */
    public NavigationRequest setBundle(Bundle bundle) {
        if (bundle != null) {
            this.bundle = bundle;
        }
        return this;
    }

    /**
     * Set timeout time when navigation process.
     * <p>
     * Unit is{@link java.util.concurrent.TimeUnit#MILLISECONDS}
     */
    public NavigationRequest setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Set green channel associated with this NavigationRequest.
     *
     * @param isGreenChannel if true will ignore Route INTERCEPTORS.
     */
    public NavigationRequest setGreenChannel(boolean isGreenChannel) {
        this.isGreenChannel = isGreenChannel;
        return this;
    }

    /**
     * Set request code for the navigation.
     */
    public NavigationRequest setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    /**
     * Add interceptor for the request.
     */
    public NavigationRequest addInterceptor(@NonNull IInterceptor interceptor) {
        interceptors.add(interceptor);
        return this;
    }

    /**
     * Add navigation interceptor for the request.
     */
    public NavigationRequest addNavigationInterceptor(@NonNull IInterceptor interceptor) {
        navigationInterceptors.add(interceptor);
        return this;
    }

    /**
     * Set activity options when jump to other page.
     */
    public NavigationRequest setActivityOptions(ActivityOptionsCompat options) {
        this.activityOptions = options;
        return this;
    }

    /**
     * Get NavigationRequest data.
     */
    public String getPath() {
        return path;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isGreenChannel() {
        return isGreenChannel;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public ActivityOptionsCompat getActivityOptions() {
        return activityOptions;
    }

    public List<IInterceptor> getInterceptors() {
        return interceptors;
    }

    public List<IInterceptor> getNavigationInterceptors() {
        return navigationInterceptors;
    }

    /**
     * Start navigation.
     */
    public NavigationResponse navigation() {
        return this.navigation(null);
    }

    public NavigationResponse navigation(Context context) {
        return SRouter.getInstance().navigation(context, this);
    }

    // ######################### annotation @FlagInt copy from #{Intent}  ##########################
    @IntDef({
            Intent.FLAG_ACTIVITY_SINGLE_TOP,
            Intent.FLAG_ACTIVITY_NEW_TASK,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
            Intent.FLAG_DEBUG_LOG_RESOLUTION,
            Intent.FLAG_FROM_BACKGROUND,
            Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT,
            Intent.FLAG_ACTIVITY_CLEAR_TASK,
            Intent.FLAG_ACTIVITY_CLEAR_TOP,
            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS,
            Intent.FLAG_ACTIVITY_FORWARD_RESULT,
            Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY,
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK,
            Intent.FLAG_ACTIVITY_NO_ANIMATION,
            Intent.FLAG_ACTIVITY_NO_USER_ACTION,
            Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP,
            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED,
            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT,
            Intent.FLAG_ACTIVITY_TASK_ON_HOME,
            Intent.FLAG_RECEIVER_REGISTERED_ONLY
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface FlagInt {
    }

    /**
     * Set special flags controlling how this intent is handled.  Most values
     * here depend on the type of component being executed by the Intent,
     * specifically the FLAG_ACTIVITY_* flags are all for use with
     * {@link Context#startActivity Context.startActivity()} and the
     * FLAG_RECEIVER_* flags are all for use with
     * {@link Context#sendBroadcast(Intent) Context.sendBroadcast()}.
     */
    public NavigationRequest withFlags(@FlagInt int flag) {
        this.flags = flag;
        return this;
    }

    public int getFlags() {
        return flags;
    }

    // #############################  Follow api copy from #{Bundle}  ##############################

    /**
     * Inserts a String value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a String, or null
     * @return current
     */
    public NavigationRequest withString(@Nullable String key, @Nullable String value) {
        bundle.putString(key, value);
        return this;
    }

    /**
     * Inserts a Boolean value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a boolean
     * @return current
     */
    public NavigationRequest withBoolean(@Nullable String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    /**
     * Inserts a short value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a short
     * @return current
     */
    public NavigationRequest withShort(@Nullable String key, short value) {
        bundle.putShort(key, value);
        return this;
    }

    /**
     * Inserts an int value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value an int
     * @return current
     */
    public NavigationRequest withInt(@Nullable String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    /**
     * Inserts a long value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a long
     * @return current
     */
    public NavigationRequest withLong(@Nullable String key, long value) {
        bundle.putLong(key, value);
        return this;
    }

    /**
     * Inserts a double value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a double
     * @return current
     */
    public NavigationRequest withDouble(@Nullable String key, double value) {
        bundle.putDouble(key, value);
        return this;
    }

    /**
     * Inserts a byte value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a byte
     * @return current
     */
    public NavigationRequest withByte(@Nullable String key, byte value) {
        bundle.putByte(key, value);
        return this;
    }

    /**
     * Inserts a char value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a char
     * @return current
     */
    public NavigationRequest withChar(@Nullable String key, char value) {
        bundle.putChar(key, value);
        return this;
    }

    /**
     * Inserts a float value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a float
     * @return current
     */
    public NavigationRequest withFloat(@Nullable String key, float value) {
        bundle.putFloat(key, value);
        return this;
    }

    /**
     * Inserts a CharSequence value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence, or null
     * @return current
     */
    public NavigationRequest withCharSequence(@Nullable String key, @Nullable CharSequence value) {
        bundle.putCharSequence(key, value);
        return this;
    }

    /**
     * Inserts a Parcelable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Parcelable object, or null
     * @return current
     */
    public NavigationRequest withParcelable(@Nullable String key, @Nullable Parcelable value) {
        bundle.putParcelable(key, value);
        return this;
    }

    /**
     * Inserts an array of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an array of Parcelable objects, or null
     * @return current
     */
    public NavigationRequest withParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        bundle.putParcelableArray(key, value);
        return this;
    }

    /**
     * Inserts a List of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList of Parcelable objects, or null
     * @return current
     */
    public NavigationRequest withParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
        bundle.putParcelableArrayList(key, value);
        return this;
    }

    /**
     * Inserts a SparceArray of Parcelable values into the mapping of this
     * Bundle, replacing any existing value for the given key.  Either key
     * or value may be null.
     *
     * @param key   a String, or null
     * @param value a SparseArray of Parcelable objects, or null
     * @return current
     */
    public NavigationRequest withSparseParcelableArray(@Nullable String key, @Nullable SparseArray<? extends Parcelable> value) {
        bundle.putSparseParcelableArray(key, value);
        return this;
    }

    /**
     * Inserts an ArrayList value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList object, or null
     * @return current
     */
    public NavigationRequest withIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        bundle.putIntegerArrayList(key, value);
        return this;
    }

    /**
     * Inserts an ArrayList value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList object, or null
     * @return current
     */
    public NavigationRequest withStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        bundle.putStringArrayList(key, value);
        return this;
    }

    /**
     * Inserts an ArrayList value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList object, or null
     * @return current
     */
    public NavigationRequest withCharSequenceArrayList(@Nullable String key, @Nullable ArrayList<CharSequence> value) {
        bundle.putCharSequenceArrayList(key, value);
        return this;
    }

    /**
     * Inserts a Serializable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Serializable object, or null
     * @return current
     */
    public NavigationRequest withSerializable(@Nullable String key, @Nullable Serializable value) {
        bundle.putSerializable(key, value);
        return this;
    }

    /**
     * Inserts a byte array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a byte array object, or null
     * @return current
     */
    public NavigationRequest withByteArray(@Nullable String key, @Nullable byte[] value) {
        bundle.putByteArray(key, value);
        return this;
    }

    /**
     * Inserts a short array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a short array object, or null
     * @return current
     */
    public NavigationRequest withShortArray(@Nullable String key, @Nullable short[] value) {
        bundle.putShortArray(key, value);
        return this;
    }

    /**
     * Inserts a char array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a char array object, or null
     * @return current
     */
    public NavigationRequest withCharArray(@Nullable String key, @Nullable char[] value) {
        bundle.putCharArray(key, value);
        return this;
    }

    /**
     * Inserts a float array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a float array object, or null
     * @return current
     */
    public NavigationRequest withFloatArray(@Nullable String key, @Nullable float[] value) {
        bundle.putFloatArray(key, value);
        return this;
    }

    /**
     * Inserts a CharSequence array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence array object, or null
     * @return current
     */
    public NavigationRequest withCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
        bundle.putCharSequenceArray(key, value);
        return this;
    }

    /**
     * Inserts a Bundle value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Bundle object, or null
     * @return current
     */
    public NavigationRequest withBundle(@Nullable String key, @Nullable Bundle value) {
        bundle.putBundle(key, value);
        return this;
    }

}
