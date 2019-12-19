package com.sharry.srouter.support;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.SparseArray;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
public final class Request {

    static final int NON_REQUEST_CODE = -1;
    static final int NON_FLAGS = -1;

    /**
     * U can get an instance of Request from this method.
     */
    static Request create(String authority, String path) {
        return new Request(
                TextUtils.isEmpty(authority) ? "" : authority,
                TextUtils.isEmpty(path) ? "" : path
        );
    }

    /**
     * U can instant Request by parse URL
     */
    static Request parseUri(String uriStr) {
        // Fetch query items.
        Request request;
        try {
            Uri uri = Uri.parse(TextUtils.isEmpty(uriStr) ? "" : uriStr);
            // Fetch authority.
            String authority = Preconditions.checkNotEmpty(uri.getAuthority());
            // Fetch path and remove '/' at start.
            String path = Preconditions.checkNotEmpty(uri.getPath()).substring(1);
            // Fetch query items.
            Bundle datum = new Bundle();
            Bundle urlDatum = new Bundle();
            for (String queryParameterName : uri.getQueryParameterNames()) {
                String key = queryParameterName;
                String value = uri.getQueryParameter(key);
//                Log.e("TAG", "key = " + key + ", value = " + value);
                urlDatum.putString(key, value);
            }
            datum.putBundle(Constants.INTENT_EXTRA_URL_DATUM, urlDatum);
            request = create(authority, path);
            request.setDatum(datum);
        } catch (Throwable throwable) {
            Logger.e(throwable.getMessage(), throwable);
            // Parse url failed.
            request = Request.create(null, null);
        }
        return request;
    }

    /**
     * U can instant Request by parse URL
     */
    static Request parseForwardIntent(Intent forwardIntent) {
        Bundle targetInfo;
        if (forwardIntent == null || (targetInfo = forwardIntent.getBundleExtra(
                ForwardIntentBuilder.EXTRA_TARGET_INFO)) == null) {
            return Request.create(null, null);
        }
        // Build request
        Request request;
        if (targetInfo.containsKey(ForwardIntentBuilder.BUNDLE_EXTRA_URI)) {
            request = Request.parseUri(targetInfo.getString(ForwardIntentBuilder.BUNDLE_EXTRA_URI));
            // remove key.
            targetInfo.remove(ForwardIntentBuilder.BUNDLE_EXTRA_URI);
        } else if (targetInfo.containsKey(targetInfo.getString(ForwardIntentBuilder.BUNDLE_EXTRA_AUTHORITY)) &&
                targetInfo.containsKey(targetInfo.getString(ForwardIntentBuilder.BUNDLE_EXTRA_PATH))) {
            request = Request.create(
                    targetInfo.getString(ForwardIntentBuilder.BUNDLE_EXTRA_AUTHORITY),
                    targetInfo.getString(ForwardIntentBuilder.BUNDLE_EXTRA_PATH)
            );
            // remove key.
            targetInfo.remove(ForwardIntentBuilder.BUNDLE_EXTRA_AUTHORITY);
            targetInfo.remove(ForwardIntentBuilder.BUNDLE_EXTRA_PATH);
        } else {
            request = Request.create(null, null);
        }
        // add other.
        request.addDatum(targetInfo);
        return request;
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
     * The interceptorURIs will be intercept before {@link DataSource#TABLE_ROUTES_INTERCEPTORS}
     */
    private final List<IInterceptor> interceptors = new ArrayList<>();

    /**
     * The interceptorURIs will be intercept before {@link DataSource#TABLE_ROUTES_INTERCEPTORS}
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
    private int activityFlags = NON_FLAGS;

    /**
     * The Flag for the pending Intent.
     */
    private int pendingIntentFlags = NON_FLAGS;

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

    /**
     * The meta holder target address.
     */
    private RouteMeta routeMeta;

    private Request(String authority, String path) {
        this.authority = authority;
        this.path = path;
        datum = new Bundle();
    }


    // ///////////////////////////////////////// Do request ////////////////////////////////////////////////////

    /**
     * Start navigation.
     */
    public void navigation() {
        navigation(null, null);
    }

    public void navigation(@Nullable Context context) {
        navigation(context, null);
    }

    public void navigation(@Nullable LambdaCallback successCallback) {
        navigation(null, successCallback);
    }

    public void navigation(@Nullable Context context, @Nullable LambdaCallback successCallback) {
        SRouter.navigation(context, this, successCallback);
    }

    public ICall newCall() {
        return newCall(null);
    }

    public ICall newCall(@Nullable Context context) {
        return SRouter.newCall(context, this);
    }

    // ///////////////////////////////////// Get method. ///////////////////////////////////////////////////

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

    public int getActivityFlags() {
        return activityFlags;
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

    public int getPendingIntentFlags() {
        return pendingIntentFlags;
    }

    // ///////////////////////////////////// Set method. ///////////////////////////////////////////////////

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
    public Request setDatum(@Nullable Bundle newDatum) {
        if (newDatum != null) {
            this.datum = newDatum;
        }
        return this;
    }

    /**
     * BE ATTENTION TO THIS METHOD WAS <P>ADD, NOT SET!</P>
     */
    public Request addDatum(@Nullable Bundle newDatum) {
        if (datum == null) {
            datum = newDatum;
        } else if (newDatum != null) {
            datum.putAll(newDatum);
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
     * Set Activity jump activityFlags
     */
    public Request setActivityFlags(int activityFlags) {
        this.activityFlags = activityFlags;
        return this;
    }

    /**
     * Add flag.
     */
    public Request addActivityFlag(@ActivityFlags int flag) {
        if (this.activityFlags != NON_FLAGS) {
            this.activityFlags |= flag;
        } else {
            this.activityFlags = flag;
        }
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

    // #############################  Follow api copy from #{Bundle}  ##############################

    // ######################### annotation @ActivityFlags copy from #{Intent}  ##########################
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
    @interface ActivityFlags {
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

    @Override
    public String toString() {
        return "Request{" +
                "authority='" + authority + '\'' +
                ", path='" + path + '\'' +
                ", delay=" + delay +
                ", activityFlags=" + activityFlags +
                ", requestCode=" + requestCode +
                ", isGreenChannel=" + isGreenChannel +
                '}';
    }


    /**
     * Set route target address meta data.
     *
     * @param routeMeta target meta data.
     */
    void setRouteMeta(@NonNull RouteMeta routeMeta) {
        this.routeMeta = routeMeta;
    }

    RouteMeta getRouteMeta() {
        return routeMeta;
    }

}
