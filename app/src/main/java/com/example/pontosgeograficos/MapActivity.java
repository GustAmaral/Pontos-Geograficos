package com.example.pontosgeograficos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // Coordenadas das três localizações
    private final LatLng CARANDAI  = new LatLng(-20.9669, -43.8003);
    private final LatLng VICOSA    = new LatLng(-20.7546, -42.8825);
    private final LatLng CCE_UFV   = new LatLng(-20.7613, -42.8691);

    private int opcaoInicial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        opcaoInicial = getIntent().getIntExtra("opcao", 0);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button btnCarandai = findViewById(R.id.btnCarandai);
        Button btnVicosa   = findViewById(R.id.btnVicosa);
        Button btnDPI      = findViewById(R.id.btnDPI);

        btnCarandai.setOnClickListener(v -> centralizarMapa(0));
        btnVicosa.setOnClickListener(v -> centralizarMapa(1));
        btnDPI.setOnClickListener(v -> centralizarMapa(2));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Adiciona os três marcadores fixos
        mMap.addMarker(new MarkerOptions()
                .position(CARANDAI).title("Minha casa em Carandaí"));
        mMap.addMarker(new MarkerOptions()
                .position(VICOSA).title("Minha casa em Viçosa"));
        mMap.addMarker(new MarkerOptions()
                .position(CCE_UFV).title("CCE/UFV"));

        // Centraliza no marcador correspondente à opção escolhida
        centralizarMapa(opcaoInicial);
    }

    private void centralizarMapa(int opcao) {
        if (mMap == null) return;

        LatLng destino;
        switch (opcao) {
            case 1:  destino = VICOSA;   break;
            case 2:  destino = CCE_UFV;  break;
            default: destino = CARANDAI; break;
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destino, 15f));
    }
}
