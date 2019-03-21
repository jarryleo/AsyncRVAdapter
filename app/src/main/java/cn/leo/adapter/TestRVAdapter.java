package cn.leo.adapter;


import android.view.View;

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
        return position % 3 == 0 ? R.layout.item_test_rv : R.layout.item_test_rv1;
    }

    @Override
    protected void bindData(ItemHelper helper, final TestBean data) {
        int layout = helper.getItemLayout();
        if (layout == R.layout.item_test_rv) {
            helper.setText(R.id.tv_test, data.content);
            //订阅条目内文本框点击事件
            helper.subscribeClick(R.id.tv_test);
        }else if (layout == R.layout.item_test_rv1){
            helper.setText(R.id.tv_test, data.content);
            helper.setBackgroundResource(R.id.tv_test,R.drawable.ic_launcher_background);
        }
    }

    @Override
    protected void onClickObserve(View v, TestBean data) {
        if (v.getId() == R.id.tv_test) {
            System.out.println("点击条目内文本框" + data.content);
        }
    }
}
