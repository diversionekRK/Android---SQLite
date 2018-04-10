package com.example.div.sqlite_test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Div on 2018-03-20.
 */

public class MyDBHelper extends SQLiteOpenHelper {
    private Context context;

    public final static int DB_VERSION = 3;
    public final static String DB_NAME = "baza_testowa";
    public final static String TABLE_NAME = "wartosci";
    public final static String ID_COLUMN = "_id";
    public final static String PRODUCER_COLUMN = "producer";
    public final static String MODEL_COLUMN = "model";
    public final static String VERSION_COLUMN = "version";
    public final static String URL_COLUMN = "url";
    public final static String TAG = "MyDBHelper";

    //kod generujący bazę
    public final static String DB_CREATION = "CREATE TABLE " + TABLE_NAME +
            "(" + ID_COLUMN + " integer primary key autoincrement, " +
            PRODUCER_COLUMN + " varchar(30) not null, " +
            MODEL_COLUMN + " varchar(30) not null, " +
            VERSION_COLUMN + " varchar(10) not null, " +
            URL_COLUMN + " varchar(50) not null);";

    //kod usuwający bazę
    public final static String DB_DELETION = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public MyDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_CREATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DB_DELETION);
        onCreate(sqLiteDatabase);
    }
}
