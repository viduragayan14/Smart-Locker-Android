package com.example.smartlockerandroid.adapters;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlockerandroid.PreferencesActivity;
import com.example.smartlockerandroid.R;
import com.example.smartlockerandroid.UserSettingsActivity;
import com.example.smartlockerandroid.data.model.User;
import com.example.smartlockerandroid.data.viewmodel.UserViewModel;

import java.util.List;

/**
 * @author itschathurangaj on 6/22/23
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    public static PreferencesActivity activity;
    private List<User> userList;
    UserViewModel userViewModel;


    public UserListAdapter(PreferencesActivity activity, UserViewModel userViewModel) {
        UserListAdapter.activity = activity;
        this.userViewModel = userViewModel;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.user = user;
        holder.tvUserName.setText(user.getUsername());
        holder.createdOn.setText(user.getCreatedDate());
        holder.tvUserLevel.setText(user.getUserRole().toString());
        holder.btnDeleteUser.setOnClickListener(view -> {

            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.dialog_delete_record_layout);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            RelativeLayout btnDialogCancel = dialog.findViewById(R.id.btn_dialog_cancel);
            RelativeLayout btnDialogYes = dialog.findViewById(R.id.btn_dialog_yes);

            btnDialogCancel.setOnClickListener(viewCancel -> {
                dialog.dismiss();
            });

            btnDialogYes.setOnClickListener(viewYes -> {
                dialog.dismiss();
                handleDeleteUser(user.getUserId());
            });
            dialog.show();
        });
    }

    private void handleDeleteUser(Long userId) {
        userViewModel.deleteUser(userId);
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        User user;
        ImageView btnEditUser;
        ImageView btnDeleteUser;
        TextView tvUserName;
        TextView createdOn;
        TextView tvUserLevel;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnEditUser = itemView.findViewById(R.id.btn_edit_user_list_item_user);
            btnDeleteUser = itemView.findViewById(R.id.btn_delete_user_list_item_user);
            tvUserName = itemView.findViewById(R.id.tv_name_list_item_user);
            createdOn = itemView.findViewById(R.id.created_on);
            tvUserLevel = itemView.findViewById(R.id.tv_user_level_list_item_user);

            btnEditUser.setOnClickListener(view -> {
                Intent intent = new Intent(activity, UserSettingsActivity.class);
                intent.putExtra("flow", "UPDATE_USER");
                intent.putExtra("username", user.getUsername());
                activity.startActivity(intent);
            });

        }


    }
}
