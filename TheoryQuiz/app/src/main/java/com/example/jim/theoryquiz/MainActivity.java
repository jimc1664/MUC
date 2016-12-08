package com.example.jim.theoryquiz;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    AlertDialog About;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.Bttn_Practise)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), Questions.class);
                i.putExtra( "practiceFlag", true );
                startActivity(i);
            }
        });
        ((Button) findViewById(R.id.Bttn_Test)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), Questions.class);
                i.putExtra( "practiceFlag", false );
                startActivity(i);
            }
        });

        final MainActivity _this = this;
        ((Button) findViewById(R.id.Bttn_Routes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), Routes.class);
                new RssReader(_this, "local");
                startActivity(i);
            }
        });


        ((Button) findViewById(R.id.Bttn_Contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                        + 1337)));
            }
        });


        AlertDialog.Builder bldr = new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("Quiz app for Driving theory practise.\n\n Ver 0.1 ")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info);

        About = bldr.create();

     //   getFragmentManager().beginTransaction().replace( android.R.id.content, new PrefencesFrg() ).commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                About.show();
                return true;
            case R.id.preferences:
                Intent i = new Intent(getApplicationContext(), Preferences.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
