package com.example.barbershop.UI;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.example.barbershop.R;

public class ViewImageExtended extends AppCompatDialogFragment {

    public Bitmap PICTURE_SELECTED;

    public ViewImageExtended() {
        // Required empty public constructor
    }

    public static ViewImageExtended newInstance(Bundle arguments) {
        Bundle args = arguments;
        ViewImageExtended fragment = new ViewImageExtended();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
        Bundle arguments = getArguments();
        PICTURE_SELECTED = arguments.getParcelable("PROFILE_PICTURE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_image_extended, container, false);

        ImageView ivImage = view.findViewById(R.id.ivImage);

        if (PICTURE_SELECTED != null)
            ivImage.setImageBitmap(PICTURE_SELECTED);

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}