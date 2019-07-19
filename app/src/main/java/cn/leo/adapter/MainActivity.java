package cn.leo.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.leo.adapter_lib.LeoRvAdapter;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author Leo
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TestRVAdapter mAdapter;
    private StatePager mStatePager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        mRecyclerView = findViewById(R.id.rvTest);
        mSwipeRefreshLayout = findViewById(R.id.srl_Refresh);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new TestRVAdapter();
        mRecyclerView.setAdapter(mAdapter);
        initView();
        initData();
    }

    private boolean noSwipe = true;

    private void initView() {
        mSwipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
                return noSwipe;
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    noSwipe = recyclerView.canScrollVertically(-1);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                noSwipe = true;
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

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
                mStatePager.showSuccess();
            }
        }, 2000);

        findViewById(R.id.btnTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, MainActivity2.class));
                mAdapter.edit(0, new Function1<TestBean, Unit>() {
                    @Override
                    public Unit invoke(TestBean testBean) {
                        testBean.content = "xiugaichenggong";
                        return null;
                    }
                });
            }
        });
        mAdapter.setOnItemClickListener((adapter, v, position) -> {
            TestBean item = (TestBean) adapter.getItem(position);
            Toast.makeText(MainActivity.this, "点击条目" + item.content, Toast.LENGTH_SHORT).show();
            return null;
        });

        mAdapter.setOnItemLongClickListener((adapter, v, position) -> {
            TestBean item = (TestBean) adapter.getItem(position);
            Toast.makeText(MainActivity.this, "长按条目" + item.content, Toast.LENGTH_SHORT).show();
            return null;
        });
        mAdapter.setOnItemChildClickListener((adapter, v, position) -> {
            TestBean item = (TestBean) adapter.getItem(position);
            Toast.makeText(MainActivity.this, "点击条目内的文本框" + item.content, Toast.LENGTH_SHORT).show();
            return null;
        });

        mAdapter.setLoadMoreListener(new Function2<LeoRvAdapter<?>, Integer, Unit>() {
            @Override
            public Unit invoke(LeoRvAdapter<?> leoRvAdapter, Integer integer) {
                loadMore(integer + 1);
                return null;
            }
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

    private void loadMore(int index) {
        List<TestBean> list = new ArrayList<>();
        for (int i = index; i < index + 20; i++) {
            TestBean e = new TestBean();
            e.id = i;
            e.content = "测试条目" + i;
            list.add(e);
        }
        Log.d("-====", "loadMore: 加载更多");
        mAdapter.add(list);
    }
}
