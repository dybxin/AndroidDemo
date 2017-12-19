package com.example.mydemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Stack;

/**
 * 管理所有activity的生命周期
 */

public class ActivityManager implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "ActivityManager";
    private boolean showLog = true;
    private static Stack<Activity> mActivityStack;
    private int mStageActivityCount = 0; //前台activity数
    private Activity mStageActivity;//当前的Activity

    public ActivityManager() {
        mActivityStack = new Stack<>();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (showLog) {
            Log.i(TAG, "onActivityCreated :" + activity);
        }
        addActivity(activity);
    }

    /**
     * 添加Activity到堆栈
     */
    private void addActivity(Activity activity) {
        mActivityStack.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (showLog) {
            Log.i(TAG, "onActivityStarted :" + activity);
        }
        mStageActivityCount++;
        mStageActivity = activity;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        if (showLog) {
            Log.i(TAG, "onActivitySaveInstanceState :" + activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (showLog) {
            Log.i(TAG, "onActivityPaused :" + activity);
        }

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (showLog) {
            Log.i(TAG, "onActivityResumed :" + activity);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (showLog) {
            Log.i(TAG, "onActivityStopped :" + activity);
        }
        mStageActivityCount--;
        if (mStageActivity == activity) {
            mStageActivity = null;
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (showLog) {
            Log.i(TAG, "onActivityDestroyed :" + activity);
        }
        removeActivity(activity);
    }

    private boolean removeActivity(Activity activity) {
        return activity != null && mActivityStack.remove(activity);
    }

    /**
     * 获取指定的Activity
     */
    public static Activity getActivity(Class<?> cls) {
        if (mActivityStack != null) {
            for (Activity activity : mActivityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        }
        return null;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        return mActivityStack.lastElement();
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = mActivityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            mActivityStack.remove(activity);
            activity.finish();
            activity = null;
            mStageActivityCount--;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity != null && !activity.isFinishing() && activity.getClass().equals(cls)) {
                iterator.remove();
                activity.finish();
                activity = null;
                mStageActivityCount--;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity(){
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i)) {
                //finishActivity方法中的activity.isFinishing()方法会导致某些activity无法销毁
                //貌似跳转的时候最后一个activity 是finishing状态，所以没有执行
                //内部实现不是很清楚，但是实测结果如此，使用下面代码则没有问题
                // find by TopJohn
                //agreeFriendSuccess(activityStack.get(i));

                mActivityStack.get(i).finish();
                //break;
            }
        }
        mActivityStack.clear();
        mStageActivityCount = 0;
    }

    /**
     * 判断应用是否处于前台
     *
     */
    public boolean isStagApp() {
        return mStageActivityCount > 0;
    }

    /**
     * 获取当前获得焦点的activity
     *
     */
    public Activity getStageActivity() {
        return mStageActivity;
    }
    /**
     * 重启应用程序
     */
    public void resetApp() {
        Intent i = AppApplication.context().getPackageManager()
                .getLaunchIntentForPackage(AppApplication.context().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        AppApplication.context().startActivity(i);
        AppExit();
    }

    public void AppExit() {
        try {
            finishAllActivity();
            // 杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
