package com.example.sleep;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private WindowManager windowManager;
    private View myWindow;
    private WindowManager.LayoutParams layoutParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        checkPrivilege();
        if(getIntent().getBooleanExtra("isStart",false)) {
            setTimer(60000);
            showFloatingWindow();
        }
        else initView();
    }
    private void setTimer(int delay) {
        // 计时任务,锁定时间完成后程序自动退出
        sp = this.getSharedPreferences("lockerConfig",MODE_PRIVATE);
        editor = sp.edit();
        editor.putBoolean("isStart",true);
        editor.commit();
        Timer time = new Timer();
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                editor.putBoolean("isStart",false);
                editor.commit();
                AccessibilityManager abm = (AccessibilityManager) MainActivity.this.getSystemService(ACCESSIBILITY_SERVICE);
                abm.interrupt();  //强力模式退出
                System.exit(0);  //退出程序
            }
        }, delay);
    }
    private void showFloatingWindow() {
        // 设置悬浮窗属性
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        // 将全局悬浮窗控件添加到Window
        myWindow = LayoutInflater.from(this).inflate(R.layout.float_window,null);
        windowManager.addView(myWindow, layoutParams);
    }
    private void initView() {
        Button lockButton = findViewById(R.id.lock_button);
        Button enableButton = findViewById(R.id.buttonXMode);
        EditText timeDelay = findViewById(R.id.timeset);
        lockButton.setOnClickListener(v -> {
            // 设置锁定时间
            if(timeDelay.getText().toString().equals(""))
                setTimer(60000); // 默认一分钟
            else setTimer(Integer.parseInt(timeDelay.getText().toString()) * 60000);
            showFloatingWindow();
        });
        enableButton.setOnClickListener(v -> {
            xModeWarning();  //强力模式启用前警告
        });
    }
    private void checkPrivilege() {
        if(!Settings.canDrawOverlays(this)) {
            Toast.makeText(this,"请允许获取必要权限",Toast.LENGTH_LONG).show();  //检测必须权限
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }
    private void xModeWarning() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("警告");
        dialog.setMessage("强力模式需要无障碍服务权限,请在无障碍服务中找到 已下载的服务 开启Locker权限");
        dialog.setPositiveButton("开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }
}

