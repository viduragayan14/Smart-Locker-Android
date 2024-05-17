package com.example.smartlockerandroid.adapters;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlockerandroid.R;
import com.example.smartlockerandroid.data.enums.BayDoorStatus;
import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.serialimpl.SerialHelperImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author itschathurangaj on 8/10/23
 */
public class BayListAdapter extends RecyclerView.Adapter<BayListAdapter.ViewHolder> {
    private final List<Bay> bayList;
    private final Preference preference;
    private final BayViewModel bayViewModel;
    private final SerialHelperImpl serialHelper;
    int noOfBays;

    public BayListAdapter(List<Bay> bayList, Preference preference, BayViewModel bayViewModel, SerialHelperImpl serialHelper, int noOfBays) {
        this.bayList = bayList;
        this.preference = preference;
        this.bayViewModel = bayViewModel;
        this.serialHelper = serialHelper;
        this.noOfBays = noOfBays;
    }

    @NonNull
    @Override
    public BayListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bay, parent, false);
        return new BayListAdapter.ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull BayListAdapter.ViewHolder holder, int position) {
        Bay bay = bayList.get(holder.getAdapterPosition());

        holder.bay = bay;
        holder.tvBayId.setText(String.valueOf(bay.getDefaultId()));
        holder.progressDialog = new ProgressDialog(holder.itemView.getContext());
        holder.progressDialog.setMessage("Calibrating...");
        holder.progressDialog.setCancelable(false);

        List<Integer> bayCount = new ArrayList<>();
        for (int i = 1; i <= noOfBays; i++) {
            if (i == preference.getServiceBay())
                continue;
            bayCount.add(i);
        }
        ArrayAdapter<Integer> dropdownItems = new ArrayAdapter<>(holder.itemView.getContext(), R.layout.list_item_dropdown, bayCount);
        holder.spinnerBayCalibratedId.setAdapter(dropdownItems);
        holder.spinnerBayCalibratedId.setSelection(dropdownItems.getPosition(bay.getCalibratedId()));


        holder.spinnerBayCalibratedId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.newCalibratedId = (int) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (bay.getStatus() == BayStatus.DEACTIVATED) {
            holder.baySwitch.setChecked(false);
        } else {
            holder.baySwitch.setChecked(true);
        }
        holder.baySwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                bay.setStatus(BayStatus.AVAILABLE);
                bay.setCalibratedId(bay.getCalibratedId());
                bay.setDoorStatus(BayDoorStatus.CLOSE);
                bayViewModel.update(bay);
                notifyItemChanged(holder.getAdapterPosition());
            } else {
                bay.setStatus(BayStatus.DEACTIVATED);
                bay.setCalibratedId(bay.getCalibratedId());
                bay.setDoorStatus(BayDoorStatus.CLOSE);
                bayViewModel.update(bay);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });


    }

    @Override
    public int getItemCount() {
        return bayList.size();
    }

    public void setNoOfBays(int noOfBays) {
        this.noOfBays = noOfBays;
        Log.e("Final Check", "" + this.noOfBays);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Bay bay;
        int newCalibratedId;
        TextView tvBayId;
        Spinner spinnerBayCalibratedId;
        RelativeLayout btnTestBay;
        ProgressDialog progressDialog;
        SwitchCompat baySwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBayId = itemView.findViewById(R.id.tv_bay_id_bay_calibration_list);
            spinnerBayCalibratedId = itemView.findViewById(R.id.spinner_bay_calibration_list);
            btnTestBay = itemView.findViewById(R.id.btn_test_bay_calibration_list);
            baySwitch = itemView.findViewById(R.id.switch_status_courier_list_item);

            btnTestBay.setOnClickListener(v -> {
                int currentBayId = bay.getCalibratedId();

                if (currentBayId == newCalibratedId) {
                    serialHelper.executeBayCalibrationTestAction2(currentBayId);
                    return;
                }
                //getting corresponding bay
                for (Bay bay : bayList) {
                    if (bay.getCalibratedId() == newCalibratedId) {
                        bay.setCalibratedId(currentBayId);
                        bay.setDoorStatus(BayDoorStatus.OPEN);
                        bayViewModel.update(bay);
                        break;
                    }
                }

                bay.setCalibratedId(newCalibratedId);
                bay.setDoorStatus(BayDoorStatus.OPEN);
                bayViewModel.update(bay);
                serialHelper.executeBayCalibrationTestAction2(newCalibratedId);
            });
        }
    }
}
