package cn.leo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * @author : Jarry Leo
 * @date : 2019/3/29 14:01
 */
public class StatePager {
    private Context mContext;
    private View mView;
    private View mTarget;
    private View mReplace;
    private int mLoadingId = View.NO_ID;
    private int mEmptyId = View.NO_ID;
    private int mErrorId = View.NO_ID;
    private int mClickId = View.NO_ID;
    private View.OnClickListener mOnClickListener;

    private StatePager(View view) {
        mView = view;
        mContext = view.getContext();
    }

    public static StatePager getInstance(@NonNull View view) {
        return new StatePager(view);
    }

    public static StatePager getInstance(@NonNull Activity activity) {
        return new StatePager(activity.getWindow().getDecorView());
    }

    public static StatePager getInstance(@NonNull Fragment fragment) {
        if (fragment.getView() == null) {
            throw new NullPointerException("fragment not attach");
        }
        return new StatePager(fragment.getView());
    }

    public static StatePager getInstance(@NonNull android.app.Fragment fragment) {
        if (fragment.getView() == null) {
            throw new NullPointerException("fragment not attach");
        }
        return new StatePager(fragment.getView());
    }

    /**
     * 替代view位置展示不同状态page
     * 如果view 的父view 是
     */
    public StatePager successViewId(@IdRes int id) {
        mTarget = getViewById(id);
        return this;
    }

    public StatePager loadingViewId(@IdRes int id) {
        mLoadingId = id;
        return this;
    }

    public StatePager emptyViewId(@IdRes int id) {
        mEmptyId = id;
        return this;
    }

    public StatePager errorViewId(@IdRes int id) {
        mErrorId = id;
        return this;
    }

    public StatePager setRetryButtonId(@IdRes int id) {
        mClickId = id;
        return this;
    }

    public StatePager setRetryClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
        return this;
    }

    public void showLoading() {
        replaceView(mLoadingId);
    }

    public void showEmpty() {
        replaceView(mLoadingId);
    }

    public void showError() {
        replaceView(mLoadingId);
        View clickView = getViewById(mReplace, mClickId);
        if (clickView != null) {
            clickView.setOnClickListener(mOnClickListener);
        }
    }

    /**
     * 根据id查找view
     */
    private View getViewById(@IdRes int viewId) {
        View view = mView.findViewById(viewId);
        if (view == null) {
            String entryName = mView.getResources().getResourceEntryName(viewId);
            throw new NullPointerException("id: R.id." + entryName + " can not find in this view!");
        }
        return view;
    }


    private View getViewById(@NonNull View view, @IdRes int viewId) {
        return view.findViewById(viewId);
    }

    private void replaceView(int layoutRes) {
        if (mReplace != null) {
            int id = mReplace.getId();
            if (id == layoutRes) {
                return;
            }
        }
        ViewGroup parent = (ViewGroup) mTarget.getParent();
        parent.removeView(mReplace);
        View inflate = LayoutInflater.from(mContext).inflate(layoutRes, null);
        inflate.setLayoutParams(mView.getLayoutParams());
        inflate.setId(layoutRes);
        mReplace = inflate;
        int index = parent.indexOfChild(mTarget);
        parent.addView(mReplace, index);
        if (parent instanceof RelativeLayout) {
            mTarget.setVisibility(View.INVISIBLE);
        } else {
            mTarget.setVisibility(View.GONE);
        }
    }
}
