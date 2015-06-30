package com.hobtimesocialapp;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmailSignUpFragment extends Fragment implements View.OnClickListener {


    private View mView;
    private EditText emailSignUpFragmentMailET, emailSignUpFragmentPasswordET;
    private TextView emailSignUpFragmentSignupTV, emailSignUpFragmentLoginTV, emailSignUpFragmentHobtimeTV;
    private CheckBox emailSignUpFragmentCB;
    String email, password;

    public EmailSignUpFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_email_sign_up, container, false);
        emailSignUpFragmentCB = (CheckBox) mView.findViewById(R.id.emailSignUpFragmentCB);
        emailSignUpFragmentCB.setOnClickListener(this);
        emailSignUpFragmentLoginTV = (TextView) mView.findViewById(R.id.emailSignUpFragmentLoginTV);
        emailSignUpFragmentLoginTV.setOnClickListener(this);
        emailSignUpFragmentSignupTV = (TextView) mView.findViewById(R.id.emailSignUpFragmentSignupTV);
        emailSignUpFragmentSignupTV.setOnClickListener(this);
        emailSignUpFragmentMailET = (EditText) mView.findViewById(R.id.emailSignUpFragmentMailET);
        emailSignUpFragmentPasswordET = (EditText) mView.findViewById(R.id.emailSignUpFragmentPasswordET);
        /*emailSignUpFragmentPasswordET.setText("asdasd");
        emailSignUpFragmentMailET.setText("nobranboy@windowslive.com");*/
        emailSignUpFragmentHobtimeTV = (TextView) mView.findViewById(R.id.emailSignUpFragmentHobtimeTV);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cookies.ttf");
        emailSignUpFragmentHobtimeTV.setTypeface(custom_font);
        return mView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emailSignUpFragmentSignupTV:
                doRegister();
                break;
            case R.id.emailSignUpFragmentLoginTV: {
                LoginFragment loginFragment = LoginFragment.newInstance();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.replace(R.id.activityMainFragmentContainer, loginFragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
            break;
            case R.id.emailSignUpFragmentCB:
                WebViewFragment webViewFragment = WebViewFragment.newInstance("http://www.hobtime.com/home/terms");
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.replace(R.id.activityMainFragmentContainer, webViewFragment);
                fragmentTransaction.commitAllowingStateLoss();
                break;
        }

    }

    private void doRegister() {
        email = emailSignUpFragmentMailET.getText().toString();
        password = emailSignUpFragmentPasswordET.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your email and password", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (AppController.isEmailValid(email)) {
                PersonalInfosFragment personalInfosFragment = PersonalInfosFragment.newInstance(email, password);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.replace(R.id.activityMainFragmentContainer, personalInfosFragment);
                fragmentTransaction.commitAllowingStateLoss();
            } else {
                Toast.makeText(getActivity(), "Email is invalid", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
