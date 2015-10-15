package com.coder.mobilesafer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.coder.mobilesafe.R;
import com.coder.mobilesafer.utils.MyUtils;
import com.coder.mobilesafer.utils.UpdateVersionUtils;

public class SplashActivity extends AppCompatActivity {

    //本地版本号
    private String mVersion;

    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
//                startActivity(i);
//
//                //启动主Activity后销毁自身
//                finish();
//            }
//        }, 3000);
        mVersion= MyUtils.getVersion(getApplicationContext());

        //初始化控件
        initView();

        final UpdateVersionUtils updateUtils = new UpdateVersionUtils(mVersion,
                SplashActivity.this);
        new Thread() {
            public void run() {
                // 获取服务器版本号
                updateUtils.getCloudVersion();
            }
        }.start();

    }

    private void initView() {
        tvVersion= (TextView) findViewById(R.id.tv_version);
        tvVersion.setText("版本号:"+mVersion);

    }


}
