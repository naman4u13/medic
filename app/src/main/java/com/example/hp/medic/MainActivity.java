package com.example.hp.medic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
    ArrayList<String> Dlist;
    ListView list;
    AccessToken key;
    ArrayAdapter<String> itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search = findViewById(R.id.search);
        symptom = findViewById(R.id.symptom);
        list = findViewById(R.id.list);
        Dlist = new ArrayList<String>();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Dlist);
        try {
            key = new Tokenkey().LoadToken("naman4u13@gmail.com","d4K9JrGn8i6X7ZsHc","https://sandbox-authservice.priaid.ch/login ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dlist.clear();
                String keyword = String.valueOf(symptom.getText());
                SSymptom(keyword);
                list.setAdapter(itemsAdapter);
            }
        });

    }

    void SSymptom(final String keyword) {


        String symurl = "https://sandbox-healthservice.priaid.ch/symptoms?token="+key+"&format=json&language=en-gb";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, symurl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray array) {
                        String diagurl="";
                        String name="";
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
                                    diagurl = "https://sandbox-healthservice.priaid.ch/diagnosis?symptoms=[" + currObject.getString("ID") + "]&gender=male&year_of_birth=1997&token="+key+"&format=json&language=en-gb";
                                    SDiagnosis(diagurl);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                        if(diagurl.isEmpty())
                        {
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
                                Dlist.add(currObject.getString("Name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
}
