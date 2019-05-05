package com.sharry.srouter.support.data;

import androidx.fragment.app.Fragment;

import com.sharry.srouter.support.providers.IProvider;

/**
 * The result associated with a navigation.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2/20/2019 10:41 AM
 */
public class Response {

    public static final Response EMPTY_RESPONSE = new Response();

    private Fragment fragmentV4;
    private android.app.Fragment fragment;
    private IProvider provider;

    public Fragment getFragmentV4() {
        return fragmentV4;
    }

    public void setFragmentV4(Fragment fragmentV4) {
        this.fragmentV4 = fragmentV4;
    }

    public android.app.Fragment getFragment() {
        return fragment;
    }

    public void setFragment(android.app.Fragment fragment) {
        this.fragment = fragment;
    }

    public IProvider getProvider() {
        return provider;
    }

    public void setProvider(IProvider provider) {
        this.provider = provider;
    }
}
