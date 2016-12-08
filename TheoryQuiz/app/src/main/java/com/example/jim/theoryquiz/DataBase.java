package com.example.jim.theoryquiz;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Created by Jim on 08/12/2016.
 */
public class DataBase extends SQLiteOpenHelper {

    private static final int DB_VER = 1;
    private static final String DB_PATH = "/data/data/com.example.jim.theoryquiz/databases/";
    private static final String DB_NAME = "questions.s3db";
    private static final String TBL_Questions = "questions", TBL_Answers = "answers";

    public static final String COL_Id = "id", COL_Question = "question";


    private final Context appContext;


    public DataBase(Context context, String name,
                                SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.appContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS [answers] ( " +
        "[txt] TEXT  NULL," +
                "[valid] BOOLEAN DEFAULT 'false' NULL," +
                "[question] INTEGER  NOT NULL" +
                ")";
        db.execSQL(sql);


        sql = "CREATE TABLE IF NOT EXISTS [questions] (" +
                 "[id] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL," +
                 "[txt] TEXT  NULL," +
                 "[img] TEXT  NULL," +
                 "[category] TEXT  NULL," +
                 "[q_pick] INTEGER DEFAULT '''1''' NOT NULL," +
                 "[q_display] INTEGER DEFAULT '''4''' NULL," +
                 "[isImgQ] BOOLEAN DEFAULT 'false' NULL" +
                 ")";
        db.execSQL(sql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TBL_Questions);
            onCreate(db);
        }
    }

    // ================================================================================
    // Creates a empty database on the system and rewrites it with your own database.
    // ================================================================================
    public void dbCreate() throws IOException {

        boolean dbExist = dbCheck();

        if(!dbExist){
            //By calling this method an empty database will be created into the default system path
            //of your application so we can overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDBFromAssets();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    // ============================================================================================
    // Check if the database already exist to avoid re-copying the file each time you open the application.
    // @return true if it exists, false if it doesn't
    // ============================================================================================
    private boolean dbCheck(){
        return false;
      /*  SQLiteDatabase db = null;

        try{
            String dbPath = DB_PATH + DB_NAME;
            db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
            db.setLocale(Locale.getDefault());
            db.setVersion(1);

        }catch(SQLiteException e){

            Log.e("SQLHelper", "dbCheck ..Database not Found!");

        }

        if(db != null){

            db.close();

        }

        return db != null ? true : false; */
    }

    // ============================================================================================
    // Copies your database from your local assets-folder to the just created empty database in the
    // system folder, from where it can be accessed and handled.
    // This is done by transfering bytestream.
    // ============================================================================================
    private void copyDBFromAssets() throws IOException{

        InputStream dbInput = null;
        OutputStream dbOutput = null;
        String dbFileName = DB_PATH + DB_NAME;

        Log.e("DB", "copy attempt" );
        try {

            dbInput = appContext.getAssets().open(DB_NAME);
            dbOutput = new FileOutputStream(dbFileName);
            //transfer bytes from the dbInput to the dbOutput
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dbInput.read(buffer)) > 0) {
                dbOutput.write(buffer, 0, length);
            }

            //Close the streams
            dbOutput.flush();
            dbOutput.close();
            dbInput.close();
        }   catch (Exception e) {
            Log.e("DB", "Err:  "+e.getMessage() );
            e.printStackTrace();
        }
    }



    public Question getQuestion(int id ) {
        String query = "Select * FROM " + TBL_Questions + " WHERE " + COL_Id + " =  \"" + id + "\"";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Question q =null;

        if (cursor.moveToFirst()) {
            q = new Question();
            q.Qi = id;
            for( int i = 0; i < cursor.getColumnCount(); i++ ) {
                Log.d("DB", "getQuestion:  " + cursor.getString(i) );
            }
            q.Q = cursor.getString(1);
            q.Image = cursor.getString(2);
            cursor.close();

            query = "Select * FROM " + TBL_Answers + " WHERE " + COL_Question + " =  \"" + id + "\"";
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                q.Answ = new String[cursor.getCount()];
                for(int i = 0; i < cursor.getCount(); i++ ) {
                    q.Answ[i]= cursor.getString(0);
                    Log.d("DB", "getQuestion  - answ :  " + cursor.getString(0) + "  --   " + cursor.getInt(3));
                    if( cursor.getInt(3) == 1  )
                        q.CorrectAns = i;
                    cursor.moveToNext();
                }

            }
            cursor.close();
        } else {
            Log.d("DB", "getQuestion:  fail " + id );
        }
        db.close();
        return q;
    }
    public int questionCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + TBL_Questions;
        SQLiteStatement statement = db.compileStatement(sql);
        int ret =  (int)statement.simpleQueryForLong();
        db.close();
        return ret;
    }
}