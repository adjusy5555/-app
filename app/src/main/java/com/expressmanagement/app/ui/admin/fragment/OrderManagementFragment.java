package com.expressmanagement.app.ui.admin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.Package;
import com.expressmanagement.app.ui.admin.adapter.AdminPackageAdapter;
import com.expressmanagement.app.utils.StatusUtil;

import java.util.List;

/**
 * 订单管理Fragment
 */
public class OrderManagementFragment extends Fragment {

    private Spinner spinnerStatus;
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    
    private AdminPackageAdapter adapter;
    private AppDatabase db;
    
    private int selectedStatus = -1; // -1表示全部

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        initDatabase();
        setupRecyclerView();
        setupSpinner();
        loadOrders();
    }

    private void initViews(View view) {
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
    }

    private void initDatabase() {
        db = AppDatabase.getInstance(requireContext());
    }

    private void setupRecyclerView() {
        adapter = new AdminPackageAdapter(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSpinner() {
        String[] statuses = {
            "全部订单",
            StatusUtil.getStatusName(StatusUtil.STATUS_PENDING),
            StatusUtil.getStatusName(StatusUtil.STATUS_PICKED),
            StatusUtil.getStatusName(StatusUtil.STATUS_IN_TRANSIT),
            StatusUtil.getStatusName(StatusUtil.STATUS_DELIVERING),
            StatusUtil.getStatusName(StatusUtil.STATUS_DELIVERED),
            StatusUtil.getStatusName(StatusUtil.STATUS_CANCELLED)
        };
        
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statuses
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(spinnerAdapter);
        
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = position - 1; // 0->-1(全部), 1->0, 2->1...
                loadOrders();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadOrders() {
        List<Package> packages;
        
        if (selectedStatus == -1) {
            // 加载全部订单
            packages = db.packageDao().getAllPackages();
        } else {
            // 按状态筛选
            packages = db.packageDao().getPackagesByStatus(selectedStatus);
        }
        
        if (packages.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter.setPackages(packages);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }
}