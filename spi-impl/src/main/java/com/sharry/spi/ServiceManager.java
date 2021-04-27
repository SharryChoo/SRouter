package com.sharry.spi;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 多classLoader场景下可用性未知
 * 不保证读写的put和get的线程安全
 */
public class ServiceManager {
    /**
     * 相比HashMap，ConcurrentHashMap的读性能持平，写性能较差，可以改进下
     */
    private static final ConcurrentHashMap<Class<?>, ServiceSpec<?>> servicesClassMap = new ConcurrentHashMap<>();

    public static boolean enableStrictMode = false;

    static {
        autoRegister();
    }

    public static void setEnableStrictMode(boolean enable) {
        enableStrictMode = enable;
    }

    public static <T> T getService(final Class<T> clazz) {
        if (null == clazz) {
            throw new RuntimeException("ServiceManager.getService: Null");
        }
        ServiceSpec<T> serviceSpec = (ServiceSpec<T>) servicesClassMap.get(clazz);
        if (null == serviceSpec) {
            if (enableStrictMode) {
                throw new RuntimeException("Do not found implClass " + clazz);
            }
            return null;
        }

        if (serviceSpec.singleton && null != serviceSpec.instance) {
            return serviceSpec.instance;
        }

        // 非单例模式，或者没有服务实例
        synchronized (clazz) {
            if (serviceSpec.singleton && null != serviceSpec.instance) {
                return serviceSpec.instance;
            }
            try {
                T res = (T) serviceSpec.implClazz.newInstance();
                if (serviceSpec.singleton) {
                    serviceSpec.instance = res;
                }
                return res;
            } catch (Throwable e) {
                e.printStackTrace();
                if (enableStrictMode) {
                    throw new RuntimeException("Do not found implClass " + clazz);
                }
                return null;
            }
        }
    }

    private static void autoRegister() {
        // AMS start
        // ......
        // add(...)
        // add(...)
        // AMS end
    }

    public static <T> void add(Class<? extends T> clazz, Class<? extends T> implClazz, boolean delay, boolean singleton) {
        ServiceSpec<?> spec = new ServiceSpec<>();
        spec.implClazz = implClazz;
        spec.singleton = singleton;
        spec.delayInstance = delay;
        spec.instance = null;
        servicesClassMap.put(clazz, spec);
    }

}
