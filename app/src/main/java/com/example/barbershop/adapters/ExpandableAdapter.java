package com.example.barbershop.adapters;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.models.Appointment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ExpandableAdapter extends RecyclerView.Adapter<ExpandableAdapter.MyViewHolder> {

    Context context;
    private ArrayList<Appointment> appointmentList;

    public ExpandableAdapter(Context context, ArrayList<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @NotNull
    @Override
    public ExpandableAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View appointmentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expandable_item, parent, false);

        return new MyViewHolder(appointmentView);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExpandableAdapter.MyViewHolder holder, int position) {

        Appointment appointment = appointmentList.get(position);
        holder.dateTxt.setText(appointment.getDate());
        holder.hourTxt.setText(appointment.getHour());
        holder.clientNameTxt.setText(appointment.getClientName());

        boolean isVisible = appointment.getIsExpanded();
        holder.constraintlayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateTxt;
        TextView hourTxt;
        TextView clientNameTxt;

        ConstraintLayout constraintlayout;
        ConstraintLayout cardLayout;

        Button arrowBtn;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            hourTxt = itemView.findViewById(R.id.hourTxt);
            clientNameTxt = itemView.findViewById(R.id.clientNameTxt);

            constraintlayout = itemView.findViewById(R.id.expandedLayout);
            cardLayout = itemView.findViewById(R.id.card);
            arrowBtn = itemView.findViewById(R.id.arrowBtn);

            cardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Appointment appointment = appointmentList.get(getAdapterPosition());
                    appointment.setIsExpanded(!appointment.getIsExpanded());

                    if (constraintlayout.getVisibility() == View.VISIBLE) {
                        TransitionManager.beginDelayedTransition(cardLayout, new AutoTransition());
                        constraintlayout.setVisibility(View.VISIBLE);
                        arrowBtn.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    } else {
                        TransitionManager.beginDelayedTransition(cardLayout, new AutoTransition());
                        constraintlayout.setVisibility(View.GONE);
                        arrowBtn.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    }

                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
