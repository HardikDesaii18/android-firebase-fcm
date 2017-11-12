package com.example.hardikdesaii.myfirebasework;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
{
    TextView token1,token2;
    String token,dummytoken;
    EditText username,number;
    Button register;
    public static final String FLAG="flag";
    final String Myprefrences="Prefrences";
    public static final String TOKEN_NAME="token";
    ProgressDialog pd;
    SharedPreferences prefrences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefrences=getSharedPreferences(Myprefrences, MODE_PRIVATE);
        boolean flag=prefrences.getBoolean(FLAG,false);
        Toast.makeText(MainActivity.this,"shared prefrences boolean : "+flag,Toast.LENGTH_LONG).show();
        if(flag==true)
        {
            Intent intent=new Intent(MainActivity.this,SendMessage.class);
            startActivity(intent);

        }


        Log.e("main activity" ,"before token");
        dummytoken= FirebaseInstanceId.getInstance().getToken();
        Log.e("main activity" ,"after token");


        username=(EditText)findViewById(R.id.username);
        number=(EditText)findViewById(R.id.phonenumber);
        register=(Button)findViewById(R.id.register);
        token1=(TextView)findViewById(R.id.token1);
        token2=(TextView)findViewById(R.id.token2);
        token1.setText(dummytoken);

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean flag=validate();
                if(flag==true)
                {
                    SharedPreferences prefrences=getSharedPreferences(Myprefrences, MODE_PRIVATE);
                    token=prefrences.getString(TOKEN_NAME,"Null");
                    token2.setText(token);

                    startJSON();

                }

            }
        });
    }



    public boolean validate()
    {
        if(username.getText().length()<5)
        {
            username.setError("Must be of minimum 5 characters");
            username.requestFocus();
            return false;
        }
        if(!(number.length()==10))
        {
            number.setError("Must be of minimum 5 characters");
            number.requestFocus();
            return false;
        }
        return true;
    }
    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void startJSON()
    {

        String name=username.getText().toString();
        String contact=number.getText().toString();

        String serverURL = "http://hardikdesaii.in.net/firebasework/webservice.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("header_username","hardikdesai")
                .add("header_password","ets")
                .add("method","register")
                .add("username",name)
                .add("contact",contact)
                .add("token",token)
                .build();

        try
        {
            if(isNetworkAvailable())
            {

                pd = new ProgressDialog(MainActivity.this);
                pd.setTitle("Loading");
                pd.setMessage("Synchronizing Data , Please Wait");
                pd.show();
                post(serverURL,requestBody, new Callback()
                {


                    @Override
                    public void onFailure(okhttp3.Call call, IOException e)
                    {

                        Log.e("onFailure", " " + e.getMessage());

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run()
                            {
                                Toast.makeText(MainActivity.this," Error in Registration , Please try again later.",Toast.LENGTH_LONG).show();
                                pd.dismiss();
                            }
                        });

                    } // onFailure ends here

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException
                    {

                        SharedPreferences.Editor editor = prefrences.edit();

                        editor.putString(TOKEN_NAME, token);
                        editor.putBoolean(FLAG,true);
                        editor.commit();
                        final String responseFromServer = response.body().string();
                        Log.e("responseFromServer", " " + responseFromServer);


                        //parse the json
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                pd.dismiss();

                                Toast.makeText(MainActivity.this,"Registratioin successfull",Toast.LENGTH_LONG).show();
                                Toast.makeText(MainActivity.this," "+responseFromServer,Toast.LENGTH_LONG).show();

                                Intent intent=new Intent(MainActivity.this,SendMessage.class);
                                startActivity(intent);
                            }
                        });

                    } // onResonse ends here


                }); // post method ends here



            }
            else
            {
                Toast.makeText(MainActivity.this," Enable data connectioin/wifi",Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(MainActivity.this,"Exception in main try block",Toast.LENGTH_LONG).show();
        }
    } // startJSON ends here
    private final OkHttpClient client = new OkHttpClient();

    okhttp3.Call post(String url, RequestBody formBody, Callback callback) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        okhttp3.Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
} // main class ends here
