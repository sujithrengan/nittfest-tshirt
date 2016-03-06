package org.delta.nittfest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class WelcomePage extends ActionBarActivity {
    LinearLayout layoutOfPopup;
    PopupWindow popupMessage;

    Button couponButton;
    TextView popupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        TextView welcomeText = (TextView) findViewById(R.id.welcomeText);
        welcomeText.setText(welcomeText.getText().toString() + Utilities.username);
        couponButton = (Button) findViewById(R.id.couponSelect);
        couponButton.setText(Utilities.coupon);
        couponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                Log.e("bb", "bb");
                if (!Utilities.coupon.equals("meh.")) {
                    ClipData clip = ClipData.newPlainText("couponCode", Utilities.coupon);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();

                }
            }
        });

        TextView hlptxt = (TextView) findViewById(R.id.helpText);

        hlptxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//DIALOG INSTRUCTIONS MUST BE CHANGED
                new AlertDialog.Builder(WelcomePage.this)
                        .setTitle("Coupon Instructions")
                        .setMessage("Dummy instruction")
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();                                                    

            }
        });
        if (Utilities.status == 2) {
            //couponButton.setVisibility(View.INVISIBLE);
            ImageView qrCodeImage = (ImageView) findViewById(R.id.qr_code_image);
            qrCodeImage.setVisibility(View.VISIBLE);
            Bitmap bitmap = new SaveImage(Utilities.username, null).loadFromCacheFile();
            if (bitmap == null) new myAsyncTask().execute();
            else {
                qrCodeImage.setImageBitmap(bitmap);
            }

            if (Utilities.coupon.equals("meh."))
                new coupTask().execute();
        }
    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_welcome_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            //Logout
            SharedPreferences.Editor editor = Utilities.prefs.edit();
            editor.putInt("status", 0);
            editor.putString("user_name", null);
            editor.putString("user_pass", null);
            editor.putString("coupon", null);
            editor.apply();
            Intent intent = new Intent(WelcomePage.this, SplashScreen.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (Utilities.status != 2)
            super.onBackPressed();
        else finish();
    }

    class myAsyncTask extends AsyncTask<String, Void, Bitmap> {
        ProgressDialog myPd_ring = null;

        @Override
        protected void onPreExecute() {

            myPd_ring = new ProgressDialog(WelcomePage.this);
            myPd_ring.setMessage("Loading...");
            myPd_ring.show();

        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            Bitmap image = null;
            HttpPost httppost = new HttpPost(Utilities.url_qr);
            try {
                List nameValuePairs = new ArrayList();
                nameValuePairs.add(new BasicNameValuePair("user_roll", Utilities.username));
                nameValuePairs.add(new BasicNameValuePair("user_pass", Utilities.password));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                URL url2 = new URL(Utilities.url_qr);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = null;

                httpEntity = response.getEntity();
                byte[] img = EntityUtils.toByteArray(httpEntity);
                image = BitmapFactory.decodeByteArray(img, 0, img.length);


            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return image;


        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);
            myPd_ring.dismiss();
            ImageView show_image = (ImageView) findViewById(R.id.qr_code_image);
            show_image.setImageBitmap(bmp);
            SaveImage save = new SaveImage(Utilities.username, bmp);
            save.saveToCacheFile(bmp);
            addImageToGallery(save.getCacheFilename(), WelcomePage.this);

            Toast.makeText(WelcomePage.this, "Image saved to gallery", Toast.LENGTH_LONG).show();
        }
    }


    class coupTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {


        }

        @Override
        protected String doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            String coupon = "no-coupon";
            JSONObject jsonObject;
            HttpPost httppost = new HttpPost(Utilities.url_coup);
            try {
                List nameValuePairs = new ArrayList();
                nameValuePairs.add(new BasicNameValuePair("user_roll", Utilities.username));
                nameValuePairs.add(new BasicNameValuePair("user_pass", Utilities.password));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                URL url2 = new URL(Utilities.url_coup);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = null;

                httpEntity = response.getEntity();


                String s = EntityUtils.toString(httpEntity);
                try {
                    jsonObject = new JSONObject(s);
                    Log.e("response", s);
                    Utilities.status = jsonObject.getInt("status");
                    coupon = jsonObject.getString("data");
                    Utilities.coupon = coupon;


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return coupon;


        }

        @Override
        protected void onPostExecute(String coup) {
            super.onPostExecute(coup);

            SharedPreferences prefs = Utilities.prefs;
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("coupon", Utilities.coupon);
            editor.apply();

            couponButton.setText(Utilities.coupon);

            Toast.makeText(WelcomePage.this, "Coupon received", Toast.LENGTH_SHORT).show();
        }
    }


}
