package com.coder.mobilesafer;

/**
 * Created by QiWangming on 2015/10/15.
 * Blog: www.jycoder.com
 * GitHub: msAndroid
 */
public class VersionEntity {
    //服务器版本号
    private String versioncode;
    //版本描述
    private String description;
    //apk下载地址
    private String apkurl;

    public String getVersioncode() {
        return versioncode;
    }

    public String getDescription() {
        return description;
    }

    public String getApkurl() {
        return apkurl;
    }

    public VersionEntity() {
        super();
    }
    public VersionEntity(String v,String d,String url) {
        this.versioncode=v;
        this.description=d;
        this.apkurl=url;
    }

}
