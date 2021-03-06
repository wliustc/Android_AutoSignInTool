package com.pyy.signin;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.concurrent.TimeUnit;

import static com.pyy.signin.SignInService.autoCondition;
import static com.pyy.signin.SignInService.autoLock;
import static com.pyy.signin.Utils.delay;

/**
 * Created by pyy on 2017/8/9.
 */

public class autoSignInJDF {
    public boolean gestureLockFlag = false;
    private boolean found = false;

    public void doJDF(AccessibilityService service) {
        autoLock.lock();
        boolean ret;
        try {
            delay(10000);
            if (checkGestureLock(service.getRootInActiveWindow())) {
                ret = autoCondition.await(20, TimeUnit.SECONDS); // 20s
                if (!ret) {
                    autoLock.unlock();
                    MainPage.condition.signal();
                    return;
                }
            }
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            delay(1000);
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            delay(1000);
            iteratorJDF(service.getRootInActiveWindow());  // 主页签到
            delay(6000);
            iteratorJDF(service.getRootInActiveWindow());  // 右上角签到
            ret = autoCondition.await(20, TimeUnit.SECONDS); // 10s
            //iteratorJDF(service.getRootInActiveWindow());  // 右上角钢蹦明细
            //ret = autoCondition.await(30, TimeUnit.SECONDS); // 30s
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            delay(1000);
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            delay(1000);
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            delay(1000);
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            delay(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        autoLock.unlock();
        MainPage.condition.signal();
    }

    private boolean checkGestureLock(AccessibilityNodeInfo node) {
        iteratorJDF(node);
        return gestureLockFlag;
    }

    private void iteratorJDF(AccessibilityNodeInfo info) {
        if (info.getText() != null) {
            if (info.getText().equals("忘记手势密码")) {
                gestureLockFlag =  true;
                return;
            }

            /*
            if (info.getText().toString().contains("钢镚明细")) {
                if (info.isClickable() && info.findAccessibilityNodeInfosByViewId("com.jd.jrapp:id/btn_feedback_summit") != null) {
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    found = true;
                    return;
                }
            }
            */

            if (info.getText().equals("签到")) {
                AccessibilityNodeInfo parent = info.getParent();
                if (info.findAccessibilityNodeInfosByViewId("com.jd.jrapp:id/tv_item_text") != null
                        && "android.widget.RelativeLayout".equals(parent.getClassName())
                        && parent.isClickable()) {
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    found = true;
                    return;
                }
                if (info.isClickable() && info.findAccessibilityNodeInfosByViewId("com.jd.jrapp:id/btn_feedback_summit") != null) {
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    found = true;
                    return;
                }
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i) != null){
                    if (gestureLockFlag || found) {
                        found = false;
                        break;
                    }
                    iteratorJDF(info.getChild(i));
                }
            }
        }
        return ;
    }


}
