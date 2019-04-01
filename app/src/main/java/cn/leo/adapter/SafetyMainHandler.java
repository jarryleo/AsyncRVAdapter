package cn.leo.adapter;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Handler;
import android.os.Looper;

/**
 * @author : Jarry Leo
 * @date : 2019/2/26 10:54
 */
public class SafetyMainHandler extends Handler implements LifecycleObserver {

    public SafetyMainHandler(Activity activity) {
        super(Looper.getMainLooper());
        bindLifecycle(activity);
    }

    public SafetyMainHandler(Activity activity, Callback callback) {
        super(Looper.getMainLooper(), callback);
        bindLifecycle(activity);
    }

    private void bindLifecycle(Activity activity) {
        if (activity instanceof LifecycleOwner) {
            ((LifecycleOwner) activity).getLifecycle().addObserver(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        this.removeCallbacksAndMessages(null);
    }


}
