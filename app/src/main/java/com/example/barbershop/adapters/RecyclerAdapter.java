package com.example.barbershop.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.barbershop.R;
import com.example.barbershop.controllers.Screen2;
import com.example.barbershop.controllers.Screen4;
import com.example.barbershop.models.Appointment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private ArrayList<Appointment> appointmentList;
    Context context;

    public RecyclerAdapter(Context context, ArrayList<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView clientNameTxt;
        private TextView dateTxt;
        private TextView stateTxt;
        private TextView hourTxt;
        private ImageView imageView;

        public MyViewHolder(final View view) {
            super(view);
            clientNameTxt = view.findViewById(R.id.clientNameTxt);
            dateTxt = view.findViewById(R.id.dateTxt);
            stateTxt = view.findViewById(R.id.stateTxt);
            hourTxt = view.findViewById(R.id.hourTxt);
            imageView = view.findViewById(R.id.imageView);

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
        String state = appointmentList.get(position).getStatus();
        String hour = appointmentList.get(position).getHour();
        String image =  appointmentList.get(position).getPhoto_link();
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
}
