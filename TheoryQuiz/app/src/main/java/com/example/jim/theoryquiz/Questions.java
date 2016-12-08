package com.example.jim.theoryquiz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Vector;

class Question {
    public String Q, Image;


    public int CorrectAns;

    public String[] Answ;

    public int Qi;
}

public class Questions extends AppCompatActivity {


    static DataBase DB; //there can be only one

    RadioGroup Rg;
    RadioButton[] Bttns;
    TextView Lbl_Q;
    ImageView ImgV;
    int Qc;


    class Answer {
        public int Qi, Ai, Correct;
    };
    Vector< Answer> Answers = new Vector<Answer>();
    Vector<Integer> QuestionPool  = new Vector<Integer>();
    int CurI = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Bttns = new RadioButton[4];
        Bttns[0] = (RadioButton) findViewById(R.id.radioButton);
        Bttns[1] = (RadioButton) findViewById(R.id.radioButton2);
        Bttns[2] = (RadioButton) findViewById(R.id.radioButton3);
        Bttns[3] = (RadioButton) findViewById(R.id.radioButton4);

        Rg = (RadioGroup) findViewById(R.id.RB_Group);

        Lbl_Q = (TextView) findViewById(R.id.Lbl_Q);
        ImgV = (ImageView) findViewById(R.id.imageView);
        ImgV.setVisibility(View.INVISIBLE);


        if (DB == null) {
            try {
                StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy(); //todo - fix this - async database...
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                        .permitDiskWrites()
                        .build());

                DB = new DataBase(this, "questions.s3db", null, 1);
                DB.dbCreate();

                // StrictMode.setThreadPolicy(old);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Qc = DB.questionCount();


        Log.d("quest", "  -count  " + Integer.toString(Qc));

        for( int i = Qc; i -- >0; )
            QuestionPool.add(i+1);

        getQuestion();

       // Question Cur;


        ((Button) findViewById(R.id.Bttn_Done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateAnsw();

                Answer ans = Answers.get( CurI );
                CharSequence text = "Incorrect!";
                if (ans.Ai == ans.Correct )
                    text = "Correct!";

                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                next();
            }
        });

        ((Button) findViewById(R.id.Bttn_Skip)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateAnsw();
                next();
            }
        });
        ((Button) findViewById(R.id.Bttn_Back)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if( CurI > 0 ) {
                    updateAnsw();
                    CurI --;
                    updateQuestion();
                }
            }
        });

    }

    void updateAnsw() {
        int i;
        int ci = Rg.getCheckedRadioButtonId();

        for (i = 4; i-- > 0; )
            if (Bttns[i].getId() == ci)
                break;

        Answer ans = Answers.get( CurI );
        ans.Ai = i;
    }


    void next() {

        if( CurI +1 >= Answers.size() ) {

            getQuestion();
        } else {
            CurI ++;
            updateQuestion();
        }
    }
    void updateQuestion() {
        Answer ans = Answers.get( CurI );
        Question q = DB.getQuestion(ans.Qi);
        setQuestion(q);
        int ci = -1;
        if( ans.Ai >= 0 && ans.Ai < Bttns.length )
            ci = Bttns[ans.Ai].getId();
        Log.d("quest", "updateQuestion    ci  " + ci + " ans.Ai  " + ans.Ai );
        Rg.check(ci );
    }

    void getQuestion() {
        Rg.clearCheck();

        int qpi = Math.abs(new Random().nextInt()) % QuestionPool.size();

        int qi = QuestionPool.get(qpi);

        QuestionPool.remove(qpi);

        Question q = DB.getQuestion(qi);
        Log.d("quest", "  q  " + q.Q);


        if (q == null) {
            Qc--;
            return;
        }

        Answer answ = new Answer();
        answ.Qi = qi;
        answ.Ai = -1;
        answ.Correct = q.CorrectAns;
        CurI = Answers.size();
        Answers.add(answ);

        if (QuestionPool.size() < 2 ) {
            QuestionPool.add(Answers.get(Answers.size()+1 - Qc).Qi);
        }

        setQuestion( q );
    }
    void setQuestion( Question q ) {

        if (q.Image != null) {
            ImgV.setVisibility(View.VISIBLE);

            if (q.Image.equalsIgnoreCase("bridge"))  //todo - a better way of doing this...
                ImgV.setImageResource(R.drawable.bridge);
            else if (q.Image.equalsIgnoreCase("crossing"))
                ImgV.setImageResource(R.drawable.crossing);
            else if (q.Image.equalsIgnoreCase("motorway_sign"))
                ImgV.setImageResource(R.drawable.motorway_sign);
            else
                ImgV.setVisibility(View.INVISIBLE);

        } else
            ImgV.setVisibility(View.INVISIBLE);


        Lbl_Q.setText("Q: " + q.Q);

        for (int i = 4; i-- > 0; ) {
            Bttns[i].setText(q.Answ[i]);
        }
    }

}
