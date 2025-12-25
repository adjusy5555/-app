package com.expressmanagement.app.ui.courier;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.Package;
import com.expressmanagement.app.ui.user.adapter.PackageAdapter;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.expressmanagement.app.utils.StatusUtil;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

/**
 * 历史记录Activity
 * 显示快递员已完成的所有订单
 */
public class HistoryActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    
    private PackageAdapter adapter;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list); // 复用地址列表布局
        
        database = AppDatabase.getInstance(this);
        
        initViews();
        setupRecyclerView();
        loadData();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        
        // 设置工具栏
        setSupportActionBar(toolbar);
        toolbar.setTitle("历史记录");
        toolbar.setNavigationOnClickListener(v -> finish());
        
        // 隐藏FAB按钮
        View fabAdd = findViewById(R.id.fabAdd);
        if (fabAdd != null) {
            fabAdd.setVisibility(View.GONE);
        }
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PackageAdapter(this);
        recyclerView.setAdapter(adapter);
    }
    
    private void loadData() {
        int courierId = PreferencesUtil.getCurrentUserId(this);
        
        // 查询已完成的订单
        List<Package> packages = database.packageDao()
                .getCourierPackagesByStatus(courierId, StatusUtil.STATUS_DELIVERED);
        
        if (packages.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter.setPackages(packages);
        }
    }
}