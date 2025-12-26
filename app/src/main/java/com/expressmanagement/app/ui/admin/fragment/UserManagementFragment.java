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
import com.expressmanagement.app.entity.User;
import com.expressmanagement.app.ui.admin.adapter.UserAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理Fragment
 */
public class UserManagementFragment extends Fragment {

    private Spinner spinnerRole;
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    
    private UserAdapter adapter;
    private AppDatabase db;
    
    private int selectedRole = -1; // -1表示全部

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        initDatabase();
        setupRecyclerView();
        setupSpinner();
        loadUsers();
    }

    private void initViews(View view) {
        spinnerRole = view.findViewById(R.id.spinnerRole);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
    }

    private void initDatabase() {
        db = AppDatabase.getInstance(requireContext());
    }

    private void setupRecyclerView() {
        adapter = new UserAdapter(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSpinner() {
        String[] roles = {"全部用户", "普通用户", "快递员", "管理员"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                roles
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(spinnerAdapter);
        
        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = position - 1; // 0->-1(全部), 1->0(普通), 2->1(快递员), 3->2(管理员)
                loadUsers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadUsers() {
        List<User> users;
        
        if (selectedRole == -1) {
            // 加载全部用户
            users = db.userDao().getAllUsers();
        } else {
            // 按角色筛选
            users = db.userDao().getUsersByRole(selectedRole);
        }
        
        if (users.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter.setUsers(users);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsers();
    }
}