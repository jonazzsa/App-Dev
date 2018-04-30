package com.example.jonazz.appdev;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivityFragment extends Fragment {

    RecyclerView mRecyclerView;
    MainImagesAdapter mMainImagesAdapter;
    private final int PERMISSION_CHECK = 100;
    private final int REQUEST_CODE = 101;

    public MainActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_activity, container, false);


        Toolbar toolbar = view.findViewById(R.id.main_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        mRecyclerView = view.findViewById(R.id.main_recyclerview);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mMainImagesAdapter = new MainImagesAdapter();
        mMainImagesAdapter.setClick(new MainImagesAdapter.ClickListener() {
            @Override
            public void clickedItem(String location, String placeId) {
                startActivity(new Intent(getContext(), LocationImagesActivity.class)
                        .putExtra("extra_place_id", placeId)
                        .putExtra("extra_location", location));
            }
        });


        locationInitialize();


        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CHECK && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationInitialize();
        } else {
            Toast.makeText(getContext(), getString(R.string.permission_unallowed), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            locationInitialize();
        }
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
        private ProgressDialog progressDialog;
        public ImageDataAsyncTask() {
            super();
            progressDialog = new ProgressDialog(getContext());
        }

        @Override
        protected ArrayList doInBackground(String... params) {
            return downloadImageData(params[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.progress_message));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList o) {
            super.onPostExecute(o);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (o != null) {
                mMainImagesAdapter = new MainImagesAdapter(getContext(), o);
                mRecyclerView.setAdapter(mMainImagesAdapter);
            } else {
                Toast.makeText(getContext(), getString(R.string.general_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void locationInitialize() {
    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CHECK);
        } else {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!locationEnabled) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle(getString(R.string.alert_title));
                alertDialog.setMessage(getString(R.string.alert_message));
                alertDialog.setPositiveButton(getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                });

                alertDialog.setNegativeButton(getString(R.string.alert_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.create().show();
                return;
            }

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String cityName = addresses.get(0).getLocality();
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(cityName);
                                new ImageDataAsyncTask().
                                        execute("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=c1d3ffe7d4f65308ad0a70c716bb0ada&lat=" +
                                                location.getLatitude() + "&lon=" + location.getLongitude() + "&format=json&nojsoncallback=1");


                            }
                        }
                    });
        }
    }

    private ArrayList downloadImageData(String p1) {
        HttpURLConnection urlConnection;
        BufferedReader reader;
        URL url;

        try {

            url = new URL(p1);

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

            ArrayList<ArrayList<String>> photoIds = new ArrayList<>();
            try {
                JSONObject mainObject = new JSONObject(buffer.toString());
                JSONObject locObj = mainObject.getJSONObject("photos");
                JSONArray arrayObject = locObj.getJSONArray("photo");
                for (int i = 0; i < arrayObject.length(); i++) {
                    JSONObject finalObject = arrayObject.getJSONObject(i);
                    String photo = "https://farm" + finalObject.getString("farm") + ".staticflickr.com/" + finalObject.getString("server") + "/" + finalObject.getString("id") + "_" + finalObject.getString("secret") + ".jpg";
                    String title = finalObject.getString("title");
                    String id = finalObject.getString("id");
                    String owner = finalObject.getString("owner");
                    ArrayList<String> subData = new ArrayList<>();
                    subData.add(photo);
                    subData.add(title);
                    subData.add(id);
                    subData.add(owner);
                    photoIds.add(subData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return downloadExtraImageData(photoIds);
        } catch (Exception e) {
            return null;
        }
    }


    private ArrayList downloadExtraImageData(ArrayList<ArrayList<String>> photos) {
        HttpURLConnection urlConnection;
        BufferedReader reader;
        URL url;
        StringBuffer buffer;
        ArrayList<ArrayList<String>> mainData = new ArrayList<>();
        String imageTitle = "";
        String imageOwner = "";
        try {
            for (int i = 0; i < photos.size(); i++) {

                //avoiding possible redundancy
                if (imageOwner.equals(photos.get(i).get(3)) &&
                                photos.get(i).get(1).substring(0,5).equals(imageTitle.substring(0,5))) {

                    continue;
                }

                    //assign requested title for next iteration check
                    imageTitle = photos.get(i).get(1);
                    imageOwner = photos.get(i).get(3);

                    url = new URL("https://api.flickr.com/services/rest/?method=flickr.photos.geo.getLocation&api_key=c1d3ffe7d4f65308ad0a70c716bb0ada&photo_id=" + photos.get(i).get(2) + "&format=json&nojsoncallback=1");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    buffer = new StringBuffer();

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
                        JSONObject mainObj = new JSONObject(buffer.toString());
                        JSONObject locObj = mainObj.getJSONObject("photo").getJSONObject("location").getJSONObject("neighbourhood");
                        String locality = locObj.getString("_content");
                        String id = locObj.getString("place_id");


                        ArrayList<String> subData = new ArrayList<>();

                        if (!mainData.toString().contains(locality)) {
                            subData.add(photos.get(i).get(0));
                            subData.add(id);
                            subData.add(locality);
                            mainData.add(subData);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            }
            return mainData;
        } catch (Exception e) {
            return null;

        }
    }
}
