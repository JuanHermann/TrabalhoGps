package com.example.juan.trabalhogps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MeuAdapter extends BaseAdapter{

    private Context c;
    private SQLiteDatabase db;
    private Cursor registros;

    LayoutInflater inflater;

    public MeuAdapter(Context c, SQLiteDatabase db){
        this.c = c;
        this.db = db;
        this.registros = db.query("cadastro",null, null, null, null, null, null, null);
        this.inflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return  registros.getCount();
    }

    @Override
    public Object getItem(int i) {
        registros.moveToPosition(i);
        return registros;
    }

    @Override
    public long getItemId(int i) {
        registros.moveToPosition(i);
        int id = registros.getInt(registros.getColumnIndex("id"));
        return id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = inflater.inflate (R.layout.elemento_listar,null);

        TextView tvDescricaoElemento = (TextView) v.findViewById(R.id.tvDescricaoListar);
        ImageView ivFotoElemento = (ImageView) v.findViewById(R.id.ivFotoListar);
        Button btnExcluir = (Button) v.findViewById(R.id.btnExcluir);

        registros.moveToPosition(i);

        String descricaoDb = registros.getString(registros.getColumnIndex("nome"));
        byte[] fotoDb = registros.getBlob(registros.getColumnIndex("foto"));
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(fotoDb, 0, fotoDb.length);
        tvDescricaoElemento.setText(descricaoDb);
        ivFotoElemento.setImageBitmap(bitmapImage);

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(registros.getString(registros.getColumnIndex("nome")));
            }
        });



        return v;
    }
}
