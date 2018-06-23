package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.Task;

/**
 * Created by marceloquinta on 10/02/17.
 */

public class TaskDAO extends SQLiteOpenHelper {

    private static final String DB_NAME = "tasks.db";
    private static final int DB_VERSION = 3;
    private static final String TABLE_TASKS = "tasks";

    //COLUMN_NAMES
    private static final String ROW_ID = "id";
    private static final String ROW_NAME = "name";
    private static final String ROW_DESCRIPTION = "description";

    public TaskDAO (Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE "
                + TABLE_TASKS + "("
                + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ROW_NAME + " TEXT,"
                + ROW_DESCRIPTION + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public void create(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ROW_NAME, task.getName());
        values.put(ROW_DESCRIPTION, task.getDescription());

        db.insert(TABLE_TASKS, null, values);
        db.close();
    }

    public List<Task> getAll() {
        List<Task> taskList = new ArrayList<Task>();

        String selectQuery = "SELECT * FROM " + TABLE_TASKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(Integer.parseInt(cursor.getString(
                        0)));
                task.setName(cursor.getString(1));
                task.setDescription(cursor.getString(2));
                // Adding contact to list
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        return taskList;
    }

    public Task retrieve(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS, new String[] { ROW_ID,
                        ROW_NAME, ROW_DESCRIPTION },
                ROW_ID + "=?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null);
        Task task = null;
        if (cursor != null) {
            cursor.moveToFirst();
            task = new Task();
            task.setId(Integer.parseInt(cursor.getString(
                    0)));
            task.setName(cursor.getString(1));
            task.setDescription(cursor.getString(2));
            return task;
        } else{
            throw new RuntimeException("Não existe");
        }

    }

    public int update(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ROW_NAME, task.getName());
        values.put(ROW_DESCRIPTION, task.getDescription());

        // updating row
        return db.update(TABLE_TASKS, values,
                ROW_ID + " = ?",
                new String[] { String.valueOf(task.getId()) });
    }

    public void delete(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, ROW_ID + " = ?",
                new String[] { String.valueOf(task.getId()) });
        db.close();
    }
}
