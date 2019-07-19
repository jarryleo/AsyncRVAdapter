package cn.leo.adapter

import cn.leo.adapter_lib.LeoRvAdapter

class TestKoltinRvAdapter:LeoRvAdapter<TestKotlinBean>() {
    override fun getItemLayout(position: Int): Int {

        return 0
    }

    override fun bindData(helper: ItemHelper, item: TestKotlinBean) {
        helper.setItemHolder(TestHolder::class.java)
    }
}