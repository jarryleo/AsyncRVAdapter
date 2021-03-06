# AsyncRVAdapter
极简封装使用的RecyclerView的adapter

优点：
- 采用官方的AsyncListDiffer实现数据处理，保证性能
- 最少只需要实现2个方法，不需要holder
- 高性能，高复用，view id复用，点击事件复用。
- 所有数据操作异步执行，可以在子线程操作数据更新UI ，注意不需要使用notifyDataSetChanged方法！
- 内置异步去重功能，需要重写2个条目判断方法
- 条目布局绑定数据可以链式调用，书写优雅

注意:
> 需要依赖版本com.android.support:recyclerview-v7:27.1.0 及以上
> 不要从adapter拿数据修改后再塞回去，否则不会更新UI

最简用法：
```
public class TestRVAdapter extends AsyncRVAdapter<TestBean> {

    @Override
    protected int getItemLayout(int position) {
        return R.layout.item_test_rv;
    }

    @Override
    protected void bindData(ItemHelper helper, final TestBean data) {
        helper.setText(R.id.tv_test, data.content);
    }
}
```

点击事件：      
```
         mAdapter.setOnItemClickListener(new AsyncRVAdapter.OnItemClickListener() {
             @Override
             public void onItemClick(AsyncRVAdapter adapter, View v, int position) {
                 TestBean item = (TestBean) adapter.getItem(position);
                 Toast.makeText(MainActivity.this, "点击条目" + item.content, Toast.LENGTH_SHORT).show();
             }
         });
         mAdapter.setOnItemLongClickListener(new AsyncRVAdapter.OnItemLongClickListener() {
             @Override
             public void onItemLongClick(AsyncRVAdapter adapter, View v, int position) {
                 TestBean item = (TestBean) adapter.getItem(position);
                 Toast.makeText(MainActivity.this, "长按条目" + item.content, Toast.LENGTH_SHORT).show();
             }
         });
         mAdapter.setOnItemChildClickListener(new AsyncRVAdapter.OnItemChildClickListener() {
             @Override
             public void onItemChildClick(AsyncRVAdapter adapter, View v, int position) {
                 TestBean item = (TestBean) adapter.getItem(position);
                 Toast.makeText(MainActivity.this, "点击条目内的文本框" + item.content, Toast.LENGTH_SHORT).show();
             }
         });
```

拓展用法：   
```
public class TestRVAdapter extends AsyncRVAdapter<TestBean> {

    //以下两个方法用于条目判断，如果第一个相同，第二个不同，则替换条目，都相同则去重

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
                   //订阅条目内文本框点击事件（对应上面的条目内控件点击事件）
                  .addOnClickListener(R.id.tv_test);
        } else if (layout == R.layout.item_test_rv1) {
            helper.setText(R.id.tv_test, data.content)
                  .setBackgroundResource(R.id.tv_test, R.drawable.ic_launcher_background);
        }
    }
}

```
多条目类型，条目分类处理用法：
```
public class TestRvAdapter1 extends BaseRVAdapter<TestBean> {

    @Override
    protected int getItemLayout(int position) {
        TestBean bean = getData().get(position);
        if (bean.type == 1) {
            return R.layout.item_banner1;
        } else if (bean.type == 2){
            return R.layout.item_recycler_view_vertical;
        }
        return R.layout.item_recycler_view_horizontal;
    }

    @Override
    protected void bindData(final ItemHelper helper, TestBean data) {
        if (data.type == 1) {
            helper.setItemHolder(BannerHolder.class);
        } else if(data.type == 0) {
            helper.setItemHolder(RvHorizontalHolder.class);
        }else if(data.type == 2) {
            helper.setItemHolder(RvVerticalHolder.class);
        }

    }
}
```
用法：  
点击复制这个类[LeoRvAdapter.kt](https://github.com/jarryleo/AsyncRVAdapter/blob/master/adapter_lib/src/main/java/cn/leo/adapter_lib/LeoRvAdapter.kt)

因为只有一个类，推荐上面用法

