package com.mayiwei.me.mredrock_exam.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.mayiwei.me.mredrock_exam.R;
import com.mayiwei.me.mredrock_exam.config.API;
import com.mayiwei.me.mredrock_exam.model.bean.UpdataInfo;

/**
 * Created by Frank on 15/5/31.
 */
public class UpdateManager {
    private Context mContext;

    //提示语
    private String updateMsg = "有最新的软件包哦，尽管是坑也要入";

    //返回的安装包url
    private String apkUrl = "http://hongyan.cqupt.edu.cn/app/com.mredrock.cyxbs.apk";


    private Dialog noticeDialog;

    private Dialog downloadDialog;
    /* 下载包安装路径 */
    private static final String savePath = "/sdcard/updatedemo/";

    private static final String saveFileName = savePath + "UpdateDemoRelease.apk";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;


    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private Thread downLoadThread;

    private boolean interceptFlag = false;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:

                    installApk();
                    break;
                default:
                    break;
            }
        };
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    //外部接口让主Activity调用
    public void checkUpdateInfo(){
        try {
            URL url=new URL(API.Mredrock_DownLoad);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //连接超时时间
            conn.setConnectTimeout(5000);
            int code = conn.getResponseCode();
            if(code==200){
                InputStream is=conn.getInputStream();
                UpdataInfo updateInfo=new UpdataInfo();
                updateInfo=UpdateInfoProvider.getUpdateInfo(is);
                Log.i("XML",updateInfo.getApkURL());
                if(updateInfo!=null){
                    //解析成功
                    Log.i("XMLTest","解析成功");
                    apkUrl = updateInfo.getApkURL();

                }else{
                    //解析失败
                     Log.i("XMLTest","解析失败");
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        showNoticeDialog();
    }


    private void showNoticeDialog(){
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("软件版本更新");
        builder.setMessage(updateMsg);
        builder.setPositiveButton("现在跳坑", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        builder.setNegativeButton("滚你丫的", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.show();
    }

    private void showDownloadDialog(){
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("软件版本更新");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_softupdate_progress, null);
        mProgress = (ProgressBar)v.findViewById(R.id.progress);

        builder.setView(v);
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();

        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(savePath);
                if(!file.exists()){
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                if(!ApkFile.exists()){
                    ApkFile.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do{
                    int numread = is.read(buf);
                    count += numread;
                    progress =(int)(((float)count / length) * 100);
                    //更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if(numread <= 0){
                        //下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf,0,numread);
                }while(!interceptFlag);//点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }

        }
    };

    /**
     * 下载apk
     */

    private void downloadApk(){
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }
    /**
     * 安装apk
     */
    private void installApk(){
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);

    }
}
