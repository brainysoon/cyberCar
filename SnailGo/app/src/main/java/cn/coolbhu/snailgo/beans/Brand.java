package cn.coolbhu.snailgo.beans;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/5/21.
 */
public class Brand extends BmobObject {

    private String Brand_Name;
    private BmobFile Brand_Sign;

    //get
    public String getBrand_Name() {
        return this.Brand_Name;
    }

    public BmobFile getBrand_Sign() {
        return this.Brand_Sign;
    }
}
