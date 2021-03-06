package com.example.khseob0715.sanfirst.ServerConn;

import android.os.AsyncTask;

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

public class SendAQIH {
    public static final String url = "http://teama-iot.calit2.net/aqiarrayapp";

    OkHttpClient client = new OkHttpClient();

    public static String responseBody = null;

    public void SendAQIH_Asycn(final int usn, final String datetime, final Double lat, final Double lon, final Double co , final Double so2, final Double no2, final Double o3, final Double pm25, final Double temp) {
        (new AsyncTask<UserActivity, Void, String>() {

            @Override
            protected String doInBackground(UserActivity... mainActivities) {
                SendAQIH.ConnectServer connectServerPost = new SendAQIH.ConnectServer();
                connectServerPost.requestPost(url, usn, datetime, lat, lon, co, so2, no2, o3, pm25, temp);
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

        return;
    }

    class ConnectServer {
        //Client 생성

        public void requestPost(String url, int usn, String datetime, Double lat, Double lon, Double co , Double so2, Double no2, Double o3, Double pm25, Double temp) {

            //Request Body에 서버에 보낼 데이터 작성
            final RequestBody requestBody = new FormBody.Builder()
                    .add("usn", String.valueOf(usn))
                    .add("ts", datetime)
                    .add("lat", String.valueOf(lat))
                    .add("lng", String.valueOf(lon))
                    .add("co", String.valueOf(co))
                    .add("so2", String.valueOf(so2))
                    .add("no2", String.valueOf(no2))
                    .add("o3", String.valueOf(o3))
                    .add("pm25", String.valueOf(pm25))
                    .add("tem", String.valueOf(temp))
                    .build();
            //RequestBody requestBody = new FormBody.Builder().add("email", id).add("password", password).build();


            //작성한 Request Body와 데이터를 보낼 url을 Request에 붙임
            Request request = new Request.Builder().url(url).post(requestBody).build();

            //request를 Client에 세팅하고 Server로 부터 온 Response를 처리할 Callback 작성
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        responseBody = response.body().string();

                        JSONObject jsonObject = new JSONObject(responseBody);
                        String Message = jsonObject.getString("message");

                        if (Message.equals("Success")) {

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }
}