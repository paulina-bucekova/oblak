package com.example.oblak.oblak.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.example.oblak.client.Client;
import com.example.oblak.oblak.R;
import java.io.IOException;

/**
 * LinkUploader dialog activity class.
 */
public class LinkUploader extends Activity {

    String urlString = "";

    /**
     * Initializes content layout and view.
     * Retrieves the Uri of the remote file from the intent-filter.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_LEFT_ICON);

        setContentView(R.layout.activity_link_uploader);

        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.mipmap.ic_launcher);

        Uri data = getIntent().getData();
        urlString = data.toString();
    }

    /**
     * AsyncTask class to send the Uri string to the server.
     */
    class SenderLink extends AsyncTask<String, Void, String> {

        /**
         * Creates client, connects to server and sends the string with the Uri of the remote file to be downloaded.
         * @param params
         * @return String with the Uri of the remote file to be downloaded.
         */
        @Override
        protected String doInBackground(String... params) {

            String urlString = params[0];

            Client client = new Client();
            try {
                client.getSocketIO().getDataOutputStream().writeInt(0);
                client.getSocketIO().getDataOutputStream().writeInt(0);
                client.getSocketIO().getDataOutputStream().writeUTF(urlString);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return urlString;
        }

        protected void onPostExecute(String urlString) {
            super.onPostExecute(urlString);
        }
    }

    /**
     * Action listener.
     * Executes the AsyncTask SenderLink.
     * Returns to previous activity.
     */
    public void sendLink(View view){
        SenderLink sender = new SenderLink();
        sender.execute(urlString);
        super.onBackPressed();
    }

    /**
     * Returns to previous activity.
     * @param view View of the component calling the function.
     */
    public void cancel(View view){
        super.onBackPressed();
    }

    /**
     * Initializes the menu of the activity.
     * @param menu Menu.
     * @return true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_link_uploader, menu);
        return true;
    }

    /**
     * @param item MenuItem.
     * @return boolean OptionsItemSelected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}