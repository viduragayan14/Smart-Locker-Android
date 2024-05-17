package com.example.smartlockerandroid.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlockerandroid.R;
import com.example.smartlockerandroid.adapters.HistoryAdapter;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.model.relation.OrderWithCourierAndBays;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderHistoryViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderViewModel;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.serialimpl.SerialHelperImpl;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;


public class HistoryFragment extends Fragment implements HistoryAdapter.OnItemClickListener, UdpServerThread.MessageReceived {


    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private OrderHistoryViewModel orderViewModel;
    private BayViewModel bayViewModel;
    private PreferenceViewModel preferenceViewModel;
    private SerialHelperImpl serialHelper;
    private Preference preference;
    UdpServerThread bh;
    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        bh =  UdpServerThread.getInstance(context,  this);
        bh.setViewModelStoreOwner(context,this);
        mRecyclerView = view.findViewById(R.id.rv_order_list_order_management);
        mLayoutManager = new LinearLayoutManager(requireContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        serialHelper = SerialHelperImpl.getInstance(requireContext());
        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
        preference = preferenceViewModel.getPreference();
        orderViewModel = new ViewModelProvider(this).get(OrderHistoryViewModel.class);
        bayViewModel = new ViewModelProvider(this).get(BayViewModel.class);

        orderViewModel.getOrderHistory().observe(requireActivity(), orderWithCourierAndBays -> {

            HistoryAdapter orderListAdapter = new HistoryAdapter(orderWithCourierAndBays, orderViewModel, bayViewModel, preference, serialHelper, requireContext(), 1);
            orderListAdapter.setOnItemClickListener(this);
            //orderListAdapter.setOrderList(orderWithCourierAndBays);
            mRecyclerView.setAdapter(orderListAdapter);
        });

        return view.getRootView();
    }

    @Override
    public void onItemClick(OrderWithCourierAndBays order, Handler handler, Runnable updateTimeRunnable) {

    }

    @Override
    public void onMessageReceived2(String message) {

    }
}