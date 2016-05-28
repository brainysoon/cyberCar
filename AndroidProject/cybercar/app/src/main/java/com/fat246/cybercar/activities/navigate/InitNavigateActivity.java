package com.fat246.cybercar.activities.navigate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fat246.cybercar.R;
import com.fat246.cybercar.fragments.InitNavigateFragment;
import com.fat246.cybercar.fragments.NavigatePreferFragment;

public class InitNavigateActivity extends AppCompatActivity
        implements InitNavigateFragment.canToPrefer, NavigatePreferFragment.canToInit {

    //holder
    private static Fragment[] mFragmentHolder = new Fragment[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_navigate);

        initToolbar();

        mFragmentHolder[0] = InitNavigateFragment.newInstance();
        mFragmentHolder[1] = NavigatePreferFragment.newInstance();

        toInit();
    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_initnavigate_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            toolbar.setTitle("设置导航");

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    InitNavigateActivity.this.finish();
                }
            });
        }
    }

    @Override
    public void toPrefer() {

        if (mFragmentHolder[1] != null) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_initnavigate_container, mFragmentHolder[1]).commit();
        }
    }

    @Override
    public void toInit() {

        if (mFragmentHolder[0] != null) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_initnavigate_container, mFragmentHolder[0]).commit();
        }
    }
}
