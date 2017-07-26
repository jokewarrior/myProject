package com.example.joseph.webapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    User user;
    LocalDb localDb;
    TextView name, reg;

    public DashboardFragment() {
        // Required empty public constructor
    }

    private void viewUsersDetails(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        localDb = new LocalDb(getActivity().getApplicationContext());
        if(localDb.checkLogin()){

            user = localDb.getUser();
            name = (TextView) view.findViewById(R.id.name);
            reg = (TextView) view.findViewById(R.id.reg);

            name.setText(user.name);
            reg.setText(user.regNo);

        }
        return view;
    }

}
