package com.fat246.cybercar.activities.regulations;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.fat246.cybercar.R;

public class RegulationActivity extends AppCompatActivity {

    //View
//    private CardView mCustomRegulation;
    private ListView mCarInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regulation);

        findView();

        setListener();
    }

    //findView
    private void findView() {

//        mCustomRegulation = (CardView) findViewById(R.id.activity_regulation_cardview_custom_regulation);
//        mCarInfos = (ListView) findViewById(R.id.activity_regulation_listview_carinfos);


    }

    //设置监听事件
    private void setListener() {
//
//        mCustomRegulation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent mIntent = new Intent(RegulationActivity.this, CustomRegulationActivity.class);
//
//                startActivity(mIntent);
//
//                RegulationActivity.this.finish();
//            }
//        });
    }
}
