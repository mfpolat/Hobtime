package com.hobtimesocialapp;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final String PERMISSION = "publish_actions";
    private View mView;
    private TextView loginFragmentForgetSıgnUpTV, loginFragmentForgetPassTV, loginFragmentLoginTV, fragmentLoginHobtimeTV, loginFragmentFBLoginTV;
    private EditText loginFragmentPasswordET, loginFragmentMailET;
    private String mail = "", password = "";
    private LoginButton loginButton;
    private GraphUser user;

    boolean isFBactive;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    private UiLifecycleHelper uiHelper;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        AppController.staticSession = Session.getActiveSession();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login, container, false);
        loginFragmentMailET = (EditText) mView.findViewById(R.id.loginFragmentMailET);
        loginFragmentPasswordET = (EditText) mView.findViewById(R.id.loginFragmentPasswordET);
        loginButton = (LoginButton) mView.findViewById(R.id.login_button);
        loginFragmentForgetPassTV = (TextView) mView.findViewById(R.id.loginFragmentForgetPassTV);
        loginFragmentForgetSıgnUpTV = (TextView) mView.findViewById(R.id.loginFragmentForgetSıgnUpTV);
        loginFragmentLoginTV = (TextView) mView.findViewById(R.id.loginFragmentLoginTV);
        loginFragmentForgetPassTV.setOnClickListener(this);
        loginFragmentForgetSıgnUpTV.setOnClickListener(this);
        loginFragmentLoginTV.setOnClickListener(this);
/*
        loginFragmentPasswordET.setText("asdasd");
        loginFragmentMailET.setText("nobranboy@windowslive.com");*/


        loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                LoginFragment.this.user = user;
            }
        });
        fragmentLoginHobtimeTV = (TextView) mView.findViewById(R.id.fragmentLoginHobtimeTV);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cookies.ttf");
        fragmentLoginHobtimeTV.setTypeface(custom_font);
        loginFragmentFBLoginTV = (TextView) mView.findViewById(R.id.loginFragmentFBLoginTV);
        loginFragmentFBLoginTV.setOnClickListener(this);
        return mView;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.loginFragmentLoginTV:
                doLogin();
                break;
            case R.id.loginFragmentForgetPassTV:
                openForgetPassWebPage();
                break;
            case R.id.loginFragmentForgetSıgnUpTV:
                openSignupFragment();
                break;
            case R.id.loginFragmentFBLoginTV:
                onClickLogin();
                break;
        }

    }

    private void openSignupFragment() {
        EmailSignUpFragment emailSignUpFragment = new EmailSignUpFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.replace(R.id.activityMainFragmentContainer, emailSignUpFragment);
        fragmentTransaction.commitAllowingStateLoss();

    }

    private void openForgetPassWebPage() {
        WebViewFragment webViewFragment = WebViewFragment.newInstance("http://www.hobtime.com/users/password");
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.replace(R.id.activityMainFragmentContainer, webViewFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void doFaceBookLogin() {

    }

    private void doLogin() {
        mail = loginFragmentMailET.getText().toString();
        password = loginFragmentPasswordET.getText().toString();
        if (mail.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Email and password can not be empty", Toast.LENGTH_SHORT).show();
            return;
        } else {
            new LoginTask().execute();
        }

    }
    AlertDialog errorDialog;

    public class LoginTask extends AsyncTask<Void, Void, Void> {
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
                String reqURL = Constants.BASE_URL + Constants.LOGIN_USER;
                HttpPost httpPost = new HttpPost(reqURL);
                String json = "";
                JSONObject reqparams = new JSONObject();
                reqparams.accumulate("appId", "1966477342111");
                reqparams.accumulate("secret", Constants.secret);
                if (!isFBactive) {
                    reqparams.accumulate("email", mail);
                    reqparams.accumulate("password", password);
                } else {
                    reqparams.accumulate("facebook_token", AppController.getFBToken(getActivity()));
                }
                reqparams.accumulate("device_id", AppController.getRegistrationId(getActivity()));
                reqparams.accumulate("os_type", "and");
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
                    String message  = error.getString("message");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);

    }


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            //  if (mSession == null || isSessionChanged(session)) {
            AppController.staticSession = session;
            String fbToken = session.getAccessToken();
            Log.v("Session opened FACEBOKK session token : ", session.getAccessToken());
            if (!fbToken.isEmpty()) {
                isFBactive = true;
                AppController.storeFBToken(fbToken, getActivity());
                new LoginTask().execute();
            }
            //}
        } else if (state.isClosed()) {
            Log.v("Session opened FACEBOKK session token : ", session.getAccessToken());
        }
    }

    private boolean isSessionChanged(Session session) {

        if (AppController.staticSession.getState() != session.getState())
            return true;
        if (AppController.staticSession.getAccessToken() != null) {
            if (!AppController.staticSession.getAccessToken().equals(session.getAccessToken()))
                return true;
        } else if (session.getAccessToken() != null) {
            return true;
        }
        return false;
    }

    private static final List<String> basic_permissions = Arrays.asList("email", "public_profile", "user_friends");

    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this)
                    .setPermissions(basic_permissions)
                    .setCallback(callback));
        } else {
            Session.openActiveSession(getActivity(), this, true, callback);
        }
    }
}
