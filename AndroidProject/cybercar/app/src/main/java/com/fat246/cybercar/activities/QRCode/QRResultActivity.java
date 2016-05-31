package com.fat246.cybercar.activities.QRCode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.fat246.cybercar.R;

public class QRResultActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrresult);

        mTextView = (TextView) findViewById(R.id.activity_qrresult_text);

        Intent mIntent = getIntent();

        Bundle mBundle = mIntent.getExtras();

        String str = mBundle.getString("qr_info");

        mTextView.setText(str);
    }
}
