package com.bananaplan.workflowandroid.main;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.jobassign.JobAssignFragment;


/**
 * Main component to control the UI
 *
 * @author Danny Lin
 * @since 2015.05.28
 */
public class UIController {

    private ActionBarActivity mMainActivity;
    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private FragmentManager mFragmentManager;
    private DrawerMenuFragment mDrawerMenuFragment;
    private JobAssignFragment mJobAssignFragment;


    private static class FragmentTag {
        public static final String TAG_DRAWER_MENU_FRAGMENT = "tag_drawer_menu_fragment";
        public static final String TAG_JOB_ASSIGN_FRAGMENT = "tag_job_assign_fragment";
    }

    public UIController(ActionBarActivity activity) {
        mMainActivity = activity;
    }

    public void onActivityCreate(Bundle savedInstanceState) {
        initialize();
    }

    private void initialize() {
        mFragmentManager = mMainActivity.getFragmentManager();
        findViews();
        initActionbar();
        initDrawer();
        initFragments();
    }

    private void findViews() {
        mToolbar = (Toolbar) mMainActivity.findViewById(R.id.tool_bar);
        mDrawerLayout = (DrawerLayout) mMainActivity.findViewById(R.id.drawer_layout);
    }

    private void initActionbar() {
        mMainActivity.setSupportActionBar(mToolbar);
        mActionBar = mMainActivity.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(mMainActivity, mDrawerLayout,
                                                  mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initFragments() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        mDrawerMenuFragment = (DrawerMenuFragment) mFragmentManager.findFragmentByTag(FragmentTag.TAG_DRAWER_MENU_FRAGMENT);
        if (mDrawerMenuFragment == null) {
            mDrawerMenuFragment = new DrawerMenuFragment();
        }
        fragmentTransaction.add(R.id.drawer_menu_container, mDrawerMenuFragment);

        mJobAssignFragment = (JobAssignFragment) mFragmentManager.findFragmentByTag(FragmentTag.TAG_JOB_ASSIGN_FRAGMENT);
        if (mJobAssignFragment == null) {
            mJobAssignFragment = new JobAssignFragment();
        }
        fragmentTransaction.add(R.id.content_container, mJobAssignFragment);

        fragmentTransaction.commit();
    }

}
