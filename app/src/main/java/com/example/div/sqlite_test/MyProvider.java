package com.example.div.sqlite_test;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Div on 2018-03-20.
 */

public class MyProvider extends ContentProvider {
    private MyDBHelper myDBHelper;

    //identyfikator (authority) dostawcy
    public final static String AUTHORITY = "com.example.div.sqlite_test.MyProvider";

    //stała - aby nie trzeba było wpisywać tekstu samodzielnie
    public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY +
            "/" + MyDBHelper.TABLE_NAME);

    //stałe pozwalające zidentyfikować rodzaj rozpoznanego URI
    public final static int WHOLE_TABLE = 1;
    public final static int ONE_ROW = 2;

    //URiMatcher z pustym korzeniem drzewa URI (NO_MATCH)
    public final static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //dopasowanie rozpoznawanych URI
        uriMatcher.addURI(AUTHORITY, MyDBHelper.TABLE_NAME, WHOLE_TABLE);
        uriMatcher.addURI(AUTHORITY, MyDBHelper.TABLE_NAME + "/#", ONE_ROW);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public boolean onCreate() {
        myDBHelper = new MyDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase database = myDBHelper.getWritableDatabase();
        long addedID = 0;

        //sprawdzenie, czy operacja dotyczy całej tabeli, czy pojedynczego wiersza
        switch (uriType) {
            case WHOLE_TABLE:
                addedID = database.insert(MyDBHelper.TABLE_NAME, null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }

        //powiadomienie o zmianie danych (-> np. odświeżenie listy)
        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(MyDBHelper.TABLE_NAME + "/" + addedID);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase database = myDBHelper.getReadableDatabase();
        Cursor cursor = null;

        //sprawdzenie, czy operacja dotyczy całej tabeli, czy pojedynczego wiersza
        switch (uriType) {
            case WHOLE_TABLE:
                cursor = database.query(false,
                        MyDBHelper.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        null,
                        null);
                break;
            case ONE_ROW:
                cursor = database.query(false,
                        MyDBHelper.TABLE_NAME,
                        projection,
                        addIdToSelection(selection, uri),
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        null,
                        null);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }

        //URI może być monitorowane pod kątem zmiany danych – tu jest rejestrowane.
        //obserwator (którego trzeba zarejestrować będzie powiadamiany o zmianie danych)
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    //dodaje do klauzuli WHERE identyfikator wiersza odczytany z URI
    private String addIdToSelection(String selection, Uri uri) {
        //jeżeli już jest to dodajemy tylko dodatkowy warunek
        if (selection != null && !selection.equals(""))
            selection = selection + " and " + MyDBHelper.ID_COLUMN + "=" + uri.getLastPathSegment();

        //jeżeli nie ma WHERE tworzymy je od początku
        else
            selection = MyDBHelper.ID_COLUMN + "=" + uri.getLastPathSegment();
        return selection;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase database = myDBHelper.getWritableDatabase();
        int deletedNumber = 0;

        //sprawdzenie, czy operacja dotyczy całej tabeli, czy pojedynczego wiersza
        switch (uriType) {
            case WHOLE_TABLE:
                deletedNumber = database.delete(MyDBHelper.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case ONE_ROW:
                deletedNumber = database.delete(MyDBHelper.TABLE_NAME,
                        addIdToSelection(selection, uri),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }

        //powiadomienie o zmianie danych
        getContext().getContentResolver().notifyChange(uri, null);

        return deletedNumber;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase database = myDBHelper.getWritableDatabase();
        int updatedNumber = 0;

        //sprawdzenie, czy operacja dotyczy całej tabeli, czy pojedynczego wiersza
        switch (uriType) {
            case WHOLE_TABLE:
                updatedNumber = database.update(MyDBHelper.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case ONE_ROW:
                updatedNumber = database.update(MyDBHelper.TABLE_NAME,
                        contentValues,
                        addIdToSelection(selection, uri),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }

        //powiadomienie o zmianie danych
        getContext().getContentResolver().notifyChange(uri, null);

        return updatedNumber;
    }
}
