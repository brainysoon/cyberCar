package com.fat246.cybercar.activities;

import android.os.Bundle;

import com.fat246.cybercar.R;
import com.fat246.cybercar.fragments.MenuFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;

public class MainActivity extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle savedInstanceState) {


        MaterialAccount mAccount = new MaterialAccount(getResources(),
                "孙晓聪", "sunxcchn918@163.com", R.drawable.avator, R.drawable.shadow);

        this.addAccount(mAccount);

        // create sections
        this.addSection(newSection("Section 1", MenuFragment.newInstance()));

    }
}
