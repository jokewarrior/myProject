package com.example.joseph.webapp;

import android.content.Context;
import android.content.SharedPreferences;

import static com.example.joseph.webapp.R.drawable.phone;

/**
 * Created by JOSEPH on 7/19/2017.
 */

public class LocalDb {

    public static final String sharedPrefs = "AllUserDetails";
    SharedPreferences localStore;


    public LocalDb(Context ctx){

        localStore = ctx.getSharedPreferences(sharedPrefs,0);

    }


    public void storeUser(User user){

        SharedPreferences.Editor spe = localStore.edit();
        spe.putInt("id",user.id);
        spe.putString("phone",user.phone);
        spe.putString("name",user.name);
        spe.putString("regNo",user.regNo);
        spe.putString("password",user.password);
        spe.commit();


    }

    public User getUser(){

        String name = localStore.getString("name","");
        String password = localStore.getString("password","");
        String regNo = localStore.getString("regNo","");
        int id = localStore.getInt("id",-1);
        String phone = localStore.getString("phone","");

        User alreadyStoredUser = new User(id,name,phone,regNo,password);
        return alreadyStoredUser;

    }


    public void logUserIn(boolean loggedIn){

        SharedPreferences.Editor spe = localStore.edit();
        spe.putBoolean("loggedIn",loggedIn);
        spe.commit();

    }

    public boolean checkLogin(){

        if(localStore.getBoolean("loggedIn",false)){
            return true;
        }
        else{
            return false;
        }

    }

    public void logout(){

        SharedPreferences.Editor spe = localStore.edit();
        spe.clear();
        spe.commit();

    }



}
