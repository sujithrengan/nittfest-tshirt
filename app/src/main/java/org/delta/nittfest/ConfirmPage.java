package org.delta.nittfest;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ConfirmPage extends ActionBarActivity {

    int NetworkState = 0;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_page);
        setTextOnScreen();
        handleButtonClick();
    }

    private void setTextOnScreen() {
        TextView rollNumber = (TextView) findViewById(R.id.rollNumber);
        rollNumber.setText(Utilities.username);
        rollNumber.setTypeface(Utilities.typefaceR);
        TextView coupon = (TextView) findViewById(R.id.coupon);
        coupon.setText("\u20B9 " + Integer.toString(Utilities.amount));
        coupon.setTypeface(Utilities.typefaceR);
        if (Utilities.amount == 700) {
            LinearLayout genderLayout = (LinearLayout) findViewById(R.id.genderLayout);
            genderLayout.setVisibility(View.VISIBLE);
            LinearLayout sizeLayout = (LinearLayout) findViewById(R.id.sizeLayout);
            sizeLayout.setVisibility(View.VISIBLE);
            TextView gender = (TextView) findViewById(R.id.gender);
            gender.setText(Utilities.gender.toUpperCase());
            gender.setTypeface(Utilities.typefaceR);
            TextView shirtSize = (TextView) findViewById(R.id.shirtSize);
            shirtSize.setText(Utilities.shirtSize);
            shirtSize.setTypeface(Utilities.typefaceR);

        }
    }

    private void handleButtonClick() {
        button = (Button) findViewById(R.id.confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new check_net_class().execute();
                new myAsyncTask().execute();
                button.setClickable(false);
            }
        });
    }

    class myAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog myPd_ring = null;

        @Override
        protected void onPreExecute() {

            myPd_ring = new ProgressDialog(ConfirmPage.this);
            myPd_ring.setMessage("Loading...");
            myPd_ring.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpPost httppost = new HttpPost(Utilities.url_reg);
            JSONObject jsonObject;
            String coupon = null;

            String hash = Sha1.getHash(Utilities.username + "LifeLaEppdiPogudhu");
            try {
                List nameValuePairs = new ArrayList();
                nameValuePairs.add(new BasicNameValuePair("user_roll", Utilities.username));
                nameValuePairs.add(new BasicNameValuePair("user_pass", Utilities.password));
                nameValuePairs.add(new BasicNameValuePair("user_amount", Integer.toString(Utilities.amount)));

                nameValuePairs.add(new BasicNameValuePair("user_tshirt_size", Utilities.shirtSize));
                nameValuePairs.add(new BasicNameValuePair("user_gender", Utilities.gender));
                nameValuePairs.add(new BasicNameValuePair("user_delta_secret", hash));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                HttpResponse response = httpclient.execute(httppost);

                httpEntity = response.getEntity();
                String s = EntityUtils.toString(httpEntity);
                try {
                    jsonObject = new JSONObject(s);
                    Log.e("response", s);
                    Utilities.status = jsonObject.getInt("status");
                    coupon = jsonObject.getString("data");
                    Utilities.coupon = coupon;
                    NetworkState = 1;

                } catch (JSONException e) {
                    e.printStackTrace();
                    NetworkState = 0;
                }


            } catch (ClientProtocolException e) {
                NetworkState = 0;
            } catch (IOException e) {
                NetworkState = 0;
            }

            return coupon;
        }

        @Override
        protected void onPostExecute(String error) {
            super.onPostExecute(error);


            if (NetworkState == 1) {
                myPd_ring.dismiss();


                switch (Utilities.status) {
                    // case 0:
                    //   Toast.makeText(ConfirmPage.this, error, Toast.LENGTH_SHORT).show();
                    //   break;
                    case 2:
                    case 3:
                        SharedPreferences prefs = Utilities.prefs;
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("status", Utilities.status);
                        editor.putString("coupon", Utilities.coupon);
                        editor.apply();
                        setResult(1);
                        finish();
                        break;
                }
            } else {
                myPd_ring.dismiss();
                Toast.makeText(ConfirmPage.this, "No Internet Access", Toast.LENGTH_LONG).show();
            }
        }
    }

    class check_net_class extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpPost httppost = new HttpPost("https://www.google.com");
            String error = null;
            try {
                List nameValuePairs = new ArrayList();
                nameValuePairs.add(new BasicNameValuePair("user_name", "hi"));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                HttpResponse response = httpclient.execute(httppost);
                if (response == null) {
                    NetworkState = 0;
                } else NetworkState = 1;
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return error;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (NetworkState == 0) {
                Toast.makeText(ConfirmPage.this, "No internet access", Toast.LENGTH_LONG).show();
                button.setClickable(true);
            } else {
                new myAsyncTask().execute();
            }

        }
    }
}
