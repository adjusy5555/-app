package com.expressmanagement.app.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.Address;
import com.expressmanagement.app.ui.user.adapter.AddressAdapter;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.expressmanagement.app.utils.SystemLogHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * 地址列表Activity
 */
public class AddressListActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    private FloatingActionButton fabAdd;
    
    private AddressAdapter adapter;
    private AppDatabase database;
    private int currentUserId;
    
    private boolean selectMode = false; // 是否为选择地址模式
    
    private static final int REQUEST_ADD_ADDRESS = 1001;
    private static final int REQUEST_EDIT_ADDRESS = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        
        // 获取选择模式标识
        selectMode = getIntent().getBooleanExtra("select_mode", false);
        
        database = AppDatabase.getInstance(this);
        currentUserId = PreferencesUtil.getCurrentUserId(this);
        
        initViews();
        setupRecyclerView();
        setListeners();
        loadData();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        fabAdd = findViewById(R.id.fabAdd);
        
        // 设置工具栏
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AddressAdapter(this, selectMode);
        
        // 设置点击事件
        adapter.setOnItemClickListener(new AddressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Address address) {
                if (selectMode) {
                    // 选择模式：返回选中的地址
                    Intent intent = new Intent();
                    intent.putExtra("address_id", address.getAid());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onEditClick(Address address) {
                // 编辑地址
                Intent intent = new Intent(AddressListActivity.this, EditAddressActivity.class);
                intent.putExtra("address_id", address.getAid());
                startActivityForResult(intent, REQUEST_EDIT_ADDRESS);
            }

            @Override
            public void onDeleteClick(Address address) {
                showDeleteDialog(address);
            }

            @Override
            public void onSetDefaultClick(Address address) {
                setDefaultAddress(address);
            }
        });
        
        recyclerView.setAdapter(adapter);
    }
    
    private void setListeners() {
        fabAdd.setOnClickListener(v -> {
            // 检查地址数量限制
            int count = database.addressDao().getAddressCount(currentUserId);
            if (count >= 10) {
                Toast.makeText(this, "最多只能保存10个地址", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Intent intent = new Intent(this, EditAddressActivity.class);
            startActivityForResult(intent, REQUEST_ADD_ADDRESS);
        });
    }
    
    private void loadData() {
        List<Address> addresses = database.addressDao().getUserAddresses(currentUserId);
        
        if (addresses.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter.setAddresses(addresses);
        }
    }
    
    private void showDeleteDialog(Address address) {
        new AlertDialog.Builder(this)
                .setTitle("删除地址")
                .setMessage("确定要删除该地址吗？")
                .setPositiveButton("确定", (dialog, which) -> deleteAddress(address))
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void deleteAddress(Address address) {
        database.addressDao().delete(address);
        
        // 记录日志
        SystemLogHelper.logAddressAction(this, currentUserId, "DELETE", address.getAid());
        
        Toast.makeText(this, "地址已删除", Toast.LENGTH_SHORT).show();
        loadData();
    }
    
    private void setDefaultAddress(Address address) {
        // 先清除所有默认地址
        database.addressDao().clearDefaultAddress(currentUserId);
        
        // 设置新的默认地址
        address.setDefault(true);
        database.addressDao().update(address);
        
        Toast.makeText(this, "已设为默认地址", Toast.LENGTH_SHORT).show();
        loadData();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            // 刷新列表
            loadData();
        }
    }
}