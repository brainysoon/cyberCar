package com.fat246.cybercar.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.fat246.cybercar.R;
import com.fat246.cybercar.beans.CarInfo;

import java.util.ArrayList;
import java.util.List;

public class MaintainCarInfoFragment extends Fragment {

    //View
    private ListView mListView;
    private List<CarInfo> mCarInfos = new ArrayList<>();

    //Adapter
    private CarInfosAdapter mCarInfosAdapter;

    //inflater
    private LayoutInflater mInflater;

    public MaintainCarInfoFragment() {


    }

    public static MaintainCarInfoFragment newInstance() {
        MaintainCarInfoFragment fragment = new MaintainCarInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        mInflater = LayoutInflater.from(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maintain_car_info, container, false);

        setRootView(rootView);

        return rootView;
    }

    //setView
    private void setRootView(View rootView) {

        findView(rootView);

        setView();
    }

    //setView
    private void setView() {

        mCarInfosAdapter = new CarInfosAdapter();

        mListView.setAdapter(mCarInfosAdapter);
    }

    //findView
    private void findView(View rootView) {

        mListView = (ListView) rootView.findViewById(R.id.fragment_maintain_car_info_listview_infos);
    }

    class CarInfosAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCarInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mCarInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = mInflater.inflate(R.layout.maintain_car_info_item, null);

            return convertView;
        }
    }
}
