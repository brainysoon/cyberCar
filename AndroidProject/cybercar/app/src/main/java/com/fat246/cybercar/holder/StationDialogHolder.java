package com.fat246.cybercar.holder;

import android.view.View;
import android.widget.TextView;

import com.fat246.cybercar.R;
import com.fat246.cybercar.beans.GasStationInfo;

/**
 * Created by Administrator on 2016/4/29.
 */
//ViewHolder
public class StationDialogHolder {

    //Dialog 相关 View
    private TextView mTitle;
    private TextView mPosition;
    private TextView mBrand;
    private TextView mType;
    private TextView mDiscount;
    private TextView mPrice;
    private TextView mFwlsmc;
    private TextView mDistance;

    //RootView
    private View mRootViwew;

    public StationDialogHolder(View RootView) {

        this.mRootViwew = RootView;

        //findView
        findView(mRootViwew);
    }

    private void findView(View rootView) {
        //Dialog View 相关
        mTitle = (TextView) rootView.findViewById(R.id.activity_more_gas_textview_title);
        mPosition = (TextView) rootView.findViewById(R.id.activity_more_gas_textview_position);
        mBrand = (TextView) rootView.findViewById(R.id.activity_more_gas_textview_brand);
        mType = (TextView) rootView.findViewById(R.id.activity_more_gas_textview_type);
        mDiscount = (TextView) rootView.findViewById(R.id.activity_more_gas_textview_discount);
        mPrice = (TextView) rootView.findViewById(R.id.activity_more_gas_textview_price);
        mFwlsmc = (TextView) rootView.findViewById(R.id.activity_more_gas_textview_fwlsmc);
        mDistance = (TextView) rootView.findViewById(R.id.activity_more_gas_textview_distance);
    }

    public void replaceDialog(GasStationInfo newGasStationInfo) {

        //修改Dialog
        mTitle.setText(newGasStationInfo.getGas_station_name());
        mBrand.setText(newGasStationInfo.getGas_station_brandname());
        mType.setText(newGasStationInfo.getGas_station_type());
        mDiscount.setText(newGasStationInfo.getGas_station_discount());
        mPrice.setText(newGasStationInfo.getGas_station_gastprice());
        mFwlsmc.setText(newGasStationInfo.getGas_station_fwlsmc());
        mDistance.setText(newGasStationInfo.getGas_station_distance());
        mPosition.setText(newGasStationInfo.getGas_station_address());
    }
}