package com.example.khseob0715.sanfirst.navi_fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.khseob0715.sanfirst.R;

public class Fragment_MyDoctor extends Fragment {

    public Fragment_MyDoctor() {
        // Required empty public constructor
    }
    Fragment fragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_mydoctor, container, false);


/*
        Button updateBtn = (Button)rootView.findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"complete",Toast.LENGTH_SHORT).show();
            }
        });
*/

        return rootView;
    }

}
