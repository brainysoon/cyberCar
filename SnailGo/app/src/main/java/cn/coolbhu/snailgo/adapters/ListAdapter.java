package cn.coolbhu.snailgo.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.beans.ListModel;

public class ListAdapter extends BaseAdapter {

    private List<ListModel> mDate;
    private Context mContext;

    public ListAdapter(Context mContext, List mDate) {
        this.mContext = mContext;
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

        View view = View.inflate(mContext, R.layout.activity_city_list_item, null);

        //初始化
        ListModel model = mDate.get(position);
        TextView txt_name = (TextView) view.findViewById(R.id.txt_name);
        //ImageView image=(ImageView)view.findViewById(R.id.iv_1);


        //绑定数据
        txt_name.setText(model.getTextName());
        txt_name.setTag(model.getNameId());
        //返回
        return view;
    }

}