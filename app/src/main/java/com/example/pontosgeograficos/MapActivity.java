package com.example.pontosgeograficos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // Coordenadas das três localizações fixas
    private final LatLng CARANDAI = new LatLng(-20.9669, -43.8003);
    private final LatLng VICOSA   = new LatLng(-20.7546, -42.8825);
    private final LatLng CCE_UFV  = new LatLng(-20.7613, -42.8691);

    // Marcador da posição atual (azul) — começa nulo
    private Marker marcadorAtual = null;

    private int opcaoInicial;

    private static final int REQUEST_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        opcaoInicial = getIntent().getIntExtra("opcao", 0);

        // Cliente de localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Inicializa o mapa
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Botões de navegação entre locais
        Button btnCarandai = findViewById(R.id.btnCarandai);
        Button btnVicosa   = findViewById(R.id.btnVicosa);
        Button btnDPI      = findViewById(R.id.btnDPI);
        Button btnLocalizacao = findViewById(R.id.btnLocalizacao);

        btnCarandai.setOnClickListener(v -> centralizarMapa(0));
        btnVicosa.setOnClickListener(v -> centralizarMapa(1));
        btnDPI.setOnClickListener(v -> centralizarMapa(2));

        // Botão de localização atual
        btnLocalizacao.setOnClickListener(v -> obterLocalizacaoAtual());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Adiciona os três marcadores fixos (vermelhos)
        mMap.addMarker(new MarkerOptions()
                .position(CARANDAI).title("Minha casa em Carandaí"));
        mMap.addMarker(new MarkerOptions()
                .position(VICOSA).title("Minha casa em Viçosa"));
        mMap.addMarker(new MarkerOptions()
                .position(CCE_UFV).title("CCE/UFV"));

        // Centraliza no marcador da opção escolhida no menu
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

    private void obterLocalizacaoAtual() {
        // Verifica se tem permissão
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicita permissão ao usuário
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            return;
        }

        // Obtém última localização conhecida
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                atualizarMarcadorAtual(location);
            } else {
                Toast.makeText(this,
                        "Não foi possível obter a localização. Tente novamente.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarMarcadorAtual(Location location) {
        LatLng posicaoAtual = new LatLng(location.getLatitude(), location.getLongitude());

        // Remove marcador azul anterior, se existir
        if (marcadorAtual != null) {
            marcadorAtual.remove();
        }

        // Adiciona novo marcador azul
        marcadorAtual = mMap.addMarker(new MarkerOptions()
                .position(posicaoAtual)
                .title("Minha localização atual")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // Centraliza o mapa na posição atual
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicaoAtual, 17f));

        // Calcula distância até a casa em Viçosa
        float[] resultado = new float[1];
        Location.distanceBetween(
                location.getLatitude(), location.getLongitude(),
                VICOSA.latitude, VICOSA.longitude,
                resultado
        );
        float distancia = resultado[0];

        Toast.makeText(this,
                String.format("Você está a %.0f metros da sua casa em Viçosa", distancia),
                Toast.LENGTH_LONG).show();
    }

    // Callback da resposta do usuário ao pedido de permissão
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obterLocalizacaoAtual(); // tenta novamente com permissão concedida
            } else {
                Toast.makeText(this,
                        "Permissão de localização negada.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
