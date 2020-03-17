package com.example.intro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();                                        // get data from prev activity
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE); // get string from intent

        TextView text = findViewById(R.id.textView);                        // init text view from "design"(xml file) by id
        text.setText(message);                                              // filling text view
    }

    public void copyClipPass(View view){

        TextView text = findViewById(R.id.textView);
        ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE); // init clipboard manager using system service
        ClipData data = ClipData.newPlainText("passtext",text.getText());                 // filling clip data with text view from current onCreate()
        clip.setPrimaryClip(data);                                                              // set data in buffer from clip data
    }

}
