package com.example.pontosgeograficos;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private List<String[]> logs; // cada item: {id, msg, timestamp}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        dbHelper = new DatabaseHelper(this);
        logs = dbHelper.getLogs();

        // Monta lista de strings "msg - timestamp"
        List<String> itens = new ArrayList<>();
        for (String[] row : logs) {
            itens.add(row[1] + " - " + row[2]); // msg - timestamp
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                itens
        );

        ListView listView = findViewById(R.id.listViewReport);
        listView.setAdapter(adapter);

        // Ao clicar num item, mostra lat/lon via INNER JOIN
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int logId = Integer.parseInt(logs.get(position)[0]);
                double[] coords = dbHelper.getCoordsDoLog(logId);

                Toast.makeText(ReportActivity.this,
                        String.format("Latitude: %.4f | Longitude: %.4f",
                                coords[0], coords[1]),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}