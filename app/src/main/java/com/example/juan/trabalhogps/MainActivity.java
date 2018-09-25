package com.example.juan.trabalhogps;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private EditText etNome;
    private ImageView ivFoto;
    private Bitmap imageBitmap;
    private ArrayList<LatLng> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db =  ConexaoDB.getConnection(this);

        locations = new ArrayList<>();
        etNome = (EditText) findViewById(R.id.etNome);
        ivFoto = (ImageView) findViewById(R.id.ivFoto);

    }

    public void btTirarFotoOnClick(View view) {
        Intent i =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1){
            imageBitmap = (Bitmap) data.getExtras().get("data");
            ivFoto.setImageBitmap(imageBitmap);


        }
    }

    public void btSalvarRegistroOnClick(View view) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100, stream);
        byte imagemBytes[] = stream.toByteArray();

        ContentValues registro = new ContentValues();
        registro.put("nome", etNome.getText().toString());
        registro.put("foto", imagemBytes);
        db.insert("cadastro", null, registro);
        Toast.makeText(this, "Registro incluído com sucesso!", Toast.LENGTH_LONG).show();


    }

    public void btListrarOnClick(View view) {
        startActivity(new Intent(this, ListarActivity.class));
    }


    public void btnPegarLocalizacaoOnClick(View view) {
        locations.add(new LatLng(45, 20));
    }
}
