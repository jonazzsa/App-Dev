package com.example.jonazz.appdev;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class DetailsActivityFragment extends Fragment {

    public DetailsActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_details_activity, container, false);

        Intent intent = getActivity().getIntent();
        String imageExtra = intent.getStringExtra("extra_image");
        String titleExtra = intent.getStringExtra("extra_title");
        String locationExtra = intent.getStringExtra("extra_location");


        ImageView imageView = view.findViewById(R.id.details_image);
        TextView textTitle = view.findViewById(R.id.details_title);
        TextView textLocation = view.findViewById(R.id.details_location);

        textTitle.setText(getString(R.string.image_title,titleExtra));
        textLocation.setText(getString(R.string.image_location,locationExtra));

        Glide.with(getActivity())
                .load(imageExtra)
                .into(imageView);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
