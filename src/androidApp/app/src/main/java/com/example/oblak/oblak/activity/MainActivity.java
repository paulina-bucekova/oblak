package com.example.oblak.oblak.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.example.oblak.oblak.settings.OblakSettingsActivity;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import com.example.oblak.oblak.R;

/**
 * MainActivity activity class.
 */
public class MainActivity extends AppCompatActivity {

    public static Activity instance;
    private SSLSocketFactory socketFactory;
    private String serverIP = "";
    private int serverPort = 0;
    private final String keystorePassword = "clientpassword";

    /**
     * Action listener.
     * Starts LinkDownloader activity.
     * @param view View of the component calling the function.
     */
    public void showLinkDownloader(View view){
        Intent intent = new Intent(this, LinkDownloader.class);
        startActivity(intent);
    }

    /**
     * Action listener.
     * Starts RemoteStorage activity.
     * @param view View of the component calling the function.
     */
    public void showRemoteStorage(View view){
        Intent intent = new Intent(this, RemoteStorage.class);
        startActivity(intent);
    }

    /**
     * Initializes content layout and view.
     * Creates ssl socket factory.
     * Retrieves the shared preferences for server IP and server port.
     * Initializes the serverIP and serverPort variables.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverIP = sharedPreferences.getString("server_ip", "");
        serverPort = Integer.parseInt(sharedPreferences.getString("server_port", "0"));

        instance = this;
        socketFactory = initSSL();
    }

    /**
     * Retrieves the shared preferences for server IP and server port.
     * Initializes the serverIP and serverPort variables.
     */
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverIP = sharedPreferences.getString("server_ip", "");
        serverPort = Integer.parseInt(sharedPreferences.getString("server_port", "0"));
    }

    /**
     * Initializes the menu of the activity.
     * @param menu Menu.
     * @return true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Starts the OblakSettingsActivity activity.
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
            Intent intent = new Intent(this, OblakSettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Initializes SSL.
     * @return A SSLSocketFactory capable of creating SSL sockets.
     */
    private SSLSocketFactory initSSL(){
        SSLSocketFactory socketFactory = null;
        InputStream inputStream = null;

        try{
            inputStream = getResources().openRawResource(
                    getResources().getIdentifier("client_key", "raw", getPackageName()));

            String defaultType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(defaultType);
            keyStore.load(inputStream, this.keystorePassword.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, this.keystorePassword.toCharArray());

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            socketFactory = ctx.getSocketFactory();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            if (inputStream != null){
                try{
                    inputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return socketFactory;
    }

    /**
     * @return A SSL socket using the socket factory.
     */
    public SSLSocket createSocket() {
        SSLSocket socket = null;

        try {
            socket = (SSLSocket)this.socketFactory.createSocket(this.serverIP, this.serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return socket;
    }
}