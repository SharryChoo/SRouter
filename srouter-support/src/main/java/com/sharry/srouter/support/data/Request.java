package com.sharry.srouter.support.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sharry.srouter.support.call.ICall;
import com.sharry.srouter.support.facade.Callback;
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

    public static final int NON_REQUEST_CODE = -1;
    public static final int NON_FLAGS = -1;

    /**
     * Navigation authority
     */
    private final String authority;

    /**
     * Navigation path.
     */
    private final String path;

    /**
     * The interceptorURIs will be intercept before {@link Warehouse#TABLE_ROUTES_INTERCEPTORS}
     */
    private final List<IInterceptor> interceptors = new ArrayList<>();

    /**
     * The interceptorURIs will be intercept before {@link Warehouse#TABLE_ROUTES_INTERCEPTORS}
     */
    private final List<String> interceptorURIs = new ArrayList<>();

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
     * The Flag for the route navigation.
     */
    private int flags = NON_FLAGS;

    /**
     * The requestCode for the requestCode.
     */
    private int requestCode = NON_REQUEST_CODE;

    /**
     * The jump activity configs for the request.
     */
    private Bundle activityOptions;

    /**
     * if true, it will ignore interceptor.
     */
    private boolean isGreenChannel;

    private Request(String authority, String path) {
        this.authority = authority;
        this.path = path;
        datum = new Bundle();
    }

    /**
     * U can get an instance of Request from this method.
     */
    public static Request create(@NonNull String authority, @NonNull String path) {
        return new Request(Preconditions.checkNotEmpty(authority), Preconditions.checkNotEmpty(path));
    }

    /**
     * U can instant Request by parse URL
     */
    public static Request parseFrom(@NonNull String url) {
        Uri uri = Uri.parse(Preconditions.checkNotEmpty(url));
        // Fetch authority.
        String authority = Preconditions.checkNotEmpty(uri.getAuthority());
        // Fetch path and remove '/' at start.
        String path = Preconditions.checkNotEmpty(uri.getPath()).substring(1);
        // Fetch query items.
        Bundle datum = new Bundle();
        for (String queryParameterName : uri.getQueryParameterNames()) {
            datum.putString(queryParameterName, uri.getQueryParameter(queryParameterName));
        }
        Request request = create(authority, path);
        request.setDatum(datum);
        return request;
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
     * BE ATTENTION TO THIS METHOD WAS <P>SET, NOT ADD!</P>
     */
    public Request setDatum(@Nullable Bundle datum) {
        if (datum != null) {
            this.datum = datum;
        }
        return this;
    }

    /**
     * Set delay time when navigation intercept.
     * <p>
     * Unit is {{@link TimeUnit#MILLISECONDS}}
     */
    public Request setDelay(long delay) {
        this.delay = delay;
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
     * Set Activity jump flags
     */
    public Request setFlags(int flags) {
        this.flags = flags;
        return this;
    }

    /**
     * Set Activity jump request code.
     */
    public Request setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    /**
     * Set Activity jump options.
     */
    public Request setActivityOptions(@Nullable Bundle activityOptions) {
        this.activityOptions = activityOptions;
        return this;
    }

    public String getAuthority() {
        return authority;
    }

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

    public int getFlags() {
        return flags;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public Bundle getActivityOptions() {
        return activityOptions;
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
        navigation(null, null);
    }

    public void navigation(@Nullable Context context) {
        navigation(context, null);
    }

    public void navigation(@Nullable Callback callback) {
        navigation(null, callback);
    }

    public void navigation(@Nullable Context context, @Nullable Callback callback) {
        SRouter.navigation(context, this, callback);
    }

    /**
     * Get an instance of navigation post
     */
    public ICall newCall() {
        return newCall(null);
    }

    public ICall newCall(@Nullable Context context) {
        return SRouter.newCall(context, this);
    }

    /**
     * Inserts a String key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
     *
     * @param key   a String, or null
     * @param value a String, or null
     * @return current
     */
    public Request withString(@Nullable String key, @Nullable String value) {
        datum.putString(key, value);
        return this;
    }

    // #############################  Follow api copy from #{Bundle}  ##############################

    /**
     * Inserts a Boolean key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
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
     * Inserts a short key into the mapping of this Bundle, replacing
     * any existing key for the given key.
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
     * Inserts an int key into the mapping of this Bundle, replacing
     * any existing key for the given key.
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
     * Inserts a long key into the mapping of this Bundle, replacing
     * any existing key for the given key.
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
     * Inserts a double key into the mapping of this Bundle, replacing
     * any existing key for the given key.
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
     * Inserts a byte key into the mapping of this Bundle, replacing
     * any existing key for the given key.
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
     * Inserts a char key into the mapping of this Bundle, replacing
     * any existing key for the given key.
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
     * Inserts a float key into the mapping of this Bundle, replacing
     * any existing key for the given key.
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
     * Inserts a CharSequence key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
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
     * Inserts a Parcelable key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
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
     * replacing any existing key for the given key.  Either key or key may
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
     * replacing any existing key for the given key.  Either key or key may
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
     * Bundle, replacing any existing key for the given key.  Either key
     * or key may be null.
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
     * Inserts an ArrayList key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
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
     * Inserts an ArrayList key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
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
     * Inserts an ArrayList key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
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
     * Inserts a Serializable key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
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
     * Inserts a byte array key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
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
     * Inserts a short array key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
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
     * Inserts a char array key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
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
     * Inserts a float array key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
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
     * Inserts a CharSequence array key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
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
     * Inserts a Bundle key into the mapping of this Bundle, replacing
     * any existing key for the given key.  Either key or key may be null.
     *
     * @param key   a String, or null
     * @param value a Bundle object, or null
     * @return current
     */
    public Request withBundle(@Nullable String key, @Nullable Bundle value) {
        datum.putBundle(key, value);
        return this;
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

}
