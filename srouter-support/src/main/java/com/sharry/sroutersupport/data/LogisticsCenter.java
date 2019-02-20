package com.sharry.sroutersupport.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.sharry.sroutersupport.BuildConfig;
import com.sharry.sroutersupport.exceptions.NoRouteFoundException;
import com.sharry.sroutersupport.frame.Logger;
import com.sharry.sroutersupport.template.IRoute;
import com.sharry.sroutersupport.template.IRouteInterceptor;
import com.sharry.sroutersupport.utils.ClassUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import static com.sharry.sroutersupport.utils.Constants.DOT;
import static com.sharry.sroutersupport.utils.Constants.NAME_OF_INTERCEPTOR;
import static com.sharry.sroutersupport.utils.Constants.NAME_OF_ROUTERS;
import static com.sharry.sroutersupport.utils.Constants.PACKAGE_OF_GENERATE_FILE;

/**
 * Perform Route logistics, it contains all of the map.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public class LogisticsCenter {

    private static Context sContext;
    private static ThreadPoolExecutor sTpe;
    private static final String SROUTER_SP_CACHE_KEY = "srouter_sp_cache_key";

    /**
     * Warehouse data set will be load at here.
     */
    public static synchronized void init(Context context, ThreadPoolExecutor executor) {
        sContext = context;
        sTpe = executor;
        try {
            Set<String> routerMap;
            if (BuildConfig.DEBUG) {
                // 若为调试, 则每次都扫描包级目录, 获取路由映射信息类
                routerMap = ClassUtils.getFileNameByPackageName(sContext, PACKAGE_OF_GENERATE_FILE);
            } else {
                // 若为正式环境, 则优先从缓存读
                SharedPreferences sp = context.getSharedPreferences(SROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE);
                Collection<String> caches = sp.getStringSet(SROUTER_SP_CACHE_KEY, null);
                if (null == caches) {
                    routerMap = ClassUtils.getFileNameByPackageName(sContext, PACKAGE_OF_GENERATE_FILE);
                    // 写入缓存
                    sp.edit().putStringSet(SROUTER_SP_CACHE_KEY, routerMap).apply();
                } else {
                    routerMap = new HashSet<>(caches);
                }
            }
            // 遍历集合, 将缓存的集合加载进相应的集合中
            for (String className : routerMap) {
                if (className.startsWith(PACKAGE_OF_GENERATE_FILE + DOT + NAME_OF_ROUTERS)) {
                    // 加载根元素(com.sharry.android.srouter.SRouter$$Routes$$XXX)
                    IRoute route = (IRoute) (Class.forName(className).getConstructor().newInstance());
                    route.loadInto(Warehouse.ROUTES);
                } else if (className.startsWith(PACKAGE_OF_GENERATE_FILE + DOT + NAME_OF_INTERCEPTOR)) {
                    // 加载拦截器标签元素(com.sharry.android.srouter.SRouter$$Interceptors$$XXX)
                    IRouteInterceptor routeInterceptor = (IRouteInterceptor) (Class.forName(className).getConstructor().newInstance());
                    routeInterceptor.loadInto(Warehouse.INTERCEPTORS);
                }
                Logger.e(className);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetch data from warehouse and then inject to request.
     */
    public static void completion(Request request) throws NoRouteFoundException {
        String authority = request.getPath();
        RouteMeta routeMeta = Warehouse.ROUTES.get(authority);
        if (null == routeMeta) {
            throw new NoRouteFoundException("SRouter cannot found: " + authority);
        }
        // Load data to request before navigation
        request.setType(routeMeta.getType());
        request.setRouteClass(routeMeta.getRouteClass());
        request.setThreadMode(routeMeta.getThreadMode());
        request.setInterceptors(routeMeta.getInterceptors());
        // If Type is PROVIDER, the request cannot be intercepted.
        request.setIsGreenChannel(routeMeta.getType() == RouteMeta.Type.PROVIDER);
    }

}
