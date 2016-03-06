package org.delta.nittfest;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Coupon extends ActionBarActivity implements CouponFragment.OnClickListener, GenderSelectFragment.OnClickListener, SizeSelectFragment.OnClickListener, PreviewFragment.OnClickListener {
    FragmentManager fm;
    TextView instruction;
    String instruction_text[] = {"Select your coupon", "Tap to flip", "Select gender", "Select shirt size"};
    int CONFIRMATION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        instruction = (TextView) findViewById(R.id.instructionText);
        instruction.setTypeface(Utilities.typefaceR);
        instruction.setText(instruction_text[0]);
        CouponFragment fragment = new CouponFragment();
        fm = getFragmentManager();
        fm.beginTransaction().add(R.id.frameLayout, fragment, "CouponSelect").commit();
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
            editor.apply();
            Intent intent = new Intent(Coupon.this, SplashScreen.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void CouponToPreview() {
        PreviewFragment previewFragment = new PreviewFragment();
        fm.beginTransaction().replace(R.id.frameLayout, previewFragment, "Preview").addToBackStack(null).commit();
        instruction.setText(instruction_text[1]);
    }

    @Override
    public void PreviewToGender() {
        GenderSelectFragment genderSelectFragment = new GenderSelectFragment();
        fm.beginTransaction().replace(R.id.frameLayout, genderSelectFragment, "GenderSelect").addToBackStack(null).commit();
        instruction.setText(instruction_text[2]);

    }

    @Override
    public void GenderToShirtSize() {
        SizeSelectFragment sizeSelectFragment = new SizeSelectFragment();
        fm.beginTransaction().replace(R.id.frameLayout, sizeSelectFragment, "SizeSelect").addToBackStack(null).commit();
        instruction.setText(instruction_text[3]);
    }

    @Override
    public void OpenConfirmPage() {
        Intent intent = new Intent(getBaseContext(), ConfirmPage.class);
        startActivityForResult(intent, CONFIRMATION_REQUEST);
    }

    @Override
    public void onBackPressed() {
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate();
            instruction.setText(instruction_text[fm.getBackStackEntryCount()]);
        } else super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONFIRMATION_REQUEST) {
            if (resultCode == 1) {
                Intent intent = new Intent(Coupon.this, WelcomePage.class);
                startActivity(intent);
                finish();
            }
        }

    }
}