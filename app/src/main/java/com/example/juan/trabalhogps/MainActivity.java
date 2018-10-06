package com.example.juan.trabalhogps;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private SQLiteDatabase db;
    private EditText etNome;
    private ImageView ivFoto;
    private Bitmap imageBitmap;
    private ArrayList<LatLng> locais;
    private Spinner spMapa,spZoom;
    private static final String PREF_NAME = "pref";
    private int mapa,zoom;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private double lat =0,lon=0;
    private Button btnBuscarMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db =  ConexaoDB.getConnection(this);

        locais = new ArrayList<>();
        etNome = (EditText) findViewById(R.id.etNome);
        ivFoto = (ImageView) findViewById(R.id.ivFoto);
        spMapa = (Spinner) findViewById(R.id.spMapa);
        spZoom = (Spinner) findViewById(R.id.spZoom);
        btnBuscarMapa = (Button) findViewById(R.id.btnBuscarMapa);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);

        setarOpcoes();

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, this);


        ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},1);

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
        registro.put("perimetro",pegarPerimetro());
        registro.put("area",pegarArea());
        registro.put("foto", imagemBytes);
        db.insert("cadastro", null, registro);
        Toast.makeText(this, "Registro incluído com sucesso!", Toast.LENGTH_LONG).show();

        etNome.setText("");
        locais = new ArrayList<>();
    }


    public void btListrarOnClick(View view) {
        startActivity(new Intent(this, ListarActivity.class));
    }


    public void btnPegarLocalizacaoOnClick(View view) {
        locais.add(new LatLng(lat, lon));

        if(locais.size()>=3 && btnBuscarMapa.isEnabled()==false){
            btnBuscarMapa.setEnabled(true);
        }

        mostrarLocais();
    }

    private void mostrarLocais() {
        String t = "";
        for (int numPontos = 0;numPontos<locais.size();numPontos++) {
            t = t + "Localização " + (numPontos + 1) + "\nlat :" + locais.get(numPontos).latitude + " | long: " + locais.get(numPontos).latitude + "\n";
        }

        Toast.makeText(this,t , Toast.LENGTH_LONG).show();
    }

    private void setarOpcoes() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.zoomSpinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spZoom.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.mapaSpinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMapa.setAdapter(adapter2);

        spMapa.setOnTouchListener(spinnerOnTouch);
        spZoom.setOnTouchListener(spinnerOnTouch);

        SharedPreferences settings = this.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        zoom = settings.getInt("zoom",0);
        mapa = settings.getInt("mapa",0);

        spMapa.setSelection(mapa);
        spZoom.setSelection(zoom);
    }



    private void persistirOpcoes(int zoom,int mapa){
        SharedPreferences settings = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt("mapa", mapa);
        editor.putInt("zoom", zoom);

        editor.commit();
    }
    public  double pegarPerimetro(){
        double peri =0.0;
        for(int i =0;i<locais.size();i++){
            if(i+1==locais.size()){
                peri = peri + calcularDistancia(locais.get(0).latitude,locais.get(0).longitude,locais.get(i).latitude,locais.get(i).longitude);
            }else{
                peri = peri + calcularDistancia(locais.get(i).latitude,locais.get(i).longitude,locais.get(i).latitude,locais.get(i).longitude);
            }
        }
        return peri;
    }

    public double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371; // Radius of the earth
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;

    }

    private double pegarArea() {
        return 5;
    }

    private View.OnTouchListener spinnerOnTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                persistirOpcoes(spZoom.getSelectedItemPosition(),spMapa.getSelectedItemPosition());
            }
            return false;
        }
    };

    @Override
    public void onLocationChanged(Location location) {

        lat = location.getLatitude();
        lon = location.getLongitude();

        tvLatitude.setText(String.valueOf( location.getLatitude()));
        tvLongitude.setText(String.valueOf( location.getLongitude()));

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void btnBuscarMapaOnClick(View view) {
        new ConexaoThread().start();
    }

    public void btnExcluirLocalizacaoOnClick(View view) {


         int i =locais.size();

         if(i>0) {
             locais.remove(i - 1);
             Toast.makeText(this, "Ultima localização excluida!", Toast.LENGTH_LONG).show();
         }else{
             Toast.makeText(this, "Nenhum ponto para excluir!", Toast.LENGTH_LONG).show();

         }
        if(locais.size()<3 && btnBuscarMapa.isEnabled()==true){
            btnBuscarMapa.setEnabled(false);
        }

    }

    class ConexaoThread extends Thread{
        public void run(){



            try {

                String latitude = tvLatitude.getText().toString();
                String longitude = tvLongitude.getText().toString();

                String endereco = "http://maps.googleapis.com/maps/api/staticmap?center=45,45.2+8&zoom=15&size=900x900&sensor=false";

                URL url = new URL(endereco);
                URLConnection con = url.openConnection();

                InputStream in = con.getInputStream();

                final Bitmap bmp = BitmapFactory.decodeStream(in);

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivFoto.setImageBitmap(bmp);

                    }
                });


            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
