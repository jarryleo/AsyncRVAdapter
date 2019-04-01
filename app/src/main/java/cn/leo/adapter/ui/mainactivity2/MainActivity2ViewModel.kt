package cn.leo.adapter.ui.mainactivity2

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class MainActivity2ViewModel : ViewModel() {
    var bean: MutableLiveData<UserBean>? = null

    fun getUsers(): LiveData<UserBean> {
        if (bean == null) {
            bean = MutableLiveData<UserBean>()
            loadBean()
        }
        return bean as MutableLiveData<UserBean>
    }

    private fun loadBean() {
        // 执行异步操作获取 bean
        bean!!.value = UserBean("111")
    }
}
