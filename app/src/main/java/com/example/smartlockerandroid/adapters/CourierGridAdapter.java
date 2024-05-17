package com.example.smartlockerandroid.adapters;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlockerandroid.R;
import com.example.smartlockerandroid.ScanOrderActivity;
import com.example.smartlockerandroid.data.enums.Flow;
import com.example.smartlockerandroid.data.model.Courier;

import java.util.List;

public class CourierGridAdapter extends RecyclerView.Adapter<CourierGridAdapter.ViewHolder> {

    public CourierGridAdapter(BaysFull bFull) {
        this.bFull = bFull;
    }

    private List<Courier> courierList;
    private Flow flow;
    private String name;
    private Integer noOfBaysAvailable;

    BaysFull bFull;

    public CourierGridAdapter() {
    }

    public void setCourierList(List<Courier> courierList) {
        this.courierList = courierList;
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.courier_grid_layout, null);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Courier courier = this.courierList.get(position);
        holder.courier = courier;

        holder.textView.setText(courier.getName());
        if (courier.getLogoData() != null) {
            holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(courier.getLogoData(), 0, courier.getLogoData().length));
        }
        holder.flow = this.flow;
    }

    @Override
    public int getItemCount() {
        if (courierList == null)
            return 0;
        else
            return courierList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        Courier courier;
        Flow flow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgVw_courier_grid);
            textView = itemView.findViewById(R.id.txtVw_courier_grid);

            itemView.setOnClickListener(view -> {
                if (noOfBaysAvailable > 0 && flow.equals(Flow.LOADING_FLOW)) {
                    Intent intent = new Intent(view.getContext(), ScanOrderActivity.class);
                    intent.putExtra("flow", flow);
                    intent.putExtra("courierId", courier.getCourierId());
                    intent.putExtra("courier", courier);
                    intent.putExtra("userName", name);
                    view.getContext().startActivity(intent);
                } else if (flow.equals(Flow.PICKUP_FLOW)) {
                    Intent intent = new Intent(view.getContext(), ScanOrderActivity.class);
                    intent.putExtra("flow", flow);
                    intent.putExtra("courierId", courier.getCourierId());
                    intent.putExtra("courier", courier);
                    intent.putExtra("userName", name);
                    view.getContext().startActivity(intent);
                } else {
                    bFull.onBayFull();
                }
            });
        }
    }

    public interface BaysFull{
        void onBayFull();
    }
}
