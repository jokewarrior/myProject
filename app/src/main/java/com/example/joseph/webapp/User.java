package com.example.joseph.webapp;

/**
 * Created by JOSEPH on 7/19/2017.
 */

public class User {

    int id;
    String name,regNo,password,phone;
    public User(int id, String name, String phone, String regNo, String password){

        this.id = id;
        this.name = name;
        this.phone = phone;
        this.regNo = regNo;
        this.password = password;

    }

}
