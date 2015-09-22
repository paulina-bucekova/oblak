package com.example.oblak.oblak.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.example.oblak.client.Client;
import com.example.oblak.oblak.R;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ImageUploader dialog activity class.
 */
public class ImageUploader extends Activity {

    List<String> fileList = new ArrayList<>();

    /**
     * Initializes content layout and view.
     * Retrieves the path of the image or images files from the intent-filter.
     * Creates a list of strings with the paths of the image files
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.activity_image_uploader);
        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.mipmap.ic_launcher);

        Intent intent = getIntent();
        ContentResolver resolver = getContentResolver();

        if (Intent.ACTION_SEND.equals(intent.getAction())){
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (uri != null)
                fileList.add(getPathFromUri(uri, resolver));
        }
        else if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())){
            ArrayList<Uri> uris= intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            if (uris != null) {
                for(Uri uri : uris){
                    fileList.add(getPathFromUri(uri, resolver));
                }
            }
        }
    }

    /**
     * AsyncTask class to send the image to server.
     */
    class Sender extends AsyncTask<String, Void, String> {

        /**
         * Creates client, connects to server and sends the image.
         * @param params String image path.
         * @return String file name.
         */
        @Override
        protected String doInBackground(String... params) {

            String imagePath = params[0];
            String[] pathArray = imagePath.split("/");
            String fileName = pathArray[pathArray.length - 1];

            File file = new File(imagePath);
            byte[] imageBytes = null;
            try
            {
                InputStream inputStream = new FileInputStream(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] bytesArray = new byte[(int)file.length()];
                int bytesRead;

                while ((bytesRead = inputStream.read(bytesArray)) != -1)
                    baos.write(bytesArray, 0, bytesRead);

                imageBytes = baos.toByteArray();
                baos.close();
                inputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

           Client client = new Client();
            try {
                client.getSocketIO().getDataOutputStream().writeInt(1);
                client.getSocketIO().getDataOutputStream().writeInt(0);
                client.getSocketIO().getDataOutputStream().writeUTF("images");
                client.getSocketIO().getDataOutputStream().writeUTF(fileName);
                if (imageBytes != null) {
                    client.getSocketIO().getDataOutputStream().writeInt(imageBytes.length);
                    client.getSocketIO().getDataOutputStream().write(imageBytes);
                }
                else{
                    client.getSocketIO().getDataOutputStream().writeInt(0);
                    client.getSocketIO().getDataOutputStream().write(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
        }
    }

    private String getPathFromUri(Uri uri, ContentResolver resolver) {

        if(uri.toString().startsWith("file://")){
            return uri.getPath();
        }

        String[] data = { MediaStore.Images.Media.DATA };
        Cursor cursor = resolver.query(uri, data, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path= cursor.getString(column_index);
        cursor.close();
        return path;
    }

    /**
     * Action listener.
     * Executes the AsyncTask Sender.
     * Returns to previous activity.
     */
    public void sendImage(View view){
        for(String file: fileList){
            Sender sender = new Sender();
            sender.execute(file);
        }
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
        getMenuInflater().inflate(R.menu.menu_image_uploader, menu);
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