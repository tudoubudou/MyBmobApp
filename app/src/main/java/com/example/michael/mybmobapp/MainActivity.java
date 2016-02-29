package com.example.michael.mybmobapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.ResultCode;
import com.alibaba.sdk.android.callback.CallbackContext;
import com.alibaba.sdk.android.callback.InitResultCallback;
import com.alibaba.sdk.android.login.LoginService;
import com.alibaba.sdk.android.login.callback.LoginCallback;
import com.alibaba.sdk.android.login.callback.LogoutCallback;
import com.alibaba.sdk.android.session.model.Session;
import com.alibaba.sdk.android.trade.CartService;
import com.alibaba.sdk.android.trade.ItemService;
import com.alibaba.sdk.android.trade.TradeConfigs;
import com.alibaba.sdk.android.trade.TradeService;
import com.alibaba.sdk.android.trade.callback.TradeProcessCallback;
import com.alibaba.sdk.android.trade.model.TaokeParams;
import com.alibaba.sdk.android.trade.model.TradeResult;
import com.alibaba.sdk.android.trade.page.ItemDetailPage;
import com.alibaba.sdk.android.trade.page.MyCartsPage;
import com.alibaba.sdk.android.trade.page.MyOrdersPage;
import com.example.michael.mybmobapp.model.Person;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView = (TextView)findViewById(R.id.text);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Person p2 = new Person();
                p2.setName("gzw");
                p2.setAddress("shenzhen");
                p2.save(MainActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "save success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(MainActivity.this, "save fail! errorcode=" + i + " string=" + s, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        AlibabaSDK.asyncInit(this, new InitResultCallback() {

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "初始化成功", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFailure(int code, String message) {
                Toast.makeText(MainActivity.this, "初始化异常", Toast.LENGTH_SHORT)
                        .show();
            }

        });
        Bmob.initialize(this, "14fc2142c3fa7d48d597f565a3540aaf");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallbackContext.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id ){
            case R.id.action_list:
//                showMyOrdersPage(0,false);
//                showMyCartsPage();
//                showItemDetailPage();
                showPayPage();
                return true;
            case R.id.action_settings:
                queryData();
                return true;
            case R.id.action_taobao:
                showLogin();
                return true;
            case R.id.action_get_taobaoke:
//                showLogin();
                showItemDetailPage();
                return true;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getTaobaoke() {

    }
    public void queryData(){
        BmobQuery query = new BmobQuery("Person");
        query.findObjects(this, new FindCallback() {

            @Override
            public void onSuccess(JSONArray arg0) {
                //注意：查询的结果是JSONArray,需要自行解析
                showToast("查询成功:" + arg0.length());
                textView.setText(arg0.toString());
                for (int i = 0; i < arg0.length(); i++) {

                    Person p = JSON.parseObject(arg0.optString(i), Person.class);
                    showToast(p.toString());
                }
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                showToast("查询失败:"+arg1 + " errorcode=" + arg0);
            }
        });
    }

    public void showLogin() {
        LoginService loginService = AlibabaSDK.getService(LoginService.class);
        loginService.showLogin(MainActivity.this, new LoginCallback() {

            @Override
            public void onSuccess(Session session) {
                Toast.makeText(MainActivity.this, "欢迎" + session.getUser().nick + session.getUser().avatarUrl,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String message) {
                Toast.makeText(MainActivity.this, "授权取消" + code + message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void logout(View view) {
        LoginService loginService = AlibabaSDK.getService(LoginService.class);
        loginService.logout(this, new LogoutCallback() {

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(MainActivity.this, "登出失败", Toast.LENGTH_SHORT)
                        .show();

            }

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "登出成功", Toast.LENGTH_SHORT)
                        .show();

            }
        });
    }
    public void showItemDetailPage(){
        TradeService tradeService = AlibabaSDK.getService(TradeService.class);
//        ItemDetailPage itemDetailPage = new ItemDetailPage("AAEdNWF_ABrk9HpuLchgVlYX", null);
        ItemDetailPage itemDetailPage = new ItemDetailPage("37196464781", null);
        TaokeParams taokeParams = new TaokeParams(); //若非淘客taokeParams设置为null即可
        taokeParams.pid = "mm_24942362_0_0";
        tradeService.show(itemDetailPage, taokeParams, MainActivity.this, null, new TradeProcessCallback() {

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(MainActivity.this, "失败 " + code + msg,
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPaySuccess(TradeResult tradeResult) {
                Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT)
                        .show();

            }
        });
    }
    public void showMyCartsPage(){
        TradeService tradeService = AlibabaSDK.getService(TradeService.class);
        MyCartsPage myCartsPage = new MyCartsPage();
        TradeConfigs.defaultISVCode = "kdkdkdkd"; //传入isv_code
        tradeService.show(myCartsPage, null, MainActivity.this, null, new TradeProcessCallback(){

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(MainActivity.this, "失败 "+code+msg,
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPaySuccess(TradeResult tradeResult) {
                Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT)
                        .show();

            }});
    }
    public void addItem2Cart(String openId) {
        CartService cartService = AlibabaSDK.getService(CartService.class);
        cartService.addItem2Cart(this, new TradeProcessCallback() {

            @Override
            public void onPaySuccess(TradeResult tradeResult) {
                Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT)
                        .show();

            }

            @Override
            public void onFailure(int code, String msg) {
                if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
                    Toast.makeText(MainActivity.this, "确认交易订单失败",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "交易取消 " + code + msg,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }, "加入购物车", openId, null);
    }
    /**
     * 我的订单页
     *
     * @param status
     *            默认跳转页面；填写：0：全部；1：待付款；2：待发货；3：待收货；4：待评价。若传入的不是这几个数字，则跳转到“全部”页面且“allOrder”失效
     * @param allOrder true：显示全部订单。False：显示当前appKey下的订单
     *
     */
    public void showMyOrdersPage(int status, boolean allOrder){//to order management page
        TradeService tradeService = AlibabaSDK.getService(TradeService.class);
        MyOrdersPage myOrdersPage = new MyOrdersPage(status, allOrder);
        tradeService.show(myOrdersPage, null, MainActivity.this, null, new TradeProcessCallback() {

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(MainActivity.this, "失败 " + code + msg,
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPaySuccess(TradeResult tradeResult) {
                Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT)
                        .show();

            }
        });
    }
    public void showPayPage() {
        ItemService itemService = AlibabaSDK.getService(ItemService.class);
        itemService.showPage(this, new TradeProcessCallback() {

            @Override
            public void onPaySuccess(TradeResult tradeResult) {
                Toast.makeText(
                        MainActivity.this,
                        "支付成功" + tradeResult.paySuccessOrders + "   "
                                + tradeResult.payFailedOrders,
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int code, String msg) {
                if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
                    Toast.makeText(MainActivity.this, "确认交易订单失败",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "交易异常",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }, null, "http://m.taobao.com");
    }
    private void showToast(String s) {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
    }
}
