package com.mayiwei.me.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
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


}
