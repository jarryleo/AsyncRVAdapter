# AsyncRVAdapter
极简封装使用的RecyclerView的adapter

继承本adapter不需要holder，
高性能高复用，最符合官方用法。  

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
        } else if (layout == R.layout.item_test_rv1) {
            helper.setText(R.id.tv_test, data.content)
                  .setBackgroundResource(R.id.tv_test, R.drawable.ic_launcher_background);
        }
    }
}

```
用法：  
点击复制这个类[AsyncRVAdapter.java](https://github.com/jarryleo/AsyncRVAdapter/blob/master/adapter_lib/src/main/java/cn/leo/adapter_lib/AsyncRVAdapter.java)

因为只有一个类，推荐上面用法

或者依赖： 
```
dependencies {

	       implementation 'com.github.jarryleo:AsyncRVAdapter:v1.0'
}
```
