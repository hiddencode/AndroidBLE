package com.example.intro;
import android.os.Binder;
import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "Some description from extra message"; // const string

    /* Init current intent */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);             // init bundle
        setContentView(R.layout.activity_main);         // set "design" from R.layout.(xml file)
        Toolbar toolbar = findViewById(R.id.toolbar);   // get toolbar from "design file"(xml file) by id
        setSupportActionBar(toolbar);                   // set actions to toolbar var
    }

    /* Advanced option */
    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);   // init Intent with Activity(Select activity for Intent for transition)
        EditText editText = (EditText) findViewById(R.id.editText);                     // Text area
        String message = editText.getText().toString();                                 // Init string from text area
        intent.putExtra(EXTRA_MESSAGE, message);                                        // Add extended data to Intent
        startActivity(intent);                                                          // Transition with intent (intent to determine transition)
    }

    /* Transition to Bluetooth */
    public void checkBLE(View view){
        Intent intent_ble = new Intent(this, BluetoothActivity.class);
        /* . . . */
        startActivity(intent_ble);
    }
}

