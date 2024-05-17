package com.example.smartlockerandroid.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlockerandroid.PreferencesActivity;
import com.example.smartlockerandroid.adapters.CourierListAdapter;
import com.example.smartlockerandroid.data.model.Courier;
import com.example.smartlockerandroid.data.viewmodel.CourierViewModel;
import com.example.smartlockerandroid.databinding.FragmentCourierListBinding;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.RecyclerRowMoveCallback;
import com.example.smartlockerandroid.utils.UdpServerThread;

import java.util.ArrayList;
import java.util.List;

public class CourierListFragment extends Fragment implements UdpServerThread.MessageReceived {
    private FragmentCourierListBinding binding;
    private CourierViewModel courierViewModel;
    private PreferencesActivity activity;
    CourierListAdapter listAdapter;
    UdpServerThread bh;

    List<Courier> updatedCourierList = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCourierListBinding.inflate(inflater, container, false);
        bh = UdpServerThread.getInstance(activity, this);
        bh.setViewModelStoreOwner(activity,this);
        View root = binding.getRoot();
        init();
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (PreferencesActivity) context;

    }

    private void init() {
        //    private Button btnAddCourier;
        RecyclerView mRecyclerView = binding.rvCourierListCourierSettings;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        courierViewModel = new ViewModelProvider(this).get(CourierViewModel.class);
        listAdapter = new CourierListAdapter(updatedCourierList, activity);

        binding.btnSaveOrderList.setOnClickListener(view -> {
            updatedCourierList = listAdapter.getUpdatedList();
            int i = 0;
            for (Courier c : updatedCourierList) {
                c.setPosition(i);
                i++;
            }
            if (!updatedCourierList.isEmpty()) {
                courierViewModel.updateList(updatedCourierList);

            }
            Toast.makeText(getContext(), "Order Apps saved successfully!", Toast.LENGTH_LONG).show();
        });
        ItemTouchHelper.Callback callback = new RecyclerRowMoveCallback(listAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(listAdapter);
        courierViewModel.getCouriers().observe(getViewLifecycleOwner(), couriers -> {
            listAdapter.updateData(couriers);
        });

    }


    @Override
    public void onMessageReceived2(String message) {

    }
}