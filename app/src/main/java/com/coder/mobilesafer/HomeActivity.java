package com.coder.mobilesafer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.coder.mobilesafe.R;

public class HomeActivity extends AppCompatActivity {

    private GridView gridView;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView= (GridView) findViewById(R.id.gv_home);

        //填充GridView
        gridView.setAdapter(new HomeAdapter(HomeActivity.this));
        //单击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0://手机防盗

                        break;
                    case 1://通讯卫士
                        break;
                    case 2://软件管家
                        break;
                    case 3://病毒查杀
                        break;
                    case 4://缓存清理
                        break;
                    case 5://进程管理
                        break;
                    case 6://流量统计
                        break;
                    case 7://高级工具
                        break;
                    case 8://设置中心
                        break;
                }
            }
        });

    }

    //按两次返回键退出程序

    /**
     * Take care of calling onBackPressed() for pre-Eclair platforms.
     *
     * @param keyCode
     * @param event
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if((System.currentTimeMillis()-mExitTime)<2000){
                System.exit(0);
            }else{
                Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                mExitTime=System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
