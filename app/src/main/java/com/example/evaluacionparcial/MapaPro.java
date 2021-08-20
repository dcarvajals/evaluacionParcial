package com.example.evaluacionparcial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;

public class MapaPro extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowClickListener{

    GoogleMap  map;
    ModelPais pais;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_pro);

        Bundle bundle = this.getIntent().getExtras();
        String jsonResponse = (bundle.getString("DataPais"));

        Log.i("Logs", "DATOS RECIBIDOS => " + jsonResponse);

        JsonObject datos_pais = Methods.stringToJSON(jsonResponse);

        pais = new ModelPais();

        getPais(datos_pais);

        TextView nombre = findViewById(R.id.idnombrepais);
        nombre.setText(pais.getNombre());

        TextView datos = findViewById(R.id.iddatos);
        datos.setText(
                "Capital : " + pais.getCapital() +"\n"+
                "Code ISO 2 : " + pais.getIso2() +"\n"+
                "Tel Prefix : " + pais.getPref_tel() +"\n"+
                        "Code ISO 3 : " + pais.getIso3() +"\n"+
                        "Code FIPS : " + pais.getFips()
        );

        ImageView userImage = findViewById(R.id.iv_bandera);
        Glide.with(this)
                .load("http://www.geognos.com/api/en/countries/flag/"
                        + pais.getAlpha() + ".png")
                .into(userImage);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        Log.i("Logs", String.valueOf(pais.getRecnorth()));
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(pais.getRecnorth(), pais.getReceast()),
                        new LatLng(pais.getRecnorth(), pais.getRecwest()),
                        new LatLng(pais.getRecsouth(), pais.getRecwest()),
                        new LatLng(pais.getRecsouth(), pais.getReceast()),
                        new LatLng(pais.getRecnorth(), pais.getReceast())));
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.setTag("A");

    }

    public void getPais (JsonObject jsonPro) {
        //obtener los datos del pais encontrado
        pais.setNombre(Methods.JsonToString(jsonPro, "Name", ""));
        //obtener los datos de la capital del pais
        JsonObject jsonCapital = Methods.JsonToSubJSON(jsonPro, "Capital");
        pais.setCapital(Methods.JsonToString(jsonCapital, "Name", ""));
        //obtener los codigos del pais
        JsonObject jsonCode = Methods.JsonToSubJSON(jsonPro, "CountryCodes");
        pais.setIso2(Methods.JsonToString(jsonCode, "iso2", ""));
        pais.setIsonum(Methods.JsonToString(jsonCode, "isoN", ""));
        pais.setIso3(Methods.JsonToString(jsonCode, "iso3", ""));
        pais.setFips(Methods.JsonToString(jsonCode, "fips", ""));

        pais.setAlpha(Methods.JsonToString(jsonCode, "iso2", ""));

        //obtener los puntos para crear el cuadrado contenedor del pais
        JsonObject jsonRectangle = Methods.JsonToSubJSON(jsonPro, "GeoRectangle");
        Log.i("Logs", Methods.JsonToString(jsonRectangle, "West", ""));
        Log.i("Logs", Methods.JsonToString(jsonRectangle, "East", ""));
        Log.i("Logs", Methods.JsonToString(jsonRectangle, "North", ""));
        Log.i("Logs", Methods.JsonToString(jsonRectangle, "South", ""));
        pais.setRecwest(Double.parseDouble(Methods.JsonToString(jsonRectangle, "West", "")));
        pais.setReceast(Double.parseDouble(Methods.JsonToString(jsonRectangle, "East", "")));
        pais.setRecnorth(Double.parseDouble(Methods.JsonToString(jsonRectangle, "North", "")));
        pais.setRecsouth(Double.parseDouble(Methods.JsonToString(jsonRectangle, "South", "")));

        pais.setPref_tel(Methods.JsonToString(jsonPro, "TelPref", ""));
    }
}