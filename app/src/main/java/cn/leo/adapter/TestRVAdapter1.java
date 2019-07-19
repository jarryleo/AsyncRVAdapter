package cn.leo.adapter;


import cn.leo.adapter_lib.LeoRvAdapter;

/**
 * @author : Jarry Leo
 * @date : 2019/3/20 14:44
 */
public class TestRVAdapter1 extends LeoRvAdapter<TestBean> {
    @Override
    protected int getItemLayout(int position) {
        return R.layout.item_test_rv;
    }
    @Override
    protected void bindData(ItemHelper helper, final TestBean data) {
        helper.setText(R.id.tv_test, data.content);
        helper.setItemHolder(TestHolder.class);
    }
}
