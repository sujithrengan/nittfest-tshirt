package org.delta.nittfest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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

public class LoginActivity extends Activity {
    String rollNumber;
    String password;
    EditText rollNumberText, passwordText;
    Button button;
    private int screen_height;
    private int screen_width;
    private Handler myHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_screen);

        myHandler = new Handler();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screen_height =Utilities.screen_height= displaymetrics.heightPixels;
        screen_width =Utilities.screen_width= displaymetrics.widthPixels;

        handleButtonClick();
        //animatebg();
    }

    private void animatebg()
    {
        final ImageView l2=(ImageView)findViewById(R.id.bg);

        //Animation anim = AnimationUtils.loadAnimation(this, R.anim.translater);
        //Animation animr = AnimationUtils.loadAnimation(this, R.anim.translate);
        //anim.reset();

        final Animation animation = new TranslateAnimation(screen_width,0,0, 0);
        animation.setDuration(300);
        animation.setRepeatMode(Animation.INFINITE);

        animation.setInterpolator(new LinearInterpolator());
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
               l2.startAnimation(animation);
            }
        }, 500);
    }

    private void handleButtonClick() {
        rollNumberText = (EditText) findViewById(R.id.rollNumber);
        passwordText = (EditText) findViewById(R.id.password);
        rollNumberText.setTypeface(Utilities.typefaceR);
        passwordText.setTypeface(Utilities.typefaceR);
        button = (Button) findViewById(R.id.signInButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollNumber = rollNumberText.getText().toString();
                if (rollNumber.length() != 9)
                    rollNumberText.setError("Invalid roll number");
                else {
                    password = passwordText.getText().toString();
                    //Pass rollNumber and password to the server



                    new myAsyncTask().execute();
                    //Testing

                //    Intent i = new Intent(LoginActivity.this, Coupon.class);
                //    LoginActivity.this.startActivity(i);

                    button.setClickable(false);
                }
            }
        });
        CheckBox checkBox = (CheckBox) findViewById(R.id.showPasswordCheckBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                if (!isChecked) {
                    passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    class myAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog myPd_ring = null;
        @Override
        protected void onPreExecute() {

            myPd_ring  = new ProgressDialog (LoginActivity.this);
            myPd_ring.setMessage("Loading...");
            myPd_ring.setCancelable(false);
            myPd_ring.setCanceledOnTouchOutside(false);
            myPd_ring.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            String error = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpPost httppost = new HttpPost(Utilities.url_auth);
            JSONObject jsonObject;


            try {
                List nameValuePairs = new ArrayList();
                nameValuePairs.add(new BasicNameValuePair("user_roll", rollNumber));
                nameValuePairs.add(new BasicNameValuePair("user_pass", password));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                HttpResponse response = httpclient.execute(httppost);
                httpEntity = response.getEntity();
                String s = EntityUtils.toString(httpEntity);
                try {
                    jsonObject = new JSONObject(s);
                    Log.e("response",s);
                    Utilities.status = jsonObject.getInt("status");
                    error = jsonObject.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return error;
        }

        @Override
        protected void onPostExecute(String error) {
            super.onPostExecute(error);
            System.out.println("Error: " + error);
            myPd_ring.dismiss();


            switch (Utilities.status) {
                case 0:
                    Toast.makeText(LoginActivity.this, "There was a problem connecting to the server. Please check your username and password and try again.", Toast.LENGTH_LONG).show();
                    rollNumberText.setText("");
                    passwordText.setText("");
                    button.setClickable(true);
                    break;
                case 1:
                    Intent intent = new Intent(getBaseContext(), Coupon.class);
                    SharedPreferences.Editor editor = Utilities.prefs.edit();
                    editor.putInt("status", Utilities.status);
                    editor.putString("user_name", rollNumber);
                    Utilities.username = rollNumber;
                    editor.putString("user_pass", password);
                    Utilities.password = password;
                    editor.apply();
                    startActivity(intent);
                    finish();
                    break;
                case 2:
                    Intent i = new Intent(LoginActivity.this, WelcomePage.class);
                    SharedPreferences.Editor editor2 = Utilities.prefs.edit();
                    editor2.putInt("status", Utilities.status);
                    editor2.putString("user_name", rollNumber);
                    Utilities.username = rollNumber;
                    editor2.putString("user_pass", password);
                    Utilities.password = password;
                    editor2.putString("coupon", "meh.");
                    Utilities.coupon="meh.";
                    editor2.apply();
                    startActivity(i);
                    finish();
                    break;
                case 3:
                    Toast.makeText(LoginActivity.this, "Your account is not on the system. Please contact NITTFEST OC", Toast.LENGTH_SHORT).show();
                    rollNumberText.setText("");
                    passwordText.setText("");
                    button.setClickable(true);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);



    }

}
