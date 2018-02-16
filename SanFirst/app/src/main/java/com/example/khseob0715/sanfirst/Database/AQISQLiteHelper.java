package com.example.khseob0715.sanfirst.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AQISQLiteHelper {

    public int dbMode = Context.MODE_PRIVATE;

    // Database 생성 및 열기
    //public SQLiteDatabase db;

    // Table 생성
    public  void AQIcreateTable(SQLiteDatabase db){
        String AQI_CT = "create table if not exists AQI_HISTORY(usn INTEGER, ts TEXT, Lat DOUBLE, Lng DOUBLE, Co DOUBLE, So2 DOUBLE, No2 DOUBLE, O3  DOUBLE, PM25 DOUBLE, Temp DOUBLE);";
        db.execSQL(AQI_CT);
        Log.e("SQLite-AQI_CreateTable", AQI_CT);
    }

    // Table 삭제
    public  void AQIdropTable(SQLiteDatabase db){
        String AQI_DT = "drop table if exists AQI_HISTORY;";
        db.execSQL(AQI_DT);
        Log.e("SQLite-AQI_removeTable", AQI_DT);
    }

    // Data 추가
    public  void AQIinsertData(SQLiteDatabase db, int usn, String ts, double lat, double lng, double co, double so2, double no2, double o3, double pm25, double temp){
        String AQI_ID = "insert into AQI_HISTORY values(" + usn +", '" + ts + "',"+lat+","+lng+","+co+","+so2+","+no2+","+o3+","+pm25+","+temp+")";
        db.execSQL(AQI_ID);
        Log.e("SQLite", AQI_ID);
    }

    //--------------------------insert 확인
    public  void AQIselectAll(SQLiteDatabase db){
        String sql = "select * from AQI_HISTORY ;";
        Cursor results = db.rawQuery(sql, null);

        results.moveToFirst();

        while(!results.isAfterLast()){
            int usn = results.getInt(0);
            String ts = results.getString(1);
            Double lat = results.getDouble(2);
            Double lng = results.getDouble(3);
            Double co = results.getDouble(4);
            Double so2 = results.getDouble(5);
            Double no2 = results.getDouble(6);
            Double o3 = results.getDouble(7);
            Double pm25 = results.getDouble(8);
            Double temp = results.getDouble(9);
            Log.e("Select All = ", usn +"/"+ ts +"/"+ lat +"/"+ lng +"/"+ co +"/"+so2 +"/"+no2 +"/"+o3+"/"+pm25+"/"+temp);
            results.moveToNext();
        }
        results.close();
    }
}