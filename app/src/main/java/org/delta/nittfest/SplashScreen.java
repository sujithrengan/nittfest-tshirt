package org.delta.nittfest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;


public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //Utilities.typeface=Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Light.ttf");
        Utilities.typefaceR=Typeface.createFromAsset(this.getAssets(),"fonts/bangwhackpow.ttf");
        Utilities.init();
        Utilities.prefs = getSharedPreferences("check_" + "status", 0);
        Utilities.status = Utilities.prefs.getInt("status", 0);
        if (Utilities.status != 0) {
            Utilities.username = Utilities.prefs.getString("user_name", "User");
            Utilities.password = Utilities.prefs.getString("user_pass", "Password");
            Utilities.coupon = Utilities.prefs.getString("coupon", "meh.");
        }
        Intent i;

        Log.e("splash", String.valueOf(Utilities.status) + Utilities.coupon);
        switch (Utilities.status) {
            case 0:
                i = new Intent(this, LoginActivity.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(this, Coupon.class);
                startActivity(i);
                break;
            default:
                i = new Intent(this, WelcomePage.class);
                startActivity(i);
                break;
        }
        finish();
    }
}
