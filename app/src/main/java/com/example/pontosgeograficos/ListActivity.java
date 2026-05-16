package com.example.pontosgeograficos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        String[] opcoes = {
                "Minha casa na cidade natal",
                "Minha casa em Viçosa",
                "Meu departamento",
                "Fechar aplicação"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                opcoes
        );

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Toast.makeText(ListActivity.this,
                                "Minha casa na cidade natal", Toast.LENGTH_SHORT).show();
                        abrirMapa(0);
                        break;
                    case 1:
                        Toast.makeText(ListActivity.this,
                                "Minha casa em Viçosa", Toast.LENGTH_SHORT).show();
                        abrirMapa(1);
                        break;
                    case 2:
                        Toast.makeText(ListActivity.this,
                                "Meu departamento", Toast.LENGTH_SHORT).show();
                        abrirMapa(2);
                        break;
                    case 3:
                        finish();
                        break;
                }
            }
        });
    }

    private void abrirMapa(int opcao) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("opcao", opcao);
        startActivity(intent);
    }
}