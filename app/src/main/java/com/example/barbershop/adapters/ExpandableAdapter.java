package com.example.barbershop.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

        String image = appointmentList.get(position).getPhoto_link();
        Glide.with(context).load(image).into(holder.haircutImage);

        holder.dateTxt.setText(appointment.getDate());
        holder.hourTxt.setText(appointment.getHour());
        holder.clientNameTxt.setText(appointment.getClientName());

        String video = appointmentList.get(position).getVideo_link();

        if (!video.equals("NO_VIDEO")) {
            holder.videoView.setVideoURI(Uri.parse(video));

            holder.btnDownloadVideo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    downloadManager(video);
                }
            });
            holder.videoIcon.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.VISIBLE);
            holder.btnDownloadVideo.setVisibility(View.VISIBLE);

            holder.videoView.start();
        }
        else {
            holder.videoIcon.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.GONE);
            holder.btnDownloadVideo.setVisibility(View.GONE);

        }

        boolean isVisible = appointment.getIsExpanded();
        holder.constraintlayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void downloadManager(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("download");
        request.setTitle(url.replaceAll("\\p{Punct}", ""));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + url.replaceAll("\\p{Punct}", "") + ".mp4");

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView haircutImage;
        TextView dateTxt;
        TextView hourTxt;
        TextView clientNameTxt;

        ImageView videoIcon;
        Button btnDownloadVideo;
        VideoView videoView;

        ConstraintLayout constraintlayout;
        ConstraintLayout cardLayout;

        Button arrowBtn;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            haircutImage = itemView.findViewById(R.id.haircutImage);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            hourTxt = itemView.findViewById(R.id.hourTxt);
            clientNameTxt = itemView.findViewById(R.id.clientNameTxt);

            videoIcon = itemView.findViewById(R.id.videoIcon);
            btnDownloadVideo = itemView.findViewById(R.id.btnDownloadVideo);
            videoView = itemView.findViewById(R.id.videoView);

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
