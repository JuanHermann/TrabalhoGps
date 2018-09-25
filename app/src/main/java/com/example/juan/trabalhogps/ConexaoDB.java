package com.example.juan.trabalhogps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class ConexaoDB {

    private static SQLiteDatabase db;

    public static SQLiteDatabase getConnection (Context c){

        if (db == null){
            db = c.openOrCreateDatabase("trabalho", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS cadastro (_id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT,perimetro REAL,area REAL, foto BLOB)");
        }
        return db;
    }
}
