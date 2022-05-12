package com.example.sosmessagesendapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class PhoneNumberDB extends SQLiteOpenHelper {

    public final static String tableName="phone_number";

    public PhoneNumberDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public String getTableName(){
        return tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE if not exists "+tableName+" (phoneNum VARCHAR(30));";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void onRemoveNumber(SQLiteDatabase db, String phone){
        String sql = "DELETE FROM "+tableName+" where phoneNum='"+phone+"'";
        db.execSQL(sql);
    }

    public void onInsertNumber(SQLiteDatabase db, String phoneNum){
        String sql = "INSERT INTO "+tableName+" (phoneNum) VALUES ('"+phoneNum+"');";
        db.execSQL(sql);
    }
}
