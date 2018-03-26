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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edition);
        initFields();


        try {
            int rowId = getIntent().getExtras().getInt("ROW_ID");
            fillEditsFromDatabase(rowId);
        }
        catch (NullPointerException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
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
                "_id=" + rowId,
                null,
                null
        );
        cursor.moveToFirst();
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
                //if url == OK
                //else error message
                Intent wwwIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.google.pl"));
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
                //validate
                //add/update
                addValue();
            }
        });
    }

    private void addValue() {
        ContentValues values = new ContentValues();
        producerEditText = findViewById(R.id.producerEditText);
        modelEditText = findViewById(R.id.modelEditText);
        versionEditText = findViewById(R.id.versionEditText);
        wwwEditText = findViewById(R.id.wwwEditText);

        values.put(MyDBHelper.PRODUCER_COLUMN, producerEditText.getText().toString());
        values.put(MyDBHelper.MODEL_COLUMN, modelEditText.getText().toString());
        values.put(MyDBHelper.VERSION_COLUMN, versionEditText.getText().toString());
        values.put(MyDBHelper.URL_COLUMN, wwwEditText.getText().toString());

        Uri uriOfNew = getContentResolver().insert(MyProvider.CONTENT_URI, values);
    }
}
