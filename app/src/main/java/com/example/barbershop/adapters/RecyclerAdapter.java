package com.example.barbershop.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.barbershop.R;
import com.example.barbershop.controllers.Screen1;
import com.example.barbershop.models.Appointment;
import com.google.android.gms.common.data.DataHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    Context context;
    private ArrayList<Appointment> appointmentList;
    private View.OnClickListener listener;
    private View.OnLongClickListener longListener;
    private ArrayList<Appointment> orinalAppointmentList;
    private static Screen1.RecyclerViewClickListener itemListener;



    public RecyclerAdapter(Context context, ArrayList<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
        this.context = context;
        this.orinalAppointmentList = appointmentList;
    }


    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }
    public void setOnLongClickListener(View.OnLongClickListener longListener) {
        Log.e("LONG PRESSS", "PRESIONADO LARGO desde recycler");

        this.longListener = longListener;
    }
    @Override
    public boolean onLongClick(View view) {
        Log.e("LONG CLICKKKK", "LONG CLICK DESDE RECYCLER");
        if (longListener != null) {
            longListener.onLongClick(view);
        }
        return false;
    }
    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onClick(view);
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View appointmentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);

        appointmentView.setOnClickListener(this);
        appointmentView.setOnLongClickListener(this);

        return new MyViewHolder(appointmentView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerAdapter.MyViewHolder holder, int position) {
        String clientName = appointmentList.get(position).getClientName();
        String date = appointmentList.get(position).getDate();
        String state = appointmentList.get(position).getStatus();
        String hour = appointmentList.get(position).getHour();
        String image = appointmentList.get(position).getPhoto_link();
        holder.clientNameTxt.setText(clientName);
        holder.dateTxt.setText(date);
        holder.stateTxt.setText(state);
        holder.hourTxt.setText(hour);
        Glide.with(context).load(image).into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }



    public void updateList(ArrayList<Appointment> list){
        Log.e("Size", String.valueOf(list.size()));
        appointmentList = list;
        Log.e(" A A Size", String.valueOf(appointmentList.size()));
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView clientNameTxt;
        private final TextView dateTxt;
        private final TextView stateTxt;
        private final TextView hourTxt;
        private final ImageView imageView;



        public MyViewHolder(final View view) {
            super(view);
            clientNameTxt = view.findViewById(R.id.clientNameTxt);
            dateTxt = view.findViewById(R.id.dateTxt);
            stateTxt = view.findViewById(R.id.stateTxt);
            hourTxt = view.findViewById(R.id.hourTxt);
            imageView = view.findViewById(R.id.imageView);

        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ItemViewHolder(View convertView) {
            super(convertView);
            convertView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getPosition());

        }
    }


}


