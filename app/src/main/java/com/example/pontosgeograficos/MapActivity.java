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
    private Marker marcadorAtual = null;

    // Coordenadas carregadas do banco
    private LatLng CARANDAI;
    private LatLng VICOSA;
    private LatLng CCE_UFV;

    private int opcaoInicial;
    private static final int REQUEST_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        opcaoInicial = getIntent().getIntExtra("opcao", 0);
        dbHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Carrega coordenadas do banco de dados
        double[] coordCarandai = dbHelper.getCoordenadasPorId(DatabaseHelper.ID_CARANDAI);
        double[] coordVicosa   = dbHelper.getCoordenadasPorId(DatabaseHelper.ID_VICOSA);
        double[] coordCCE      = dbHelper.getCoordenadasPorId(DatabaseHelper.ID_CCE_UFV);

        CARANDAI = new LatLng(coordCarandai[0], coordCarandai[1]);
        VICOSA   = new LatLng(coordVicosa[0],   coordVicosa[1]);
        CCE_UFV  = new LatLng(coordCCE[0],      coordCCE[1]);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button btnCarandai    = findViewById(R.id.btnCarandai);
        Button btnVicosa      = findViewById(R.id.btnVicosa);
        Button btnDPI         = findViewById(R.id.btnDPI);
        Button btnLocalizacao = findViewById(R.id.btnLocalizacao);

        btnCarandai.setOnClickListener(v -> centralizarMapa(0));
        btnVicosa.setOnClickListener(v -> centralizarMapa(1));
        btnDPI.setOnClickListener(v -> centralizarMapa(2));
        btnLocalizacao.setOnClickListener(v -> obterLocalizacaoAtual());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions()
                .position(CARANDAI).title("Minha casa em Carandaí"));
        mMap.addMarker(new MarkerOptions()
                .position(VICOSA).title("Minha casa em Viçosa"));
        mMap.addMarker(new MarkerOptions()
                .position(CCE_UFV).title("CCE/UFV"));

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                atualizarMarcadorAtual(location);
            } else {
                Toast.makeText(this,
                        "Não foi possível obter a localização.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarMarcadorAtual(Location location) {
        LatLng posicaoAtual = new LatLng(location.getLatitude(), location.getLongitude());

        if (marcadorAtual != null) {
            marcadorAtual.remove();
        }

        marcadorAtual = mMap.addMarker(new MarkerOptions()
                .position(posicaoAtual)
                .title("Minha localização atual")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicaoAtual, 17f));

        float[] resultado = new float[1];
        Location.distanceBetween(
                location.getLatitude(), location.getLongitude(),
                VICOSA.latitude, VICOSA.longitude,
                resultado
        );

        Toast.makeText(this,
                String.format("Você está a %.0f metros da sua casa em Viçosa", resultado[0]),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obterLocalizacaoAtual();
            } else {
                Toast.makeText(this,
                        "Permissão de localização negada.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
