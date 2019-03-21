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
        mAdapter.setOnItemClickListener(new AsyncRVAdapter.OnItemClickListener<TestBean>() {

            @Override
            public void onItemClick(TestBean data, int position) {
                Toast.makeText(MainActivity.this, data.content, Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setOnItemLongClickListener(new AsyncRVAdapter.OnItemLongClickListener<TestBean>() {
            @Override
            public void onItemLongClick(TestBean data, int position) {
                Toast.makeText(MainActivity.this, "长按" + data.content, Toast.LENGTH_SHORT).show();
            }
        });
```

拓展用法：   
```
public class TestRVAdapter extends AsyncRVAdapter<TestBean> {

    //以下两个方法用于条目判断，如果第一个相同，第二个不同，则替换老条目位置为新条目   

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
