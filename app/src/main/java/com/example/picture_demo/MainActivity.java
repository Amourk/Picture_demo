package com.example.picture_demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.picture_demo.app.PhotoWallAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 照片墙主活动，使用GridView展示照片墙。
 */
public class MainActivity extends Activity {

    //用于展示照片墙的GridView
    private GridView mPhotoWall = null;

    private MaterialRefreshLayout materialRefreshLayout;

    //GridView的适配器
    private PhotoWallAdapter mAdapter = null;

    private Context mContext = null;

    private int mImageThumbSize;
    private int mImageThumbSpacing;

    private String ima[] = new String[10];
//    private String ima1[] = new String[10];
    private String ID[] = new String[10];
//    private String ID1[] = new String[10];


    OkHttpClient okHttpClient = new OkHttpClient();
    private String url1 = "http://192.168.0.100:8080/TotemDown/LoginServe?username=linyuanbin&password=123456";

    private String UserID = "Thu Apr 27 20:28:09 CST 201731ZDD";

    private String msg1;//接收到的服务回馈消息


    Handler handle = new Handler() {
        public void handleMessage(Message msg) {
                    System.out.println("111");
//                    tup = (Bitmap[]) msg.obj;
//                    tupp = (ArrayList<Bitmap>) msg.obj;

                       String data = (String) msg.obj;
                       Type listType = new TypeToken<ArrayList<Picture>>() {
                       }.getType();
                       ArrayList<com.example.picture_demo.Picture> foos = new Gson().fromJson(data, listType);
                       for (int i = 0; i < foos.size(); i++) {
                                 ima[i] = foos.get(i).getPAddress();
                                 ID[i] = foos.get(i).getPID();
                                  System.out.println("name [" + i + "] = " + foos.get(i).getPAddress());
                }
                    for (int k = 0; k < ima.length; k++) {
                        Log.i("ccccc", "tupp =  " + ima[k]);
                    }

                    mAdapter = new PhotoWallAdapter(MainActivity.this, 0, ima, mPhotoWall);
                    mPhotoWall.setAdapter(mAdapter);
                    mPhotoWall.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @SuppressWarnings("deprecation")
                        @Override
                        public void onGlobalLayout() {
                            final int numColumns = (int) Math.floor(mPhotoWall.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                int columnWidth = (mPhotoWall.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setItemHeight(columnWidth);
                                mPhotoWall.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }
                        }
                    });


                    mPhotoWall.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Intent intent = new Intent(MainActivity.this,Image.class);
                            intent.putExtra("image",ima[position]);
                            intent.putExtra("ID",ID[position]);
                            Log.i("BBBBBBB", " ima = " + ima[position]);
                            Log.i("BBBBBBB", " ID = " + ID[position]);
                            startActivityForResult(intent,0);
//                            startActivity(intent);
                            Toast.makeText(mContext , position + "" , Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh);
        try {
            flash2();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            flash2();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(MainActivity.this, "111111", Toast.LENGTH_SHORT).show();
                        materialRefreshLayout.finishRefresh();
                    }
                }, 3000);
            }

            @Override
            public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "222222222", Toast.LENGTH_SHORT).show();
                        materialRefreshLayout.finishRefreshLoadMore();
                    }
                }, 3000);
            }
        });



        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        mPhotoWall = (GridView) findViewById(R.id.id_photo_wall);
//        mAdapter = new PhotoWallAdapter(this, 0, Images.imageThumbUrls, mPhotoWall);
        mContext = MainActivity.this;

//        Log.i("DDDDDDD", "textMain = " + Image.Label);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String MarkName = data.getStringExtra("a");
        String PID = data.getStringExtra("b");
//        String TabID =UserID+PID;
        switch (requestCode) {
            case 0:
                Log.i("DDDDDDD", "info = " + PID);
                Log.i("DDDDDDD", "textMain = " + MarkName);

                String name1 = "mark";

                String key = "{\"state\":\"" +name1+ "\"," +
                        "\"UserID\":\""+UserID+"\",\"PID\":\""+PID+"\",\"MarkName\":\""+MarkName+"\"}";
                try {
                    URLDecoder.decode(key, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody1 = RequestBody
                        .create(MediaType.parse("text/plain; charset=utf-8"), key);
                Request.Builder builder3 = new Request.Builder();
                Request request2 = builder3
                        .url(url1)
                        .post(requestBody1)
                        .build();

                okhttp3.Call call1 = okHttpClient.newCall(request2);
                call1.enqueue(new Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        Log.i("info", " GET请求失败！！！");
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {

                        final String res = response.body().string();
                        Log.i("info", " labelres"+res);
                        Gson gson = new Gson();
                        User user = gson.fromJson(res, User.class);
//                        user.getState();
                        if ( !user.getState().equals("true"))
                        {
                            Log.i("info", " 标签添加失败了哦！！！");
                        }
                    }
                });
                break;

            default:
                break;
        }
    }

    public void flash2() throws UnsupportedEncodingException {
        String name1 = "request";

        String key = "{\"state\":\"" + name1 + "\"}";
        URLDecoder.decode(key, "utf-8");
        RequestBody requestBody1 = RequestBody
                .create(MediaType.parse("text/plain; charset=utf-8"), key);
        Request.Builder builder3 = new Request.Builder();
        Request request2 = builder3
                .url(url1)
                .post(requestBody1)
                .build();

        okhttp3.Call call1 = okHttpClient.newCall(request2);
        call1.enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.i("info", " GET请求失败！！！");
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {

                final String res = response.body().string();
                Log.i("infoo", " GET请求成功！！！");

                Log.i("infoo", "res = " + res);

                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.obj = res;
                        handle.sendMessage(msg); //新建线程加载图片信息，发送到消息队列中
                    }
                };
                mThread.start();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.fluchCache();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出程序时结束所有的下载任务
        mAdapter.cancelAllTasks();
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}


