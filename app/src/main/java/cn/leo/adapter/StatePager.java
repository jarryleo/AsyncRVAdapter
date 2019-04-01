package cn.leo.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Jarry Leo
 * @date : 2019/3/29 14:01
 */
public class StatePager {
    private Builder mBuilder;

    private StatePager(Builder builder) {
        mBuilder = builder;
    }

    /**
     * 传入要替换展示状态的view
     */
    public static Builder builder(@NonNull View replaceView) {
        return new Builder(replaceView);
    }

    public static class Builder {
        private Context mContext;
        private View mTarget;
        private View mReplace;
        private int mLoadingId = View.NO_ID;
        private int mEmptyId = View.NO_ID;
        private int mErrorId = View.NO_ID;
        private List<Integer> mClickIds;
        private View.OnClickListener mOnClickListener;
        private boolean mIsRelative;

        private Builder(View view) {
            mTarget = view;
            mContext = view.getContext();
            initSuccessView();
        }


        /**
         * 替代view位置展示不同状态page
         * 如果view 的父view 是
         */
        private void initSuccessView() {
            ViewGroup parent = (ViewGroup) mTarget.getParent();
            mIsRelative = (parent instanceof RelativeLayout ||
                    parent instanceof ConstraintLayout);
            if (parent == null) {
                if (mTarget instanceof ViewGroup) {
                    parent = (ViewGroup) mTarget;
                    mTarget = parent.getChildAt(0);
                } else {
                    return;
                }
            }
            if (!mIsRelative) {
                FrameLayout frameLayout = new FrameLayout(mContext);
                frameLayout.setLayoutParams(mTarget.getLayoutParams());
                int index = parent.indexOfChild(mTarget);
                parent.removeView(mTarget);
                frameLayout.addView(mTarget);
                parent.addView(frameLayout, index);
            }
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
        public Builder addRetryButtonId(@IdRes int id) {
            if (mClickIds == null) {
                mClickIds = new ArrayList<>();
            }
            mClickIds.add(id);
            return this;
        }

        public Builder setRetryClickListener(View.OnClickListener listener) {
            mOnClickListener = listener;
            return this;
        }

        public StatePager build() {
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
        ViewGroup parent = (ViewGroup) mBuilder.mTarget.getParent();
        parent.removeView(mBuilder.mReplace);
        mBuilder.mReplace = null;
    }

    private void setClick() {
        if (mBuilder.mClickIds == null) {
            return;
        }
        for (Integer clickId : mBuilder.mClickIds) {
            View clickView = getViewById(mBuilder.mReplace, clickId);
            if (clickView != null) {
                clickView.setOnClickListener(mBuilder.mOnClickListener);
            }
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
        if (mBuilder.mIsRelative) {
            mBuilder.mTarget.setVisibility(View.INVISIBLE);
        } else {
            mBuilder.mTarget.setVisibility(View.GONE);
        }
    }
}
