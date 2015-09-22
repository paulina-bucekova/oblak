package com.example.oblak.oblak.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.example.oblak.oblak.settings.RemoteStorageSettingsActivity;
import com.example.oblak.oblak.R;

/**
 * RemoteStorage activity class.
 */
public class RemoteStorage extends AppCompatActivity {

    /**
     * Action listener.
     * Starts RemoteStorageBrowser activity.
     * @param view View of the component calling the function.
     */
    public void showBrowser(View view){
        Intent intent = new Intent(this, RemoteStorageBrowser.class);
        startActivity(intent);
    }

    /**
     * Initializes content layout and view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        setContentView(R.layout.activity_remote_storage);
    }

    /**
     * Initializes the menu of the activity.
     * @param menu Menu.
     * @return true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_remote_storage, menu);
        return true;
    }

    /**
     * Starts the RemoteStorageSettingsActivity activity.
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
            Intent intent = new Intent(this, RemoteStorageSettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}