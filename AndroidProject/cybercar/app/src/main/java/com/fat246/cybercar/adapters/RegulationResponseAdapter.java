package com.fat246.cybercar.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cheshouye.api.client.json.WeizhangResponseHistoryJson;
import com.fat246.cybercar.R;

import java.util.List;

public class RegulationResponseAdapter extends BaseAdapter {

    private List<WeizhangResponseHistoryJson> mDate;
    private Context mContext;

    public RegulationResponseAdapter(Context mContex, List mDate) {
        this.mContext = mContex;
        this.mDate = mDate;
    }

    @Override
    public int getCount() {
        return mDate.size();
    }

    @Override
    public Object getItem(int position) {
        return mDate.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.activity_regulation_result_item, null);
        // 获取ID
        WeizhangResponseHistoryJson model = mDate.get(position);

        TextView wz_time = (TextView) view.findViewById(R.id.wz_time);
        TextView wz_money = (TextView) view.findViewById(R.id.wz_money);
        TextView wz_addr = (TextView) view.findViewById(R.id.wz_addr);
        TextView wz_info = (TextView) view.findViewById(R.id.wz_info);
        // 填写值

        wz_time.setText(model.getOccur_date());
        wz_money.setText("计" + model.getFen() + "分, 罚" + model.getMoney() + "元");
        wz_addr.setText(model.getOccur_area());
        wz_info.setText(model.getInfo());

        return view;
    }

}
