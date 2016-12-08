package com.example.jim.theoryquiz;

import android.app.Activity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Preferences extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_preferences);

        getFragmentManager().beginTransaction().replace( android.R.id.content, new PrefencesFrg() ).commit();
    }


    public static  class PrefencesFrg extends PreferenceFragment {
        @Override
        public void onCreate( Bundle bndl ) {
            super.onCreate(bndl);
            addPreferencesFromResource( R.xml.preferences);
        }
    }

}
