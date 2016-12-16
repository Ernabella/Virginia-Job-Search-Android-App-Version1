package com.example.yiwenren.virginiajobsearch.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yiwenren.virginiajobsearch.R;
import com.example.yiwenren.virginiajobsearch.models.SearchResult;
import com.example.yiwenren.virginiajobsearch.models.SearchResultGeneral;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yiwenren on 12/9/16.
 */

public class RecommendedJob extends AppCompatActivity {

    private SearchResultAdapter searchResultAdapter;
    private static String URL = MainActivity.IP + MainActivity.IPById;
    private ArrayList<SearchResult> searchData;
    private Gson gson = new Gson();
    String jsonString;


    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommended_job);

        //add back button in the UI
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Log.i("tag", "sdk版本" + Build.VERSION.SDK_INT);

        if(!getDataFromActivity()){
            ((TextView) findViewById(R.id.recommandedJobText)).setText("Sorry, no recommanded jobs");


            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 30, 0, 0); // llp.setMargins(left, top, right, bottom);
            ((TextView) findViewById(R.id.recommandedJobText)).setLayoutParams(llp);
        } else {
            setupSearchResult(searchData);
        }

    }


    //setup ListView
    private void setupSearchResult(@NonNull final List<SearchResult> sData){
        ListView listView = (ListView) findViewById(R.id.recommendJob);
        searchResultAdapter = new SearchResultAdapter(this, sData);
        listView.setAdapter(searchResultAdapter);

        //click the item of listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Log.d("selectListView", sData.get(i).toString());
                //Toast.makeText(EditSearchResultActivity.this, "you click ：" + i, Toast.LENGTH_SHORT).show();
                System.out.println("GET(i) ID = " + sData.get(i).getID());

                sendListViewItemData(sData.get(i).getID());

//                Intent intent = new Intent(RecommendedJob.this, EditDetailedResult.class);
//                startActivity(intent);

            }
        });
    }

    //post query: query + location + ID
    private void sendListViewItemData(final String ID) {

        new Thread() {
            public void run() {
                //get return Json value
                String resultJson = postRequest(URL, ID);
                System.out.println(resultJson);
                Intent intent = new Intent(RecommendedJob.this, EditDetailedResult.class);
                intent.putExtra("RecommandedJobDetail", resultJson);
                String getString = getIntent().getStringExtra("dataFromActivity2_query_location");
                intent.putExtra("dataFromActivity2_query_location", getString);
                startActivity(intent);
            }
        }.start();
    }

    //add "back button" and "home button" to this activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.home_key:
                Intent intent = new Intent(RecommendedJob.this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //post query: query + location + ID
//    private void sendListViewItemData(final String ID) {
//        EditText editText = (EditText) findViewById(R.id.search_bar_text2);
//        query = editText.getText().toString();
//        locationString = loc_spinner.getSelectedItem().toString();
//
//        new Thread() {
//            public void run() {
//                //get return Json value
//                String resultJson = postRequest("http://150.212.8.117:8080/DynmacinIR/ProcessRetrieveDocDetailByID", query, locationString, ID);
//                System.out.println(resultJson);
//                Intent intent = new Intent(EditSearchResultActivity.this, EditDetailedResult.class);
//                intent.putExtra("ListviewItem", resultJson);
//                startActivity(intent);
//            }
//        }.start();
//    }




//for debug
    private void readData(){
        try {
            InputStream is = null;
            try {
                is = getAssets().open("test1206.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
            int size = is.available();
            byte[] buffer  = new byte[size];
            is.read(buffer);
            String text = new String(buffer);
            System.out.println("iiiiii + " + text);
            convertJson(text);

            //get data from main activity


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //get data from detailed activity
    private boolean getDataFromActivity(){
        jsonString = getIntent().getStringExtra("RecommandListviewItem");
        if(jsonString.trim().equals("no result")){
            return false;
        } else {
            SearchResultGeneral recommandJobGeneral = gson.fromJson(jsonString, new TypeToken<SearchResultGeneral>() {
            }.getType());
            searchData = new ArrayList(Arrays.asList(recommandJobGeneral.getDocs()));
            return true;
        }
    }

//for debug
    private void convertJson(String jsonString){

        System.out.println(jsonString);
        SearchResultGeneral srg = gson.fromJson(jsonString, new TypeToken<SearchResultGeneral>(){}.getType());

        searchData = new ArrayList(Arrays.asList(srg.getDocs()));

    }

    private String postRequest(String urlString,  String ID){
        String resultpost="";
        try{
            java.net.URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);

            connection.setInstanceFollowRedirects(true);
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(20000);
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.connect();

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            //String data = "query = " + URLEncoder.encode(query, "UTF-8") + "&location = " + URLEncoder.encode(locationString, "UTF-8") +"&feedbackjobid = " + URLEncoder.encode(ID, "UTF-8");
            String data = "feedbackjobid=" + URLEncoder.encode(ID, "UTF-8");
            System.out.println("feedbackid = " + ID);

            os.writeBytes(data);
            os.flush();
            os.close();

            // input stream
            InputStream is = connection.getInputStream();
            // output stream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len = 0;

            byte buffer[] = new byte[1024];

            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            // release resource
            is.close();
            baos.close();

            resultpost = new String(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("HTTP post failed！");
            resultpost = "http post error";
        }
        return resultpost;
    }


    //add shortcut to go to home page
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



}
