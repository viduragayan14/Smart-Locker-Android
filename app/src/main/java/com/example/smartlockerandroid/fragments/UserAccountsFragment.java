package com.example.smartlockerandroid.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlockerandroid.PreferencesActivity;
import com.example.smartlockerandroid.adapters.UserListAdapter;
import com.example.smartlockerandroid.data.viewmodel.UserViewModel;
import com.example.smartlockerandroid.databinding.FragmentUserAccountsBinding;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

public class UserAccountsFragment extends Fragment implements UdpServerThread.MessageReceived {

    PreferencesActivity activity;
    private FragmentUserAccountsBinding binding;
    //    private TextView btnAddUser;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private UserViewModel userViewModel;
    UdpServerThread bh;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserAccountsBinding.inflate(inflater, container, false);
        bh =  UdpServerThread.getInstance(activity,  this);
        bh.setViewModelStoreOwner(activity,this);
        View root = binding.getRoot();
        init();
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (PreferencesActivity) context;
    }

    private void init() {
//        btnAddUser = binding.btnAddUser;
        mRecyclerView = binding.rvUsersList;

        mLayoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        UserListAdapter listAdapter = new UserListAdapter(activity,userViewModel);


        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            listAdapter.setUserList(users);
            mRecyclerView.setAdapter(listAdapter);
        });

        addUser();
    }

    private void addUser() {

    }

    @Override
    public void onMessageReceived2(String message) {

    }
}