package com.example.barbershop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private ArrayList<Appointment> appointmentList;

    public RecyclerAdapter(ArrayList<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView clientNameTxt;
        private TextView dateTxt;
        private TextView stateTxt;

        public MyViewHolder(final View view) {
            super(view);
            clientNameTxt = view.findViewById(R.id.clientNameTxt);
            dateTxt = view.findViewById(R.id.dateTxt);
            stateTxt = view.findViewById(R.id.stateTxt);
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View appointmentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new MyViewHolder(appointmentView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerAdapter.MyViewHolder holder, int position) {
        String clientName = appointmentList.get(position).getClientName();
        String date = appointmentList.get(position).getDate();
        String state = appointmentList.get(position).getState();

        holder.clientNameTxt.setText(clientName);
        holder.dateTxt.setText(date);
        holder.stateTxt.setText(state);
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }
}
