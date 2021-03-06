package com.bananaplan.workflowandroid.utility;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TabHost;

import com.bananaplan.workflowandroid.R;

import java.util.HashMap;

/**
 * Created by Ben on 2015/8/14.
 */
public class TabManager implements TabHost.OnTabChangeListener {
    private final AppCompatActivity mActivity;
    private final TabHost mTabHost;
    private final int mContainerId;
    private final HashMap<String, TabInfo> mTabs = new HashMap<>();
    TabInfo mLastTab;

    static final class TabInfo {
        private final String tag;
        private final Class<?> cls;
        private final Bundle args;
        private Fragment fragment;

        TabInfo(String _tag, Class<?> _class, Bundle _args) {
            tag = _tag;
            cls = _class;
            args = _args;
        }
    }

    public static class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    public TabManager(AppCompatActivity activity, TabHost tabHost, int containerId) {
        mActivity = activity;
        mTabHost = tabHost;
        mContainerId = containerId;
        mTabHost.setOnTabChangedListener(this);
    }

    public void addTab(TabHost.TabSpec tabSpec, Class<?> cls, Bundle args) {
        tabSpec.setContent(new DummyTabFactory(mActivity));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, cls, args);

        Fragment frag = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
        info.fragment = frag;
        if (info.fragment != null && !info.fragment.isDetached()) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
            ft.detach(info.fragment);
            ft.commit();
        }

        mTabs.put(tag, info);
        mTabHost.addTab(tabSpec);
    }

    @Override
    public void onTabChanged(String tabId) {
        TabInfo newTab = mTabs.get(tabId);

        if (mLastTab != newTab) {
            if (mActivity.findViewById(R.id.scroll) != null) {
                ((OverviewScrollView) mActivity.findViewById(R.id.scroll)).setScrollEnable(false);
            }
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
            if (mLastTab != null) {
                //ft.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }

            if (newTab != null) {
                newTab.fragment = Fragment.instantiate(mActivity,
                        newTab.cls.getName(), newTab.args);
                ft.add(mContainerId, newTab.fragment, newTab.tag);
                if (newTab.fragment == null) {
                    ft.detach(mLastTab.fragment);
                } else {
                    mActivity.getFragmentManager().popBackStack();
                    ft.replace(mContainerId, newTab.fragment);
                    ft.attach(newTab.fragment);
                }
            }

            mLastTab = newTab;
            ft.commit();
            mActivity.getFragmentManager().executePendingTransactions();
        }
    }

    public void selectItem(Object item) {
        if (mLastTab == null) return;
        if (mLastTab.fragment instanceof OvTabFragmentBase) {
            ((OvTabFragmentBase) mLastTab.fragment).selectItem(item);
        }
    }
}
