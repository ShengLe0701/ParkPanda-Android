package com.example.angel.parkpanda;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.Firebase.ResultHandler;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link change_password.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link change_password#newInstance} factory method to
 * create an instance of this fragment.
 */
public class change_password extends Fragment implements  View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private Firebase firebaseRef;

    String strFIREPATH="https://parkpanda-1372.firebaseio.com/";
    private FirebaseAuth firebaseAuth;
    EditText et_password,et_repassword;
    Button btn_ok;


    public change_password() {
        // Required empty public constructor
    }

    public static change_password newInstance(String param1, String param2) {
        change_password fragment = new change_password();
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
        View view= inflater.inflate(R.layout.fragment_change_password, container, false);

        Firebase.setAndroidContext(getContext());
        firebaseRef = new Firebase(strFIREPATH);
        firebaseAuth = FirebaseAuth.getInstance();

        initGUI(view);
        return view;
    }
    public void initGUI(View view)
    {
            et_password= (EditText) view.findViewById(R.id.txt_chn_newpassword);
            et_repassword= (EditText) view.findViewById(R.id.txt_chn_confirmpassword);

            btn_ok= (Button) view.findViewById(R.id.btn_chn_ok);

            btn_ok.setOnClickListener(this);
    }

    public boolean checkField()
    {
        int int_result=10;
        if(et_password.length()<8)
        {
            int_result++;
            et_password.setError("incorrect");
        }
        if(et_repassword.length()<8)
        {
            int_result++;
            et_repassword.setError("incorrect");
        }

        if((et_repassword.length()>7)&&(et_password.length()>7))
        {
            String st1=et_repassword.getText().toString();
            String st2=et_password.getText().toString();
            if(st1.equals(st2)) {}
            else
            {
                int_result++;
                et_repassword.setError("incorrect");
            }
        }

        if(int_result==10) return true;
        else return false;
    }
    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.btn_chn_ok)
        {
            if(checkField())
            {
                sendDataToDataBase();

                Fragment fragment = new my_account();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction FT = fragmentManager.beginTransaction();
                FT.setCustomAnimations(R.anim.enter_from_right,
                        R.anim.exit_to_left);
                FT.replace(R.id.flContent, fragment).commit();
            }
        }
    }
    public void sendDataToDataBase()
    {

        String strNewpass=et_password.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(strNewpass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        } else {
                        }
                    }
                });
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
