package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.Reclamacao;

public class ReclamacaoDAO extends SQLiteOpenHelper {

    private static final String DB_NAME = "reclamacoes.db";
    private static final int DB_VERSION = 3;
    private static final String TABLE_RECLAIMS = "reclamacoes";

    //COLUMN_NAMES
    private static final String ROW_ID = "id";
    private static final String ROW_DATA = "data";
    private static final String ROW_HORA = "hora";
    private static final String ROW_OBS = "obs";
    private static final String ROW_LATIDUDE = "latitude";
    private static final String ROW_LONGITUDE = "longitude";

    public ReclamacaoDAO (Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE "
                + TABLE_RECLAIMS + "("
                + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ROW_DATA + " TEXT,"
                + ROW_HORA + " TEXT,"
                + ROW_OBS + " TEXT,"
                + ROW_LATIDUDE + " TEXT,"
                + ROW_LONGITUDE + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECLAIMS);
        onCreate(db);
    }

    public void create(Reclamacao reclamacao) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ROW_DATA, reclamacao.getData());
        values.put(ROW_HORA, reclamacao.getHora());
        values.put(ROW_OBS, reclamacao.getObs());
        values.put(ROW_LATIDUDE, reclamacao.getLatitude());
        values.put(ROW_LONGITUDE, reclamacao.getLongitude());

        db.insert(TABLE_RECLAIMS, null, values);
        db.close();
    }

    public List<Reclamacao> getAll() {
        List<Reclamacao> reclamacoesList = new ArrayList<Reclamacao>();

        String selectQuery = "SELECT * FROM " + TABLE_RECLAIMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Reclamacao reclamacao = new Reclamacao();
                reclamacao.setId(Integer.parseInt(cursor.getString(0)));
                reclamacao.setData(cursor.getString(1));
                reclamacao.setHora(cursor.getString(2));
                reclamacao.setObs(cursor.getString(3));
                reclamacao.setLatitude(cursor.getString(4));
                reclamacao.setLongitude(cursor.getString(5));
                // Adding contact to list
                reclamacoesList.add(reclamacao);
            } while (cursor.moveToNext());
        }

        return reclamacoesList;
    }

    public Reclamacao retrieve(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECLAIMS, new String[] { ROW_ID, ROW_DATA, ROW_HORA, ROW_OBS, ROW_LATIDUDE, ROW_LONGITUDE },
                ROW_ID + "=?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null);
        Reclamacao reclamacao = null;
        if (cursor != null) {
            cursor.moveToFirst();
            reclamacao = new Reclamacao();
            reclamacao.setId(Integer.parseInt(cursor.getString(0)));
            reclamacao.setData(cursor.getString(1));
            reclamacao.setHora(cursor.getString(2));
            reclamacao.setObs(cursor.getString(3));
            reclamacao.setLatitude(cursor.getString(4));
            reclamacao.setLongitude(cursor.getString(5));
            return reclamacao;
        } else{
            throw new RuntimeException("Não existe");
        }

    }

    public int update(Reclamacao reclamacao) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ROW_DATA, reclamacao.getData());
        values.put(ROW_HORA, reclamacao.getHora());
        values.put(ROW_OBS, reclamacao.getObs());
        values.put(ROW_LATIDUDE, reclamacao.getLatitude());
        values.put(ROW_LONGITUDE, reclamacao.getLongitude());

        // updating row
        return db.update(TABLE_RECLAIMS, values,
                ROW_ID + " = ?",
                new String[] { String.valueOf(reclamacao.getId()) });
    }

    public void delete(Reclamacao reclamacao) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECLAIMS, ROW_ID + " = ?",
                new String[] { String.valueOf(reclamacao.getId()) });
        db.close();
    }
}
