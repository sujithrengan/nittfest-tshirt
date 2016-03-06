package org.delta.nittfest;

import android.content.SharedPreferences;
import android.graphics.Typeface;

public class Utilities {
    public static String username;
    public static String password;
    public static String coupon;
    public static int amount;
    public static String gender;
    public static String shirtSize;
    public static int status;
    public static SharedPreferences prefs;
    public static String url_auth = "https://nittfest.in/api/tshirt/userauth";
    public static String url_reg = "https://nittfest.in/api/tshirt/register";
    public static String url_qr = "https://nittfest.in/api/tshirt/qrcode";
    public static String url_coup = "https://nittfest.in/api/tshirt/getcoupon";
    public static int screen_height;
    public static int screen_width;
    //public static Typeface typeface;
    public static Typeface typefaceR;

    public static void init() {
        username = password = shirtSize = gender = null;
        amount = 0;
        status = 0;
    }
}
