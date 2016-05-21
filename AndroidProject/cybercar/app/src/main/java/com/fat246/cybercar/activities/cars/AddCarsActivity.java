package com.fat246.cybercar.activities.cars;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fat246.cybercar.R;

public class AddCarsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cars);

        initToolbar();
    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_my_cars_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AddCarsActivity.this.finish();
                }
            });
        }
    }

}
