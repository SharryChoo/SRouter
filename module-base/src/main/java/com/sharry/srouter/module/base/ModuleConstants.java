package com.sharry.srouter.module.base;

/**
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2/22/2019 3:21 PM
 */
public final class ModuleConstants {

    public static final class Login {
        public static final String NAME = "login";
        public static final String LOGIN_ACTIVITY = "login_activity";
        // interceptors
        public static final String LOGIN_INTERCEPTOR = NAME + "/login_interceptor";
        public static boolean isLogin = false;
    }

    public static final class Found {

        public static final String NAME = "found";

        public static final String FOUND_FRAGMENT = "found_fragment";
    }

    public static final class Personal {

        public static final String NAME = "personal";

        public static final String PERSONAL_ACTIVITY = "personal_activity";

        // interceptors
        public static final String PERMISSION_INTERCEPTOR = NAME + "/permission_interceptor";
    }


}
