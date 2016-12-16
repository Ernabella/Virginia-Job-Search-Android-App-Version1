package com.example.yiwenren.virginiajobsearch.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yiwenren on 11/30/16.
 */


public class EditSearchResultActivity extends AppCompatActivity {

    private List<SearchResult> searchData; //data for listview
    private Spinner loc_spinner;
    private Spinner education_spinner;
    private ImageButton searchBarBtn;
    private static Gson gson = new Gson();
    private SearchResultAdapter searchResultAdapter;
    ArrayList<SearchResult> copyData; //store the data received
    int totalDocs; //the number of doc
    List<String> education_data;//store the education for spinner
    String query = "";
    String locationString = "";
    boolean locationClick = false;
    boolean locSetup = false;
    String hint;
    String resultJson0;

    AutoCompleteTextView auto;
    private Set<String> set;
    ArrayList<String> wordss;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    String preference = MainActivity.preference;



    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        //add back button in UI
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        searchData = new ArrayList<SearchResult>();
        convertJson(receiveJson());
        setupSearchResult(searchData);
        setupShowDocumentText();
        searchBarHint();


        setupSpinner2(inputLocData());
        setupSearchButton();

        //for auto complete
        set = new HashSet<String>();
        set.add("");
        wordss = new ArrayList<String>();
        iniPreference();
        load();
        autoComplt(wordss);

    }


    //add shortcut to go to home page
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //add "back button" to this activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.home_key:
                Intent intent = new Intent(EditSearchResultActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //for auto complete
    private void iniPreference(){

        prefs = getSharedPreferences(preference, Context.MODE_PRIVATE);
        edit = prefs.edit();
        edit.clear();
    }

    private void save(){
        if(wordss != null && wordss.size() > 0){
            if(set == null){
                System.out.println("set is null");
            } else {
                System.out.println("set situation: " + set.size());
            }


            set.addAll(wordss);
        }

        edit.putStringSet("set", set);
        edit.commit();
    }



    private List<String> load(){
        Set<String> test = prefs.getStringSet("set", null);
        //set = prefs.getStringSet("set", null);
        if(test != null && test.size() > 0){
            set.addAll(test);
            wordss = new ArrayList<String>(set);
        }
        return wordss;
    }

    private void autoComplt(ArrayList<String> wordss){

        auto = (AutoCompleteTextView) findViewById(R.id.auto2);
        auto.setDropDownHeight(350);
        auto.setDropDownVerticalOffset(5);
        //int itemHeight = ((TextView) findViewById(R.id.autoItem)).getHeight();
        //auto.setDropDownHeight(itemHeight * 4);

        auto.setAdapter(new ArrayAdapter<String>(this, R.layout.list_detail, wordss));
    }


//    //mock up data for listview
//    private List<SearchResult> mockSearch(){
//        List<SearchResult> sData = new ArrayList<>();
//       // copyData = new ArrayList<>();
//
////        for(int i = 0; i <= 10; i++) {
////            SearchResult sdata = new SearchResult("degree " + i, "company " + i, "title " + i, "location " + i);
////String toJson = gson.toJson(sdata);
//////debug
////receiveData(i, toJson);
////            sData.add(sdata);
////            copyData.add(sdata);
////        }
////SearchResult sdata = new SearchResult("degree " + 2, "company " + 2, "title " + 2, "location " + 2);
////sData.add(sdata);
////copyData.add(sdata);
//
//        return sData;
//    }

    //setup ListView
    private void setupSearchResult(@NonNull final List<SearchResult> sData){

        ListView listView = (ListView) findViewById(R.id.search_result);
        searchResultAdapter = new SearchResultAdapter(this, sData);
        listView.setAdapter(searchResultAdapter);

        //click the item of listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(EditSearchResultActivity.this, "you click ：" + i, Toast.LENGTH_SHORT).show();
                System.out.println(sData.get(i).getID());
                sendListViewItemData(sData.get(i).getID());

//                Intent intent = new Intent(EditSearchResultActivity.this, EditDetailedResult.class);
//                startActivity(intent);
            }
        });
    }

    //Image Button
    private void setupSearchButton() {
        searchBarBtn = (ImageButton)findViewById(R.id.search_bar_btn);
        searchBarBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //for auto complete
                String getAutoContent = auto.getText().toString();
                String[] getContentS = getAutoContent.split(" ");
                for(int i = 0; i < getContentS.length; i++){
                    wordss.add(getContentS[i]);
                }
                save();
                //sendData();

                sendQueryData();

            }
        });
    }

    private void setupSpinner2(String[] loc_data_arr){
        //location spinner
        loc_spinner = (Spinner) findViewById(R.id.location_spinner2);
        List<String> loc_data = new ArrayList<>();

//add Loction in first sentence:
        loc_data.add("Location");

        ArrayAdapter<String> loc_adapter = null;
        for (int i = 0; i < loc_data_arr.length; i++) {
            loc_data.add(loc_data_arr[i]);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, loc_data);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loc_spinner.setAdapter(dataAdapter);

//set default value for spinner
        int locationIndex = getIntent().getIntExtra("locationIndex", 0);
        loc_spinner.setSelection(locationIndex);

        System.out.println("locationIndex" + locationIndex);

        loc_spinner.setSelection(locationIndex, false);
        loc_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                locationClick = true;
//debug
Toast.makeText(parent.getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //setup search bar, add hint
    private void searchBarHint(){
        EditText searchBar = (EditText) findViewById(R.id.auto2);

        hint = getIntent().getStringExtra("query");
        searchBar.setHint(hint);
        query = hint;
    }


    //need json input
    private void setupShowDocumentText() {

        if(searchData == null || searchData.size() < 1){
            ((TextView) findViewById(R.id.show_document_number)).setText("Sorry, we don't find results.");
        } else {
            if(this.totalDocs < 10 ){
                ((TextView)findViewById(R.id.show_document_number)).setText("Top " + this.totalDocs + " documents");
            } else {
                ((TextView)findViewById(R.id.show_document_number)).setText("top 10 of " + this.totalDocs + " documents");
            }
        }



    }



    //according to degree filter to update ListView
    private void changeDegree(HashSet<String> educations){

        searchData.clear();
        for(int i = 0; i < copyData.size(); i++){
            if(educations.contains(copyData.get(i).getEducation())){
                searchData.add(copyData.get(i));
            }
        }
        searchResultAdapter.notifyDataSetChanged();
    }

    //add data to degree spinner
//    private void addDegreeFilterData(){
//        HashSet<String> educations = new HashSet<>();
//        for(int i = 0; i < copyData.size(); i++){
//            String education = copyData.get(i).getEducation();
//            if(!educations.contains(education)){
//                education_data.add(education);
//                educations.add(education);
//            }
//
//        }
//    }


//input data
//private void inputJson() {
//    Gson gson = new Gson();
//
//    String text = "";
//    try {
//        InputStream is = getAssets().open("test1206.json");
//        int size = is.available();
//        byte[] buffer  = new byte[size];
//        is.read(buffer);
//        text = new String(buffer);
//        Toast.makeText(EditSearchResultActivity.this, "success! good start!", Toast.LENGTH_SHORT).show();
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//      SearchResultGeneral srg = gson.fromJson(text, new TypeToken<SearchResultGeneral>(){}.getType());
//
//    this.totalDocs = srg.getTotalDocs();
//
//
//    copyData = srg.getDocs();
//    //Toast.makeText(EditSearchResultActivity.this, copyData.length, Toast.LENGTH_SHORT).show();
//    searchData = new ArrayList<SearchResult>();
//    for(int i = 0; i < copyData.length; i++){
//        searchData.add(copyData[i]);
//        outputForSearch(searchData.get(i));
//    }
//}

    //get data from main activity or this activity itself
    private String receiveJson(){
        String s1 = getIntent().getStringExtra("act1_json");
//        String s2 = getIntent().getStringExtra("dataFromActivity2");
//        if(s1 == null){
//            return s2;
//        } else {
//            return s1;
//        }

        return s1;
//        return getIntent().getStringExtra("act1_json");
    }

    //get data from main activity
    private boolean convertJson(String jsonString){

        if(jsonString.trim().equals("no result")){
            return false;
        }

        System.out.println(jsonString);
        SearchResultGeneral srg = gson.fromJson(jsonString, new TypeToken<SearchResultGeneral>(){}.getType());

        this.totalDocs = srg.getTotalDocs();
        System.out.println(totalDocs);

        if(searchData != null) searchData.clear();

        searchData = new ArrayList(Arrays.asList(srg.getDocs()));


//        for(int i = 0; i < copyData.size(); i++){
//            searchData.add(copyData.get(i));
//            //outputForSearch(searchData.get(i));
//        }
//        searchResultAdapter.notifyDataSetChanged();
//
        return true;
    }

    private void updateData(SearchResult[] searchResults){
        copyData.clear();
        searchData.clear();
        for(int i = 0; i < searchResults.length; i++){
            copyData.add(searchResults[i]);
            searchData.add(searchResults[i]);
        }
    }

    private void setTotalDocs(int num){
        this.totalDocs = num;
    }

//debug
private void outputForSearch(SearchResult data){
    System.out.println(data.getID());
    System.out.println(data.getTitle());
    System.out.println(data.getLocation());
    System.out.println(data.getDatePosted());
    System.out.println(data.getURL());
    System.out.println(data.getEducation());
}

    //post query: query + location
    //image button
    private void sendQueryData(){

        query = ((EditText) findViewById(R.id.auto2)).getText().toString().trim();
        locationString = loc_spinner.getSelectedItem().toString().trim();
        System.out.println("activity searchresult " + query + " " + locationString);

        if((query == null || query.length() < 1 || query.trim().length() < 1) && loc_spinner.getSelectedItemPosition() == 0){
            System.out.println();
            Toast.makeText(this, "Please input query or choose a location.", Toast.LENGTH_SHORT).show();

        } else {
            new Thread() {
                public void run(){

                    System.out.println("activity searchresult " + query + " " + locationString);
                    //get return Json value

                    resultJson0= postRequest(MainActivity.IP + MainActivity.IPTop10, query);
                    Intent intent = new Intent(EditSearchResultActivity.this, EditSearchResultActivity.class);
                    intent.putExtra("act1_json", resultJson0);
                    intent.putExtra("query", query);
                    intent.putExtra("locationIndex", loc_spinner.getSelectedItemPosition());
                    startActivity(intent);

                }
            }.start();
            //convertJson(resultJson0);
        }
    }

    private String postRequest(String urlString, String query){
        String resultpost="";
        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);

            connection.setInstanceFollowRedirects(true);
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.connect();

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());

            //judge whether the location is null. If it is, send null to server
            String data;
//            if(locationString.trim().length() < 1){
//                data = "query=" + URLEncoder.encode(query, "UTF-8") + "&location=" + URLEncoder.encode(null, "UTF-8");
//            } else {
//                data = "query=" + URLEncoder.encode(query, "UTF-8") + "&location=" + URLEncoder.encode(locationString, "UTF-8");
//            }
            //if query == null, the return will
            if(locationString.equals("Location")){
                data = "query=" + URLEncoder.encode(query, "UTF-8");
            } else {
                if(query == null || query.length() < 1) {
                    data = "location=" + URLEncoder.encode(locationString, "UTF-8");
                } else {
                    data = "query=" + URLEncoder.encode(query, "UTF-8") + "&location=" + URLEncoder.encode(locationString, "UTF-8");
                }
            }

            //String data = "query = " + URLEncoder.encode(query, "UTF-8") + "location = " + URLEncoder.encode(locationString, "UTF-8");

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

    //post query: query + location + ID
    private void sendListViewItemData(final String ID){
        EditText editText = (EditText) findViewById(R.id.auto2);
        query = editText.getText().toString();
        query = hint;
        locationString = loc_spinner.getSelectedItem().toString();

        new Thread() {
            public void run(){

                //get return Json value
                String resultJson = postRequest(MainActivity.IP + MainActivity.IPById, query, locationString, ID);
                System.out.println(resultJson);
                Intent intent = new Intent(EditSearchResultActivity.this, EditDetailedResult.class);
                intent.putExtra("ID", ID);
                intent.putExtra("ListviewItem", resultJson);
                //if(locationString == "Location")
                if(locationString.equals("Location")){
                    intent.putExtra("dataFromActivity2_query_location", query + "&");
                } else {
                    intent.putExtra("dataFromActivity2_query_location", query + "&" + locationString);
                }

                startActivity(intent);
            }
        }.start();


//        if((query == null || query.length() < 1) && !locationClick){
//            Intent intent = new Intent(EditSearchResultActivity.this, EditSearchResultActivity.class);
//            startActivity(intent);
//        } else {
//
//        }
    }

    private String postRequest(String urlString, String query, String locationString, String ID){
        String resultpost="";
        try{
            URL url = new URL(urlString);
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

    private String[] inputLocData() {
        // Gson gson = new Gson();

        String text = "";
        String[] error = new String[0];


        try {
            InputStream is = getAssets().open("locationsum.txt");
            int size = is.available();
            byte[] buffer  = new byte[size];
            is.read(buffer);
            text = new String(buffer);

            String[] loc_data_arr = text.split("\n");

            System.out.println("arr 1" + loc_data_arr[0] + " arr 10" + loc_data_arr[11]);
            return loc_data_arr;

            //Toast.makeText(MainActivity.this, "success! good start!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return error;
    }



}
