package com.example.yiwenren.virginiajobsearch.utils;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yiwenren.virginiajobsearch.R;
import com.example.yiwenren.virginiajobsearch.models.DetailedResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by yiwenren on 12/1/16.
 */

public class EditDetailedResult extends AppCompatActivity {
    private static Gson gson = new Gson();
    Button recommandedJob;
    String ID;
    String jsonString;
    String query ;
    String location ;


    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_result);

        //add back button in the UI
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(receiveJsonData()){
            setupButton();
            setupUI(convertJson());
        } else {

        }


        //setupUI(receiveJsonData());

    }

    //setup UI
    private void setupUI(DetailedResult data){
        TextView jobtitle = (TextView) findViewById(R.id.title3);
        hightLight(jobtitle, "", query, data.getTitle());
        //jobtitle.setText();

        hightLight((TextView) findViewById(R.id.organization), "Organization: ", query, data.getOrganizationName());
        hightLight((TextView) findViewById(R.id.location3), "Location: ", query, data.getLocation());
        hightLight((TextView) findViewById(R.id.postTime), "post on ", query, data.getDatePosted());
        hightLight((TextView) findViewById(R.id.jobDescription), "", query, data.getJobDescription());
        TextView url = (TextView) findViewById(R.id.url);
        url.setAutoLinkMask(Linkify.ALL);
        url.setText(data.getURL());

    }


    //add "back button" and "home button" to this activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.home_key:
                Intent intent = new Intent(EditDetailedResult.this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Image Button
    private void setupButton() {
        recommandedJob = (Button)findViewById(R.id.recommendJobButon);
        recommandedJob.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //judge whether query == null
                if(query == null || query.length() < 1){
                    Toast.makeText(EditDetailedResult.this, "No Recommand Jobs for location only search", Toast.LENGTH_SHORT).show();

                } else {
                    sendListViewItemData();
                }

            }
        });
    }



    private DetailedResult mockDetailedResult(){
        String jobDes = "Drive advertising revenue with aggressive sales efforts that penetrate new markets and increase existing market presence\\n" + "Consult with customers to be able to properly articulate the value proposition of a product package that exceeds customer goals and aligns with the business direction.\\n"+ "Build customer relationships on an ongoing basis";
        String responsibility = "College degree preferred" + "2-5 years experience in print and/or online advertising sales and be able to show consistent sales results in previous positions";
        String experience = responsibility;
        String skills = responsibility;
        DetailedResult detailedResult = new DetailedResult("Media Consultant", "2016-03-17", "http://my.jobs/ac1ebffe73894baf8571398bbcba1a951839","location1","1001", "1105 Media, Inc.", jobDes);

//debug
String jsonString = gson.toJson(detailedResult);
System.out.println("mock data for detailed result : " + jsonString);
DetailedResult detailedResult2 = gson.fromJson(jsonString, new TypeToken<DetailedResult>(){}.getType());


        return detailedResult;

    }

    private boolean receiveJsonData(){

        String resultJsonFromListview = getIntent().getStringExtra("ListviewItem");
        String resultJsonFromRecommand = getIntent().getStringExtra("RecommandedJobDetail");
        String getString = getIntent().getStringExtra("dataFromActivity2_query_location");

        System.out.println("getString : " + getString);
        String getStringArr[] = getString.split("&");
        if(getString.substring(0,getString.indexOf("&")).length()>1) {
            query = getStringArr[0];
        }
        if(getString.substring(getString.indexOf("&")).length()>1) {
            location = getStringArr[1];
        }
        ID = getIntent().getStringExtra("ID");

        System.out.println("editDetail ID : " + ID);



        if(resultJsonFromListview == null){
            jsonString = resultJsonFromRecommand;
        } else {
            jsonString = resultJsonFromListview;
        }
        if(jsonString.trim().equals("no result")){
            return false;
        } else {
            return true;
        }

    }

    private DetailedResult convertJson(){
        DetailedResult srg = gson.fromJson(jsonString, new TypeToken<DetailedResult>(){}.getType());
        ID = srg.getID();
        return srg;
    }


    private void hightLight(TextView tv, String filedName, String queries, String item){
        if(queries == null || queries.length() < 1){
            tv.setText(item);
            return;
        }
        String[] tokens = queries.split(" ");

        Spannable spannable = new SpannableString(item);

        item = item.toLowerCase();

        for(int i = 0; i < tokens.length; i++){
            String query0 = tokens[i].toLowerCase();

            if(item.indexOf(query0) < 0){
                tv.setText(item);
                return;
            }

            int endPos = 0; //= startPos + query.length();
            int index = 0;

            int startPos = 0;
            int length = query0.length();

            System.out.println("startpos is " + startPos);

            while(endPos < item.length() && item.substring(endPos).indexOf(query0) >= 0){
                startPos = item.substring(endPos).indexOf(query0);
                index = endPos + startPos;
                endPos = index + length;

//for debug
//                System.out.println("start = " + startPos);
//                System.out.println("index = " + index);
//                System.out.println("end = " + endPos);

                ColorStateList textColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.BLUE });
                //TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, 50, textColor, null);
                BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.YELLOW);
                spannable.setSpan(backgroundColorSpan, index, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(spannable);
            }

        }

    }



    //debug
    private String inputJson() {
        // Gson gson = new Gson();

        String text = "";
        try {
            InputStream is = getAssets().open("test1206.json");
            int size = is.available();
            byte[] buffer  = new byte[size];
            is.read(buffer);
            text = new String(buffer);
            System.out.println("recommand button " + text);
            //Toast.makeText(MainActivity.this, "success! good start!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    //post query: query + location + ID
    private void sendListViewItemData(){

        new Thread() {
            public void run(){

                //get return Json value
                String resultJson = postRequest(MainActivity.IP + MainActivity.IPById, query, location, ID);
                System.out.println("");
                System.out.println(resultJson);
                Intent intent = new Intent(EditDetailedResult.this, RecommendedJob.class);
                intent.putExtra("RecommandListviewItem", resultJson);
                intent.putExtra("ID", ID);
                intent.putExtra("dataFromActivity2_query_location", query + "&" + location);

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
            String data;
            if(locationString == null ||locationString.length() < 1){
                data = "query=" + URLEncoder.encode(query, "UTF-8") + "&feedbackjobid=" + URLEncoder.encode(ID, "UTF-8");
            } else {
                if(query == null || query.length() < 1) {
                    data = "location=" + URLEncoder.encode(locationString, "UTF-8") + "&feedbackjobid=" + URLEncoder.encode(ID, "UTF-8");
                } else {
                    data = "query=" + URLEncoder.encode(query, "UTF-8") + "&location=" + URLEncoder.encode(locationString, "UTF-8") + "&feedbackjobid=" + URLEncoder.encode(ID, "UTF-8");
                }
            }

            //String data = "query=" + URLEncoder.encode(query, "UTF-8") + "&location=" + URLEncoder.encode(locationString, "UTF-8") + "&feedbackjobid=" + URLEncoder.encode(ID, "UTF-8");
            System.out.println("EditDetailResult recommand button ---> query =" + query + "location = " + locationString + " ID = " + ID);


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
