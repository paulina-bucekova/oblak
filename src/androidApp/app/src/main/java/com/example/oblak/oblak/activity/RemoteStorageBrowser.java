package com.example.oblak.oblak.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.oblak.client.Client;
import com.example.oblak.oblak.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RemoteStorageBrowser activity class.
 */
public class RemoteStorageBrowser extends AppCompatActivity {

    private String directoryPath;

    /**
     * Initializes content layout and view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        setContentView(R.layout.activity_remote_storage_browser);

        directoryPath = "/oblak";
        listFiles();
    }

    /**
     * AsyncTask class to consult the active remote file downloads and updates the layout of the activity.
     */
    class Browser extends AsyncTask<Void, Void, Map<String, String>> {

        /**
         * Creates client and connects to server.
         * @param params
         * @return Map<String, String> with the names of the files stored in remote server.
         */
        @Override
        protected Map<String, String> doInBackground(Void... params) {
            Client client = new Client();
            Map<String, String> fileMap = new HashMap<>();
            try {
                client.getSocketIO().getDataOutputStream().writeInt(1);
                client.getSocketIO().getDataOutputStream().writeInt(1);
                client.getSocketIO().getDataOutputStream().writeUTF(directoryPath);

                try {
                    fileMap = (Map<String, String>) client.getSocketIO().getObjectInputStream().readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  fileMap;
        }

        /**
         * Updates the layout of the activity.
         * @param fileMap Map<String, String> with the names of the files stored in remote server.
         */
        protected void onPostExecute(Map<String, String> fileMap) {
            super.onPostExecute(fileMap);

            LinearLayout linearLayoutBack = (LinearLayout) findViewById(R.id.linearLayoutHorizontalBack);
            if((linearLayoutBack).getChildCount() > 0){
                linearLayoutBack.removeAllViews();
            }

            if((!directoryPath.equals("/oblak"))){
                Button buttonBack = new Button(RemoteStorageBrowser.this);
                buttonBack.setText(getString(R.string.button_text_rsb_back));
                buttonBack.setClickable(true);
                buttonBack.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        if ((!directoryPath.equals("/oblak"))) {
                            String[] directoryPathArray = directoryPath.split("/");
                            directoryPath = "";
                            for (int i = 0; i < directoryPathArray.length - 1; i++) {
                                if (directoryPathArray[i].equals("")) {
                                    directoryPath += directoryPathArray[i];
                                } else {
                                    directoryPath += "/" + directoryPathArray[i];
                                }
                            }
                            listFiles();
                        }
                    }
                });
                linearLayoutBack.addView(buttonBack);
            }

            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutVerticalFileList);
            if((linearLayout).getChildCount() > 0)
                linearLayout.removeAllViews();

            List<String> fileList = new ArrayList<>();
            List<String> directoryList = new ArrayList<>();

            for(Map.Entry<String,String> entry: fileMap.entrySet()){
                if(entry.getValue().equals("file")){
                    fileList.add(entry.getKey());
                }
                else{
                    directoryList.add(entry.getKey());
                }
            }
            Collections.sort(fileList);
            Collections.sort(directoryList);

            generate(directoryList, linearLayout, "directory");
            generate(fileList, linearLayout, "file");
        }

        /**
         * Generates the components of the list and adds them to the layout.
         * @param fileList List of stings with file names.
         * @param linearLayout Layout to be updated.
         * @param fileType String which specifies the type of the file.
         */
        private void generate(List<String> fileList, LinearLayout linearLayout, String fileType) {
            for(String entry: fileList){
                final String fileNameText = entry;

                LinearLayout horizontalLayout = new LinearLayout(RemoteStorageBrowser.this);
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

                ImageView imageView = new ImageView(RemoteStorageBrowser.this);
                imageView.setAdjustViewBounds(true);
                imageView.setMaxHeight(90);
                imageView.setMaxWidth(120);
                imageView.setPadding(15, 15, 0, 0);
                if(fileType.equals("file")){
                    imageView.setImageResource(R.drawable.file);
                }
                else{
                    imageView.setImageResource(R.drawable.folder);
                    imageView.setClickable(true);
                    imageView.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            directoryPath += "/" + fileNameText;
                            listFiles();
                        }
                    });
                }
                horizontalLayout.addView(imageView);

                TextView textView = new TextView(RemoteStorageBrowser.this);
                textView.setTextAppearance(RemoteStorageBrowser.this, android.R.style.TextAppearance_Holo_Medium);
                textView.setText(entry);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
                layoutParams.setMargins(20, 20, 0, 0);
                textView.setLayoutParams(layoutParams);
                if(!fileType.equals("file")) {
                    textView.setClickable(true);
                    textView.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            directoryPath += "/" + fileNameText;
                            listFiles();
                        }
                    });
                }
                horizontalLayout.addView(textView);
                linearLayout.addView(horizontalLayout);
            }
        }
    }

    /**
     * Executes the AsyncTask Browser.
     */
    public void listFiles(){
        Browser browser = new Browser();
        browser.execute();
    }

    /**
     * Initializes the menu of the activity.
     * @param menu Menu.
     * @return true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_remote_storage_browser, menu);
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