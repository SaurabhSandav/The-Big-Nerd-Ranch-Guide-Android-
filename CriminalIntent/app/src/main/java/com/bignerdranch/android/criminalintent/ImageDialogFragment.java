package com.bignerdranch.android.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by saurabh on 24/1/16.
 */
public class ImageDialogFragment extends DialogFragment{

    public static final String ARG_IMAGE = "image_path";

    public static ImageDialogFragment newInstance(File photoFile){
        Bundle args = new Bundle();
        args.putSerializable(ARG_IMAGE, photoFile);

        ImageDialogFragment fragment = new ImageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        File photoFile = (File) getArguments().getSerializable(ARG_IMAGE);
        Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity(), null);


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image, null);

        ImageView photoView = (ImageView) v.findViewById(R.id.dialog_crime_image);
        photoView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.title_image))
                .setView(v)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
