package com.hobtimesocialapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;


public class WebViewFragment extends Fragment {

    private static final String PAGE_URL = "pageUrl";
    private String pageUrl;
    public static WebView webViewFragmentWV;
    private View mView;
    private static final int FILECHOOSER_RESULTCODE = 2888;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;

    public static WebViewFragment newInstance(String url) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(PAGE_URL, url);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageUrl = getArguments().getString(PAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_web_view, container, false);
        webViewFragmentWV = (WebView) mView.findViewById(R.id.webViewFragmentWV);
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        if (AfterLoginActivity.actionBarHobtime != null)
            AfterLoginActivity.actionBarHobtime.setBackgroundColor(getActivity().getResources().getColor(R.color.hobtimeGreen));
        if (AfterLoginActivity.actionBarShareIV != null)
            AfterLoginActivity.showShareButton();
        webViewFragmentWV.loadUrl(pageUrl);
        webViewFragmentWV.getSettings().setJavaScriptEnabled(true);
        progressDialog.setMessage("Loading...");
        webViewFragmentWV.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                AppController.shareLink = url;
                if (url.startsWith("mailto:")) {
                    url = url.replaceFirst("mailto:", "");
                    url = url.trim();
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("plain/text").putExtra(Intent.EXTRA_EMAIL, new String[]{url});
                    getActivity().startActivity(i);
                } else if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse(url));
                    startActivity(intent);
                } else if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                } else if (url.startsWith("whatsapp://send?")) {
                    String[] splits = url.split("\\=");
//                            "whatsapp://send?text=http://www.hobtime.com/event-Sergi-Gezisi/653/C1273e887b88A8D";
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, splits[1]);
                    try {
                        getActivity().startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }

        });
        webViewFragmentWV.setWebChromeClient(new FileChooserChromeClient());
        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return;
            Uri result = data == null || resultCode != Activity.RESULT_OK ? null
                    : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    private class FileChooserChromeClient extends WebChromeClient {

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

            // Update message
            mUploadMessage = uploadMsg;

            try {

                // Create AndroidExampleFolder at sdcard

                File imageStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES)
                        , "AndroidExampleFolder");

                if (!imageStorageDir.exists()) {
                    // Create AndroidExampleFolder at sdcard
                    imageStorageDir.mkdirs();
                }

                // Create camera captured image file path and name
                File file = new File(
                        imageStorageDir + File.separator + "IMG_"
                                + String.valueOf(System.currentTimeMillis())
                                + ".jpg");

                mCapturedImageURI = Uri.fromFile(file);

                // Camera capture image intent
                final Intent captureIntent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");

                // Create file chooser intent
                Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

                // Set camera intent to file chooser
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                        , new Parcelable[]{captureIntent});

                // On select image call onActivityResult method of activity
                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

            } catch (Exception e) {
                Toast.makeText(getActivity(), "Exception:" + e,
                        Toast.LENGTH_LONG).show();
            }

        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {

            openFileChooser(uploadMsg, acceptType);
        }


        // The webPage has 2 filechoosers and will send a
        // console message informing what action to perform,
        // taking a photo or updating the file

        public boolean onConsoleMessage(ConsoleMessage cm) {

            onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
            return true;
        }

        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            //Log.d("androidruntime", "Show console messages, Used for debugging: " + message);

        }
    }
}
