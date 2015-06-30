package com.hobtimesocialapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Session;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;


public class AfterLoginActivity extends FragmentActivity implements View.OnClickListener {

    public static LinearLayout hobtimeOptionLL;
    TextView actionBarMessagesTV, shortCutViewLogoutTV, shortCutViewCreateActivityTV, shortCutViewCreateGroupTV, shortCutViewMyProfileMyTV, shortCutViewSupportTV, shortCutViewAboutTV, afteLoginActivityHeaderTV;
    public static boolean isShowingMenu = false;
    public static RelativeLayout actionBarHobtime;
    public static ImageView actionBarMoreIV, actionBarShareIV;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);
        hobtimeOptionLL = (LinearLayout) findViewById(R.id.hobtimeOptionLL);
        actionBarShareIV = (ImageView) findViewById(R.id.actionBarShareIV);
        actionBarMessagesTV = (TextView) findViewById(R.id.actionBarMessagesTV);
        actionBarShareIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareDialog();
            }
        });

        actionBarMoreIV = (ImageView) findViewById(R.id.actionBarMoreIV);
        actionBarMoreIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowingMenu) {
                    isShowingMenu = false;
                    hobtimeOptionLL.setVisibility(View.GONE);
                } else {
                    hobtimeOptionLL.setVisibility(View.VISIBLE);
                    hobtimeOptionLL.requestFocus();
                    isShowingMenu = true;
                }
            }
        });
        actionBarHobtime = (RelativeLayout) findViewById(R.id.actionBarHobtime);
        afteLoginActivityHeaderTV = (TextView) findViewById(R.id.afteLoginActivityHeaderTV);
        afteLoginActivityHeaderTV.setOnClickListener(this);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Cookies.ttf");
        afteLoginActivityHeaderTV.setTypeface(custom_font);
        shortCutViewCreateActivityTV = (TextView) findViewById(R.id.shortCutViewCreateActivityTV);
        shortCutViewCreateGroupTV = (TextView) findViewById(R.id.shortCutViewCreateGroupTV);
        shortCutViewMyProfileMyTV = (TextView) findViewById(R.id.shortCutViewMyProfileMyTV);
        shortCutViewSupportTV = (TextView) findViewById(R.id.shortCutViewSupportTV);
        shortCutViewSupportTV.setOnClickListener(this);
        shortCutViewAboutTV = (TextView) findViewById(R.id.shortCutViewAboutTV);
        shortCutViewLogoutTV = (TextView) findViewById(R.id.shortCutViewLogoutTV);
        shortCutViewLogoutTV.setOnClickListener(this);
        shortCutViewAboutTV.setOnClickListener(this);
        shortCutViewCreateActivityTV.setOnClickListener(this);
        shortCutViewCreateGroupTV.setOnClickListener(this);
        shortCutViewMyProfileMyTV.setOnClickListener(this);
        shortCutViewAboutTV.setOnClickListener(this);

        new GetMessageCountTask().execute();
        if (AppController.redirectUrl != null) {
            openNotificationPage(AppController.redirectUrl + "/&access_token=" + AppController.getToken(this));
        } else {
            AfterLoginFragment afterLoginFragment = AfterLoginFragment.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.afterLoginFragmentContainer, afterLoginFragment);
            fragmentTransaction.commit();
        }
        RelativeLayout actionBarMessagesRL = (RelativeLayout) findViewById(R.id.actionBarMessagesRL);
        actionBarMessagesRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareButton();
                String requestedPage = Constants.NOTIFICATIONS_LINK + "?access_token=" + AppController.getToken(AfterLoginActivity.this);
                WebViewFragment webViewFragment = WebViewFragment.newInstance(requestedPage);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.afterLoginFragmentContainer, webViewFragment);
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();
            }
        });

    }

    @Override
    protected void onResume() {

        super.onResume();

        new GetMessageCountTask().execute();
        if (AppController.redirectUrl != null) {
            openNotificationPage(AppController.redirectUrl + "/&access_token=" + AppController.getToken(this));
            AppController.redirectUrl = null;
        }
//        else if(AppController.redirectUrl ==null){
//            AfterLoginFragment afterLoginFragment = AfterLoginFragment.newInstance();
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.afterLoginFragmentContainer, afterLoginFragment);
//            fragmentTransaction.commit();
//        }
        RelativeLayout actionBarMessagesRL = (RelativeLayout) findViewById(R.id.actionBarMessagesRL);
        actionBarMessagesRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareButton();
                String requestedPage = Constants.NOTIFICATIONS_LINK + "?access_token=" + AppController.getToken(AfterLoginActivity.this);
                WebViewFragment webViewFragment = WebViewFragment.newInstance(requestedPage);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.afterLoginFragmentContainer, webViewFragment);
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();
            }
        });
    }

    private void showShareDialog() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, AppController.shareLink);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public static void showMoreButton() {
        actionBarMoreIV.setVisibility(View.VISIBLE);
        actionBarShareIV.setVisibility(View.GONE);
    }

    public static void showShareButton() {
        actionBarMoreIV.setVisibility(View.GONE);
        actionBarShareIV.setVisibility(View.VISIBLE);
    }

    public class GetMessageCountTask extends AsyncTask<Void, Void, Void> {
        InputStream inputStream = null;
        String result = "";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AfterLoginActivity.this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                String reqURL = Constants.BASE_URL + Constants.MESSAGE_COUNT;
                HttpPost httpPost = new HttpPost(reqURL);
                String json = "";
                JSONObject reqparams = new JSONObject();
                reqparams.accumulate("appId", "1966477342111");
                reqparams.accumulate("secret", Constants.secret);
                reqparams.accumulate("access_token", AppController.getToken(AfterLoginActivity.this));
                reqparams.accumulate("device_id", AppController.getRegistrationId(AfterLoginActivity.this));
                reqparams.accumulate("os_type", "android");
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
                JSONArray array = object.getJSONArray("data");
                message_count = array.length();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        actionBarMessagesTV.setText(String.valueOf(message_count));
                        Log.v("XXXXXXXXXXXX", "data sayısını bastı");
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        actionBarMessagesTV.setText("0");
                        Log.v("XXXXXXXXXXXX", "data sayısını basamaıd");
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    int message_count;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.shortCutViewAboutTV:
                closeShortcut();
                openWebViewFragment(Constants.INFO_PAGE_LINK);
                break;
            case R.id.shortCutViewCreateActivityTV:
                closeShortcut();
                openWebViewFragment(Constants.CREATE_EVENT_LINK);
                break;
            case R.id.shortCutViewCreateGroupTV:
                closeShortcut();
                openWebViewFragment(Constants.CREATE_GROUP_LINK);
                break;
            case R.id.shortCutViewMyProfileMyTV:
                closeShortcut();
                openWebViewFragment(Constants.PROFILE_PAGE_LINK);
                break;
            case R.id.shortCutViewSupportTV:
                closeShortcut();
                openWebViewFragment(Constants.TERMS);
                break;
            case R.id.afteLoginActivityHeaderTV:
                AfterLoginFragment afterLoginFragment = AfterLoginFragment.newInstance();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.afterLoginFragmentContainer, afterLoginFragment);
                fragmentTransaction.commit();
                break;
            case R.id.shortCutViewLogoutTV:
                doLogout();
                break;
        }
    }

    private void doLogout() {
        AppController.deleteToken(this);
        AppController.sdeleteFBToken(this);

        Session session = AppController.staticSession.getActiveSession();
        if (session != null)
            session.closeAndClearTokenInformation();
        Intent i = new Intent(AfterLoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public static void closeShortcut() {
        isShowingMenu = false;
        hobtimeOptionLL.setVisibility(View.GONE);
    }

    private void openNotificationPage(String url){

        actionBarHobtime.setBackgroundColor(getResources().getColor(R.color.hobtimeGreen));
        WebViewFragment webViewFragment = WebViewFragment.newInstance(url);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.afterLoginFragmentContainer, webViewFragment);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();

    }
    private void openWebViewFragment(String url) {

        showShareButton();
        actionBarHobtime.setBackgroundColor(getResources().getColor(R.color.hobtimeGreen));
        String requestedPage = url + "?access_token=" + AppController.getToken(this);
        AppController.shareLink = url;
        WebViewFragment webViewFragment = WebViewFragment.newInstance(requestedPage);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.afterLoginFragmentContainer, webViewFragment);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (AppController.redirectUrl != null) {
            AppController.redirectUrl = null;
            AfterLoginFragment afterLoginFragment = AfterLoginFragment.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.afterLoginFragmentContainer, afterLoginFragment);
            fragmentTransaction.commit();
        }
        if (WebViewFragment.webViewFragmentWV != null)
            if (WebViewFragment.webViewFragmentWV.copyBackForwardList().getCurrentIndex() > 0) {
                WebViewFragment.webViewFragmentWV.goBack();
            } else {
                // Your exit alert code, or alternatively line below to finish
                super.onBackPressed(); // finishes activity
            }
        else
            super.onBackPressed();
    }
}
