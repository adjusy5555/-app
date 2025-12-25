package com.expressmanagement.app.ui.courier.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.expressmanagement.app.entity.User;
import com.expressmanagement.app.ui.courier.adapter.TaskAdapter;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.expressmanagement.app.utils.StatusUtil;
import com.expressmanagement.app.utils.SystemLogHelper;

import java.util.List;

/**
 * 任务大厅Fragment - 显示所有待揽件订单
 */
public class TaskHallFragment extends Fragment {

    private TextView tvTaskCount;
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    
    private TaskAdapter adapter;
    private AppDatabase database;
    private int currentCourierId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_hall, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        database = AppDatabase.getInstance(requireContext());
        currentCourierId = PreferencesUtil.getCurrentUserId(requireContext());
        
        initViews(view);
        setupRecyclerView();
        loadData();
    }
    
    private void initViews(View view) {
        tvTaskCount = view.findViewById(R.id.tvTaskCount);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        adapter = new TaskAdapter(requireContext());
        
        // 设置接单点击事件
        adapter.setOnPickupClickListener(pkg -> showPickupDialog(pkg));
        
        recyclerView.setAdapter(adapter);
    }
    
    private void loadData() {
        // 查询所有待揽件订单
        List<Package> packages = database.packageDao().getAvailablePackages();
        
        if (packages.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            tvTaskCount.setText("当前有 0 个待接单任务");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            tvTaskCount.setText("当前有 " + packages.size() + " 个待接单任务");
            adapter.setPackages(packages);
        }
    }
    
    private void showPickupDialog(Package pkg) {
        new AlertDialog.Builder(requireContext())
                .setTitle("接单确认")
                .setMessage("确定要接下这个订单吗？\n\n运单号：" + pkg.getTrackingNo() + 
                           "\n收件人：" + pkg.getReceiverName())
                .setPositiveButton("确定接单", (dialog, which) -> pickupPackage(pkg))
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void pickupPackage(Package pkg) {
        // 获取快递员信息
        User courier = database.userDao().getUserById(currentCourierId);
        String courierName = courier.getNickname();
        if (courierName == null || courierName.isEmpty()) {
            courierName = courier.getUsername();
        }
        
        // 更新订单状态
        pkg.setStatus(StatusUtil.STATUS_PICKED); // 已揽件
        pkg.setCourierId(currentCourierId);
        pkg.setUpdateTime(System.currentTimeMillis());
        database.packageDao().update(pkg);
        
        // 添加物流轨迹
        String logisticsMsg = "快递员 " + courierName + " 已揽件";
        LogisticsInfo logistics = new LogisticsInfo(pkg.getPid(), logisticsMsg);
        database.logisticsInfoDao().insert(logistics);
        
        // 记录日志
        SystemLogHelper.logPickupPackage(requireContext(), currentCourierId, 
                pkg.getPid(), pkg.getTrackingNo());
        
        Toast.makeText(requireContext(), "接单成功！", Toast.LENGTH_SHORT).show();
        
        // 刷新列表
        loadData();
    }
    
    /**
     * 刷新数据（供Activity调用）
     */
    public void refreshData() {
        if (isAdded() && getView() != null) {
            loadData();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}