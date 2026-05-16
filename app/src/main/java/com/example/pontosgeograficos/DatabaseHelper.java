package com.example.pontosgeograficos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "pratica03.db";
    private static final int    DB_VERSION = 1;

    // IDs fixos das localizações na tabela Location
    public static final int ID_CARANDAI = 1;
    public static final int ID_VICOSA   = 2;
    public static final int ID_CCE_UFV  = 3;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Cria tabela Location
        db.execSQL("CREATE TABLE Location (" +
                "id        INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "descricao TEXT, " +
                "latitude  REAL, " +
                "longitude REAL" +
                ")");

        // Cria tabela Logs
        db.execSQL("CREATE TABLE Logs (" +
                "id          INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "msg         TEXT, " +
                "timestamp   TEXT, " +
                "id_location INTEGER, " +
                "FOREIGN KEY(id_location) REFERENCES Location(id)" +
                ")");

        // Popula a tabela Location com as três localizações fixas
        db.execSQL("INSERT INTO Location (descricao, latitude, longitude) VALUES " +
                "('Minha casa em Carandaí', -20.9669, -43.8003)");
        db.execSQL("INSERT INTO Location (descricao, latitude, longitude) VALUES " +
                "('Minha casa em Viçosa', -20.7546, -42.8825)");
        db.execSQL("INSERT INTO Location (descricao, latitude, longitude) VALUES " +
                "('CCE/UFV', -20.7613, -42.8691)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Logs");
        db.execSQL("DROP TABLE IF EXISTS Location");
        onCreate(db);
    }

    // Habilita suporte a chaves estrangeiras
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON");
    }

    // Insere um log quando o usuário clica no menu
    public void inserirLog(String msg, String timestamp, int idLocation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg", msg);
        values.put("timestamp", timestamp);
        values.put("id_location", idLocation);
        db.insert("Logs", null, values);
        db.close();
    }

    // Retorna coordenadas de uma localização pelo ID
    public double[] getCoordenadasPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT latitude, longitude FROM Location WHERE id = ?",
                new String[]{String.valueOf(id)}
        );
        double[] coords = {0, 0};
        if (cursor.moveToFirst()) {
            coords[0] = cursor.getDouble(0);
            coords[1] = cursor.getDouble(1);
        }
        cursor.close();
        db.close();
        return coords;
    }

    // Retorna todos os logs para a tela de relatório
    public List<String[]> getLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT l.id, l.msg, l.timestamp FROM Logs l ORDER BY l.id ASC",
                null
        );
        List<String[]> logs = new ArrayList<>();
        while (cursor.moveToNext()) {
            String[] row = new String[]{
                    cursor.getString(0), // id
                    cursor.getString(1), // msg
                    cursor.getString(2)  // timestamp
            };
            logs.add(row);
        }
        cursor.close();
        db.close();
        return logs;
    }

    // INNER JOIN: retorna latitude e longitude do local associado a um log
    public double[] getCoordsDoLog(int logId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT loc.latitude, loc.longitude " +
                        "FROM Logs l " +
                        "INNER JOIN Location loc ON l.id_location = loc.id " +
                        "WHERE l.id = ?",
                new String[]{String.valueOf(logId)}
        );
        double[] coords = {0, 0};
        if (cursor.moveToFirst()) {
            coords[0] = cursor.getDouble(0);
            coords[1] = cursor.getDouble(1);
        }
        cursor.close();
        db.close();
        return coords;
    }
}