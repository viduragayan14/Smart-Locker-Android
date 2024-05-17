package com.example.smartlockerandroid.adapters;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlockerandroid.CourierSettingsActivity;
import com.example.smartlockerandroid.PreferencesActivity;
import com.example.smartlockerandroid.R;
import com.example.smartlockerandroid.data.enums.CourierStatus;
import com.example.smartlockerandroid.data.model.Courier;
import com.example.smartlockerandroid.utils.RecyclerRowMoveCallback;

import java.util.Collections;
import java.util.List;

/**
 * @author itschathurangaj on 7/3/23
 */
public class CourierListAdapter extends RecyclerView.Adapter<CourierListAdapter.ViewHolder> implements RecyclerRowMoveCallback.RecyclerViewRowTouchHelperContract {
    PreferencesActivity activity;
    private List<Courier> courierList;
    int superFrom,superTo = 0;

    public CourierListAdapter(List<Courier> courierList, PreferencesActivity activity) {
        this.courierList = courierList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CourierListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_courier, parent, false);
        return new CourierListAdapter.ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull CourierListAdapter.ViewHolder holder, int position) {
        Courier courier = courierList.get(holder.getAdapterPosition());
        holder.courier = courier;

        if (courier.getLogoData() != null) {
            holder.imageViewCourier.setImageBitmap(BitmapFactory.decodeByteArray(courier.getLogoData(), 0, courier.getLogoData().length));
        }else{
            holder.imageViewCourier.setImageDrawable(activity.getResources().getDrawable(R.drawable.default_courier_logo));
        }
        holder.switchCourierStatus.setChecked(courier.getStatus().getValue());
        holder.tvCourierName.setText(courier.getName());
        holder.switchCourierStatus.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                courier.setStatus(CourierStatus.ACTIVE);
            } else {
                courier.setStatus(CourierStatus.INACTIVE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courierList.size();
    }

    @Override
    public void onRowMoved(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(courierList, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(courierList, i, i - 1);
            }
        }
        superFrom = from;
        superTo = to;
        notifyItemMoved(from, to);
        Log.e("Recycler View","Item Moving");

    }

    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
        myViewHolder.parent.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
        myViewHolder.parent.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onRowSettled(ViewHolder myViewHolder) {

    }

    public void updateData(List<Courier> newCouriers) {
        courierList.clear();
        courierList.addAll(newCouriers);
        notifyDataSetChanged();
    }

    public List<Courier> getUpdatedList(){
        return courierList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Courier courier;
        SwitchCompat switchCourierStatus;
        ImageView imageViewCourier;
        TextView tvCourierName;
        ImageView btnEditCourier;
        RelativeLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            switchCourierStatus = itemView.findViewById(R.id.switch_status_courier_list_item);
            imageViewCourier = itemView.findViewById(R.id.imageView_courier_list_item);
            tvCourierName = itemView.findViewById(R.id.tv_name_courier_list_item);
            btnEditCourier = itemView.findViewById(R.id.btn_edit_courier_list_item);
            parent = itemView.findViewById(R.id.parent_layout);

            btnEditCourier.setOnClickListener(view -> {

                Intent intent = new Intent(activity, CourierSettingsActivity.class);
                intent.putExtra("flow", "UPDATE_COURIER");
                intent.putExtra("courierId", courier.getCourierId());
                activity.startActivity(intent);
            });

        }
    }

}
