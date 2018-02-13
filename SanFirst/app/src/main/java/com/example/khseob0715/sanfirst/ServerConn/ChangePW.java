package com.example.khseob0715.sanfirst.ServerConn;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.khseob0715.sanfirst.UserActivity.LoginActivity;
import com.example.khseob0715.sanfirst.UserActivity.UserActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.khseob0715.sanfirst.UserActivity.FindPWNewActivity.FindPWNewContext;
import static com.example.khseob0715.sanfirst.UserActivity.UserActivity.UserActContext;

/**
 * Created by Kim Jin Hyuk on 2018-02-08.
 */

public class ChangePW {
    public static final String url = "http://teama-iot.calit2.net/changepwdapp";

    OkHttpClient client = new OkHttpClient();

    public static String responseBody = null;

    public void changepw_Asycn(final int usn, final String PW){
        (new AsyncTask<UserActivity, Void, String>(){

            @Override
            protected String doInBackground(UserActivity... mainActivities) {
                ChangePW.ConnectServer connectServerPost = new ChangePW.ConnectServer();
                connectServerPost.requestPost(url, usn, PW);
                return responseBody;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
            }
        }).execute();

        return ;
    }

    class ConnectServer {
        //Client 생성

        public int requestPost(String url, final int usn, String password) {

            //Request Body에 서버에 보낼 데이터 작성
            final RequestBody requestBody = new FormBody.Builder()
                    .add("usn", String.valueOf(usn))
                    .add("pwd", password).build();
            //RequestBody requestBody = new FormBody.Builder().add("email", id).add("password", password).build();

            Log.e("RequestBody", requestBody.toString());

            //작성한 Request Body와 데이터를 보낼 url을 Request에 붙임
            Request request = new Request.Builder().url(url).post(requestBody).build();

            //request를 Client에 세팅하고 Server로 부터 온 Response를 처리할 Callback 작성
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("error", "Connect Server Error is " + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        responseBody = response.body().string();
                        Log.e("aaaa", "Response Body is " + responseBody);

                        JSONObject jsonObject = new JSONObject(responseBody);
                        String Message = jsonObject.getString("message");

                        if(Message.equals("Success"))   {
                            changePW_Success();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Log.e("aaaa", "Response Body is " + response.body().string());

                }
            });
            return 0;
        }
    }


    public void changePW_Success() {
        /*
        Fragment fragment = null;
        //fragment = new Fragment_TabMain();
        fragment = new Fragment_Account();

        FragmentTransaction ft = UserActContext.getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
        */

        try {
            Intent LoginPage1 = new Intent(UserActContext.getApplicationContext(), LoginActivity.class);
            UserActContext.startActivity(LoginPage1);
        }   catch (NullPointerException e) {
            Intent LoginPage2 = new Intent(FindPWNewContext.getApplicationContext(), LoginActivity.class);
            LoginPage2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            FindPWNewContext.startActivities(new Intent[]{LoginPage2});
        }
    }
}
