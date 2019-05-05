package com.sharry.srouter.support.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sharry.srouter.support.facade.SRouter;
import com.sharry.srouter.support.interceptors.IInterceptor;
import com.sharry.srouter.support.utils.Logger;
import com.sharry.srouter.support.utils.Preconditions;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The request associated with a navigation.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public class Request extends RouteMeta {

    /**
     * U can get an instance of Request from this method.
     */
    public static Request create(@NonNull String authority, @NonNull String path) {
        return new Request(authority, path);
    }

    /**
     * Navigation authority
     */
    private final String authority;

    /**
     * Navigation path.
     */
    private final String path;

    /**
     * The datum for the route navigation.
     */
    private Bundle datum;

    /**
     * Navigation delay
     * <p>
     * Unit is {@link TimeUnit#MILLISECONDS}
     */
    private long delay = 0;

    /**
     * The Activity request params for the request.
     */
    private ActivityConfigs activityConfigs;

    /**
     * if true, it will ignore interceptor.
     */
    private boolean isGreenChannel;

    /**
     * The interceptorURIs will be process before {@link Warehouse#TABLE_ROUTES_INTERCEPTORS}
     */
    private final List<IInterceptor> interceptors = new ArrayList<>();

    /**
     * The interceptorURIs will be process before {@link Warehouse#TABLE_ROUTES_INTERCEPTORS}
     */
    private final List<String> interceptorURIs = new ArrayList<>();

    private Request(String authority, String path) {
        this.authority = authority;
        this.path = path;
        datum = new Bundle();
    }

    /**
     * BE ATTENTION TO THIS METHOD WAS <P>SET, NOT ADD!</P>
     */
    public Request setDatum(@Nullable Bundle datum) {
        if (datum != null) {
            this.datum = datum;
        }
        return this;
    }

    /**
     * Set delay time when navigation process.
     * <p>
     * Unit is {{@link TimeUnit#MILLISECONDS}}
     */
    public Request setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    public Request setActivityConfigs(@NonNull ActivityConfigs activityConfigs) {
        Preconditions.checkNotNull(activityConfigs);
        this.activityConfigs = activityConfigs;
        return this;
    }

    /**
     * Set green channel associated with this Request.
     *
     * @param isGreenChannel if true will ignore Route INTERCEPTORS.
     */
    public Request setGreenChannel(boolean isGreenChannel) {
        this.isGreenChannel = isGreenChannel;
        return this;
    }

    /**
     * Add interceptors for the request.
     * <p>
     * it will clear before added.
     */
    public Request addInterceptors(@NonNull IInterceptor... interceptors) {
        Preconditions.checkNotNull(interceptors);
        this.interceptors.clear();
        for (IInterceptor interceptor : interceptors) {
            addInterceptor(interceptor);
        }
        return this;
    }

    /**
     * Add interceptor for the request.
     */
    public Request addInterceptor(@NonNull IInterceptor interceptor) {
        Preconditions.checkNotNull(interceptor);
        if (interceptors.contains(interceptor)) {
            Logger.i("The interceptor already added: " + interceptor.toString());
            return this;
        }
        interceptors.add(interceptor);
        return this;
    }

    /**
     * Add interceptor URIs for the request.
     * <p>
     * it will clear before added.
     */
    public Request addInterceptorURIs(@NonNull String... interceptorURIs) {
        Preconditions.checkNotNull(interceptorURIs);
        this.interceptorURIs.clear();
        for (String interceptorURI : interceptorURIs) {
            addInterceptorURI(interceptorURI);
        }
        return this;
    }

    /**
     * Add interceptor URI for the request.
     */
    public Request addInterceptorURI(@NonNull String interceptorURI) {
        Preconditions.checkNotEmpty(interceptorURI);
        if (interceptorURIs.contains(interceptorURI)) {
            Logger.i("The interceptorURI already added: " + interceptorURI);
            return this;
        }
        interceptorURIs.add(interceptorURI);
        return this;
    }

    /**
     * Get request authority.
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * Get Request path.
     */
    public String getPath() {
        return path;
    }

    public Bundle getDatum() {
        return datum;
    }

    public long getDelay() {
        return delay;
    }

    public boolean isGreenChannel() {
        return isGreenChannel;
    }

    public ActivityConfigs getActivityConfigs() {
        return activityConfigs;
    }

    public List<IInterceptor> getInterceptors() {
        return interceptors;
    }

    public List<String> getInterceptorURIs() {
        return interceptorURIs;
    }

    /**
     * Start navigation.
     */
    public void navigation() {
        this.navigation(null);
    }

    public void navigation(Context context) {
        SRouter.getInstance().navigation(context, this);
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

    // #############################  Follow api copy from #{Bundle}  ##############################

    /**
     * Inserts a String value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a String, or null
     * @return current
     */
    public Request withString(@Nullable String key, @Nullable String value) {
        datum.putString(key, value);
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
    public Request withBoolean(@Nullable String key, boolean value) {
        datum.putBoolean(key, value);
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
    public Request withShort(@Nullable String key, short value) {
        datum.putShort(key, value);
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
    public Request withInt(@Nullable String key, int value) {
        datum.putInt(key, value);
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
    public Request withLong(@Nullable String key, long value) {
        datum.putLong(key, value);
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
    public Request withDouble(@Nullable String key, double value) {
        datum.putDouble(key, value);
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
    public Request withByte(@Nullable String key, byte value) {
        datum.putByte(key, value);
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
    public Request withChar(@Nullable String key, char value) {
        datum.putChar(key, value);
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
    public Request withFloat(@Nullable String key, float value) {
        datum.putFloat(key, value);
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
    public Request withCharSequence(@Nullable String key, @Nullable CharSequence value) {
        datum.putCharSequence(key, value);
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
    public Request withParcelable(@Nullable String key, @Nullable Parcelable value) {
        datum.putParcelable(key, value);
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
    public Request withParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        datum.putParcelableArray(key, value);
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
    public Request withParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
        datum.putParcelableArrayList(key, value);
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
    public Request withSparseParcelableArray(@Nullable String key, @Nullable SparseArray<? extends Parcelable> value) {
        datum.putSparseParcelableArray(key, value);
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
    public Request withIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        datum.putIntegerArrayList(key, value);
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
    public Request withStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        datum.putStringArrayList(key, value);
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
    public Request withCharSequenceArrayList(@Nullable String key, @Nullable ArrayList<CharSequence> value) {
        datum.putCharSequenceArrayList(key, value);
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
    public Request withSerializable(@Nullable String key, @Nullable Serializable value) {
        datum.putSerializable(key, value);
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
    public Request withByteArray(@Nullable String key, @Nullable byte[] value) {
        datum.putByteArray(key, value);
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
    public Request withShortArray(@Nullable String key, @Nullable short[] value) {
        datum.putShortArray(key, value);
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
    public Request withCharArray(@Nullable String key, @Nullable char[] value) {
        datum.putCharArray(key, value);
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
    public Request withFloatArray(@Nullable String key, @Nullable float[] value) {
        datum.putFloatArray(key, value);
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
    public Request withCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
        datum.putCharSequenceArray(key, value);
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
    public Request withBundle(@Nullable String key, @Nullable Bundle value) {
        datum.putBundle(key, value);
        return this;
    }

}
