package cn.leo.adapter_lib

import android.os.Handler
import android.os.Looper
import android.support.annotation.*
import android.support.v7.recyclerview.extensions.AsyncListDiffer
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.concurrent.Executor
import java.util.concurrent.Executors


/**
 * 方便快速开发RecyclerView的adapter
 *
 * @author : Jarry Leo
 * @date : 2019/3/19 16:09
 */
abstract class AsyncRvAdapterKt<T : Any> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mAutoLoadMore = true
    private val mDiffer: AsyncListDiffer<T>
    private lateinit var mOnLoadMoreListener:
            (adapter: AsyncRvAdapterKt<T>, lastItemPosition: Int) -> Boolean
    private lateinit var mOnItemClickListener:
            (adapter: AsyncRvAdapterKt<T>, v: View, position: Int) -> Unit
    private lateinit var mOnItemLongClickListener:
            (adapter: AsyncRvAdapterKt<T>, v: View, position: Int) -> Unit
    private lateinit var mOnItemChildClickListener:
            (adapter: AsyncRvAdapterKt<out Any>, v: View, position: Int) -> Unit
    private val diffCallback = object : DiffUtil.ItemCallback<T>() {

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            //是不是同一个item，不是则加到列表末尾
            return this@AsyncRvAdapterKt.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            //如果是同一个item，判断内容是不是相同，不相同则替换成新的
            return this@AsyncRvAdapterKt.areContentsTheSame(oldItem, newItem)
        }
    }

    val mOnItemChildClickListenerProxy:
            (adapter: AsyncRvAdapterKt<out Any>, v: View, position: Int) -> Unit =
            { adapter, v, position ->
                if (::mOnItemChildClickListener.isInitialized) {
                    mOnItemChildClickListener(adapter, v, position)
                }
            }

    /**
     * 获取和设置新的数据集
     */
    var data: MutableList<T>
        get() = mDiffer.currentList.toMutableList()
        set(list) {
            mAutoLoadMore = true
            asyncSubmitList(list)
        }

    /**
     * 编辑单个数据
     */
    fun edit(position: Int, call: (t: T) -> Unit) {
        call(getItem(position))
        notifyItemChanged(position)
    }


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
        sDiffExecutor.execute {
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
                asyncSubmitList(oldList)
            }
        }
    }

    private fun asyncAddData(item: List<T>) {
        val oldList = data
        asyncAddData(oldList, item)
    }

    private fun asyncAddData(item: T) {
        val oldList = data
        val newList = ArrayList<T>()
        newList.add(item)
        asyncAddData(oldList, newList)
    }

    /**
     * 异步提交数据
     */
    private fun asyncSubmitList(item: List<T>?) {
        sMainThreadExecutor.execute { mDiffer.submitList(item) }
    }

    /**
     * 新增数据集
     *
     * @param newData 数据集
     */
    fun addData(newData: List<T>) {
        asyncAddData(newData)
    }

    /**
     * 新增单条数据
     *
     * @param newData 数据
     */
    fun addData(newData: T) {
        asyncAddData(newData)
    }

    /**
     * 根据索引移除条目
     * 暂时不能连续移除，因为异步操作，会导致数据不准
     *
     * @param position 条目索引
     */
    fun removeData(position: Int) {
        if (position < 0 || position >= mDiffer.currentList.size) {
            return
        }
        val list = data
        list.removeAt(position)
        asyncSubmitList(list)
    }

    /**
     * 根据对象移除条目
     * 对象必须重写equals
     *
     * @param item 条目对象
     */
    fun removeData(item: T) {
        val list = data
        list.remove(item)
        asyncSubmitList(list)
    }

    /**
     * 清空列表
     */
    fun removeAll() {
        asyncSubmitList(null)
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
    open fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
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
    open fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem === newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AsyncRvAdapterKt<out Any>.ViewHolder).setData(position)
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

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        val viewHolder = holder as AsyncRvAdapterKt<out Any>.ViewHolder
        val helper = viewHolder.itemHelper
        val itemHolder = helper.mItemHolder
        itemHolder?.onViewDetach(helper)
    }

    /**
     * 给条目绑定数据
     *
     * @param helper 条目帮助类
     * @param item   对应数据
     */
    protected abstract fun bindData(helper: ItemHelper, item: T)

    fun setOnItemClickListener(onItemClickListener:
                               (adapter: AsyncRvAdapterKt<out Any>, v: View, position: Int) -> Unit) {
        mOnItemClickListener = onItemClickListener
    }

    fun setLoadMoreListener(onLoadMoreListener:
                            (adapter: AsyncRvAdapterKt<out Any>, lastItemPosition: Int) -> Boolean) {
        mOnLoadMoreListener = onLoadMoreListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener:
                                   (adapter: AsyncRvAdapterKt<out Any>, v: View, position: Int) -> Unit) {
        mOnItemLongClickListener = onItemLongClickListener
    }

    fun setOnItemChildClickListener(onItemChildClickListener:
                                    (adapter: AsyncRvAdapterKt<out Any>, v: View, position: Int) -> Unit) {
        mOnItemChildClickListener = onItemChildClickListener
    }

    private class MainThreadExecutor : Executor {
        internal val mHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mHandler.post(command)
        }
    }

    /**
     * 多条目类型防止 adapter臃肿，每个条目请继承此类
     *
     * @param <T> 数据类型
    </T> */
    abstract class ItemHolder<T : Any> {

        /**
         * 绑定数据
         *
         * @param helper 帮助类
         * @param item   数据
         */
        abstract fun bindData(helper: ItemHelper, item: T)

        /**
         * 初始化view，只在view第一次创建调用
         *
         * @param helper 帮助类
         * @param item   数据
         */
        fun initView(helper: ItemHelper, item: T) {}


        /**
         * 被回收时调用，用来释放一些资源，或者重置数据等
         *
         * @param helper 帮助类
         */
        fun onViewDetach(helper: ItemHelper) {}
    }

    class ItemHelper(val itemView: View) : View.OnClickListener {
        private val viewCache = SparseArray<View>()
        private val clickListenerCache = ArrayList<Int>()
        private val mTags = HashMap<String, Any>()
        var adapter: AsyncRvAdapterKt<out Any>? = null
            private set
        @LayoutRes
        @get:LayoutRes
        var itemLayout: Int = 0
        var position: Int = 0
        /**
         * 携带额外绑定数据便于复用
         */
        var tag: Any? = null
        private lateinit var mOnItemChildClickListener:
                (adapter: AsyncRvAdapterKt<out Any>, v: View, position: Int) -> Unit

        fun setLayoutResId(@LayoutRes layoutResId: Int) {
            this.itemLayout = layoutResId
        }

        fun setOnItemChildClickListener(onItemChildClickListener:
                                        (adapter: AsyncRvAdapterKt<out Any>, v: View, position: Int) -> Unit) {
            mOnItemChildClickListener = onItemChildClickListener
        }

        fun setRVAdapter(RVAdapter: AsyncRvAdapterKt<out Any>) {
            adapter = RVAdapter
        }

        fun setTag(key: String, tag: Any) {
            mTags[key] = tag
        }

        fun getTag(key: String): Any {
            return mTags[key]!!
        }

        fun <V : View> getViewById(@IdRes viewId: Int, call: (V) -> Unit = {}): ItemHelper {
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
            call(view)
            return this
        }

        /**
         * 给按钮或文本框设置文字
         *
         * @param viewId 控件id
         * @param text   设置的文字
         */
        fun setText(@IdRes viewId: Int, text: CharSequence): ItemHelper {
            getViewById<View>(viewId) {
                if (it is TextView) {
                    it.text = text
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not TextView")
                }
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
            getViewById<View>(viewId) {
                if (it is TextView) {
                    it.text = try {
                        it.resources.getString(resId)
                    } catch (e: Exception) {
                        resId.toString()
                    }
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not TextView")
                }
            }
            return this
        }

        /**
         * 设置文本颜色
         *
         * @param viewId 要设置文本的控件，TextView及其子类都可以
         * @param color  颜色int值，不是资源Id
         */
        fun setTextColor(@IdRes viewId: Int, @ColorInt color: Int): ItemHelper {
            getViewById<View>(viewId) {
                if (it is TextView) {
                    it.setTextColor(color)
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not TextView")
                }
            }
            return this
        }

        /**
         * 设置文本颜色
         *
         * @param viewId     要设置文本的控件，TextView及其子类都可以
         * @param colorResId 颜色资源Id
         */
        fun setTextColorRes(@IdRes viewId: Int, @ColorRes colorResId: Int): ItemHelper {
            getViewById<View>(viewId) {
                if (it is TextView) {
                    it.setTextColor(it.getResources().getColor(colorResId))
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not TextView")
                }
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
            getViewById<View>(viewId) {
                if (it is ImageView) {
                    it.setImageResource(resId)
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not ImageView")
                }
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
            getViewById<View>(viewId){
                it.setBackgroundResource(resId)
            }
            return this
        }

        fun setVisibility(@IdRes viewId: Int, visibility: Int): ItemHelper {
            getViewById<View>(viewId){
                it.visibility = visibility
            }
            return this
        }

        fun setViewVisible(@IdRes vararg viewId: Int): ItemHelper {
            for (id in viewId) {
                getViewById<View>(id){
                    it.visibility = View.VISIBLE
                }
            }
            return this
        }

        fun setViewInvisible(@IdRes vararg viewId: Int): ItemHelper {
            for (id in viewId) {
                getViewById<View>(id){
                    it.visibility = View.INVISIBLE
                }
            }
            return this
        }

        fun setViewGone(@IdRes vararg viewId: Int): ItemHelper {
            for (id in viewId) {
                getViewById<View>(id){
                    it.visibility = View.GONE
                }
            }
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
                getViewById<View>(viewId){it.setOnClickListener(this)}
                clickListenerCache.add(viewId)
            }
            return this
        }

        override fun onClick(v: View) {
            if (::mOnItemChildClickListener.isInitialized) {
                mOnItemChildClickListener(adapter!!, v, position)
            }
        }

        var mItemHolder: ItemHolder<Any>? = null
        fun setItemHolder(itemHolderClass: Class<out ItemHolder<out Any>>) {
            try {
                if (mItemHolder == null) {
                    val newInstance = itemHolderClass.newInstance()
                    mItemHolder = newInstance as ItemHolder<Any>?
                    mItemHolder!!.initView(this, adapter!!.getItem(position))
                }
                mItemHolder!!.bindData(this, adapter!!.getItem(position))
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }
    }

    inner class ViewHolder internal constructor(parent: ViewGroup, layout: Int) :
            RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(layout, parent, false)),
            View.OnClickListener,
            View.OnLongClickListener {
        val itemHelper: ItemHelper = ItemHelper(itemView)

        init {
            itemHelper.setLayoutResId(layout)
            itemHelper.setOnItemChildClickListener(mOnItemChildClickListenerProxy)
            itemHelper.setRVAdapter(this@AsyncRvAdapterKt)
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        fun setData(position: Int) {
            itemHelper.position = position
            bindData(itemHelper, getItem(position))
            if (::mOnLoadMoreListener.isInitialized && mAutoLoadMore && position == itemCount - 1) {
                mAutoLoadMore = mOnLoadMoreListener(
                        this@AsyncRvAdapterKt, itemCount - 1)
            }
        }

        override fun onClick(v: View) {
            if (::mOnItemClickListener.isInitialized) {
                mOnItemClickListener(this@AsyncRvAdapterKt, v, itemHelper.position)
            }
        }

        override fun onLongClick(v: View): Boolean {
            if (::mOnItemLongClickListener.isInitialized) {
                mOnItemLongClickListener(this@AsyncRvAdapterKt, v, itemHelper.position)
                return true
            }
            return false
        }
    }

    companion object {
        private val sDiffExecutor = Executors.newFixedThreadPool(2)
        private val sMainThreadExecutor = MainThreadExecutor()
    }
}
