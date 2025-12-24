package com.expressmanagement.app.ui.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.Package;
import com.expressmanagement.app.entity.User;
import com.expressmanagement.app.ui.user.PackageDetailActivity;
import com.expressmanagement.app.ui.user.adapter.PackageAdapter;
import com.expressmanagement.app.utils.PreferencesUtil;

import java.util.List;

/**
 * 快递列表Fragment
 * 用于显示"我寄出的"或"我收到的"快递
 */
public class PackageListFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    public static final int TYPE_SENT = 0;      // 我寄出的
    public static final int TYPE_RECEIVED = 1;  // 我收到的

    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    private PackageAdapter adapter;

    private AppDatabase database;
    private int listType;
    private int currentUserId;
    private String currentUserPhone;

    public static PackageListFragment newInstance(int type) {
        PackageListFragment fragment = new PackageListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listType = getArguments().getInt(ARG_TYPE, TYPE_SENT);
        }

        database = AppDatabase.getInstance(requireContext());
        currentUserId = PreferencesUtil.getCurrentUserId(requireContext());

        // 获取当前用户手机号（用于查询收到的快递）
        User currentUser = database.userDao().getUserById(currentUserId);
        if (currentUser != null) {
            currentUserPhone = currentUser.getPhone();
        }
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

        adapter = new PackageAdapter(requireContext());

        // 设置点击事件
        adapter.setOnItemClickListener(pkg -> {
            // 跳转到详情页
            Intent intent = new Intent(requireContext(), PackageDetailActivity.class);
            intent.putExtra("package_id", pkg.getPid());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        List<Package> packages;

        if (listType == TYPE_SENT) {
            // 查询我寄出的快递
            packages = database.packageDao().getSentPackages(currentUserId);
        } else {
            // 查询我收到的快递（通过手机号匹配）
            packages = database.packageDao().getReceivedPackages(currentUserPhone);
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
        // 每次显示时刷新数据
        loadData();
    }
}