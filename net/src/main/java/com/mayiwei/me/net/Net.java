package com.mayiwei.me.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Frank on 15/5/30.
 */
public class Net {
    //单例模式
    private static Net instance;
    //无构造方法
    private Net(){};
    //唯一方法取得对象
    public static Net getInstance(){
        if(instance==null){
            instance = new Net();
        }
        return instance;
    }
    //文本请求回调
    public interface StrCallback{
        void response(String result);
    }
    //图片请求回调
    public  interface ImageCallback{
        void response(Bitmap result);
    }
    //get请求:这里的请求参数必须写在string url里面，通过URL realurl=new URL(url);来访问地址
    public void netGet(final String url ,final Map<String,String> params,final StrCallback callBack){
        final Handler handler=new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String requestData="";
                    if(params==null){
                        requestData=url;
                    }
                    if(params!=null){
                        requestData=url+"?";
                        for(Map.Entry<String,String> entry:params.entrySet()){
                            requestData+= URLEncoder.encode(entry.getKey(), "UTF-8")+
                                    "="+URLEncoder.encode(entry.getValue(),"UTF-8")+"&";
                        }

                    }
                    URL realurl=new URL(requestData);
                    HttpURLConnection connection= (HttpURLConnection) realurl.openConnection();
                    //貌似默认的是get请求
                    connection.setRequestMethod("GET");
                    //获取输入流(inputstream和outputstream是其他字节流的父类（抽象类）是不能直接new的
                    // 必须继承他，实例化它的子类)
                    InputStream in=connection.getInputStream();
                    ByteArrayOutputStream out=new ByteArrayOutputStream();
                    int a=0;
                    byte[] bytes=new byte[1024*20];
                    while ((a=in.read(bytes))!=-1){
                        out.write(bytes,0,a);
                    }
                    in.close();
                    out.flush();
                    out.close();
                    //toByteArray:返回这个流的当前内容作为一个字节数组。
                    final String result=new String(out.toByteArray());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.response(result);
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void netImage(final String url, final ImageCallback callBack){
        final Handler handler=new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL realurl = null;
                try {
                    realurl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) realurl.openConnection();
                    //貌似默认的是get请求
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.connect();
                    //获取输入流(inputstream和outputstream是其他字节流的父类（抽象类）是不能直接new的
                    // 必须继承他，实例化它的子类)
                    InputStream in = connection.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(in);
                    in.close();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.response(bitmap);
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();

    }

    //post请求
    public  void  netPost(final String url, final Map<String,String> params, final StrCallback callback) {
        //在主线程new一个handler;
        //handler.post会把Runable扔给主线程去运行。
        final Handler handler = new Handler();

        //开启线程
        new Thread(new Runnable() {
            @Override
            public void run() {

                //请求参数
                StringBuilder paramsStr = new StringBuilder();

                if (params!=null){

                    //构造参数  参数格式什么的百度"HTTP请求 POST与GET"相关
                    Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();
                    while (iterator.hasNext()){
                        Map.Entry<String, String> entry = iterator.next();
                        paramsStr.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    }
                }


                //开始网络请求
                final String result = httpRequest(url, paramsStr.toString());

                //回调请求结果
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.response(result);
                    }
                });

            }
        }).start();
    }

    public String httpRequest(String url, String params){
        URL realurl = null;
        InputStream in = null;
        HttpURLConnection conn = null;
        String result = "";
        try{
            realurl = new URL(url);
            conn = (HttpURLConnection)realurl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            PrintWriter pw = new PrintWriter(conn.getOutputStream());
            pw.print(params);
            pw.flush();
            pw.close();
            Log.i("test", "url:" + url);
            Log.i("test","params:"+params);

            in = conn.getInputStream();
            BufferedReader bin = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = bin.readLine()) != null) {
                result += line;
            }
        }catch(Exception eio){
            result = "error:"+eio.getMessage();
        }
        return result;
    }


}
