package cn.leo.adapter

import cn.leo.adapter_lib.LeoRvAdapter

class TestHolder : LeoRvAdapter.ItemHolder<TestKotlinBean>() {

    override fun initView(helper: LeoRvAdapter.ItemHelper, item: TestKotlinBean) {
        super.initView(helper, item)
    }

    override fun onViewDetach(helper: LeoRvAdapter.ItemHelper) {
        super.onViewDetach(helper)
        helper.getTag("")
    }

    override fun bindData(helper: LeoRvAdapter.ItemHelper, item: TestKotlinBean) {
    }
}