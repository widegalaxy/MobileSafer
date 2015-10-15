package com.coder.mobilesafer.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.coder.mobilesafe.R;
import com.coder.mobilesafer.HomeActivity;
import com.coder.mobilesafer.VersionEntity;
import com.coder.mobilesafer.utils.DownLoadUtils.MyCallBack;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by QiWangming on 2015/10/15.
 * Blog: www.jycoder.com
 * GitHub: msAndroid
 */
public class UpdateVersionUtils {
    //负责整个更新逻辑，注意更新UI逻辑

    private static final int MESSAGE_NET_EEOR = 101;
    private static final int MESSAGE_IO_EEOR = 102;
    private static final int MESSAGE_JSON_EEOR = 103;
    private static final int MESSAGE_SHOEW_DIALOG = 104;
    protected static final int MESSAGE_ENTERHOME = 105;
    private static final String BASE_URL="http://mobilenews.sinaapp.com/updateinfo.json";

    /*本地版本号*/
    private String mVersion;
    private AppCompatActivity context;
    private VersionEntity versionEntity;
    //下载进度框
    private ProgressDialog mProgressDialog;

    /**
     * Constructs a new instance of {@code Object}.
     */
    public UpdateVersionUtils(String version,AppCompatActivity activity) {
        this.mVersion=version;
        this.context=activity;
    }

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case MESSAGE_IO_EEOR:
                    Toast.makeText(context, "IO异常",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_JSON_EEOR:
                    Toast.makeText(context, "JSON解析异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_NET_EEOR:
                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_SHOEW_DIALOG:
                    showUpdateDialog(versionEntity);
                    break;
                case MESSAGE_ENTERHOME:
                    Intent intent = new Intent(context,HomeActivity.class);
                    context.startActivity(intent);
                    context.finish();
                    break;
                default:
                    enterHome();
                    break;

            }
        }
    };

    public void getCloudVersion()  {
        try {
            URL url = new URL(BASE_URL);

            // 利用HttpURLConnection对象，我们可以从网页中获取网页数据
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 单位为毫秒，设置超时时间为5秒
            conn.setConnectTimeout(5 * 1000);
            // HttpURLConnection对象是通过HTTP协议请求path路径的，所以需要设置请求方式，可以不设置，因为默认为get
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {// 判断请求码是否200，否则为失败
                InputStream is = conn.getInputStream(); // 获取输入流
                byte[] data = readStream(is); // 把输入流转换成字符串组
                String json = new String(data,"GBK"); // 把字符串组转换成字符串

                System.out.println(json);
                Log.i("MOBILESAFE",json);
                JSONObject jsonObject = new JSONObject(json); // 返回的数据形式是一个Object类型，所以可以直接转换成一个Object
                String code = jsonObject.getString("code");
                String des = jsonObject.getString("des");
                String apkurl = jsonObject.getString("apkurl");
                versionEntity = new VersionEntity(code, des, apkurl);
                if (!mVersion.equals(versionEntity.getVersioncode())) {
                    // 版本号不一致
                    handler.sendEmptyMessage(MESSAGE_SHOEW_DIALOG);
                }
            }
        }catch (IOException e){
            handler.sendEmptyMessage(MESSAGE_IO_EEOR);
            e.printStackTrace();
        }catch (JSONException e) {
            handler.sendEmptyMessage(MESSAGE_JSON_EEOR);
            e.printStackTrace();
        }catch (Exception e){
            handler.sendEmptyMessage(MESSAGE_ENTERHOME);
            e.printStackTrace();
        }

    }

    private static byte[] readStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }
        bout.close();
        inputStream.close();
        return bout.toByteArray();
    }

    private void showUpdateDialog(final VersionEntity versionEntity) {
        // 创建dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("检查到新版本：" + versionEntity.getVersioncode());// 设置标题
        builder.setMessage(versionEntity.getDescription());// 根据服务器返回描述,设置升级描述信息
        builder.setCancelable(false);// 设置不能点击手机返回按钮隐藏对话框
        builder.setIcon(R.drawable.ic_launcher);// 设置对话框图标
        // 设置立即升级按钮点击事件
        builder.setPositiveButton("立即升级",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initProgressDialog();
                        downloadNewApk(versionEntity.getApkurl());
                    }
                });
        // 设置暂不升级按钮点击事件
        builder.setNegativeButton("暂不升级",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        enterHome();
                    }
                });
        // 对话框必须调用show方法 否则不显示
        builder.show();
    }


    /**
     * 初始化进度条对话框
     */
    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("准备下载...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }

    /***
     * 下载新版本
     */
    protected void downloadNewApk(String apkurl) {
        DownLoadUtils downLoadUtils = new DownLoadUtils();
        downLoadUtils.downapk(apkurl, "/mnt/sdcard/mobilesafe2.0.apk", new MyCallBack() {

            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                // TODO Auto-generated method stub
                mProgressDialog.dismiss();
                MyUtils.installApk(context);
            }

            @Override
            public void onLoadding(long total, long current, boolean isUploading) {
                // TODO Auto-generated method stub
                mProgressDialog.setMax((int) total);
                mProgressDialog.setMessage("正在下载...");
                mProgressDialog.setProgress((int) current);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                mProgressDialog.setMessage("下载失败");
                mProgressDialog.dismiss();
                enterHome();
            }


        });


    }
    private void enterHome() {
        handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME, 2000);
    }
}
