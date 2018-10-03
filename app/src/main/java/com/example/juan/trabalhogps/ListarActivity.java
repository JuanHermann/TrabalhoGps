package com.example.juan.trabalhogps;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

public class ListarActivity extends AppCompatActivity {

    private ListView lvRegistros;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);

        lvRegistros = (ListView) findViewById(R.id.lvRegistros);
        db =  ConexaoDB.getConnection(this);

        MeuAdapter adapter = new MeuAdapter (this, db);

        lvRegistros.setAdapter(adapter);
    }


}
