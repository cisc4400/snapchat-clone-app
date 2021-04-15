package edu.fordham.snapchat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class UserViewHolder extends RecyclerView.ViewHolder {

    public final TextView emailTextView;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        emailTextView = itemView.findViewById(R.id.emailTextView);
    }
}
