package cn.leo.adapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leo
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TestRVAdapterKt mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.rvTest);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new TestRVAdapterKt();
        mRecyclerView.setAdapter(mAdapter);
        initView();
        initData();
    }

    private void initView() {
        mAdapter.setOnItemClickListener((adapter, v, position) -> {
            TestBean item = (TestBean) adapter.getItem(position);
            Toast.makeText(MainActivity.this, "点击条目" + item.content, Toast.LENGTH_SHORT).show();
        });
        mAdapter.setOnItemLongClickListener((adapter, v, position) -> {
            TestBean item = (TestBean) adapter.getItem(position);
            Toast.makeText(MainActivity.this, "长按条目" + item.content, Toast.LENGTH_SHORT).show();
        });
        mAdapter.setOnItemChildClickListener((adapter, v, position) -> {
            TestBean item = (TestBean) adapter.getItem(position);
            Toast.makeText(MainActivity.this, "点击条目内的文本框" + item.content, Toast.LENGTH_SHORT).show();
        });
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
