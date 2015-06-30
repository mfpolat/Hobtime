package com.hobtimesocialapp;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LandingFragment extends Fragment implements View.OnClickListener {

    private  View mView;
    private TextView landingFragmentSignUpTV,landingFragmentLoginTV,fragmentLandingHobtimeTV;
    public static LandingFragment newInstance() {
        LandingFragment fragment = new LandingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public LandingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.fragment_landing, container, false);
        landingFragmentLoginTV =(TextView)mView.findViewById(R.id.landingFragmentLoginTV);
        landingFragmentLoginTV.setOnClickListener(this);
        landingFragmentSignUpTV =(TextView)mView.findViewById(R.id.landingFragmentSignUpTV);
        landingFragmentSignUpTV.setOnClickListener(this);
        fragmentLandingHobtimeTV =(TextView)mView.findViewById(R.id.fragmentLandingHobtimeTV);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cookies.ttf");
        fragmentLandingHobtimeTV.setTypeface(custom_font);

        PackageInfo info = null;
        try {
            info = getActivity().getPackageManager().getPackageInfo("com.hobtimesocialapp", PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        for (Signature signature : info.signatures)
        {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            md.update(signature.toByteArray());
            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
        }

        return mView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.landingFragmentSignUpTV:
                doSignUp();
                break;
            case R.id.landingFragmentLoginTV:
                doLogin();
                break;
        }

    }

    private void doLogin() {

        LoginFragment loginFragment = LoginFragment.newInstance();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.replace(R.id.activityMainFragmentContainer,loginFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void doSignUp() {
        SignUpFragment signUpFragment = SignUpFragment.newInstance();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.replace(R.id.activityMainFragmentContainer,signUpFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }
}
