package hr.math.matej.bazapodataka;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    private int MY_PERM = 1;
    DBAdapter db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //permission za SMS
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, MY_PERM);
         db = new DBAdapter(this);

        //add page
        /*db.open();
        long id = db.insertPage("http://www.fitness.com.hr", "sport");
        id = db.insertPage("https://www.amazon.co.uk/", "shopping");
        id = db.insertPage("https://www.youtube.com","glazba");
        id = db.insertPage ("https://www.24sata.hr/","informacije");
        id = db.insertPage("https://www.math.pmf.unizg.hr","obrazovanje");
        db.close();*/

    }

public void IspisiBazu (View view){
    db.open();
    Cursor c = db.getAllContacts();
    TextView baza = (TextView) findViewById(R.id.textview);
    baza.setText("");
    if (c.moveToFirst())
    {
        do {
            Display(c);
        } while (c.moveToNext());
    }
    db.close();
}

    //funkcija za ispis
    public void Display(Cursor c)
    {
        String redak = "id: " + c.getString(0) +
                "  Adresa: " + c.getString(1) +
                "  Vrsta:  " + c.getString(2) + "\n";

        TextView redakBaze = (TextView) findViewById(R.id.textview);
        redakBaze.append(redak);

    }

    public void downloadText (View view){
        EditText url_download = (EditText) findViewById (R.id.edittext);
        new DownloadTextTask().execute(url_download.getText().toString());
        //sendSMS("5556", "Download teksta je završen.");

    }
    /*
    Testni primjeri za download:
    http://txt2html.sourceforge.net/sample.txt
    https://web.math.pmf.unizg.hr/~karaga/android/images_2/send_message.txt
    http://www.sample-videos.com/text/Sample-text-file-10kb.txt

    */

    private InputStream OpenHttpConnection(String urlString)
            throws IOException
    {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }
    private String DownloadText(String URL)
    {
        int BUFFER_SIZE = 2000;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
        } catch (IOException e) {
            Log.d("NetworkingActivity", e.getLocalizedMessage());
            return "";
        }

        InputStreamReader isr = new InputStreamReader(in);
        int charRead;
        String str = "";
        char[] inputBuffer = new char[BUFFER_SIZE];
        try {
            while ((charRead = isr.read(inputBuffer))>0) {
                //---convert the chars to a String---
                String readString =
                        String.copyValueOf(inputBuffer, 0, charRead);
                str += readString;
                inputBuffer = new char[BUFFER_SIZE];
            }
            in.close();
        } catch (IOException e) {
            Log.d("NetworkingActivity", e.getLocalizedMessage());
            return "";
        }
        return str;
    }

    private class DownloadTextTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return DownloadText(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            TextView downloadedText = (TextView) findViewById(R.id.textview);
            downloadedText.setText(result);

        }
    }


}
