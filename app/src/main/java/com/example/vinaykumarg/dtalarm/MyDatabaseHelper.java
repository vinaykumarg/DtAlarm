// Currently Not using this class


package com.example.vinaykumarg.dtalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * Created by Vinay Kumar G on 15-02-2016.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="MyDatabase";


    public MyDatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);

    }

    @Override

    public void onCreate(SQLiteDatabase database) {

        database.execSQL("CREATE TABLE desalert (source TEXT, dest TEXT, duration1 TEXT, distance1 TEXT, duration2 TEXT, distance2 TEXT, PRIMARY KEY (source, dest));");

    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS desalert");

        onCreate(db);

    }
    public void adddesalert(String source, String dest, String duration, String distance, String duration1, String distance1)

    {
        SQLiteDatabase database = getReadableDatabase();
        ContentValues values=new ContentValues(6);
        values.put("source", source);
        values.put("dest", dest);
        values.put("duration1", duration);
        values.put("distance1", distance);
        values.put("duration2", duration1);
        values.put("distance2", distance1);
        Cursor cursor = getReadableDatabase().rawQuery("select * from desalert",null);
        cursor.moveToFirst();
        boolean exists = false;
        while (cursor.moveToNext()) {
            if(cursor.getString(0).contains(source)&&cursor.getString(1).contains(dest)) {
                exists = true;
            }
        }
        cursor.close();
        if (!exists) {
            database.insert("desalert", null, values);
        }
    }
    public Cursor getevents()

    {
        Cursor cursor = getReadableDatabase().rawQuery("select * from desalert", null);
        return cursor;
    }
}