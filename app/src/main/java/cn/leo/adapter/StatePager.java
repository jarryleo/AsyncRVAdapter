package cn.leo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
    private Builder mBuilder;

    private StatePager(Builder builder) {
        mBuilder = builder;
    }

    public static Builder builder(@NonNull View view) {
        return new Builder(view);
    }

    public static Builder builder(@NonNull Activity activity) {
        return new Builder(activity.getWindow().getDecorView());
    }

    public static Builder builder(@NonNull Fragment fragment) {
        if (fragment.getView() == null) {
            throw new NullPointerException("fragment not attach");
        }
        return new Builder(fragment.getView());
    }

    public static Builder builder(@NonNull android.app.Fragment fragment) {
        if (fragment.getView() == null) {
            throw new NullPointerException("fragment not attach");
        }
        return new Builder(fragment.getView());
    }

    public static class Builder {
        private Context mContext;
        private View mView;
        private View mTarget;
        private View mReplace;
        private int mLoadingId = View.NO_ID;
        private int mEmptyId = View.NO_ID;
        private int mErrorId = View.NO_ID;
        private int mClickId = View.NO_ID;
        private View.OnClickListener mOnClickListener;

        private Builder(View view) {
            mView = view;
            mContext = view.getContext();
        }


        /**
         * 替代view位置展示不同状态page
         * 如果view 的父view 是
         */
        public Builder successViewId(@IdRes int id) {
            mTarget = getViewById(id);
            return this;
        }

        /**
         * 加载页布局资源id
         */
        public Builder loadingViewLayout(@LayoutRes int layoutRes) {
            mLoadingId = layoutRes;
            return this;
        }

        /**
         * 空页面布局资源id
         */
        public Builder emptyViewLayout(@LayoutRes int layoutRes) {
            mEmptyId = layoutRes;
            return this;
        }

        /**
         * 错误页面布局资源id
         */
        public Builder errorViewLayout(@LayoutRes int layoutRes) {
            mErrorId = layoutRes;
            return this;
        }

        /**
         * 重试按钮id
         */
        public Builder setRetryButtonId(@IdRes int id) {
            mClickId = id;
            return this;
        }

        public Builder setRetryClickListener(View.OnClickListener listener) {
            mOnClickListener = listener;
            return this;
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

        public StatePager build() {
            if (mTarget == null) {
                throw new NullPointerException("successViewId must set!");
            }
            return new StatePager(this);
        }
    }

    public View showLoading() {
        if (mBuilder.mLoadingId == View.NO_ID) {
            throw new NullPointerException("loading layout is invalid");
        }
        replaceView(mBuilder.mLoadingId);
        setClick();
        return mBuilder.mReplace;
    }

    public View showEmpty() {
        if (mBuilder.mEmptyId == View.NO_ID) {
            throw new NullPointerException("empty layout is invalid");
        }
        replaceView(mBuilder.mEmptyId);
        setClick();
        return mBuilder.mReplace;
    }

    public View showError() {
        if (mBuilder.mErrorId == View.NO_ID) {
            throw new NullPointerException("error layout is invalid");
        }
        replaceView(mBuilder.mErrorId);
        setClick();
        return mBuilder.mReplace;
    }

    public void showSuccess() {
        mBuilder.mTarget.setVisibility(View.VISIBLE);
        mBuilder.mReplace.setVisibility(View.GONE);
        mBuilder.mReplace = null;
    }

    private void setClick() {
        View clickView = getViewById(mBuilder.mReplace, mBuilder.mClickId);
        if (clickView != null) {
            clickView.setOnClickListener(mBuilder.mOnClickListener);
        }
    }


    private View getViewById(@NonNull View view, @IdRes int viewId) {
        return view.findViewById(viewId);
    }

    private void replaceView(int layoutRes) {
        if (mBuilder.mTarget == null) {
            throw new NullPointerException("success view id is invalid");
        }
        if (mBuilder.mReplace != null) {
            int id = mBuilder.mReplace.getId();
            if (id == layoutRes) {
                return;
            }
        }
        ViewGroup parent = (ViewGroup) mBuilder.mTarget.getParent();
        parent.removeView(mBuilder.mReplace);
        View inflate = LayoutInflater.from(mBuilder.mContext).inflate(layoutRes, null);
        inflate.setLayoutParams(mBuilder.mTarget.getLayoutParams());
        inflate.setId(layoutRes);
        mBuilder.mReplace = inflate;
        int index = parent.indexOfChild(mBuilder.mTarget);
        parent.addView(mBuilder.mReplace, index);
        if (parent instanceof RelativeLayout ||
                parent instanceof ConstraintLayout) {
            mBuilder.mTarget.setVisibility(View.INVISIBLE);
        } else {
            mBuilder.mTarget.setVisibility(View.GONE);
        }
    }
}
