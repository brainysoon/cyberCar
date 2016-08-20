package cn.coolbhu.snailgo.activities.unionpay;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    /*****************************************************************
     * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
     *****************************************************************/
    public final String mMode = "01";
    private static final String TN_URL_01 = "http://101.231.204.84:8091/sim/getacptn";

    public abstract void doStartUnionPayPlugin(Activity activity, String tn,
                                               String mode);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    int startpay(Activity act, String tn, int serverIdentifier) {
        return 0;
    }

    public abstract boolean verify(String msg, String sign64, String mode);
}
