package com.example.div.sqlite_test;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //adapter łączy kursor z dostawcy i listę
    private SimpleCursorAdapter cursorAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ustawienie adaptera i wyświetlenie zawartości
        listView = findViewById(R.id.lista_wartosci);
        fillList();

        //zaznaczanie wielu elementów jednocześnie
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        //menu pojawiające się po przytrzymaniu elementu
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                //generowanie menu na podstawie XML
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.pasek_akcji, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //obsługa przycisku potwierdzającego usunięcie zaznaczonych elementów
                    case R.id.akcja_druga:
                        deleteValues();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
            }
        });
    }

    private void fillList() {
        getLoaderManager().initLoader(0, //id loadera
                null, //argumenty (Bundle)
                (android.app.LoaderManager.LoaderCallbacks<Cursor>) this); //klasa implementująca LoaderCallbacks

        //utworzenie mapowania między kolumnami tabeli, a kolumnami wyświetlanej listy
        String[] mapFrom = new String[]{MyDBHelper.ID_COLUMN, MyDBHelper.PRODUCER_COLUMN};
        int[] mapTo = new int[]{R.id.pierwsza, R.id.druga};

        //adapter wymaga aby w wyniku zapytania znajdowała się kolumna _id
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.list_row, null, mapFrom, mapTo, 0);

        //obsługa kliknięcia elementu listy - przejście do edycji
        configureListItemClick();

        //powiązanie adaptera z listą
        listView.setAdapter(cursorAdapter);
    }

    private void deleteValues() {
        //lista zaznaczonych elementów
        long checked[] = listView.getCheckedItemIds();
        for (int i = 0; i < checked.length; i++)
            //usunięcie kolejnych elementów
            getContentResolver().delete(ContentUris.withAppendedId(MyProvider.CONTENT_URI, checked[i]),
                    null,
                    null);
    }

    void configureListItemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //dołączenie identyfikatora modyfikowanego elementu dla uruchamianej aktywności
                Intent intent = new Intent(MainActivity.this, EditionActivity.class);
                intent.putExtra("ROW_ID", (int) l);

                //przejście do aktywności z modyfikacją
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //tworzenie paska MENU
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pasek_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addButton:
                //uruchomienie aktywności w celu dodania nowego elementu
                Intent intent = new Intent(MainActivity.this, EditionActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //implementacja LoaderCallbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //adapter wymaga aby w wyniku zapytania znajdowała się kolumna _id
        String[] projection = {MyDBHelper.ID_COLUMN, MyDBHelper.PRODUCER_COLUMN};
        return new CursorLoader(this,
                MyProvider.CONTENT_URI,
                projection,
                null,
                null,
                null);
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
