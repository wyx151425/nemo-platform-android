package com.rumofuture.nemo.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.rumofuture.nemo.R;
import com.rumofuture.nemo.app.NemoActivity;
import com.rumofuture.nemo.app.manager.DataSourceManager;
import com.rumofuture.nemo.app.manager.NemoActivityManager;
import com.rumofuture.nemo.presenter.MyInfoUpdatePresenter;
import com.rumofuture.nemo.view.fragment.MyProfileEditFragment;

public class MyProfileEditActivity extends NemoActivity {

    private static final String EXTRA_PROFILE = "com.rumofuture.nemo.view.activity.MyProfileEditActivity.profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        MyProfileEditFragment fragment =
                (MyProfileEditFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (null == fragment) {
            fragment = MyProfileEditFragment.newInstance(getIntent().getStringExtra(EXTRA_PROFILE));
            NemoActivityManager.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.fragment_container);
        }

        MyInfoUpdatePresenter presenter = new MyInfoUpdatePresenter(
                fragment,
                DataSourceManager.provideUserRepository(MyProfileEditActivity.this)
        );
        fragment.setPresenter(presenter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void actionStart(Fragment fragment, String profile, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(fragment.getActivity(), MyProfileEditActivity.class);
        intent.putExtra(EXTRA_PROFILE, profile);
        fragment.startActivityForResult(intent, requestCode);
    }
}
