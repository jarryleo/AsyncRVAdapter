package cn.leo.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leo
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TestRVAdapter mAdapter;
    private StatePager mStatePager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        mRecyclerView = findViewById(R.id.rvTest);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new TestRVAdapter();
        mRecyclerView.setAdapter(mAdapter);
        initView();
        initData();
    }

    private void initView() {
        mStatePager = StatePager.builder(mRecyclerView)
                .loadingViewLayout(R.layout.pager_loading)
                .emptyViewLayout(R.layout.pager_empty)
                .errorViewLayout(R.layout.pager_error)
                .addRetryButtonId(R.id.tv_tips)
                .addRetryButtonId(R.id.btn_retry)
                .setRetryClickListener(new StatePager.OnClickListener() {
                    @Override
                    public void onClick(StatePager statePager, View v) {
                        if (v.getId() == R.id.btn_retry) {
                            statePager.showSuccess();
                        } else {
                            statePager.showError()
                                    .setText(R.id.btn_retry, "哦哦，失败了，点击重试一下？");
                        }
                    }
                })
                .build();

        mStatePager.showLoading()
                .setText(R.id.tv_tips, "正在使出吃奶的力气加载");

        new SafetyMainHandler(this).postDelayed(new Runnable() {
            @Override
            public void run() {
                mStatePager.showEmpty()
                        .setText(R.id.tv_tips, "这里没有发现数据");
            }
        }, 2000);

        findViewById(R.id.btnTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MainActivity2.class));
            }
        });

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
