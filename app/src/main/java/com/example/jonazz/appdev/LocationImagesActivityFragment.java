package com.example.jonazz.appdev;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class LocationImagesActivityFragment extends Fragment {
    RecyclerView mRecyclerView;
    LocationImagesAdapter mLocationImagesAdapter;

    static String placeId;
    static String locationExtra;

    public LocationImagesActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_images_activity, container, false);

        Intent intent = getActivity().getIntent();
        locationExtra = intent.getStringExtra("extra_location");
        placeId = intent.getStringExtra("extra_place_id");

        Toolbar toolbar = view.findViewById(R.id.location_activity_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(locationExtra);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ;

        mRecyclerView = view.findViewById(R.id.location_activity_recyclerview);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mLocationImagesAdapter = new LocationImagesAdapter();
        mLocationImagesAdapter.setClick(new LocationImagesAdapter.ClickListener() {
            @Override
            public void clickedItem(String image, String title) {
                startActivity(new Intent(getContext(), DetailsActivity.class)
                        .putExtra("extra_image", image)
                        .putExtra("extra_title", title)
                        .putExtra("extra_location", locationExtra));
            }
        });


        new LocationImagesActivityFragment.ImageDataAsyncTask().execute();

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


    class ImageDataAsyncTask extends AsyncTask<String, Void, ArrayList> {
        public ImageDataAsyncTask() {
            super();
        }

        @Override
        protected ArrayList doInBackground(String... params) {
            return downloadImageData();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList o) {
            super.onPostExecute(o);
            if (o != null) {
                mLocationImagesAdapter = new LocationImagesAdapter(getContext(), o);
                mRecyclerView.setAdapter(mLocationImagesAdapter);
            } else {
                Toast.makeText(getContext(), getString(R.string.general_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayList downloadImageData() {
        HttpURLConnection urlConnection;
        BufferedReader reader;
        URL url;

        ArrayList<ArrayList<String>> mainData = new ArrayList<>();
        try {

            url = new URL("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=c1d3ffe7d4f65308ad0a70c716bb0ada&&format=json&place_id=" + placeId + "&nojsoncallback=1");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }


            try {

                JSONObject weatherObj = new JSONObject(buffer.toString());
                JSONObject main = weatherObj.getJSONObject("photos");
                JSONArray arrayObject = main.getJSONArray("photo");
                for (int i = 0; i < arrayObject.length(); i++) {
                    JSONObject finalObject = arrayObject.getJSONObject(i);

                    String photo = "https://farm" + finalObject.getString("farm") + ".staticflickr.com/" + finalObject.getString("server") + "/" + finalObject.getString("id") + "_" + finalObject.getString("secret") + ".jpg";
                    String title = finalObject.getString("title");
                    ArrayList<String> subData = new ArrayList<>();
                    subData.add(photo);
                    subData.add(title);
                    mainData.add(subData);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mainData;


        } catch (Exception e) {
            return null;
        }
    }


}
