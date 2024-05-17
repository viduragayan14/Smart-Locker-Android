package com.example.smartlockerandroid.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlockerandroid.R;
import com.example.smartlockerandroid.data.enums.OrderStatus;
import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.model.relation.OrderWithCourierAndBays;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderHistoryViewModel;
import com.example.smartlockerandroid.serialimpl.SerialHelperImpl;
import com.example.smartlockerandroid.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author itschathurangaj on 6/30/23
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<OrderWithCourierAndBays> orderList;
    private Preference preference;
    private OrderHistoryViewModel orderViewModel;
    private BayViewModel bayViewModel;
    private OnItemClickListener itemClickListener;
    private SerialHelperImpl serialHelper;

    int selectedPosition = -1;
    int selectedPosition2 = -1;
    int lastSelectedPosition = -1;

    boolean editable = true;
    Context context;
    int from;


    public HistoryAdapter(List<OrderWithCourierAndBays> orderList, OrderHistoryViewModel orderViewModel, BayViewModel bayViewModel, Preference preference, SerialHelperImpl serialHelper, Context context, int from) {
        this.orderList = orderList;
        this.orderViewModel = orderViewModel;
        this.bayViewModel = bayViewModel;
        this.preference = preference;
        this.serialHelper = serialHelper;
        this.context = context;
        this.from = from;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_order, parent, false);
        return new HistoryAdapter.ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        OrderWithCourierAndBays order = orderList.get(holder.getAdapterPosition());
        holder.order = order;
        if (order.getCourier().getLogoData() != null) {
            holder.imageViewCourier.setImageBitmap(BitmapFactory.decodeByteArray(order.getCourier().getLogoData(), 0, order.getCourier().getLogoData().length));
        }else{
            holder.imageViewCourier.setImageDrawable(context.getResources().getDrawable(R.drawable.default_courier_logo));
        }
        holder.tvOrderId.setText(String.valueOf(order.getOrder().getOrderNumber()));

        holder.tvBayNumbers.setText(getBayNumbersFromBayList(order.getBays()));
        holder.tvStatus.setText(order.getOrder().getStatus().getValue());
        if (order.getOrder().getStatus().equals(OrderStatus.PICKED)){
            holder.tvStatus.setText("On Time");
        }
        if (order.getOrder().getStatus().equals(OrderStatus.OVERDUE) || order.getOrder().getStatus().equals(OrderStatus.LATE)) {
            holder.tvStatus.setTextColor(Color.RED);
        }
        holder.userName.setText(order.getOrder().getLoadedByName());

        if (selectedPosition == holder.getAdapterPosition()) {
            holder.l.setBackgroundColor(context.getColor(R.color.light_gray1));
        } else {
            holder.l.setBackgroundColor(Color.WHITE);
        }


        if (order.getOrder().getStatus().equals(OrderStatus.PICKED)) {
//            holder.tvTimer.setText(convertTimeoutValueToString(order.getOrder().getPickedUpAt() - order.getOrder().getLoadedAt()));
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            String endTimestampStr = formatter.format(new Date(Long.parseLong(order.getOrder().getPickedUpAt().toString())));
            String endTimestampStr2 = formatter2.format(new Date(Long.parseLong(order.getOrder().getPickedUpAt().toString())));
            String startTimestampStr = formatter.format(new Date(Long.parseLong(order.getOrder().getLoadedAt().toString())));
            long elapsedTimeString = TimeUtils.calculateElapsedTime(startTimestampStr, endTimestampStr);
            String time = timeFormat.format(new Date(Long.parseLong(order.getOrder().getPickedUpAt().toString())));
            String displayText = endTimestampStr2 + "\n" + time;
            holder.tvTimer.setText(displayText);
            holder.tvTimer2.setText(convertTimeoutValueToString(elapsedTimeString));

//            holder.handler = new Handler();
//            holder.updateTimeRunnable = new Runnable() {
//                @Override
//                public void run() {
//
//                    holder.handler.postDelayed(this, 1000);
//                }
//            };
//            holder.handler.post(holder.updateTimeRunnable);

        } else if (order.getOrder().getStatus().equals(OrderStatus.CANCELED)) {
//            holder.tvTimer.setText(convertTimeoutValueToString(order.getOrder().getCanceledAt() - order.getOrder().getLoadedAt()));
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String endTimestampStr = formatter.format(new Date(Long.parseLong(order.getOrder().getCanceledAt().toString())));
            String endTimestampStr2 = formatter2.format(new Date(Long.parseLong(order.getOrder().getCanceledAt().toString())));
            String startTimestampStr = formatter.format(new Date(Long.parseLong(order.getOrder().getLoadedAt().toString())));
            long elapsedTimeString = TimeUtils.calculateElapsedTime(startTimestampStr, endTimestampStr);

            String time = timeFormat.format(new Date(Long.parseLong(order.getOrder().getCanceledAt().toString())));
            String displayText = endTimestampStr2 + "\n" + time;
            holder.tvTimer.setText(displayText);
            holder.tvTimer2.setText(convertTimeoutValueToString(elapsedTimeString));

//             holder.handler = new Handler();
//             holder.updateTimeRunnable = new Runnable() {
//                 @Override
//                 public void run() {
//
//                     holder.handler.postDelayed(this, 1000);
//                 }
//             };
//             holder.handler.post(holder.updateTimeRunnable);
        } else if (order.getOrder().getStatus().equals(OrderStatus.LATE)) {
//            holder.tvTimer.setText(convertTimeoutValueToString(order.getOrder().getCanceledAt() - order.getOrder().getLoadedAt()));
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String endTimestampStr = formatter.format(new Date(Long.parseLong(order.getOrder().getCanceledAt().toString())));
            String startTimestampStr = formatter.format(new Date(Long.parseLong(order.getOrder().getLoadedAt().toString())));
            String endTimestampStr2 = formatter2.format(new Date(Long.parseLong(order.getOrder().getCanceledAt().toString())));
            long elapsedTimeString = TimeUtils.calculateElapsedTime(startTimestampStr, endTimestampStr);
            String time = timeFormat.format(new Date(Long.parseLong(order.getOrder().getCanceledAt().toString())));
            String displayText = endTimestampStr2 + "\n" + time;
            holder.tvTimer.setText(displayText);
            long timePassedSinceLoaded = System.currentTimeMillis() - order.getOrder().getCanceledAt();
            holder.tvTimer2.setText(convertTimeoutValueToString(timePassedSinceLoaded));

//             holder.handler = new Handler();
//             holder.updateTimeRunnable = new Runnable() {
//                 @Override
//                 public void run() {
//
//                     holder.handler.postDelayed(this, 1000);
//                 }
//             };
//             holder.handler.post(holder.updateTimeRunnable);
        }

        holder.itemView.setOnClickListener(view -> {

            selectedPosition2 = holder.getAdapterPosition();

            lastSelectedPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(lastSelectedPosition);
            notifyItemChanged(selectedPosition);
            editable = false;
            if (itemClickListener != null) {
                itemClickListener.onItemClick(order, holder.handler, holder.updateTimeRunnable);
            }
        });
    }

    private void handleLatePickupOrderBayAction(List<Bay> bays) {
        for (Bay bay : bays) {
            bay.setLed(preference.getLatePickupTimerLedColor());
            bayViewModel.update(bay);
            serialHelper.executeBayAction(bay);
        }
    }

    private String convertTimeoutValueToString(Long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        long remainingSeconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hours, remainingMinutes);
    }

    private String getBayNumbersFromBayList(List<Bay> bays) {
        //holder.tvBayNumbers.setText(order.getBays().stream().map(bay -> String.valueOf(bay.getCalibratedId())).collect(Collectors.joining(",")));
        StringBuilder bayNumbers = new StringBuilder();
        for (int i = 0; i < bays.size(); i++) {
            bayNumbers.append(bays.get(i).getCalibratedId());
            if (i < bays.size() - 1) {
                bayNumbers.append(",");
            }
        }
        return bayNumbers.toString();
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    public interface OnItemClickListener {
        void onItemClick(OrderWithCourierAndBays order, Handler handler, Runnable updateTimeRunnable);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        OrderWithCourierAndBays order;
        ImageView imageViewCourier;
        LinearLayout l;
        TextView tvOrderId, tvBayNumbers, tvTimer, tvTimer2, tvStatus, userName;
        Handler handler;
        Runnable updateTimeRunnable;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCourier = itemView.findViewById(R.id.imageView_list_item_order);
            tvOrderId = itemView.findViewById(R.id.tv_order_id_list_item_order);
            tvBayNumbers = itemView.findViewById(R.id.tv_bay_numbers_list_item_order);
            tvTimer = itemView.findViewById(R.id.tv_timer_list_item_order);
            tvTimer2 = itemView.findViewById(R.id.tv_timer_list_item_order2);
            tvStatus = itemView.findViewById(R.id.tv_status_list_item_order);
            userName = itemView.findViewById(R.id.tv_user_list_item_order);
            l = itemView.findViewById(R.id.parent_layout);

//            itemView.setOnClickListener(view -> {
//                lastSelectedPosition = selectedPosition;
//                selectedPosition = getAdapterPosition();
//                notifyItemChanged(lastSelectedPosition);
//                notifyItemChanged(selectedPosition);
//                if (itemClickListener != null) {
//                    itemClickListener.onItemClick(order, handler, updateTimeRunnable);
//                }
//            });
        }
    }
}
