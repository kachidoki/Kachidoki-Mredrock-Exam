package com.mayiwei.me.mredrock_exam.main;

import android.app.Activity;

/**
 * Created by Frank on 15/5/31.
 */
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mayiwei.me.mredrock_exam.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SelectPicPopupWindow extends Activity implements OnClickListener{

    private Button btn_save_photo, btn_share_photo, btn_cancel;
    private LinearLayout layout;
    private String pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);
        btn_save_photo = (Button) this.findViewById(R.id.btn_save_photo);
        btn_share_photo = (Button) this.findViewById(R.id.btn_share_photo);
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);

        layout=(LinearLayout)findViewById(R.id.pop_layout);



        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", Toast.LENGTH_SHORT).show();
            }
        });
        //添加按钮监听
        btn_cancel.setOnClickListener(this);
        btn_save_photo.setOnClickListener(this);
        btn_share_photo.setOnClickListener(this);
    }

    //实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }

    public void onClick(View v) {
        pic = getIntent().getStringExtra("pic");
        switch (v.getId()) {
            case R.id.btn_save_photo:
                showDilog(btn_save_photo);
                break;
            case R.id.btn_share_photo:

                showShareDialog();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }

    }

    /**
     * 弹出分享列表
     */
    private void showShareDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectPicPopupWindow.this);
        builder.setTitle("选择分享类型");
        builder.setItems(new String[]{"邮件","短信","其他"}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                switch (which) {
                    case 0:	//邮件
                        sendMail(pic);
                        break;

                    case 1:	//短信
                        sendSMS(pic);
                        break;

                    case 2:	//调用系统分享
                        Intent intent=new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT,"分享");
                        intent.putExtra(Intent.EXTRA_TEXT, "我正在看这个,觉得真不错,推荐给你哦~ "+pic);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(intent, "share"));
                        break;

                    default:
                        finish();
                        break;
                }
            }
        });
        builder.setNegativeButton( "取消" ,  new  DialogInterface.OnClickListener() {
            @Override
            public   void  onClick(DialogInterface dialog,  int  which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    /**
     * 发送邮件
     */
    private void sendMail(String emailUrl){
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
        email.setType("plain/text");

        String emailBody = "我正在浏览这个,觉得真不错,推荐给你哦~ 地址:" + emailUrl;
        //邮件主题
        email.putExtra(android.content.Intent.EXTRA_SUBJECT, pic);
        //邮件内容
        email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);

        startActivityForResult(Intent.createChooser(email, "请选择邮件发送内容"), 1001);
    }


    /**
     * 发短信
     */
    private   void  sendSMS(String webUrl){
        String smsBody = "我正在浏览这个,觉得真不错,推荐给你哦~ 地址:" + webUrl;
        Uri smsToUri = Uri.parse("smsto:");
        Intent sendIntent =  new  Intent(Intent.ACTION_VIEW, smsToUri);
        //sendIntent.putExtra("address", "123456"); // 电话号码，这行去掉的话，默认就没有电话
        //短信内容
        sendIntent.putExtra("sms_body", smsBody);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivityForResult(sendIntent, 1002);
    }


    //图片保存
    public  void saveImg(Bitmap bitmap) {
        String filename= Environment.getExternalStorageDirectory().toString() + "/IMG";
        File dir=new File(filename);
        if (!dir.exists()) {
            dir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(dir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void showDilog(final View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectPicPopupWindow.this);
        builder.setMessage("是否要保存");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = getIntent();
                if (intent != null) {
                    byte[] bis = intent.getByteArrayExtra("bitmap");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
                    saveImg(bitmap);
                }

                Toast.makeText(SelectPicPopupWindow.this, "保存成功", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

}
