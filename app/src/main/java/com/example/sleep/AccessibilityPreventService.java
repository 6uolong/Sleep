package com.example.sleep;


import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.*;
import android.widget.Toast;


public class AccessibilityPreventService extends AccessibilityService {
    public static AccessibilityPreventService mService;
    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 检测到下拉通知栏,锁屏
        this.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
        Toast.makeText(this,"强力模式下不能下拉菜单栏",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this,"强力模式已退出",Toast.LENGTH_LONG).show();
        this.disableSelf();
    }

}
