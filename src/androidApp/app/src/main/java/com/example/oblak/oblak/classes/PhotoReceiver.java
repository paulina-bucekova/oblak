package com.example.oblak.oblak.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.os.AsyncTask;
import android.preference.PreferenceManager;


import com.example.oblak.client.Client;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * PhotoReceiver class extends BroadcastReceiver.
 */
public class PhotoReceiver extends BroadcastReceiver {

    private String imagePath;

    /**
     * Retrieves the path of the image made by the camera from the intent-filter.
     * Executes the AsyncTask Sender.
     * @param context Context
     * @param intent Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Cursor cursor = context.getContentResolver().query(intent.getData(), null, null, null, null);
        cursor.moveToFirst();
        imagePath = cursor.getString(cursor.getColumnIndex("_data"));
        cursor.close();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        boolean automaticUpload = sharedPreferences.getBoolean("automatic_upload", true);

        if(automaticUpload){
            Sender sender = new Sender();
            sender.execute();
        }
    }

    /**
     * AsyncTask class to send the image to server.
     */
    class Sender extends AsyncTask<Void, Void, String> {

        /**
         * Creates client, connects to server and sends the image.
         * @param params Void.
         * @return String file name.
         */
        @Override
        protected String doInBackground(Void... params) {

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
}