package com.example.hp.medic;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Treatment extends AppCompatActivity {
    TextView treatment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);
        Intent intent = getIntent();
        Issue issue = new Issue();
        issue.Name = intent.getStringExtra("Name");
        issue.ID = intent.getStringExtra("ID");
        issue.ProfName = intent.getStringExtra("ProfName");
        Log.e("TreatonCreate: ", issue.Name+" "+issue.ID+" "+issue.ProfName );
        treatment = findViewById(R.id.treatment);
        treatment.setMovementMethod(new ScrollingMovementMethod());
        new treattask().execute(issue.Name);
    }
    private class treattask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            final StringBuilder builder = new StringBuilder();
            builder.append("\n");

            try {
                Document doc = Jsoup.connect("https://www.google.com/search?q=treatment for "+params[0]).get();

                Elements treattab = doc.select("div.kno-himx");
                Document ans = Jsoup.parse(treattab.outerHtml());
                Elements treatoptions = ans.select("div.hXYDxb");
                Elements subtype = ans.select("a.HZnEfd");
                Elements subdetails = ans.select("div.Rs3Epd");
                Elements counter = ans.select("div.Y6f3fc.HtP7nb");

                for (Element element : treatoptions) {
                    builder.append(element.text() + "\n");
                    if (counter.size() > 0) {
                        int commas = 0;
                        String x = counter.first().text();
                        counter.remove(0);
                        for (int i = 0; i < x.length(); i++) {
                            if (x.charAt(i) == ',') commas++;

                        }
                        if(commas==0)
                        {
                            if(x.contains("and"))
                            {
                                commas++;
                            }
                        }

                        for (int i = 0; i <= commas; i++) {
                            if (subtype.size() > 0 && subdetails.size() > 0) {
                                builder.append(subtype.first().text() + "\n");
                                builder.append(subdetails.first().text() + "\n");
                                subtype.remove(0);
                                subdetails.remove(0);
                            } else {
                                break;
                            }
                        }
                    }
                }
                Log.e("doInBackground: ", builder.toString());
                return builder.toString();


            } catch (IOException e) {
                builder.append("Error : ").append(e.getMessage()).append("\n");
                return "naman";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            treatment.setText(result);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
