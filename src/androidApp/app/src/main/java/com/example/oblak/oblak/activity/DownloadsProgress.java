package com.example.oblak.oblak.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.oblak.oblak.R;
import com.example.oblak.client.Client;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * DownloadsProgress activity class.
 */
public class DownloadsProgress extends AppCompatActivity {

    private AskStatusTask currentTask = null;

    /**
     * AsyncTask class to consult the active remote file downloads.
     */
    private class AskStatusTask extends AsyncTask<Boolean, Void, List<Map<String,List<Integer>>>> {

        private long initTime;

        public AskStatusTask()
        {
            this.initTime = System.nanoTime();
        }

        /**
         * Creates client and connects to server.
         * @param params Boolean
         * @return List with the names and size of the active downloands as well as the current bytes already downloaded.
         */
        protected List<Map<String,List<Integer>>> doInBackground(Boolean... params){

            List<Map<String,List<Integer>>> allActiveDowloadStatus = new ArrayList<>();
            boolean done = false;
            do{
                long currentTime = System.nanoTime();

                if (params[0] || currentTime - initTime >= 3000000000.0){
                    try {
                        Client client = new Client();
                        client.getSocketIO().getDataOutputStream().writeInt(0);
                        client.getSocketIO().getDataOutputStream().writeInt(1);
                        allActiveDowloadStatus = (List<Map<String,List<Integer>>>) client.getSocketIO().getObjectInputStream().readObject();
                        client.getSocketIO().getSocket().close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        done = true;
                    }
                }else{
                    if (isCancelled()){
                        done = true;
                    }

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            while(!done);

            return allActiveDowloadStatus;
        }

        /**
         * Updates the list with the active downloads.
         * Executes the AsyncTask AskStatusTask again.
         * @param downloads List with the active downloads.
         */
        @Override
        protected void onPostExecute(List<Map<String,List<Integer>>> downloads) {
            updateList(downloads);

            currentTask = new AskStatusTask();
            currentTask.execute(false);
        }

        /**
         * Updates the list with the active downloads.
         * @param downloads List with the active downloads.
         */
        @Override
        protected void onCancelled(List<Map<String,List<Integer>>> downloads) {
            updateList(downloads);
        }
    }

    /**
     * Initializes content layout and view.
     * Executes the AsyncTask AskStatusTask.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        setContentView(R.layout.activity_downloads_progress);

        if (currentTask != null){
            currentTask.cancel(false);
        }

        currentTask = new AskStatusTask();
        currentTask.execute(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Cancels the current AsyncTask AskStatusTask.
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (currentTask != null){
            currentTask.cancel(false);
            currentTask = null;
        }
    }

    /**
     * Executes the AsyncTask AskStatusTask.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (currentTask != null){
            currentTask.cancel(false);
        }

        currentTask = new AskStatusTask();
        currentTask.execute(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Initializes the menu of the activity.
     * @param menu Menu.
     * @return true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_downloads_progress, menu);
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

    /**
     * Transforms the number of bytes to a string.
     * @param size number of bytes.
     * @return String size.
     */
    private String getSizeString(int size) {
        String sizeString;

        if (size >  1024 * 1024){
            sizeString = Integer.toString(size / 1024 / 1024) + " MB";
        }
        else if (size > 1024){
            sizeString = Integer.toString(size / 1024) + " KB";
        }
        else{
            sizeString = Integer.toString(size) + " B";
        }

        return sizeString;
    }

    /**
     * Generates the components of the list and adds them to the layout..
     * @param allActiveDownloads List of all active downloads.
     */
    public void updateList(List<Map<String,List<Integer>>> allActiveDownloads) {
        if (allActiveDownloads != null){
            ScrollView scrollView = (ScrollView) findViewById(R.id.DownloadsProgressScrollViewId);
            scrollView.removeAllViews();

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            if (!allActiveDownloads.isEmpty()){
                for (Map<String, List<Integer>> statusMap : allActiveDownloads) {
                    for (Map.Entry<String, List<Integer>> entry : statusMap.entrySet()) {
                        String fileName = entry.getKey();
                        List<Integer> fileStatus = entry.getValue();
                        int currentSize = fileStatus.get(1);
                        int totalSize = fileStatus.get(0);
                        double percentage = 0;

                        if (totalSize != 0) {
                            percentage = ((double) currentSize / totalSize) * 100.0;
                        }

                        int percentageInt = (int) percentage;

                        if (percentageInt < 0) {
                            percentageInt = 0;
                        } else if (percentageInt > 100) {
                            percentageInt = 100;
                        }

                        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
                        progressBar.setIndeterminate(false);
                        progressBar.setProgress((int) percentage);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
                        layoutParams.setMargins(20, 20, 0, 0);
                        layoutParams.weight = 0.90f;
                        progressBar.setLayoutParams(layoutParams);

                        TextView percentText = new TextView(this);
                        percentText.setText(Integer.toString(percentageInt) + " %");
                        percentText.setTextAppearance(this, android.R.style.TextAppearance_Large);
                        LinearLayout.LayoutParams percentTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
                        percentTextLayoutParams.weight = 0.10f;
                        percentTextLayoutParams.setMargins(20, 20, 0, 0);
                        percentText.setLayoutParams(percentTextLayoutParams);

                        LinearLayout horizontalLayout = new LinearLayout(this);
                        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                        horizontalLayout.addView(progressBar);
                        horizontalLayout.addView(percentText);

                        TextView currentSizeView = new TextView(this);
                        currentSizeView.setText(getSizeString(currentSize < totalSize ? currentSize : totalSize) + " / " + getSizeString(totalSize));
                        LinearLayout.LayoutParams currentSizeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL);
                        currentSizeLayoutParams.setMargins(20, 20, 0, 0);
                        currentSizeLayoutParams.weight = 0.70f;
                        currentSizeView.setLayoutParams(currentSizeLayoutParams);
                        currentSizeView.setTextAppearance(this, android.R.style.TextAppearance_Holo_Small);

                        TextView fileNameView = new TextView(this);
                        fileNameView.setText(fileName);
                        LinearLayout.LayoutParams fileNameLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL);
                        fileNameLayoutParams.setMargins(20, 20, 0, 0);
                        fileNameLayoutParams.weight = 0.30f;
                        fileNameView.setLayoutParams(fileNameLayoutParams);
                        fileNameView.setTextAppearance(this, android.R.style.TextAppearance_Holo_Medium);

                        LinearLayout sizeNameLayout = new LinearLayout(this);
                        sizeNameLayout.setOrientation(LinearLayout.HORIZONTAL);
                        sizeNameLayout.addView(fileNameView);
                        sizeNameLayout.addView(currentSizeView);

                        LinearLayout verticalLayout = new LinearLayout(this);
                        verticalLayout.setOrientation(LinearLayout.VERTICAL);
                        verticalLayout.addView(horizontalLayout);
                        verticalLayout.addView(sizeNameLayout);

                        linearLayout.addView(verticalLayout);
                    }
                }
            }
            else
            {
                TextView noDownloadsText = new TextView(this);
                noDownloadsText.setText("No active downloads.");
                noDownloadsText.setTextAppearance(this, android.R.style.TextAppearance_Large);
                LinearLayout.LayoutParams noDownloadsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
                noDownloadsLayoutParams.setMargins(50, 50, 0, 0);
                noDownloadsText.setLayoutParams(noDownloadsLayoutParams);
                linearLayout.addView(noDownloadsText);
            }

            scrollView.addView(linearLayout);
        }
    }
}
