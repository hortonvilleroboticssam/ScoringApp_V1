package com.hortonvillerobotics.scoringapp_v1;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progress;

    EditText teamNumber;
    EditText matchNumber;
    EditText relicZone;
    Button button;
    String[] results = new String[13];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button=(Button)findViewById(R.id.btn_submit);
        teamNumber =(EditText)findViewById(R.id.field_teamNumber);
        matchNumber = (EditText)findViewById(R.id.field_matchNumber);
        relicZone =(EditText)findViewById(R.id.field_relicZone);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: ENTER ALL POSSIBLE PARAMETERS THAT ARE IN THE GOOGLE SCRIPT
                results[1] = teamNumber.getText().toString();

                results[6] = relicZone.getText().toString();

                new MainActivity.SendRequest().execute();
            }

        }   );

    }

    public static class SettingsFrag extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstancestate){
            super.onCreate(savedInstancestate);
            addPreferencesFromResource(R.xml.pref_notification);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
    public class SendRequest extends AsyncTask<String, Void, String> {


        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{

                URL url = new URL("https://script.google.com/a/hortonvillerobotics.com/macros/s/AKfycbzSukoOXFOX1jKz3rp7MDyrG_czuIuk6zeoA-3iNLy1AH4KD58/exec");
                JSONObject postDataParams = new JSONObject();

                String id= "14DoM0-EFK_oKTBs1sgPWpb5_Lb9PVxKGNuI44nqNT3Y";
                postDataParams.put("matchNumber",results[0]);
                postDataParams.put("teamNumber", results[1]);
                postDataParams.put("autoJewel", results[2]);
                postDataParams.put("autoGlyph", results[3]);
                postDataParams.put("autoCrypto", results[4]);
                postDataParams.put("safeZone", results[5]);
                postDataParams.put("relicZone", results[6]);
                postDataParams.put("relicUpright", results[7]);
                postDataParams.put("balanced", results[8]);
                postDataParams.put("totalGlyphs", results[9]);
                postDataParams.put("glyphRows", results[10]);
                postDataParams.put("GlyphCols", results[11]);
                postDataParams.put("Cypher", results[12]);
                postDataParams.put("id",id);


                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "SUCCESS",
                    Toast.LENGTH_LONG).show();

            finish();
            startActivity(getIntent());
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}

