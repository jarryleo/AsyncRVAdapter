package cn.leo.adapter_lib

import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.recyclerview.extensions.AsyncDifferConfig
import android.support.v7.recyclerview.extensions.AsyncListDiffer
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.*

/**
 * @author : Jarry Leo
 * @date : 2019/3/19 16:09
 */
abstract class AsyncRVAdapterKt<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mDiffer: AsyncListDiffer<T>
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemLongClickListener: OnItemLongClickListener? = null
    private var mOnItemChildClickListener: OnItemChildClickListener? = null
    private val diffCallback = object : DiffUtil.ItemCallback<T>() {

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            //是不是同一个item，不是则加到列表末尾
            return this@AsyncRVAdapterKt.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            //如果是同一个item，判断内容是不是相同，不相同则替换成新的
            return this@AsyncRVAdapterKt.areContentsTheSame(oldItem, newItem)
        }
    }

    private val mConfig = AsyncDifferConfig.Builder(diffCallback).build()

    /**
     * 设置新的数据集
     */
    var data: MutableList<T>
        get() = mDiffer.currentList
        set(data) = mDiffer.submitList(data)

    init {
        mDiffer = AsyncListDiffer(this, diffCallback)
    }

    /**
     * 异步比对去重，areItemsTheSame相同areContentsTheSame不同的则替换位置
     *
     * @param oldList 原列表
     * @param newList 新列表
     */
    private fun asyncAddData(oldList: MutableList<T>, newList: List<T>) {
        mConfig.backgroundThreadExecutor.execute {
            var change = false
            for (newItem in newList) {
                var flag = false
                for (i in oldList.indices) {
                    val oldItem = oldList[i]
                    if (!diffCallback.areItemsTheSame(oldItem, newItem)) {
                        continue
                    }
                    flag = diffCallback.areContentsTheSame(oldItem, newItem)
                    if (!flag) {
                        oldList[i] = newItem
                        change = true
                        flag = true
                    }
                    break
                }
                if (!flag) {
                    oldList.add(newItem)
                    change = true
                }
            }
            if (change) {
                mDiffer.submitList(oldList)
            }
        }
    }

    private fun asyncAddData(data: MutableList<T>) {
        asyncAddData(this.data, data)
    }

    private fun asyncAddData(data: T) {
        asyncAddData(this.data, listOf(data))
    }

    /**
     * 新增数据集
     *
     * @param data 数据集
     */
    fun addData(data: MutableList<T>) {
        asyncAddData(data)
    }

    /**
     * 新增单条数据
     *
     * @param data 数据
     */
    fun addData(data: T) {
        asyncAddData(data)
    }

    /**
     * 根据索引移除条目
     *
     * @param position 条目索引
     */
    fun removeData(position: Int) {
        if (position < 0 || position >= mDiffer.currentList.size) {
            return
        }
        this.data.removeAt(position)
        mDiffer.submitList(this.data)
    }

    /**
     * 根据对象移除条目
     * 对象必须重写equals
     *
     * @param data 条目对象
     */
    fun removeData(data: T) {
        this.data.remove(data)
        mDiffer.submitList(this.data)
    }

    /**
     * 清空列表
     */
    fun removeAll() {
        mDiffer.submitList(null)
    }

    /**
     * 获取索引位置对应的条目
     *
     * @param position 索引
     * @return 条目
     */
    fun getItem(position: Int): T {
        return mDiffer.currentList[position]
    }

    /**
     * 判断条目是不是相同
     *
     * @param oldItem 旧条目
     * @param newItem 新条目
     * @return 是否相同
     */
    protected open fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    /**
     * 判断内容是不是相同，
     * 如果条目相同，内容不同则替换条目
     *
     * @param oldItem 旧条目
     * @param newItem 新条目
     * @return 是否相同
     */
    protected open fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem === newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent, viewType)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AsyncRVAdapterKt<T>.ViewHolder).setData(position)
    }

    override fun getItemViewType(position: Int): Int {
        return getItemLayout(position)
    }

    /**
     * 获取条目类型的布局
     *
     * @param position 索引
     * @return 布局id
     */
    @LayoutRes
    protected abstract fun getItemLayout(position: Int): Int

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    /**
     * 给条目绑定数据
     *
     * @param helper 条目帮助类
     * @param data   对应数据
     */
    protected abstract fun bindData(helper: ItemHelper, data: T)


    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        /**
         * 点击条目
         *
         * @param adapter  当前适配器
         * @param v        点击的view
         * @param position 条目索引
         */
        fun onItemClick(adapter: AsyncRVAdapterKt<*>, v: View, position: Int)
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener
    }

    interface OnItemLongClickListener {
        /**
         * 长按点击条目
         *
         * @param adapter  当前适配器
         * @param v        点击的view
         * @param position 条目索引
         */
        fun onItemLongClick(adapter: AsyncRVAdapterKt<*>, v: View, position: Int)
    }

    fun setOnItemChildClickListener(onItemChildClickListener: OnItemChildClickListener) {
        mOnItemChildClickListener = onItemChildClickListener
    }

    interface OnItemChildClickListener {
        /**
         * 点击条目内部的view
         *
         * @param adapter  当前适配器
         * @param v        点击的view
         * @param position 条目索引
         */
        fun onItemChildClick(adapter: AsyncRVAdapterKt<*>, v: View, position: Int)
    }

    inner class ViewHolder internal constructor(parent: ViewGroup, layout: Int) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)), View.OnClickListener, View.OnLongClickListener {
        private val mItemHelper: ItemHelper = ItemHelper(itemView)

        init {
            mItemHelper.setLayoutResId(layout)
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        internal fun setData(position: Int) {
            mItemHelper.position = position
            bindData(mItemHelper, getItem(position))
        }

        override fun onClick(v: View) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(this@AsyncRVAdapterKt, v, mItemHelper.position)
            }
        }

        override fun onLongClick(v: View): Boolean {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener!!.onItemLongClick(this@AsyncRVAdapterKt, v, mItemHelper.position)
                return true
            }
            return false
        }
    }

    inner class ItemHelper(private val itemView: View) : View.OnClickListener {
        private val viewCache = SparseArray<View>()
        private val clickListenerCache = ArrayList<Int>()
        @LayoutRes
        @get:LayoutRes
        var itemLayout: Int = 0
            private set
        var position: Int = 0
            internal set

        internal fun setLayoutResId(@LayoutRes layoutResId: Int) {
            this.itemLayout = layoutResId
        }

        @Suppress("UNCHECKED_CAST")
        fun <V : View> getViewById(@IdRes viewId: Int): V {
            val v = viewCache.get(viewId)
            val view: V?
            if (v == null) {
                view = itemView.findViewById(viewId)
                if (view == null) {
                    val entryName = itemView.resources.getResourceEntryName(viewId)
                    throw NullPointerException("id: R.id.$entryName can not find in this item!")
                }
                viewCache.put(viewId, view)
            } else {
                view = v as V
            }
            return view
        }

        /**
         * 给按钮或文本框设置文字
         *
         * @param viewId 控件id
         * @param text   设置的文字
         */
        fun setText(@IdRes viewId: Int, text: CharSequence): ItemHelper {
            val view = getViewById<View>(viewId)
            if (view is TextView) {
                view.text = text
            } else {
                val entryName = view.resources.getResourceEntryName(viewId)
                throw ClassCastException("id: R.id.$entryName are not TextView")
            }
            return this
        }

        /**
         * 给按钮或文本框设置文字
         *
         * @param viewId 控件id
         * @param resId  设置的文字资源
         */
        fun setText(@IdRes viewId: Int, @StringRes resId: Int): ItemHelper {
            val view = getViewById<View>(viewId)
            if (view is TextView) {
                view.setText(resId)
            } else {
                val entryName = view.resources.getResourceEntryName(viewId)
                throw ClassCastException("id: R.id.$entryName are not TextView")
            }
            return this
        }

        /**
         * 给图片控件设置资源图片
         *
         * @param viewId 图片控件id
         * @param resId  资源id
         */
        fun setImageResource(@IdRes viewId: Int, @DrawableRes resId: Int): ItemHelper {
            val view = getViewById<View>(viewId)
            if (view is ImageView) {
                view.setImageResource(resId)
            } else {
                val entryName = view.resources.getResourceEntryName(viewId)
                throw ClassCastException("id: R.id.$entryName are not ImageView")
            }
            return this
        }

        /**
         * 设置view的背景
         *
         * @param viewId 控件id
         * @param resId  资源id
         */
        fun setBackgroundResource(@IdRes viewId: Int, @DrawableRes resId: Int): ItemHelper {
            val view = getViewById<View>(viewId)
            view.setBackgroundResource(resId)
            return this
        }


        fun setVisibility(@IdRes viewId: Int, visibility: Int): ItemHelper {
            val view = getViewById<View>(viewId)
            view.visibility = visibility
            return this
        }

        fun setViewVisble(@IdRes viewId: Int): ItemHelper {
            val view = getViewById<View>(viewId)
            view.visibility = View.VISIBLE
            return this
        }

        fun setViewInvisble(@IdRes viewId: Int): ItemHelper {
            val view = getViewById<View>(viewId)
            view.visibility = View.INVISIBLE
            return this
        }

        fun setViewGone(@IdRes viewId: Int): ItemHelper {
            val view = getViewById<View>(viewId)
            view.visibility = View.GONE
            return this
        }

        /**
         * 给条目中的view添加点击事件
         *
         * @param viewId 控件id
         */
        fun addOnClickListener(@IdRes viewId: Int): ItemHelper {
            val contains = clickListenerCache.contains(viewId)
            if (!contains) {
                getViewById<View>(viewId).setOnClickListener(this)
                clickListenerCache.add(viewId)
            }
            return this
        }

        override fun onClick(v: View) {
            if (mOnItemChildClickListener != null) {
                mOnItemChildClickListener!!.onItemChildClick(this@AsyncRVAdapterKt, v, position)
            }
        }
    }
}
