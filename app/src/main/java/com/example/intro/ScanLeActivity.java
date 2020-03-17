package com.example.intro;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

/*
    Yep, It`s working :)))
    MainActivity { (startActivityForResult) ... (onActivityResult) } -->  ResultActivity { ... setResult(OK) ... }
                                                                     <--                              *
*/
public class ScanLeActivity extends Activity {


    @Override
    protected void onStart(){
        super.onStart();

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

}
