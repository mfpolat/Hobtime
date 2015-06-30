package com.hobtimesocialapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RegisterLastStepFragment extends Fragment implements View.OnClickListener {

    private static final String USEMAIL = "userEmail";
    private static final String USER_NAME = "userName";
    private static final String USER_PASS = "userPassword";
    private static final String USER_PIC = "profilePic";
    private String userEmail;
    private String userName;
    private String userPassword;
    private String profilePic;

    public static RegisterLastStepFragment newInstance(String mail, String name, String pass, String pic) {
        RegisterLastStepFragment fragment = new RegisterLastStepFragment();
        Bundle args = new Bundle();
        args.putString(USEMAIL, mail);
        args.putString(USER_NAME, name);
        args.putString(USER_PASS, pass);
        args.putString(USER_PIC, pic);
        fragment.setArguments(args);
        return fragment;
    }

    TextView registerLastFragmentHobtimeTV, registerLastFragmentGenderTV, registerLastFragmetYearTV, registerLastFragmentCityTV, registerLastFragmetFinisTV;
    View mView;
    Spinner registerLastFragmentGenderSP, registerLastFragmentYearSP, registerLastFragmentCitySP;
    List<String> genderList;
    String gender = "";
    int cityId = 0;
    String year = "";
    List<String> cityList;
    List<String> yearList;
    ArrayList<City> cities = new ArrayList<City>();
    boolean didCitySelected, didYearSelected, didGenderSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userEmail = getArguments().getString(USEMAIL);
            userName = getArguments().getString(USER_NAME);
            userPassword = getArguments().getString(USER_PASS);
            profilePic = getArguments().getString(USER_PIC);
        }
        genderList = new ArrayList<String>();
        cityList = new ArrayList<String>();
        yearList = new ArrayList<String>();
        genderList.add("-Select your gender-");
        genderList.add("Male");
        genderList.add("Female");
        yearList.add("-Select your age-");
        cityList.add("-Select your city-");

    }
     int a = 0;
     int b = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_register_last_step, container, false);
       /* registerLastFragmentCityTV = (TextView) mView.findViewById(R.id.registerLastFragmentCityTV);
        registerLastFragmentGenderTV = (TextView) mView.findViewById(R.id.registerLastFragmentGenderTV);*/
        registerLastFragmentHobtimeTV = (TextView) mView.findViewById(R.id.registerLastFragmentHobtimeTV);
        registerLastFragmetFinisTV = (TextView) mView.findViewById(R.id.registerLastFragmetFinisTV);
        /*registerLastFragmetYearTV = (TextView) mView.findViewById(R.id.registerLastFragmetYearTV);*/

        //  registerLastFragmetYearTV.setOnClickListener(this);
        registerLastFragmetFinisTV.setOnClickListener(this);
        //registerLastFragmentHobtimeTV.setOnClickListener(this);
        //registerLastFragmentGenderTV.setOnClickListener(this);
        //registerLastFragmentCityTV.setOnClickListener(this);
        //registerLastFragmetYearTV.setOnClickListener(this);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cookies.ttf");
        registerLastFragmentHobtimeTV.setTypeface(custom_font);
        registerLastFragmentCitySP = (Spinner) mView.findViewById(R.id.registerLastFragmentCitySP);
        registerLastFragmentGenderSP = (Spinner) mView.findViewById(R.id.registerLastFragmentGenderSP);
        registerLastFragmentYearSP = (Spinner) mView.findViewById(R.id.registerLastFragmentYearSP);
        final ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, genderList);
        registerLastFragmentGenderSP.setAdapter(genderAdapter);

        registerLastFragmentCitySP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(a<b){
                    a++;
                    return;
                }
                cityId = Integer.parseInt(cities.get(position-1).id);
                didCitySelected = true;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cityId = 0;
            }
        });
        registerLastFragmentYearSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = yearList.get(position);
                didYearSelected = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                year = "";
            }
        });
        registerLastFragmentGenderSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = genderList.get(position);
                didGenderSelected = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = "";
            }
        });
        new GetPagesTask().execute();
        setYearAdapter();
        return mView;
    }

    AlertDialog errorDialog;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.registerLastFragmetFinisTV:
                doLastStepRegister();
                break;

        }


    }

    private void doLastStepRegister() {
        if (gender.isEmpty() || year.isEmpty() || cityId == 0) {
            Toast.makeText(getActivity(), "You must select your Age ,Gender and City to complete registration", Toast.LENGTH_SHORT).show();
            return;
        } else if ( gender.equals("-Select your gender-") || year.equals("-Select your age-")) {
            Toast.makeText(getActivity(), "You must select your Age ,Gender and City to complete registration", Toast.LENGTH_SHORT).show();
            return;
        }
        new RegisterUserTask().execute();
    }


    public class RegisterUserTask extends AsyncTask<Void, Void, Void> {
        InputStream inputStream = null;
        String result = "";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                String reqURL = Constants.BASE_URL + Constants.REGISTER_MEMBER;
                HttpPost httpPost = new HttpPost(reqURL);
                String json = "";
                JSONObject reqparams = new JSONObject();
                reqparams.accumulate("appId", "1966477342111");
                reqparams.accumulate("secret", Constants.secret);
                reqparams.accumulate("email", userEmail);
                reqparams.accumulate("fullname", userName);
                reqparams.accumulate("password", userPassword);
                reqparams.accumulate("facebook_token", "");
                reqparams.accumulate("device_id", AppController.getRegistrationId(getActivity()));
                reqparams.accumulate("os_type", "and");
                reqparams.accumulate("gender", gender);
                reqparams.accumulate("birthdate", year);
                //reqparams.accumulate("profileTypeId", 1);
                //reqparams.accumulate("languageId", 1);
                reqparams.accumulate("pageId", cityId);
                reqparams.accumulate("photo", profilePic);
                json = reqparams.toString();
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                HttpResponse httpResponse = httpclient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null)
                    result = AppController.convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            try {
                JSONObject object = new JSONObject(result);
                String token = object.getString("access_token");
                AppController.storeToken(token, getActivity());
                Intent i = new Intent(getActivity(), AfterLoginActivity.class);
                startActivity(i);
                getActivity().finish();
            } catch (JSONException e) {

                e.printStackTrace();

                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject error = object.getJSONObject("error");
                    String message = error.getString("message");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Error");
                    builder.setMessage(message);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            errorDialog.dismiss();
                        }
                    });
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorDialog = builder.create();
                            errorDialog.show();
                        }
                    });

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    public class GetPagesTask extends AsyncTask<Void, Void, Void> {
        InputStream inputStream = null;
        String result = "";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                String reqURL = Constants.BASE_URL + Constants.PAGES;
                HttpPost httpPost = new HttpPost(reqURL);
                String json = "";
                JSONObject reqparams = new JSONObject();
                reqparams.accumulate("appId", "1966477342111");
                reqparams.accumulate("secret", Constants.secret);
                json = reqparams.toString();
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                HttpResponse httpResponse = httpclient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null)
                    result = AppController.convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            try {

                JSONArray jsonArray = new JSONArray(result);
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    JSONObject temp = jsonArray.getJSONObject(i);
                    String id = temp.getString("id");
                    String name = temp.getString("name");
                    String lat = temp.getString("lat");
                    String lng = temp.getString("lng");
                    String countryCode = temp.getString("countryCode");

                    City city = new City();
                    city.id = id;
                    city.lat = lat;
                    city.countryCode = countryCode;
                    city.name = name;
                    cityList.add(name);
                    city.lng = lng;
                    cities.add(city);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            if (cityList.size() > 0) {
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, cityList);
                registerLastFragmentCitySP.setAdapter(cityAdapter);
            }

        }
    }

    List<Integer> yearListInt = new ArrayList<Integer>();

    public void setYearAdapter() {
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        for (int i = 0; i < currentYear; i++) {
            yearListInt.add(currentYear - i);
            yearList.add(String.valueOf(currentYear - i));
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, yearList);
        registerLastFragmentYearSP.setAdapter(yearAdapter);
    }
}
