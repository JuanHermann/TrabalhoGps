package com.example.juan.trabalhogps;

import android.content.Context;
import android.content.Intent;
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

    //deleta linha
    public static SQLiteDatabase deleteItem(Integer id){
        db.delete("cadastro", "_id = ?", new String[]{Integer.toString(id)});
        return db;
    }
}
