package com.example.div.sqlite_test;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    private void addButtonListeners() {
        addWwwButtonListener();
        addCancelButtonListener();
    }

    private void addWwwButtonListener() {
        wwwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void addValue() {
        ContentValues values = new ContentValues();
        producerEditText = findViewById(R.id.producerEditText);
        modelEditText = findViewById(R.id.modelEditText);
        versionEditText = findViewById(R.id.versionEditText);
        wwwEditText = findViewById(R.id.wwwEditText);

        values.put(MyDBHelper.VALUE_COLUMN, producerEditText.getText().toString());
        values.put(MyDBHelper.VALUE_COLUMN, modelEditText.getText().toString());
        values.put(MyDBHelper.VALUE_COLUMN, versionEditText.getText().toString());
        values.put(MyDBHelper.VALUE_COLUMN, wwwEditText.getText().toString());

        Uri uriOfNew = getContentResolver().insert(MyProvider.CONTENT_URI, values);
    }
}
