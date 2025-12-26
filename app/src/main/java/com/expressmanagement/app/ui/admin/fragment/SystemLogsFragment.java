package com.expressmanagement.app.ui.admin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.SystemLog;
import com.expressmanagement.app.ui.admin.adapter.SystemLogAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * 系统日志Fragment
 */
public class SystemLogsFragment extends Fragment {

    private Spinner spinnerActionType;
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    private MaterialButton btnFilter;
    private MaterialButton btnExport;
    
    private SystemLogAdapter adapter;
    private AppDatabase db;
    
    private String selectedActionType = null; // null表示全部

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_system_logs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        initDatabase();
        setupRecyclerView();
        setupSpinner();
        setListeners();
        loadLogs();
    }

    private void initViews(View view) {
        spinnerActionType = view.findViewById(R.id.spinnerActionType);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnExport = view.findViewById(R.id.btnExport);
    }

    private void initDatabase() {
        db = AppDatabase.getInstance(requireContext());
    }

    private void setupRecyclerView() {
        adapter = new SystemLogAdapter(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSpinner() {
        String[] actionTypes = {
            "全部操作",
            "用户登录",
            "用户注册",
            "创建订单",
            "更新状态",
            "取消订单",
            "接单",
            "修改密码",
            "管理员操作"
        };
        
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                actionTypes
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActionType.setAdapter(spinnerAdapter);
        
        spinnerActionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: selectedActionType = null; break;
                    case 1: selectedActionType = "LOGIN"; break;
                    case 2: selectedActionType = "REGISTER"; break;
                    case 3: selectedActionType = "CREATE_PACKAGE"; break;
                    case 4: selectedActionType = "UPDATE_STATUS"; break;
                    case 5: selectedActionType = "CANCEL_PACKAGE"; break;
                    case 6: selectedActionType = "PICKUP"; break;
                    case 7: selectedActionType = "CHANGE_PASSWORD"; break;
                    case 8: selectedActionType = "ADMIN"; break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setListeners() {
        btnFilter.setOnClickListener(v -> loadLogs());
        
        btnExport.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "导出CSV功能开发中", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadLogs() {
        List<SystemLog> logs;
        
        if (selectedActionType == null) {
            // 加载全部日志
            logs = db.systemLogDao().getAllLogs();
        } else {
            // 按操作类型筛选
            logs = db.systemLogDao().getLogsByActionType(selectedActionType);
        }
        
        if (logs.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter.setLogs(logs);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLogs();
    }
}