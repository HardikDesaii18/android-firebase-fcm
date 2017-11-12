package com.example.hardikdesaii.myfirebasework;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by HardikDesaii on 03/02/17.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService
{
    String token;
    final String Myprefrences="Prefrences";
    public static final String TOKEN_NAME="token";
    public static final String FLAG="flag";
    @Override
    public void onTokenRefresh()
    {
        Log.e("firebase class" ,"on token refresh");
        token= FirebaseInstanceId.getInstance().getToken();
        Log.e("hardik"," "+token);
        storeToken(token);

    }
    public void storeToken(String token)
    {
        Log.e("firebase class" ,"shared prefrences");
        SharedPreferences prefrences=getSharedPreferences(Myprefrences, MODE_PRIVATE);

        SharedPreferences.Editor editor = prefrences.edit();

        editor.putString(TOKEN_NAME, token);
       // editor.putBoolean(FLAG,false);
        editor.commit();
    }

}
