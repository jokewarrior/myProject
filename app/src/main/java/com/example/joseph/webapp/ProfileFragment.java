package com.example.joseph.webapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    User user;
    LocalDb localDb;
    TextView name, reg, phone;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        localDb = new LocalDb(getActivity().getApplicationContext());
        if(localDb.checkLogin()){

            user = localDb.getUser();
            name = (TextView) view.findViewById(R.id.name);
            reg = (TextView) view.findViewById(R.id.reg);
            phone = (TextView) view.findViewById(R.id.phone);

            name.setText(user.name);
            reg.setText(user.regNo);
            phone.setText(user.phone);

        }
        return view;
    }

}
