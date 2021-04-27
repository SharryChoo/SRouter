package com.sharry.spi;

/**
 * 服务的属性
 */
public class ServiceSpec<T> {
    /**
     * 实现class的
     */
    public Class<?> implClazz;

    /**
     * 服务单例化
     */
    public boolean singleton;

    /**
     * 服务延时加载，既在服务获取时才初始化
     */
    public boolean delayInstance;

    /**
     * 已经缓存下来的服务，如果处于单例模式下，始终不能缓存
     */
    public T instance = null;

    public ServiceSpec() {

    }

}
