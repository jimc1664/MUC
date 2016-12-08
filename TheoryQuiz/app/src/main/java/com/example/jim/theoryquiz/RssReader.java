package com.example.jim.theoryquiz;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Jim on 08/12/2016.
 */
public class RssReader {


    String Address;
    static XmlPullParserFactory XmlFactory;

    MainActivity Main;
    static RssReader Current;

    public RssReader() {

    }


    public RssReader( MainActivity m, String addr ) {
        //Prnt = p;
        Main = m;
        Current = this;
        // Log.d("Rss", "get  "+addr );
        Address = addr;

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                try {


                    Log.d("Rss", "get  " + Address );

                    InputStream stream;
                    if( !Address.equalsIgnoreCase( "local")) {
                        //fyi... this works..
                        URL url = new URL(Address);
                        URLConnection conn = url.openConnection();
                        conn.connect();

                        stream = conn.getInputStream();
                    } else {

                        Resources res = Main.getResources();  //todo - 90% certain this is thread safe
                        stream = res.openRawResource(R.raw.test_routes);
                    }

                    parse(stream );
                    stream.close();

                    Main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finished();
                        }
                    });
                }
/*                catch (MalformedURLException e) {
                    // new URL() failed
                    // ...
                }
                catch (IOException e) {
                    // openConnection() failed
                    // ...
                } */
                catch (Exception e) {
                    Log.e("Rss", "Err:  " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    public class Entry {
        public String Title;
        public ArrayList<PointF> Points = new ArrayList<PointF>();
    }
    public ArrayList<Entry> Dat = new ArrayList<Entry>();

    boolean Parsed = false;
    public void parse( InputStream stream ) {
        try {
            Log.d("xml", "Parsing??   <br />");
            if (XmlFactory == null)
                XmlFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xml = XmlFactory.newPullParser();

            // myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xml.setInput(stream, null);
            String t = null,d = null, lText = null;
            //float lng = Float.NaN, lat = Float.NaN;
            ArrayList<PointF> tPoints = null;

            for( int event; ( event = xml.next()) != XmlPullParser.END_DOCUMENT; ) {
                switch (event){
                    case XmlPullParser.START_TAG:
                        if( xml.getName().equals("item" )) {
                            t = d = lText = null;
                            tPoints = new ArrayList<PointF>();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        lText = xml.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        String name=xml.getName();
                        if( name.equals( "item") ) {
                            Log.d("xml - entry", t + "  --- "+d + "  --- "+ tPoints );
                            if( t != null ) {
                                Entry e = new Entry();
                                e.Title = t;
                                e.Points = tPoints;
                                Dat.add( e );
                            }
                            t = d = lText = null;
                            tPoints = null;
                        } else if( name.equals( "title" ) ){
                            t = lText; lText = null;

                        } else if( name.equals( "georss:point" ) && tPoints != null ){
                            //  <georss:point>55.1091379966125 -3.35121631622314</georss:point>
                            String[] flts =  lText.split(" ");
                            Log.d("Xml  - geo ", "Cnt "+flts.length  +"    "+flts );
                            if( flts.length == 2 ) {
                                Log.d("Xml  - geo2 ",  flts[0] +"  <> " +  flts[1] );
                                tPoints.add(new PointF( Float.parseFloat( flts[0]), Float.parseFloat( flts[1])) );
                            }
                            lText = null;
                        }
                        break;
                }
            }
        }
        catch (Exception e) {
            Log.e("Xml", "Err:  "+e.getMessage() );
            e.printStackTrace();
        }

    }

    public interface CallBack_IF{
        void onParse( RssReader r );
    }

    ArrayList<CallBack_IF> Callbacks = new ArrayList<CallBack_IF>();

    void finished() {
        Parsed = true;

        for (CallBack_IF c: Callbacks) {
            c.onParse(this);
        }
    }

    public static void get( CallBack_IF c ) {
        if( Current.Parsed ) c.onParse(Current);
        else Current.Callbacks.add(c);
    }
    // RssReader _This; //lazy (todo)

}
