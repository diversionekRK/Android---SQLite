package com.example.div.sqlite_test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditionActivity extends AppCompatActivity {
    private EditText producerEditText;
    private EditText modelEditText;
    private EditText versionEditText;
    private EditText wwwEditText;
    private Button wwwButton;
    private Button cancelButton;
    private Button saveButton;
    private final static boolean ACTION_ADD = true;
    private final static boolean ACTION_UPDATE = false;
    private boolean action;
    int rowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edition);
        initFields();


        //sprawdzenie, czy aktywność uruchomiona w celu dodania, czy modyfikacji istniejącego elementu
        try {
            rowId = getIntent().getExtras().getInt("ROW_ID");
            //uzupełnienie pól danymi powiązanymi z daną pozycją
            fillEditsFromDatabase(rowId);

            action = ACTION_UPDATE;
        } catch (NullPointerException e) {
            action = ACTION_ADD;
        }

        addButtonListeners();
    }

    private void initFields() {
        producerEditText = findViewById(R.id.producerEditText);
        modelEditText = findViewById(R.id.modelEditText);
        versionEditText = findViewById(R.id.versionEditText);
        wwwEditText = findViewById(R.id.wwwEditText);
        wwwButton = findViewById(R.id.wwwButton);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
    }

    private void fillEditsFromDatabase(int rowId) {
        String projection[] = {
                MyDBHelper.PRODUCER_COLUMN,
                MyDBHelper.MODEL_COLUMN,
                MyDBHelper.VERSION_COLUMN,
                MyDBHelper.URL_COLUMN
        };

        Cursor cursor = getContentResolver().query(
                ContentUris.withAppendedId(MyProvider.CONTENT_URI, rowId),
                projection,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        //ustawienie zawartości pól na podstawie danych z bazy
        producerEditText.setText(cursor.getString(cursor.getColumnIndex(MyDBHelper.PRODUCER_COLUMN)));
        modelEditText.setText(cursor.getString(cursor.getColumnIndex(MyDBHelper.MODEL_COLUMN)));
        versionEditText.setText(cursor.getString(cursor.getColumnIndex(MyDBHelper.VERSION_COLUMN)));
        wwwEditText.setText(cursor.getString(cursor.getColumnIndex(MyDBHelper.URL_COLUMN)));
    }

    private void addButtonListeners() {
        addWwwButtonListener();
        addCancelButtonListener();
        addSaveButtonListener();
    }

    private void addWwwButtonListener() {
        wwwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wwwIntent;

                if(action == ACTION_ADD) {
                    Toast.makeText(EditionActivity.this, "Najpierw dodaj adres WWW do bazy", Toast.LENGTH_SHORT).show();
                    return;
                }

                //odczyt adresu URL powiązanego z elementem z bazy
                Cursor cursor = getContentResolver().query(
                        ContentUris.withAppendedId(MyProvider.CONTENT_URI, rowId),
                        new String[]{MyDBHelper.URL_COLUMN},
                        null,
                        null,
                        null
                );

                cursor.moveToFirst();
                String url = cursor.getString(cursor.getColumnIndex(MyDBHelper.URL_COLUMN));

                //uruchomienie przeglądarki
                if(url.startsWith("http://") || url.startsWith("https://"))
                    wwwIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                else
                    wwwIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://" + url));
                startActivity(wwwIntent);
            }
        });
    }

    private void addCancelButtonListener() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addSaveButtonListener() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean fieldsOk = validateFields();

                //jeśli pola wypełniono poprawnie
                if (fieldsOk) {
                    //modyfikacja bazy
                    modifyDatabase();
                    //powrót do głównej aktywności
                    finish();
                }
            }
        });
    }

    private boolean validateFields() {
        String producerText = getStringFromEdit(producerEditText);
        String modelText = getStringFromEdit(modelEditText);
        String versionText = getStringFromEdit(versionEditText);
        String wwwText = getStringFromEdit(wwwEditText);
        boolean resultOfValidation = true;

        //sprawdzenie pola "Producent"
        if (!producerText.matches("[A-Z][A-Za-z0-9]{0,15}")) {
            producerEditText.setError("Wypełnij poprawnie (zacznij z dużej litery)");
            resultOfValidation = false;
        }
        //sprawdzenie pola "Model"
        if (!modelText.matches("[A-Z][A-Za-z0-9]{0,15}( [A-Za-z0-9]{0,15}){0,2}")) {
            modelEditText.setError("Wypełnij poprawnie (zacznij z dużej litery)");
            resultOfValidation = false;
        }
        //sprawdzenie pola "Wersja androida"
        if (!versionText.matches("[0-9]{1,3}([.][0-9]{1,3}){0,2}")) {
            versionEditText.setError("Wypełnij poprawnie (np. 6.0.3)");
            resultOfValidation = false;
        }
        //sprawdzenie pola "WWW"
        if(!wwwText.matches("^(http:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9]+)[.].+$")) {
            wwwEditText.setError("Podaj poprawny adres WWW");
            resultOfValidation = false;
        }

        return resultOfValidation;
    }

    private void modifyDatabase() {
        //przygotowanie danych do wstawienia
        ContentValues values = new ContentValues();
        values.put(MyDBHelper.PRODUCER_COLUMN, getStringFromEdit(producerEditText));
        values.put(MyDBHelper.MODEL_COLUMN, getStringFromEdit(modelEditText));
        values.put(MyDBHelper.VERSION_COLUMN, getStringFromEdit(versionEditText));
        values.put(MyDBHelper.URL_COLUMN, getStringFromEdit(wwwEditText));

        //w przypadku dodawania nowego elementu - insert
        if (action == ACTION_ADD) {
            getContentResolver().insert(MyProvider.CONTENT_URI, values);
        }
        //w przypadku modyfikacji - update istniejącego
        else if (action == ACTION_UPDATE) {
            getContentResolver().update(
                    ContentUris.withAppendedId(MyProvider.CONTENT_URI, rowId),
                    values,
                    null,
                    null
            );
        }
    }

    private String getStringFromEdit(EditText eT) {
        return eT.getText().toString();
    }
}
