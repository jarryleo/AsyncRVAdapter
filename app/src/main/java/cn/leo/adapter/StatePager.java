package cn.leo.adapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Jarry Leo
 * @date : 2019/3/29 14:01
 */
public class StatePager implements View.OnClickListener {
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
        private OnClickListener mOnClickListener;
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

        public Builder setRetryClickListener(OnClickListener listener) {
            mOnClickListener = listener;
            return this;
        }

        public StatePager build() {
            return new StatePager(this);
        }
    }

    public ViewHelper showLoading() {
        if (mBuilder.mLoadingId == View.NO_ID) {
            throw new NullPointerException("loading layout is invalid");
        }
        replaceView(mBuilder.mLoadingId);
        setClick();
        return new ViewHelper(mBuilder.mReplace);
    }

    public ViewHelper showEmpty() {
        if (mBuilder.mEmptyId == View.NO_ID) {
            throw new NullPointerException("empty layout is invalid");
        }
        replaceView(mBuilder.mEmptyId);
        setClick();
        return new ViewHelper(mBuilder.mReplace);
    }

    public ViewHelper showError() {
        if (mBuilder.mErrorId == View.NO_ID) {
            throw new NullPointerException("error layout is invalid");
        }
        replaceView(mBuilder.mErrorId);
        setClick();
        return new ViewHelper(mBuilder.mReplace);
    }

    public void showSuccess() {
        mBuilder.mTarget.setVisibility(View.VISIBLE);
        if (mBuilder.mReplace != null) {
            mBuilder.mReplace.setVisibility(View.GONE);
            ViewGroup parent = (ViewGroup) mBuilder.mTarget.getParent();
            parent.removeView(mBuilder.mReplace);
            mBuilder.mReplace = null;
        }
    }

    private void setClick() {
        if (mBuilder.mClickIds == null) {
            return;
        }
        for (Integer clickId : mBuilder.mClickIds) {
            View clickView = getViewById(mBuilder.mReplace, clickId);
            if (clickView != null) {
                clickView.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        mBuilder.mOnClickListener.onClick(this, v);
    }

    private View getViewById(@NonNull View view, @IdRes int viewId) {
        return view.findViewById(viewId);
    }

    private void replaceView(int layoutRes) {
        if (mBuilder.mReplace != null) {
            int id = mBuilder.mReplace.getId();
            if (id == layoutRes) {
                mBuilder.mReplace.setVisibility(View.VISIBLE);
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
        mBuilder.mReplace.setVisibility(View.VISIBLE);
    }

    public interface OnClickListener {
        void onClick(StatePager statePager, View v);
    }


    public static class ViewHelper {
        private View mView;

        public ViewHelper(View view) {
            mView = view;
        }

        public final <V extends View> V findViewById(@IdRes int viewId) {
            V view = mView.findViewById(viewId);
            if (view == null) {
                String entryName = mView.getResources().getResourceEntryName(viewId);
                throw new NullPointerException("id: R.id." + entryName + " can not find in this item!");
            }
            return view;
        }

        /**
         * 给按钮或文本框设置文字
         *
         * @param viewId 控件id
         * @param text   设置的文字
         */
        public ViewHelper setText(@IdRes int viewId, CharSequence text) {
            View view = findViewById(viewId);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            } else {
                String entryName = view.getResources().getResourceEntryName(viewId);
                throw new ClassCastException("id: R.id." + entryName + " are not TextView");
            }
            return this;
        }

        /**
         * 给按钮或文本框设置文字
         *
         * @param viewId 控件id
         * @param resId  设置的文字资源
         */
        public ViewHelper setText(@IdRes int viewId, @StringRes int resId) {
            View view = findViewById(viewId);
            if (view instanceof TextView) {
                ((TextView) view).setText(resId);
            } else {
                String entryName = view.getResources().getResourceEntryName(viewId);
                throw new ClassCastException("id: R.id." + entryName + " are not TextView");
            }
            return this;
        }

        /**
         * 给图片控件设置资源图片
         *
         * @param viewId 图片控件id
         * @param resId  资源id
         */
        public ViewHelper setImageResource(@IdRes int viewId, @DrawableRes int resId) {
            View view = findViewById(viewId);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(resId);
            } else {
                String entryName = view.getResources().getResourceEntryName(viewId);
                throw new ClassCastException("id: R.id." + entryName + " are not ImageView");
            }
            return this;
        }

        /**
         * 设置view的背景
         *
         * @param viewId 控件id
         * @param resId  资源id
         */
        public ViewHelper setBackgroundResource(@IdRes int viewId, @DrawableRes int resId) {
            View view = findViewById(viewId);
            view.setBackgroundResource(resId);
            return this;
        }


        public ViewHelper setVisibility(@IdRes int viewId, int visibility) {
            View view = findViewById(viewId);
            view.setVisibility(visibility);
            return this;
        }

        public ViewHelper setViewVisble(@IdRes int viewId) {
            View view = findViewById(viewId);
            view.setVisibility(View.VISIBLE);
            return this;
        }

        public ViewHelper setViewInvisble(@IdRes int viewId) {
            View view = findViewById(viewId);
            view.setVisibility(View.INVISIBLE);
            return this;
        }

        public ViewHelper setViewGone(@IdRes int viewId) {
            View view = findViewById(viewId);
            view.setVisibility(View.GONE);
            return this;
        }

        public ViewHelper setOnClickListener(@IdRes int viewId, View.OnClickListener onClickListener) {
            View view = findViewById(viewId);
            view.setOnClickListener(onClickListener);
            return this;
        }
    }
}
