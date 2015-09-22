package com.example.oblak.oblak.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.example.oblak.oblak.R;

/**
 * LinkDownloader class.
 */
public class LinkDownloader extends AppCompatActivity {

    /**
     * Action listener.
     * Starts DownloadsProgress activity.
     * @param view View of the component calling the function.
     */
    public void showDownloadsProgress(View view){
        Intent intent = new Intent(this, DownloadsProgress.class);
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

        setContentView(R.layout.activity_link_downloader);
    }

    /**
     * Initializes the menu of the activity.
     * @param menu Menu.
     * @return true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_link_downloader, menu);
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
