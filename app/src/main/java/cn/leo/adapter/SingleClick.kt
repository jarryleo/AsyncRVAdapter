package cn.leo.adapter

import android.view.View


inline fun <T : View> T.singleClick(time: Long = 800,crossinline block: (T) -> Unit) {
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime > time){
            lastClickTime = currentTimeMillis
            block(this)
        }
    }
}

var <T : View> T.lastClickTime: Long
    set(value) = setTag(17666133, value)
    get() = getTag(17666133) as? Long ?: 0