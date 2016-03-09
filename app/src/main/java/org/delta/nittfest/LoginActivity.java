package org.delta.nittfest;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;


public class LoginActivity extends Activity {
    String rollNumber;
    String password;
    EditText rollNumberText, passwordText;
    Button button;
    private int screen_height;
    private int screen_width;
    private Handler myHandler;
    ProgressDialog myPd_ring = null;


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

                    //sslcheck();


                    //loginvolley();
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



void loginvolley()
{
    myPd_ring = new ProgressDialog(LoginActivity.this);
    myPd_ring.setMessage("Loading...");
    myPd_ring.setCancelable(false);
    myPd_ring.setCanceledOnTouchOutside(false);
    myPd_ring.show();


    HurlStack hurlStack = new HurlStack() {
        @Override
        protected HttpURLConnection createConnection(URL url) throws IOException {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
            try {
                httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return httpsURLConnection;
        }
    };

    StringRequest postRequest = new StringRequest(Request.Method.POST, Utilities.url_auth,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        int status = jsonResponse.getInt("status");
                        Utilities.status = status;
                        String data = jsonResponse.getString("data");
                        Log.e("vv", String.valueOf(status));
                                        /*JSONObject jsonObject= new JSONObject(data);

                                        String id=jsonObject.getString("user_id");
                                        String name=jsonObject.getString("user_name");
                                        String email=jsonObject.getString("user_email");
                                        String fullname=jsonObject.getString("user_fullname");
*/
                        myPd_ring.dismiss();

                        switch (status) {
                            case 0:
                                Toast.makeText(LoginActivity.this, "There was a problem connecting to the server - Please check your username and password and try again.", Toast.LENGTH_LONG).show();
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
                                Utilities.coupon = "meh.";
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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    myPd_ring.dismiss();
                    error.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error connecting ", Toast.LENGTH_LONG).show();

                }
            }
    ) {
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            // the POST parameters:
            params.put("user_roll", rollNumber);
            params.put("user_pass", password);
            return params;
        }
    };
    int socketTimeout = 10000;//10 seconds
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    postRequest.setRetryPolicy(policy);
    Volley.newRequestQueue(LoginActivity.this,hurlStack).add(postRequest);
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

            //HttpClient httpclient = new DefaultHttpClient();

            DefaultHttpClient httpclient=new MyHttpClient(LoginActivity.this);

            HttpEntity httpEntity = null;
            HttpPost httppost = new HttpPost(Utilities.url_auth);
            JSONObject jsonObject;

            try {
                List nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("user_roll", rollNumber));
                nameValuePairs.add(new BasicNameValuePair("user_pass", password));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

                HttpResponse response = null;

                response = httpclient.execute(httppost);
                httpEntity = response.getEntity();
                String s = null;
                s = EntityUtils.toString(httpEntity);

                Log.e("ll", s);

                    jsonObject = new JSONObject(s);
                    Log.e("response", s);
                    Utilities.status = jsonObject.getInt("status");
                    error = jsonObject.getString("error");
                } catch (Exception e) {
                    e.printStackTrace();
                Log.e("ll",String.valueOf(e));


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



    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //return true;
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();

               return true;
                //return hv.verify("localhost", session);
            }
        };
    }

    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        originalTrustManager.checkClientTrusted(chain, authType);
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                        originalTrustManager.checkServerTrusted(chain, authType);
                    }

                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                }
        };
    }

    private SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = getResources().openRawResource(R.raw.ca); // this cert file stored in \app\src\main\res\raw folder path

        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);


        return sslContext.getSocketFactory();
    }




}
