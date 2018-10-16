package com.example.hp.medic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button search;
    EditText symptom;
    ArrayList<String> Issue_Names;
    ArrayList<String> Issue_ID;
    ListView list;
    AccessToken key;
    ArrayAdapter<String> itemsAdapter;
    String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        key = new AccessToken();
        search = findViewById(R.id.search);
        new GetToken(key, search).execute();
        symptom = findViewById(R.id.symptom);
        list = findViewById(R.id.list);
        Issue_Names = new ArrayList<String>();
        Issue_ID = new ArrayList<String>();
        keyword = "";
        search.setEnabled(false);

        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Issue_Names);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {
                TextView txt = (TextView)v.findViewById(android.R.id.text1);
                Intent intent = new Intent(MainActivity.this, Treatment.class);
                intent.putExtra("Name",txt.getText());
                String iid = Issue_ID.get(position);
                intent.putExtra("ID",iid);
                String issueurl = "https://sandbox-healthservice.priaid.ch/issues/"+iid+"/info?token="+key.Token+"&format=json&language=en-gb";
                SIssue(issueurl,intent);
            }
        });


        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!(String.valueOf(symptom.getText()).replace(" ", "").equalsIgnoreCase(keyword.replace(" ", "")))) {
                    Log.e("onClick: ", "yes");
                    Issue_Names.clear();
                    Issue_ID.clear();
                    itemsAdapter.clear();
                    keyword = String.valueOf(symptom.getText());
                    SSymptom(keyword);
                }
            }
        });

    }

    void SSymptom(final String keyword) {

        Log.e("SSymptom: ", key.Token);
        String symurl = "https://sandbox-healthservice.priaid.ch/symptoms?token=" + key.Token + "&format=json&language=en-gb";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, symurl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray array) {
                        String diagurl = "";
                        String name = "";
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject currObject = null;
                            try {
                                currObject = array.getJSONObject(i);
                                name = currObject.getString("Name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (name.replace(" ", "").equalsIgnoreCase(keyword.replace(" ", ""))) {
                                try {
                                    diagurl = "https://sandbox-healthservice.priaid.ch/diagnosis?symptoms=[" + currObject.getString("ID") + "]&gender=male&year_of_birth=1997&token=" + key.Token + "&format=json&language=en-gb";
                                    SDiagnosis(diagurl);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                        if (diagurl.isEmpty()) {
                            symptom.setText("Wrong Input ! Try Again");
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });


        queue.add(jsonArrayRequest);
    }

    void SDiagnosis(final String diagurl) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, diagurl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray array) {

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject currObject = null;
                            try {
                                currObject = array.getJSONObject(i).getJSONObject("Issue");
                                Issue_Names.add(currObject.getString("Name"));
                                Issue_ID.add(currObject.getString("ID"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        list.setAdapter(itemsAdapter);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        queue.add(jsonArrayRequest);


    }

    void SIssue(String issueurl,final Intent intent)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, issueurl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject object) {
                            try {
                                intent.putExtra("ProfName",object.getString("ProfName"));
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(jsonObjectRequest);

    }

}
