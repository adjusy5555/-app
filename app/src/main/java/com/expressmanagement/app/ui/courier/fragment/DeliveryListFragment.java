package com.expressmanagement.app.ui.courier.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.LogisticsInfo;
import com.expressmanagement.app.entity.Package;
import com.expressmanagement.app.ui.courier.adapter.DeliveryPackageAdapter;
import com.expressmanagement.app.utils.StatusUtil;
import com.expressmanagement.app.utils.SystemLogHelper;

import java.util.List;

/**
 * 配送列表Fragment
 * 用于显示不同状态的配送订单
 */
public class DeliveryListFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private static final String ARG_COURIER_ID = "courier_id";
    
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    private DeliveryPackageAdapter adapter;
    
    private AppDatabase database;
    private int status;
    private int courierId;

    public static DeliveryListFragment newInstance(int status, int courierId) {
        DeliveryListFragment fragment = new DeliveryListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STATUS, status);
        args.putInt(ARG_COURIER_ID, courierId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getInt(ARG_STATUS);
            courierId = getArguments().getInt(ARG_COURIER_ID);
        }
        database = AppDatabase.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_package_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupRecyclerView();
        loadData();
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        adapter = new DeliveryPackageAdapter(requireContext());
        
        // 设置拨打电话监听
        adapter.setOnCallClickListener(pkg -> callPhone(pkg.getReceiverPhone()));
        
        // 设置更新状态监听
        adapter.setOnUpdateStatusClickListener(pkg -> showUpdateStatusDialog(pkg));
        
        recyclerView.setAdapter(adapter);
    }
    
    private void loadData() {
        List<Package> packages = database.packageDao()
                .getCourierPackagesByStatus(courierId, status);
        
        if (packages.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter.setPackages(packages);
        }
    }
    
    private void callPhone(String phone) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "无法拨打电话", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showUpdateStatusDialog(Package pkg) {
        String title;
        String message;
        int newStatus;
        
        switch (pkg.getStatus()) {
            case StatusUtil.STATUS_PICKED:
                title = "更新为运输中";
                message = "确定将订单更新为运输中状态吗？";
                newStatus = StatusUtil.STATUS_IN_TRANSIT;
                break;
            case StatusUtil.STATUS_IN_TRANSIT:
                title = "更新为派送中";
                message = "确定将订单更新为派送中状态吗？\n\n到达目的地后，请及时联系收件人配送。";
                newStatus = StatusUtil.STATUS_DELIVERING;
                break;
            default:
                Toast.makeText(requireContext(), "当前状态无法更新", Toast.LENGTH_SHORT).show();
                return;
        }
        
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", (dialog, which) -> updatePackageStatus(pkg, newStatus))
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void updatePackageStatus(Package pkg, int newStatus) {
        int oldStatus = pkg.getStatus();
        
        // 更新订单状态
        pkg.setStatus(newStatus);
        pkg.setUpdateTime(System.currentTimeMillis());
        database.packageDao().update(pkg);
        
        // 添加物流轨迹
        String logisticsMsg = StatusUtil.getLogisticsDescription(newStatus, null);
        LogisticsInfo logistics = new LogisticsInfo(pkg.getPid(), logisticsMsg);
        database.logisticsInfoDao().insert(logistics);
        
        // 记录日志
        SystemLogHelper.logUpdatePackageStatus(requireContext(), courierId, 1,
                pkg.getPid(), pkg.getTrackingNo(), oldStatus, newStatus);
        
        Toast.makeText(requireContext(), "状态已更新", Toast.LENGTH_SHORT).show();
        
        // 刷新列表
        loadData();
        
        // 通知父Fragment刷新统计
        if (getParentFragment() instanceof DeliveryManagementFragment) {
            ((DeliveryManagementFragment) getParentFragment()).refreshData();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}