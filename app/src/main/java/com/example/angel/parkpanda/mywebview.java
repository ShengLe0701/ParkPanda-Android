package com.example.angel.parkpanda;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link mywebview.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link mywebview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mywebview extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    WebView webView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public mywebview() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mywebview.
     */
    // TODO: Rename and change types and number of parameters
    public static mywebview newInstance(String param1, String param2) {
        mywebview fragment = new mywebview();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_mywebview, container, false);


      //  webView = (WebView) view.findViewById(R.id.yourwebview);

        // force web view to open inside application
        //webView.setWebViewClient(new MyWebViewClient());
        /*webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setJavaScriptEnabled(true);
        openURL();*/


         String url = "http://maps.google.com/maps";
        webView = (WebView) view.findViewById(R.id.yourwebview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        //webView.loadUrl("https://maps.google.com/maps?" +"saddr=43.0054446,-87.9678884" + "&daddr=42.9257104,-88.0508355");





        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.equals("https://maps.google.com/maps")) {

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/maps?" + "saddr=43.0054446,-87.9678884" + "&daddr=42.9257104,-88.0508355"));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                } else {
                    //...
                }

                return true;
            }
        });


        return view;
    }

    private void openURL() {

        //String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", TRACKPOSITION.src_Pos.latitude, TRACKPOSITION.src_Pos.longitude, TRACKPOSITION.tar_Pos.latitude, TRACKPOSITION.tar_Pos.longitude);

        //String uri="http://maps.google.com/maps?" + "saddr=43.0054446,-87.9678884" + "&daddr=42.9257104,-88.0508355";
       // Log.d("###",uri);
        webView.loadUrl( "https://www.google.com/maps/@-27.6142357,-48.4828247,11z");

     //   webView.loadUrl(uri);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
