package cn.coolbhu.snailgo.activities.regulations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import cn.coolbhu.snailgo.R;

public class ShortProvinceGridActivity extends AppCompatActivity {

    private GridView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //获取传过来的城市
        Bundle bundle = getIntent().getExtras();
        final String short_name = bundle.getString("select_short_name");
        Log.d("select_short_name...", short_name);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_province_grid);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.csy_titlebar);

        initToolbar();

        //省份简称列表
        gv = (GridView) findViewById(R.id.gv_1);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.activity_short_province_grid_item, getDate());
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String txt = adapter.getItem(position);
                if (txt.length() > 0) {
                    Toast.makeText(ShortProvinceGridActivity.this, txt, Toast.LENGTH_LONG).show();

                    // 选择之后再打开一个 显示城市的 Activity；
                    Intent intent = new Intent();
                    intent.putExtra("short_name", txt);
                    setResult(0, intent);
                    finish();
                }
            }
        });

    }

    //initToolbar
    private void initToolbar() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(mToolbar);

        ActionBar mActionBar = getSupportActionBar();

        if (mActionBar != null) {

            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private String[] getDate() {
        return new String[]{"京", "津", "沪", "川", "鄂", "甘", "赣", "桂", "贵", "黑",
                "吉", "翼", "晋", "辽", "鲁", "蒙", "闽", "宁", "青", "琼", "陕", "苏",
                "皖", "湘", "新", "渝", "豫", "粤", "云", "藏", "浙", ""};
    }
}
