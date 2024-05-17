package com.example.smartlockerandroid.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.smartlockerandroid.databinding.FragmentSettingsLandingScreenBinding;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

public class SettingsLandingScreenFragment extends Fragment implements UdpServerThread.MessageReceived {

    Context context;
    UdpServerThread bh;
    FragmentSettingsLandingScreenBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentSettingsLandingScreenBinding.inflate(inflater, container, false);
        bh = UdpServerThread.getInstance(context, this);
        bh.setViewModelStoreOwner(context,this);
        return binding.getRoot();
    }

    @Override
    public void onMessageReceived2(String message) {

    }
}