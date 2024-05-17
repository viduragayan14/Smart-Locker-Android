package com.example.smartlockerandroid.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlockerandroid.R;
import com.example.smartlockerandroid.data.enums.Flow;
import com.example.smartlockerandroid.data.model.Courier;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends RecyclerView.Adapter<PagerAdapter.ViewHolder> implements CourierGridAdapter.BaysFull {
    List<Courier> couriers = new ArrayList<>();
    List<List<Courier>> dataPages = new ArrayList<>();
    Context context;
    private Flow flow;
    private String name;
    private Integer noOfBaysAvailable;
    int i = 0;

    public PagerAdapter(Context applicationContext) {
        this.context = applicationContext;
    }

    @NonNull
    @Override
    public PagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.pager_item, parent, false);
        return new PagerAdapter.ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull PagerAdapter.ViewHolder holder, int position) {
        CourierGridAdapter gridAdapter = new CourierGridAdapter(this);
        holder.spanCount = Math.min(dataPages.get(position).size(), 4);
        Log.e("Grid Log", "" + holder.spanCount);
        GridLayoutManager layoutManager = new GridLayoutManager(context, holder.spanCount, LinearLayoutManager.VERTICAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);

        gridAdapter.setFlow(flow);
        gridAdapter.setUserName(name);
        gridAdapter.setNoOfBaysAvailable(noOfBaysAvailable);
        gridAdapter.setCourierList(dataPages.get(position));
        holder.recyclerView.setAdapter(gridAdapter);
    }

    @Override
    public int getItemCount() {
        return dataPages.size();
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    public void setUserName(String name) {
        this.name = name;
    }

    public void setNoOfBaysAvailable(Integer noOfBaysAvailable) {
        this.noOfBaysAvailable = noOfBaysAvailable;
    }

    public void setFullList(List<Courier> couriers) {
        this.couriers = couriers;
        for (int i = 0; i < this.couriers.size(); i += 8) {
            int endIndex = Math.min(i + 8, this.couriers.size());
            dataPages.add(this.couriers.subList(i, endIndex));
        }

        if (dataPages.size() == 1 && dataPages.get(0).size() < 8) {
            // Create a single page with all the items
            dataPages.clear();
            dataPages.add(couriers);
        }
    }

    @Override
    public void onBayFull() {
        i = 0;
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_bays_full);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        ProgressBar mProgressBar = dialog.findViewById(R.id.progressbar);
        TextView tv = dialog.findViewById(R.id.seconds);
        mProgressBar.setProgress(i);
        CountDownTimer mCountDownTimer = new CountDownTimer(5000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                i++;
                tv.setText((millisUntilFinished / 1000 + 1) + " secs left");
                mProgressBar.setProgress((int) i * 100 / (5000 / 100));

            }

            @Override
            public void onFinish() {
                //Do what you want
                i++;
                mProgressBar.setProgress(100);
                dialog.dismiss();
                mProgressBar.setProgress(0);

            }
        };
        mCountDownTimer.start();
        dialog.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        int spanCount = 4;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rv_grid_container_select_courier);


        }
    }
}
