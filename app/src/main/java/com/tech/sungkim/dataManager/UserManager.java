package com.tech.sungkim.dataManager;


import android.content.Context;
import android.content.SharedPreferences;

import com.tech.sungkim.model.User;

public class UserManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static int PRIVATE_MODE = 1;
    private static final String PREFERENCE_NAME = "user_Data";

    private static final String NAME_USER = "nameUser";
    private static final String EMAIL_USER = "emailUser";
    private static final String BIRTH_USER = "birthUser";
    private static final String GENDER_USER = "genderUSER";
    private static final String KEY_USER = "keyUser";


    public UserManager(Context context) {//Intialize
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        editor = sharedPreferences.edit();
    }

    public void saveDataUser(String name, String email, int birth, String gender, String key){
        editor.putString(NAME_USER, name);
        editor.putString(EMAIL_USER, email);
        editor.putInt(BIRTH_USER, birth);
        editor.putString(GENDER_USER, gender);
        editor.putString(KEY_USER, key);
        editor.apply();
    }


    public User getUserData(){

        User user = new User();
        user.setId(sharedPreferences.getString(KEY_USER, null));
        user.setName(sharedPreferences.getString(NAME_USER, null));
        user.setEmail(sharedPreferences.getString(EMAIL_USER, null));
        user.setGender(sharedPreferences.getString(GENDER_USER, null));
        user.setAge(sharedPreferences.getInt(BIRTH_USER, 0));
        return user;
    }


    public void clearUserData(){
        sharedPreferences.edit().clear().apply();
    }



}
