package com.example.hp.medic;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

public class GetToken extends AsyncTask<Void,Void,AccessToken> {
    AccessToken Key;
    Button search;
    GetToken(AccessToken key, Button search)
    {
        this.Key = key;
        this.search = search;
    }


    @Override
    protected AccessToken doInBackground(Void... voids) {
        AccessToken accessToken = null;
        try {
            accessToken=  new Tokenkey().LoadToken("naman4u13@gmail.com","d4K9JrGn8i6X7ZsHc","https://sandbox-authservice.priaid.ch/login");
        } catch (Exception e) {
            e.printStackTrace();
        }

       return accessToken;
    }

    @Override
    protected void onPostExecute(AccessToken accessToken) {
        super.onPostExecute(accessToken);
        Log.e("Inside Async", accessToken.Token);
        Key.Token = accessToken.Token;
        Key.ValidThrough = accessToken.ValidThrough;

        search.setEnabled(true);

    }
}
