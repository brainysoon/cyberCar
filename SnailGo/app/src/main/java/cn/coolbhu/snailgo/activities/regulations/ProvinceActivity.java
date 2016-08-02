package cn.coolbhu.snailgo.activities.regulations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.ProvinceInfoJson;

import java.util.ArrayList;
import java.util.List;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.adapters.ListAdapter;
import cn.coolbhu.snailgo.beans.ListModel;

public class ProvinceActivity extends AppCompatActivity {
    private ListView lv_list;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.csy_titlebar);

        initToolbar();

        lv_list = (ListView) findViewById(R.id.lv_1ist);

        mAdapter = new ListAdapter(this, getData2());
        lv_list.setAdapter(mAdapter);

        lv_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                TextView txt_name = (TextView) view.findViewById(R.id.txt_name);

                Intent intent = new Intent();
                intent.putExtra("province_name", txt_name.getText());
                intent.putExtra("province_id", txt_name.getTag().toString());

                intent.setClass(ProvinceActivity.this, CityListActivity.class);
                startActivityForResult(intent, 20);
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


    /**
     * title:获取省份信息
     *
     * @return
     */
    private List<ListModel> getData2() {

        List<ListModel> list = new ArrayList<ListModel>();
        List<ProvinceInfoJson> provinceList = WeizhangClient.getAllProvince();

        //开通数量提示
        TextView txtListTip = (TextView) findViewById(R.id.list_tip);
        txtListTip.setText("全国已开通" + provinceList.size() + "个省份, 其它省将陆续开放");

        for (ProvinceInfoJson provinceInfoJson : provinceList) {
            String provinceName = provinceInfoJson.getProvinceName();
            int provinceId = provinceInfoJson.getProvinceId();

            ListModel model = new ListModel();
            model.setTextName(provinceName);
            model.setNameId(provinceId);
            list.add(model);
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        Bundle bundle = data.getExtras();
        // 获取城市name
        String cityName = bundle.getString("city_name");
        String cityId = bundle.getString("city_id");

        Intent intent = new Intent();
        intent.putExtra("city_name", cityName);
        intent.putExtra("city_id", cityId);
        setResult(1, intent);
        finish();
    }
}
