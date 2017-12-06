package com.hortonvillerobotics.scoringapp_v1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Switch;
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

    EditText matchNumber;
    EditText teamNumber;
    Switch jewel;
    Switch autoGlyph;
    Switch autoCrypto;
    Switch safeZone;
    EditText relicZone;
    Switch relicUpright;
    Switch balanced;
    EditText numberGlyphs;
    EditText rowGlyphs;
    EditText colGlyphs;
    Switch pattern;
    EditText notes;
    Button button;

    SharedPreferences pM;

    String[] results = new String[14];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pM = PreferenceManager.getDefaultSharedPreferences(this);

        button=(Button)findViewById(R.id.btn_submit);

        matchNumber = (EditText)findViewById(R.id.field_matchNumber);
        teamNumber =(EditText)findViewById(R.id.field_teamNumber);
        jewel = findViewById(R.id.switch_ball);
        autoGlyph = findViewById(R.id.switch_glyph);
        autoCrypto = findViewById(R.id.switch_crypto);
        safeZone = findViewById(R.id.switch_safeZone);
        relicZone =(EditText)findViewById(R.id.field_relicZone);
        relicUpright = findViewById(R.id.switch_relicUpright);
        balanced = findViewById(R.id.switch_robotBalanced);
        numberGlyphs = (EditText) findViewById(R.id.field_numberGlyphs);
        rowGlyphs = (EditText) findViewById(R.id.field_rowGlyphs);
        colGlyphs = (EditText) findViewById(R.id.field_columnGlyphs);
        notes = (EditText) findViewById(R.id.field_comments);
        pattern = findViewById(R.id.switch_pattern);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: ENTER ALL POSSIBLE PARAMETERS THAT ARE IN THE GOOGLE SCRIPT
                results[0] = matchNumber.getText().toString();
                results[1] = teamNumber.getText().toString();
                results[2] = "" + jewel.isChecked();
                results[3] = "" + autoGlyph.isChecked();
                results[4] = "" + autoCrypto.isChecked();
                results[5] = "" + safeZone.isChecked();
                results[6] = relicZone.getText().toString();
                results[7] = "" + relicUpright.isChecked();
                results[8] = "" + balanced.isChecked();
                results[9] = numberGlyphs.getText().toString();
                results[10] = rowGlyphs.getText().toString();
                results[11] = colGlyphs.getText().toString();
                results[12] = "" + balanced.isChecked();
                results[13] = notes.getText().toString();

                new MainActivity.SendRequest().execute();
            }

        }   );

    }



    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);
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
            Intent i = new Intent(this, SettingsActivity.class);
            Toast.makeText(this, "Made it here!", Toast.LENGTH_LONG);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public class SendRequest extends AsyncTask<String, Void, String> {


        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{

                String sheetID = pM.getString(getString(R.string.sheetID), "");

                URL url = new URL("https://script.google.com/a/hortonvillerobotics.com/macros/s/AKfycbzSukoOXFOX1jKz3rp7MDyrG_czuIuk6zeoA-3iNLy1AH4KD58/exec");
                JSONObject postDataParams = new JSONObject();
                String id= sheetID;//"14DoM0-EFK_oKTBs1sgPWpb5_Lb9PVxKGNuI44nqNT3Y";

                String[] parameters = {"matchNumber", "teamNumber", "autoJewel"
                , "autoGlyph", "autoCrypto", "safeZone", "relicZone", "relicUpright"
                , "balanced", "totalGlyphs", "glyphRows", "glyphCols", "pattern","notes"};

                for(int i = 0; i < parameters.length; i++) {
                    postDataParams.put(parameters[i],results[i]);
                }
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

