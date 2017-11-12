package com.example.hardikdesaii.myfirebasework;

import android.app.ProgressDialog;
import android.content.Context;
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

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendMessage extends AppCompatActivity  {

    EditText message;
    Button send;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        message=(EditText)findViewById(R.id.tvmessage);
        send=(Button)findViewById(R.id.sendmessage);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag=validate();
                if(flag==true)
                {
                    startJSON();
                }
            }
        });
    }

    public boolean validate()
    {
        if(message.getText().length()<5)
        {
            message.setError("Enter proper message");
            message.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void startJSON() {


        String serverURL = "http://hardikdesaii.in.net/firebasework/push_notification_ashish.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("message",message.getText().toString() )
                .build();

        try {
            if (isNetworkAvailable()) {

                pd = new ProgressDialog(SendMessage.this);
                pd.setTitle("Sending Message");
                pd.setMessage("Please Wait");
                pd.show();
                post(serverURL, requestBody, new Callback() {


                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {

                        Log.e("onFailure", " " + e.getMessage());

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(SendMessage.this, " Error in sending message to Server , Please try again later.", Toast.LENGTH_LONG).show();
                                pd.dismiss();
                            }
                        });

                    } // onFailure ends here

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {

                        final String responseFromServer = response.body().string();
                        Log.e("responseFromServer", " " + responseFromServer);
                        Log.d("responseFromServer", " " + responseFromServer);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                pd.dismiss();


                                Toast.makeText(SendMessage.this, " " + responseFromServer, Toast.LENGTH_LONG).show();

                            }
                        });

                    } // onResonse ends here


                }); // post method ends here


            } else {
                Toast.makeText(SendMessage.this, " Enable data connectioin/wifi", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(SendMessage.this, "Exception in main try block", Toast.LENGTH_LONG).show();
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
