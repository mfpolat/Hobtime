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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FacebookSignupFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private UiLifecycleHelper uiHelper;
    Session mSession;
    TextView fragmentFBSignupSelectCityTV, fragmentFBSignupNextTV;
    EditText fragmentFBSignupPassET;
    String password;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log.d("HelloFacebook", "Success!");
        }
    };

    public static FacebookSignupFragment newInstance() {
        FacebookSignupFragment fragment = new FacebookSignupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        mSession = Session.getActiveSession();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_facebook_signup, container, false);
        new GetPagesTask().execute();
        fragmentFBSignupSelectCityTV = (TextView) mView.findViewById(R.id.fragmentFBSignupSelectCityTV);
        fragmentFBSignupSelectCityTV.setOnClickListener(this);
        fragmentFBSignupNextTV = (TextView) mView.findViewById(R.id.fragmentFBSignupNextTV);
        fragmentFBSignupNextTV.setOnClickListener(this);
        fragmentFBSignupPassET = (EditText) mView.findViewById(R.id.fragmentFBSignupPassET);
        TextView facebookSignUpFragmentHobtimeTV = (TextView) mView.findViewById(R.id.facebookSignUpFragmentHobtimeTV);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cookies.ttf");
        facebookSignUpFragmentHobtimeTV.setTypeface(custom_font);
        return mView;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            AppController.staticSession = session;
            String fbToken = session.getAccessToken();
            Log.v("Session opened FACEBOKK session token : ", session.getAccessToken());
            if (!fbToken.isEmpty()) {
                AppController.storeFBToken(fbToken, getActivity());
                new RegisterWithFBTask().execute();
            }

        } else if (state.isClosed()) {
            Log.v("Session opened FACEBOKK session token : ", session.getAccessToken());
        }
    }

    private boolean isSessionChanged(Session session) {

        if (mSession.getState() != session.getState())
            return true;
        if (mSession.getAccessToken() != null) {
            if (!mSession.getAccessToken().equals(session.getAccessToken()))
                return true;
        } else if (session.getAccessToken() != null) {
            return true;
        }
        return false;
    }

    private static final List<String> basic_permissions = Arrays.asList("email", "public_profile", "user_friends");

    private void onClickLogin() {
        AppController.staticSession= Session.getActiveSession();
        if (!AppController.staticSession.isOpened() && !AppController.staticSession.isClosed()) {
            AppController.staticSession.openForRead(new Session.OpenRequest(this)
                    .setPermissions(basic_permissions)
                    .setCallback(callback));
        } else {
            Session.openActiveSession(getActivity(), this, true, callback);
        }
    }

    ArrayList<City> cities = new ArrayList<City>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragmentFBSignupNextTV:
                loginWithFB();
                break;
            case R.id.fragmentFBSignupSelectCityTV:
                showCitiesDialog();
                break;

        }
    }

    City selectedCity = new City();
    boolean didCitySelect;

    private void showCitiesDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle("Select City");
        String[] citiesArray = new String[cities.size()];
        for (int a = 0; a < citiesArray.length; a++) {
            citiesArray[a] = cities.get(a).name;
        }
        b.setItems(citiesArray, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                didCitySelect = true;
                selectedCity = cities.get(which);
                fragmentFBSignupSelectCityTV.setText(selectedCity.name);
            }

        });

        b.show();
    }

    private void loginWithFB() {

        password = fragmentFBSignupPassET.getText().toString();
        if (password.isEmpty()) {
            Toast.makeText(getActivity(), "Please type your password !", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!didCitySelect) {
            Toast.makeText(getActivity(), "Please select your city !", Toast.LENGTH_SHORT).show();
            return;
        }
        onClickLogin();

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
            dialog.dismiss();
            super.onPostExecute(aVoid);

        }
    }

    AlertDialog errorDialog;

    public class RegisterWithFBTask extends AsyncTask<Void, Void, Void> {
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
                reqparams.accumulate("password", password);
                reqparams.accumulate("facebook_token", AppController.getFBToken(getActivity()));
                reqparams.accumulate("device_id", AppController.getRegistrationId(getActivity()));
                reqparams.accumulate("os_type", "and");
                reqparams.accumulate("pageId", selectedCity.id);
                reqparams.accumulate("gender", "");
                reqparams.accumulate("birthdate", "");
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
                getActivity().finish();
                startActivity(i);

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

}
