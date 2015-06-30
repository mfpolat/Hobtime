package com.hobtimesocialapp;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SignUpFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private TextView signUpFragmentEmailTV,signUpFragmentFBTV,signupFragmentHobtimeTV;

    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.fragment_sign_up, container, false);
        signUpFragmentFBTV =(TextView)mView.findViewById(R.id.signUpFragmentFBTV);
        signUpFragmentFBTV.setOnClickListener(this);
        signUpFragmentEmailTV =(TextView)mView.findViewById(R.id.signUpFragmentEmailTV);
        signUpFragmentEmailTV.setOnClickListener(this);
        signupFragmentHobtimeTV =(TextView)mView.findViewById(R.id.signupFragmentHobtimeTV);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cookies.ttf");
        signupFragmentHobtimeTV.setTypeface(custom_font);
        return mView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.signUpFragmentFBTV:
                doFaceBookLogin();
                break;
            case R.id.signUpFragmentEmailTV:
                doEmailSignUp();
                break;
        }
    }

    private void doEmailSignUp() {

        EmailSignUpFragment emailSignUpFragment =new  EmailSignUpFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.replace(R.id.activityMainFragmentContainer,emailSignUpFragment);
        fragmentTransaction.commitAllowingStateLoss();

    }

    private void doFaceBookLogin() {
        FacebookSignupFragment  facebookSignupFragment = FacebookSignupFragment.newInstance();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.replace(R.id.activityMainFragmentContainer,facebookSignupFragment );
        fragmentTransaction.commitAllowingStateLoss();
    }
}
