package com.mayiwei.me.mredrock_exam.util;

import android.util.Xml;

import com.mayiwei.me.mredrock_exam.model.bean.UpdataInfo;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

/**
 * Created by Frank on 15/5/31.
 */
public class UpdateInfoProvider {
    //解析xml文件
    public  static UpdataInfo getUpdateInfo(InputStream is) {
        XmlPullParser parser = Xml.newPullParser();
        UpdataInfo info = new UpdataInfo();
        // 初始化解析器
        try {
            parser.setInput(is, "utf-8");
            int type = parser.getEventType();

            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("versionCode".equals(parser.getName())) {
                            String VersionCode = parser.nextText();
                            info.setVersionCode(VersionCode);
                        } else if ("versionName".equals(parser.getName())) {
                            String versionName = parser.nextText();
                            info.setVersionName(versionName);
                        } else if ("updateContent".equals(parser.getName())) {
                            String updateContent = parser.nextText();
                            info.setUpdateContent(updateContent);
                        }else if ("apkURL".equals(parser.getName())) {
                            String apkURL = parser.nextText();
                            info.setApkURL(apkURL);
                        }
                        break;
                }
                type = parser.next();
            }
            return info;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
