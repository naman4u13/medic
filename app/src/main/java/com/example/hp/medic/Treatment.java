package com.example.hp.medic;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.TextView;

import com.example.hp.medic.TreatFolder.TreatObj;
import com.example.hp.medic.TreatFolder.SubOption;
import com.example.hp.medic.TreatFolder.TreatOption;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Treatment extends AppCompatActivity {
    TextView treatment;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("MEDIC");
        Intent intent = getIntent();
        Issue issue = new Issue();
        issue.Name = intent.getStringExtra("Name");
        issue.ID = intent.getStringExtra("ID");
        issue.ProfName = intent.getStringExtra("ProfName");
        Log.e("TreatonCreate: ", issue.Name + " " + issue.ID + " " + issue.ProfName);
        treatment = findViewById(R.id.treatment);
        treatment.setMovementMethod(new ScrollingMovementMethod());
        addlistener(issue);

    }

    private class treattask extends AsyncTask<Issue, Void, TreatObj> {

        @Override
        protected TreatObj doInBackground(Issue... params) {
            TreatObj treatObj = new TreatObj();
            treatObj.ID = params[0].ID;
            treatObj.Name = params[0].Name;


            try {
                Document doc = Jsoup.connect("https://www.google.com/search?q=treatment for " + params[0].Name).get();
                Elements treattab = doc.select("div.kno-himx");
                if (treattab.isEmpty()) {
                    doc = Jsoup.connect("https://www.google.com/search?q=treatment for " + params[0].ProfName).get();
                    treattab = doc.select("div.kno-himx");
                    if (treattab.isEmpty()) {
                        doc = Jsoup.connect("https://legacy.priaid.ch/en-gb/glossar-details?t=issue&id=" + params[0].ID).get();
                        treattab = doc.getElementsByTag("p");

                        for (Element element : treattab) {

                            if (element.previousElementSibling().text().equals("Consequences + Treatment")) {
                                treatObj.Info = element.text();
                                break;
                            }
                        }
                        return treatObj;


                    }
                }
                treatObj.option = new ArrayList<TreatOption>();
                Document ans = Jsoup.parse(treattab.outerHtml());
                treatObj.Info = ans.select("div.K9xsvf.Uva9vc.kno-fb-ctx").text();
                Elements treatoptions = ans.select("div.hXYDxb");
                Elements subtype = ans.select("a.HZnEfd");
                Elements subdetails = ans.select("div.Rs3Epd");
                Elements counter = ans.select("div.Y6f3fc.HtP7nb");

                for (Element element : treatoptions) {
                    TreatOption treatOption = new TreatOption();
                    treatOption.OptionTitle = element.text();
                    treatOption.suboption = new ArrayList<SubOption>();
                    if (counter.size() > 0) {
                        int commas = 0;
                        String x = counter.first().text();
                        counter.remove(0);
                        for (int i = 0; i < x.length(); i++) {
                            if (x.charAt(i) == ',') commas++;

                        }
                        if (commas == 0) {
                            if (x.contains("and")) {
                                commas++;
                            }
                        }

                        for (int i = 0; i <= commas; i++) {
                            if (subtype.size() > 0 && subdetails.size() > 0) {
                                SubOption subOption = new SubOption(subtype.first().text(), subdetails.first().text());
                                treatOption.suboption.add(subOption);
                                subtype.remove(0);
                                subdetails.remove(0);
                            } else {
                                break;
                            }
                        }
                    }
                    treatObj.option.add(treatOption);
                }
                Log.e("doInBackground: ", treatObj.toString());
                return treatObj;


            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(TreatObj result) {
            Log.e("onPostExecute", "shit");
            myRef.child(result.ID).setValue(result);

        }
    }

    private void addlistener(final Issue issue) {

        Log.e("addlistener", "inside null");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("onDataChange ", "Start");
                if (dataSnapshot.hasChild(issue.ID)) {
                    Log.e("onDataChange ", "inside if");
                    TreatObj treatObj = new TreatObj();
                    treatObj = dataSnapshot.child(issue.ID).getValue(TreatObj.class);
                    Log.e("onChild", "inside");
                    setView(treatObj);
                } else {
                    Log.e("onDataChange ", "inside else");
                    new treattask().execute(issue);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value


            }
        });


    }

    void setView(TreatObj treatObj) {

        final SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("\n");
        SpannableString Name = (new SpannableString(treatObj.Name));
        Name.setSpan(new RelativeSizeSpan(2.5f), 0, Name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Name.setSpan(new ForegroundColorSpan(Color.BLACK),0, Name.length(), 0);
        builder.append(Name);
        builder.append("\n\n");
        SpannableString Info = (new SpannableString(treatObj.Info));
        Info.setSpan(new RelativeSizeSpan(1.25f), 0, Info.length(), 0);
        builder.append(Info);
        builder.append("\n");

        if (!(treatObj.option == null || treatObj.option.isEmpty())) {
            for (TreatOption treatOption : treatObj.option) {
                SpannableString optionName = (new SpannableString(treatOption.OptionTitle));
                optionName.setSpan(new RelativeSizeSpan(1.5f), 0, optionName.length(), 0);
                optionName.setSpan(new ForegroundColorSpan(Color.BLACK),0, optionName.length(), 0);
                builder.append("\n\n");
                builder.append(optionName);
                builder.append("\n\n");
                for (SubOption subOption : treatOption.suboption) {
                    SpannableString subType = (new SpannableString(subOption.subtype));
                    subType.setSpan(new RelativeSizeSpan(1.25f), 0, subType.length(), 0);
                    builder.append(subType);
                    builder.append("\n");
                    SpannableString subDetail = (new SpannableString(subOption.subdetail));
                    subDetail.setSpan(new RelativeSizeSpan(1f), 0, subDetail.length(), 0);
                    builder.append(subDetail);
                    builder.append("\n\n");
                }
            }
        }
        treatment.setText(builder);

    }

}
