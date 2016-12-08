package com.example.jim.theoryquiz;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.graphics.PointF;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class Routes extends FragmentActivity implements OnMapReadyCallback, RssReader.CallBack_IF {

    private GoogleMap Map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Map = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        Map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        Map.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        RssReader.get(this);
    }

    public void onParse( RssReader rdr ) {

        float lat = 0, lng = 0, cnt = 0;
        for ( RssReader.Entry e : rdr.Dat ) {
            if( e.Points == null ||  e.Points.isEmpty() ) continue;

            PolylineOptions plo = new PolylineOptions()
                    .width(5)
                    .color(Color.RED);
            for( PointF p : e.Points ) {
                if( p.x == Float.NaN ) continue;
                lat += p.x;
                lng += p.y;
                cnt++;

                plo.add( new LatLng(p.x, p.y ) );
                // Map.addMarker(new MarkerOptions().position(ll).title(e.Title));
            }



            LatLng ll = new LatLng( e.Points.get(0).x, e.Points.get(0).y );
            Map.addMarker(new MarkerOptions().position(ll).title(e.Title));

            Polyline line = Map.addPolyline(plo);
        }

        if( cnt == 0 ) {
            lat = 55.865101f;
            lng = -4.433177f;
        } else {  //todo - averageing long latt really make sense?  -- wrap first ??
            cnt = 1.0f / cnt;
            lat *= cnt;
            lng *= cnt;
        }

        Map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14.0f));

    }
}
