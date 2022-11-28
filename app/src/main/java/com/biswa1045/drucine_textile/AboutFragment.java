package com.biswa1045.drucine_textile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            ViewGroup root = (ViewGroup) inflater.inflate(R.layout.about_fragment, container, false);

            return  root;
        }



}
