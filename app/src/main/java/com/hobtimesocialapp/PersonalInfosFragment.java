package com.hobtimesocialapp;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class PersonalInfosFragment extends Fragment implements View.OnClickListener {

    private static final String EMAIL_PARAM = "email";
    private static final String PASSWORD_PARAM = "password";
    private String userEmail;
    private String userPassword;
    private String fullName;
    private String profilePicStr = "";
    private String firstName;
    private String lastName;
    private View mView;
    private EditText personelInfosFragmentFirstNameET, personelInfosFragmentLastNameeET;
    private TextView personalInfosFragmentTakePicTV, personalInfosFragmentOpenGalleryTV, personalInfosFragmentNextTV, personalInfosFragmentHobtimeTV;
    private ImageView personalInfosFragmentSelectedPicIV;
    private Bitmap profilcePic;

    public static PersonalInfosFragment newInstance(String email, String password) {
        PersonalInfosFragment fragment = new PersonalInfosFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL_PARAM, email);
        args.putString(PASSWORD_PARAM, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userEmail = getArguments().getString(EMAIL_PARAM);
            userPassword = getArguments().getString(PASSWORD_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_personal_infos, container, false);
        personalInfosFragmentNextTV = (TextView) mView.findViewById(R.id.personalInfosFragmentNextTV);
        personalInfosFragmentNextTV.setOnClickListener(this);
        personalInfosFragmentOpenGalleryTV = (TextView) mView.findViewById(R.id.personalInfosFragmentOpenGalleryTV);
        personalInfosFragmentOpenGalleryTV.setOnClickListener(this);
        personalInfosFragmentTakePicTV = (TextView) mView.findViewById(R.id.personalInfosFragmentTakePicTV);
        personalInfosFragmentTakePicTV.setOnClickListener(this);
        personalInfosFragmentSelectedPicIV = (ImageView) mView.findViewById(R.id.personalInfosFragmentSelectedPicIV);
        personelInfosFragmentFirstNameET = (EditText) mView.findViewById(R.id.personelInfosFragmentFirstNameET);
        personelInfosFragmentLastNameeET = (EditText) mView.findViewById(R.id.personelInfosFragmentLastNameeET);
       /* personelInfosFragmentFirstNameET.setText("asdasdasdasd");
        personelInfosFragmentLastNameeET.setText("asdasdasdsa");*/
        personalInfosFragmentHobtimeTV = (TextView) mView.findViewById(R.id.personalInfosFragmentHobtimeTV);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cookies.ttf");
        personalInfosFragmentHobtimeTV.setTypeface(custom_font);
        return mView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personalInfosFragmentNextTV:
                goNext();
                break;
            case R.id.personalInfosFragmentOpenGalleryTV:
                openImageGallery();
                break;
            case R.id.personalInfosFragmentTakePicTV:
                openCamera();
                break;
        }
    }

    int REQUEST_CAMERA = 1;
    int SELECT_FILE = 2;
    private static final int SELECT_PICTURE = 1;

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment
                .getExternalStorageDirectory(), "temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void openImageGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_FILE);

    }

    private void goNext() {
        firstName = personelInfosFragmentFirstNameET.getText().toString();
        lastName = personelInfosFragmentLastNameeET.getText().toString();
        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your fisrtname and last name", Toast.LENGTH_SHORT).show();
            return;
        } else {
            fullName = firstName + " " + lastName;
            doRegisterLastStep();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

                    bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            btmapOptions);
                    // bm = Bitmap.createScaledBitmap(bm, 70, 70, true);
                    personalInfosFragmentSelectedPicIV.setImageBitmap(bm);
                    profilcePic = bm;
                    if (profilcePic != null) ;
                    profilePicStr = AppController.encodeTobase64(profilcePic);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream fOut = null;
                    File file = new File(path, String.valueOf(System
                            .currentTimeMillis()) + ".jpg");
                    try {
                        fOut = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE) {

                String selectedImagePath;
                Uri selectedImageUri = data.getData();

                if (Build.VERSION.SDK_INT < 19) {
                    selectedImagePath = getPath(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
                    // imageView.setImageBitmap(bitmap);
                    personalInfosFragmentSelectedPicIV.setImageBitmap(bitmap);

                } else {
                    ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(selectedImageUri, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;
                        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                        profilcePic = image;
                        parcelFileDescriptor.close();
                        //imageView.setImageBitmap(image);
                        personalInfosFragmentSelectedPicIV.setImageBitmap(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                //personalInfosFragmentSelectedPicIV.setImageBitmap(bm);
                //  profilcePic = bm;
                if (profilcePic != null)
                    profilePicStr = AppController.encodeTobase64(profilcePic);
            }
        }
    }


    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    private void doRegisterLastStep() {

        RegisterLastStepFragment registerLastStepFragment = RegisterLastStepFragment.newInstance(userEmail, fullName, userPassword, profilePicStr);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.replace(R.id.activityMainFragmentContainer, registerLastStepFragment);
        fragmentTransaction.commitAllowingStateLoss();

    }


}
