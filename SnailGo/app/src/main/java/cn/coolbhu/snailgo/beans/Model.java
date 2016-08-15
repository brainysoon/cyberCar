package cn.coolbhu.snailgo.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/5/21.
 */
public class Model extends BmobObject {

    private String Model_Name;
    private String Model_Type;
    private Double Model_Low;
    private Double Model_High;
    private String Brand_Name;

    //get
    public String getModel_Name() {
        return this.Model_Name;
    }

    public String getModel_Type() {
        return this.Model_Type;
    }

    public Double getModel_Low() {
        return this.Model_Low;
    }

    public Double getModel_High() {
        return this.Model_High;
    }

    public String getBrand_Name() {
        return this.Brand_Name;
    }
}
