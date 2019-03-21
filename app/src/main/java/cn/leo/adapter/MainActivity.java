package cn.leo.adapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.leo.adapter_lib.AsyncRVAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TestRVAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.rvTest);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new TestRVAdapter();
        mRecyclerView.setAdapter(mAdapter);
        initView();
        initData();
    }

    private void initView() {

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
    }

    private void test() {
        AsyncRVAdapter<TestBean> adapter = new AsyncRVAdapter<TestBean>() {
            @Override
            protected int getItemLayout(int position) {
                return R.layout.item_test_rv1;
            }

            @Override
            protected void bindData(ItemHelper helper, TestBean data) {
                helper.setText(R.id.tv_test, "测试")
                        .setBackgroundResource(R.id.tv_test, R.drawable.ic_launcher_background)
                        .setViewVisble(R.id.tv_test);
            }
        };
        mRecyclerView.setAdapter(adapter);
    }

    private void initData() {
        List<TestBean> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            TestBean e = new TestBean();
            e.id = i;
            e.content = "测试条目" + i;
            list.add(e);
        }
        mAdapter.setData(list);
    }
}
