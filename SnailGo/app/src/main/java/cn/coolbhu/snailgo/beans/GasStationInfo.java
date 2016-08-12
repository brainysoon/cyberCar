package cn.coolbhu.snailgo.beans;

import android.content.Context;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GasStationInfo {

    //接口地址   聚合数据的接口地址
    private static final String GAS_STATION_INFO_POST_URL = "http://apis.juhe.cn/oil/local";

    //volley 请求tag
    private static final String GAS_STATION_INFO_POST_TAG = "GAS_STATION_INFO_POST_TAG";

    //Appkey  SDK 不需要这个
//    private static final String APPKEY = "21f08923d297419ad0f5272d215b511f";

    //Api id
    private static final int API_ID = 7;

    //post 请求参数
    public static final String GAS_STATION_INFO_POST_PAGE = "page";
    //    public static final String GAS_STATION_INFO_POST_APPKEY = "key";
    public static final String GAS_STATION_INFO_POST_LON = "lon";
    public static final String GAS_STATION_INFO_POST_LAT = "lat";
    public static final String GAS_STATION_INFO_POST_R = "r";
    public static final String GAS_STATION_INFO_POST_FORMAT = "format";

    //返回状态码
    private static final String GAS_STATION_INFO_POST_STATUS = "resultcode";

    //result jsonObject
    private static final String GAS_STATION_INFO_POST_RESULT = "result";

    //数据
    private static final String GAS_STATION_INFO_POST_DATA = "data";

    //页码信息
    private static final String PAGE_INFO = "pageinfo";

    //PNUMS
    private static final String PNUMS = "pnums";

    //当前页码
    private static final String CURRENT_PAGE = "current";

    //总共的页码
    private static final String ALL_PAGES = "allpage";

    //状态码成功
    private static final int GAS_STATION_INFO_POST_STATUS_SUCCEED = 200;

    //加油站名称
    public static final String GAS_STATION_NAME = "name";

    //城市邮编
    public static final String GAS_STATION_AREA = "area";

    //城市区域
    public static final String GAS_STATION_AREANAME = "areaname";

    //加油站地址
    public static final String GAS_STATION_ADDRESS = "address";

    //运营商类型
    public static final String GAS_STATION_BRANDNAME = "brandname";

    //加油站类型
    public static final String GAS_STATION_TYPE = "type";

    //加油站是否打折
    public static final String GAS_STATION_DISCOUNT = "discount";

    //尾气排放标准
    public static final String GAS_STATION_EXHAUST = "exhaust";

    //谷歌地图坐标
    public static final String GAS_STATION_POSITION = "position";

    //百度地图维度
    public static final String GAS_STATION_LAT = "lat";

    //百度地图经度
    public static final String GAS_STATION_LON = "lon";

    //省控基准油价
    public static final String GAS_STATION_PRICE = "price";

    //加油站油价
    public static final String GAS_STATION_GASTPRICE = "gastprice";

    //加油卡信息
    public static final String GAS_STATION_FWLSMC = "fwlsmc";

    //与坐标的距离
    public static final String GAS_STATION_DISTANCE = "distance";

    //Json String
    public static final String GAS_STATION_JSON_STRING = "GAS_STATION_JSON_STRING";


    //私有属性
    private String gas_station_name;
    private String gas_station_area;
    private String gas_station_areaname;
    private String gas_station_address;
    private String gas_station_brandname;
    private String gas_station_type;
    private String gas_station_discount;
    private String gas_station_exhaust;
    private String gas_station_position;
    private double gas_station_lat;
    private double gas_station_lon;
    private String gas_station_price;
    private String gas_station_gastprice;
    private String gas_station_fwlsmc;
    private String gas_station_distance;

    //JsonObject
    private String mJsonString;

    //公共构造函数
    public GasStationInfo(JSONObject gasStaion) {
        try {

            this.mJsonString = gasStaion.toString();

            //给属性符初始值
            gas_station_name = gasStaion.getString(GAS_STATION_NAME);
            gas_station_area = gasStaion.getString(GAS_STATION_AREA);
            gas_station_areaname = gasStaion.getString(GAS_STATION_AREANAME);
            gas_station_address = gasStaion.getString(GAS_STATION_ADDRESS);
            gas_station_brandname = gasStaion.getString(GAS_STATION_BRANDNAME);
            gas_station_type = gasStaion.getString(GAS_STATION_TYPE);
            gas_station_discount = gasStaion.getString(GAS_STATION_DISCOUNT);
            gas_station_exhaust = gasStaion.getString(GAS_STATION_EXHAUST);
            gas_station_position = gasStaion.getString(GAS_STATION_POSITION);
            gas_station_lat = gasStaion.getDouble(GAS_STATION_LAT);
            gas_station_lon = gasStaion.getDouble(GAS_STATION_LON);
            gas_station_price = gasStaion.getString(GAS_STATION_PRICE);
            gas_station_gastprice = gasStaion.getString(GAS_STATION_GASTPRICE);
            gas_station_fwlsmc = gasStaion.getString(GAS_STATION_FWLSMC);
            gas_station_distance = gasStaion.getString(GAS_STATION_DISTANCE);

        } catch (JSONException jsonException) {

            jsonException.printStackTrace();
        }
    }

    //组装post请求 JsonObject
    public static Parameters setPostRequest(double lon, double lat, int r, int page, int format) {


        Parameters parameters = new Parameters();

        parameters.add(GAS_STATION_INFO_POST_LON, lon);

        parameters.add(GAS_STATION_INFO_POST_LAT, lat);

        parameters.add(GAS_STATION_INFO_POST_R, r);

        parameters.add(GAS_STATION_INFO_POST_PAGE, page);

        parameters.add(GAS_STATION_INFO_POST_FORMAT, format);

/*
        JSONObject jsonObject = new JSONObject();
        Log.e("Request--", lon + "||" + lat + "||" + r);

        try {

            jsonObject.put(GAS_STATION_INFO_POST_LON, lon);

            jsonObject.put(GAS_STATION_INFO_POST_LAT, lat);

            jsonObject.put(GAS_STATION_INFO_POST_R, r);

            jsonObject.put(GAS_STATION_INFO_POST_PAGE, page);

            jsonObject.put(GAS_STATION_INFO_POST_FORMAT, format);

//            jsonObject.put(GAS_STATION_INFO_POST_APPKEY, APPKEY);

            Log.e("Request", jsonObject.toString());

        } catch (JSONException jsonException) {

            jsonException.printStackTrace();

        }

        */

        return parameters;
    }

    //获取周围加油站post请求
    public static void attmptGasStationPost(Context mContext, Parameters parameters, final canHandGasStationPostResult mHand) {

        //得用聚合数据自带的方法来得到数据
        JuheData.executeWithAPI(mContext, API_ID, GAS_STATION_INFO_POST_URL, JuheData.GET, parameters, new DataCallBack() {
            @Override
            public void onSuccess(int i, String s) {

                mHand.handGasStationPostResult(i, s);
            }

            @Override
            public void onFinish() {

                mHand.handGasStationPostFinish();
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {

                mHand.handGasStationPostError(i, s, throwable);
            }
        });

        /*JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GAS_STATION_INFO_POST_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                mHand.handGasStationPostResult(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                mHand.handGasStationPostError(volleyError);
            }
        });

        jsonObjectRequest.setTag(GAS_STATION_INFO_POST_TAG);

        MyApplication.getRequestQueue().add(jsonObjectRequest);*/
    }

    //处理请求周围加油站接口接口
    public interface canHandGasStationPostResult {

        void handGasStationPostResult(int i, String s);

        void handGasStationPostError(int i, String s, Throwable throwable);

        void handGasStationPostFinish();
    }

    //解析返回过来的json 数据
    public static Map<String, GasStationInfo> paserGasStationPostResult(JSONObject jsonObject) {

        Map<String, GasStationInfo> mGasStations = new HashMap<>();

        //尝试解析
        try {

            int errorCode = jsonObject.getInt(GAS_STATION_INFO_POST_STATUS);

            //成功的到数据
            if (errorCode == GAS_STATION_INFO_POST_STATUS_SUCCEED) {

                JSONObject result = jsonObject.getJSONObject(GAS_STATION_INFO_POST_RESULT);

                if (result != null) {

                    JSONArray data = result.getJSONArray(GAS_STATION_INFO_POST_DATA);

                    if (data != null) {

                        for (int i = 0; i < data.length(); i++) {

                            GasStationInfo temp = new GasStationInfo(data.getJSONObject(i));

                            mGasStations.put(temp.getGas_station_name(), temp);
                        }
                    }
                }
            }
        } catch (JSONException jsonException) {

            jsonException.printStackTrace();
        }

        return mGasStations;
    }

    //解析页码
    public static PageInfo paserGasStationPageInfo(JSONObject jsonObject) {

        //尝试解析
        try {

            int errorCode = jsonObject.getInt(GAS_STATION_INFO_POST_STATUS);

            //成功的到数据
            if (errorCode == GAS_STATION_INFO_POST_STATUS_SUCCEED) {

                JSONObject result = jsonObject.getJSONObject(GAS_STATION_INFO_POST_RESULT);

                if (result != null) {

                    JSONObject pageinfo = result.getJSONObject(PAGE_INFO);

                    return new PageInfo(pageinfo);
                }
            }
        } catch (JSONException jsonException) {

            jsonException.printStackTrace();
        }

        return null;
    }

    public static class PageInfo {

        //这一页总共的数量
        private int pnums;

        //当前页码
        private int current;

        //总共有多少页码
        private int allpage;

        public PageInfo(JSONObject jsonObject) {

            try {

                pnums = jsonObject.getInt(PNUMS);

                current = jsonObject.getInt(CURRENT_PAGE);

                allpage = jsonObject.getInt(ALL_PAGES);

            } catch (JSONException jsonException) {

                jsonException.printStackTrace();
            }
        }

        public int getPnums() {
            return this.pnums;
        }

        public int getCurrent() {
            return this.current;
        }

        public int getAllpage() {
            return this.allpage;
        }
    }

    //只读属性
    public double getGas_station_lat() {
        return this.gas_station_lat;
    }

    public double getGas_station_lon() {
        return this.gas_station_lon;
    }

    public String getGas_station_name() {
        return this.gas_station_name;
    }

    public String getGas_station_address() {
        return this.gas_station_address;
    }

    public String getGas_station_brandname() {
        return this.gas_station_brandname;
    }

    public String getGas_station_type() {
        return this.gas_station_type;
    }

    public String getGas_station_discount() {
        return this.gas_station_discount;
    }

    public String getGas_station_gastprice() {
        return this.gas_station_gastprice;
    }

    public String getGas_station_fwlsmc() {
        return this.gas_station_fwlsmc;
    }

    public String getGas_station_distance() {
        return this.gas_station_distance;
    }


    //get JsonString
    public String getJsonString() {
        return this.mJsonString;
    }

    /*
    area	string	城市邮编
    areaname	string	城市区域
    　position	string	谷歌地图坐标
 	　　lat	double	百度地图纬度
 	　　lon	double	百度地图经度
 	price	string	省控基准油价
     */

    /*
        name	string	加油站名称
 	　　address	string	加油站地址
 	　　brandname	string	运营商类型
 	　　type	string	加油站类型
 	　　discount	string	是否打折加油站
 	　　exhaust	string	尾气排放标准
 	　　gastprice	string	加油站油价（仅供参考，实际以加油站公布价为准）
 	　　fwlsmc	string	加油卡信息
 	　　distance	string	与坐标的距离，单位M　
     */
}
