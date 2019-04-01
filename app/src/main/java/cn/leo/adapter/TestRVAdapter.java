package cn.leo.adapter;


import cn.leo.adapter_lib.AsyncRVAdapter;

/**
 * @author : Jarry Leo
 * @date : 2019/3/20 14:44
 */
public class TestRVAdapter extends AsyncRVAdapter<TestBean> {

    //以下两个方法用于条目判断，如果第一个相同，第二个不同，则替换

    @Override
    protected boolean areItemsTheSame(TestBean oldItem, TestBean newItem) {
        //判断条目是否相同
        return oldItem.id == newItem.id;
    }

    @Override
    protected boolean areContentsTheSame(TestBean oldItem, TestBean newItem) {
        //判断条目内容是否相同
        return oldItem.content.equals(newItem.content);
    }


    @Override
    protected int getItemLayout(int position) {
        //多条目类型
        return position % 3 == 0 ? R.layout.item_test_rv1 : R.layout.item_test_rv;
    }

    @Override
    protected void bindData(ItemHelper helper, final TestBean data) {
        int layout = helper.getItemLayout();
        if (layout == R.layout.item_test_rv) {
            helper.setText(R.id.tv_test, data.content)
                    //订阅条目内文本框点击事件
                    .addOnClickListener(R.id.tv_test);
            StatePager pager = (StatePager) helper.getTag();
            if (pager == null) {
                pager = StatePager.builder(helper.getItemView())
                        .emptyViewLayout(R.layout.pager_empty)
                        .build();
                helper.setTag(pager);
            }
            pager.showEmpty();
        } else if (layout == R.layout.item_test_rv1) {
            helper.setText(R.id.tv_test, data.content)
                    .setBackgroundResource(R.id.tv_test, R.drawable.ic_launcher_background);
        }
    }
}
