package com.mayiwei.me.mredrock_exam.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mayiwei.me.mredrock_exam.R;
import com.mayiwei.me.mredrock_exam.app.BaseActivity;
import com.mayiwei.me.net.Net;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.os.Handler;
/**
 * Created by Frank on 15/5/30.
 */
public class GirlItem_Activity extends BaseActivity {
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/download_test/";
    private ProgressDialog mSaveDialog = null;
    private Bitmap mBitmap;
    private String mFileName;
    private String mSaveMessage;
    TextView tv_title;
    TextView tv_date;
    ImageView img_girl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girl);
        setToolbar(true);

        tv_title = $(R.id.Actgirl_author);
        tv_date = $(R.id.Actgirl_date);
        img_girl = $(R.id.Actgirl_img);
        Intent intent = this.getIntent();
        tv_title.setText(intent.getStringExtra("title"));
        tv_date.setText(intent.getStringExtra("date"));
        mFileName = intent.getStringExtra("pic");
        Net.getInstance().netImage(intent.getStringExtra("pic"), new Net.ImageCallback() {
            @Override
            public void response(Bitmap result) {
                img_girl.setImageBitmap(result);
                mBitmap = result;
            }
        });

//        img_girl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mSaveDialog = ProgressDialog.show(GirlItem_Activity.this, "保存图片", "图片正在保存中，请稍等...", true);
//                new Thread(saveFileRunnable).start();
//            }
//        });
        img_girl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(GirlItem_Activity.this,SelectPicPopupWindow.class);
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte [] bitmapByte =baos.toByteArray();
                intent.putExtra("bitmap", bitmapByte);
                startActivity(intent);
                return false;
            }
        });
    }

//    /**
//     * 保存文件
//     * @param bm
//     * @param fileName
//     * @throws IOException
//     */
//    public void saveFile(Bitmap bm, String fileName) throws IOException {
//        File dirFile = new File(ALBUM_PATH);
//        if(!dirFile.exists()){
//            dirFile.mkdir();
//        }
//        File myCaptureFile = new File(ALBUM_PATH + fileName);
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
//        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
//        bos.flush();
//        bos.close();
//    }
//
//    private Runnable saveFileRunnable = new Runnable(){
//        @Override
//        public void run() {
//            try {
//                saveFile(mBitmap, mFileName);
//                mSaveMessage = "图片保存成功！";
//            } catch (IOException e) {
//                mSaveMessage = "图片保存失败！";
//                e.printStackTrace();
//            }
//            messageHandler.sendMessage(messageHandler.obtainMessage());
//        }
//
//    };
//
//    private Handler messageHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            mSaveDialog.dismiss();
//            Log.d("GirlItem_Activity", mSaveMessage);
//            Toast.makeText(GirlItem_Activity.this, mSaveMessage, Toast.LENGTH_SHORT).show();
//        }
//    };


}
