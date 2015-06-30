package com.hobtimesocialapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class AfterLoginFragment extends Fragment implements View.OnClickListener {

    TextView hobtimeMainFragmentNewTV, hobtimeMainFragmentIdeasTV, hobtimeMainFragmentActivitiesTV, hobtimeMainFragmentGroupsTV, hobtimeMainFragmentComunityTV, hobtimeMainFragmentCockpitTV;
    View mView;

    public static AfterLoginFragment newInstance() {
        AfterLoginFragment fragment = new AfterLoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_after_login, container, false);
        AfterLoginActivity.showMoreButton();
        hobtimeMainFragmentNewTV = (TextView) mView.findViewById(R.id.hobtimeMainFragmentNewTV);
        hobtimeMainFragmentIdeasTV = (TextView) mView.findViewById(R.id.hobtimeMainFragmentIdeasTV);
        hobtimeMainFragmentActivitiesTV = (TextView) mView.findViewById(R.id.hobtimeMainFragmentActivitiesTV);
        hobtimeMainFragmentGroupsTV = (TextView) mView.findViewById(R.id.hobtimeMainFragmentGroupsTV);
        hobtimeMainFragmentComunityTV = (TextView) mView.findViewById(R.id.hobtimeMainFragmentComunityTV);
        hobtimeMainFragmentCockpitTV = (TextView) mView.findViewById(R.id.hobtimeMainFragmentCockpitTV);
AfterLoginActivity.actionBarHobtime.setBackgroundColor(getActivity().getResources().getColor(R.color.mainbarcolor));
        hobtimeMainFragmentNewTV.setOnClickListener(this);
        hobtimeMainFragmentIdeasTV.setOnClickListener(this);
        hobtimeMainFragmentActivitiesTV.setOnClickListener(this);
        hobtimeMainFragmentGroupsTV.setOnClickListener(this);
        hobtimeMainFragmentComunityTV.setOnClickListener(this);
        hobtimeMainFragmentCockpitTV.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hobtimeMainFragmentActivitiesTV:
                openWebViewFragment(Constants.ACTIVITIES_LINK);
                break;
            case R.id.hobtimeMainFragmentCockpitTV:
                openWebViewFragment(Constants.COCKPIT_LINK);
                break;
            case R.id.hobtimeMainFragmentComunityTV:
                openWebViewFragment(Constants.NEW_LINK);
                break;
            case R.id.hobtimeMainFragmentGroupsTV:
                openWebViewFragment(Constants.GROUPS_LINK);
                break;
            case R.id.hobtimeMainFragmentIdeasTV:
                openWebViewFragment(Constants.TIPS_LINK);
                break;
            case R.id.hobtimeMainFragmentNewTV:
                openWebViewFragment(Constants.IDEAS_CALENDER_LINK);
                break;
        }
    }

    private void openWebViewFragment(String url) {

        AfterLoginActivity.closeShortcut();
        String requestedPage = url + "?access_token=" + AppController.getToken(getActivity());
        AppController.shareLink = url;
        WebViewFragment webViewFragment = WebViewFragment.newInstance(requestedPage);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.afterLoginFragmentContainer, webViewFragment);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();


    }
}
