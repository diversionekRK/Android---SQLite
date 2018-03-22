package com.example.div.sqlite_test;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //adapter łączy kursor z dostawcy i listę
    private SimpleCursorAdapter cursorAdapter;
    private ListView listView;
    private Button addButton;
    private EditText valueEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        listView = findViewById(R.id.lista_wartosci);
        fillList();

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.pasek_akcji, menu);
                return true;}
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {return false;}
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.akcja_druga:
                        deleteValue();
                        return true;
                }
                return false;}
            @Override
            public void onDestroyActionMode(ActionMode actionMode) {            }
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {}
        });

        //obsługa przycisku dodaj
        addButton = findViewById(R.id.dodaj_przycisk);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addValue();
            }
        });

    }

    private void fillList() {
        getLoaderManager().initLoader(0, //id loadera
                null, //argumenty (Bundle)
                (android.app.LoaderManager.LoaderCallbacks<Cursor>)this); //klasa implementująca LoaderCallbacks

        //utworzenie mapowania między kolumnami tabeli, a kolumnami wyświetlanej listy
        String[] mapFrom = new String[] {MyDBHelper.ID_COLUMN, MyDBHelper.VALUE_COLUMN};
        int[] mapTo = new int[] {R.id.pierwsza, R.id.druga};

        //adapter wymaga aby w wyniku zapytania znajdowała się kolumna _id
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.list_row, null, mapFrom, mapTo, 0);

        listView.setAdapter(cursorAdapter);
    }

    private void addValue() {
        ContentValues values = new ContentValues();
        valueEditText = findViewById(R.id.wartosc_edycja);

        values.put(MyDBHelper.VALUE_COLUMN, valueEditText.getText().toString());

        Uri uriOfNew = getContentResolver().insert(MyProvider.CONTENT_URI, values);
    }

    private void deleteValue() {
        long checked[] = listView.getCheckedItemIds();
        for(int i = 0; i < checked.length; i++)
        getContentResolver().delete(ContentUris.withAppendedId(MyProvider.CONTENT_URI, checked[i]),
                null,
                null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pasek_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addButton:
                addValue();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //implementacja LoaderCallbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //adapter wymaga aby w wyniku zapytania znajdowała się kolumna _id
        String[] projection = {MyDBHelper.ID_COLUMN, MyDBHelper.VALUE_COLUMN};
        CursorLoader cursorLoader = new CursorLoader(this,
                MyProvider.CONTENT_URI,
                projection,
                null,
                null,
                null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //ustawienie danych w adapterze
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
