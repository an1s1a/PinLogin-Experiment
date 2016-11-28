package com.pinlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

public class ForgotActivity extends Activity {

    private static final int MAX_PIN_LENGTH = 4;

    private String password = "";
    StringBuilder sb = new StringBuilder();
    SessionManager sessionManager;

    private List<ViewSwitcher> switchers = new ArrayList<>(MAX_PIN_LENGTH);
    TextView textMessage;
    TextView textMessageError;
    String newPass = "";
    boolean repeatPass = false;
    boolean goBack = false;

    int attempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pin_activity);

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.createLoginSession(sessionManager.getUserPassword());

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout_small_circles);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        for(int i = 0; i < MAX_PIN_LENGTH; i++){
            ViewSwitcher view = (ViewSwitcher) inflater.inflate(R.layout.include_dot, null);
            linearLayout.addView(view);
            switchers.add(view);
        }

        textMessage = (TextView) findViewById(R.id.insert_new_PIN);
        textMessageError = (TextView) findViewById(R.id.error_less_four);
    }

    public void numberClick(View view) {
        if (sb.length() == MAX_PIN_LENGTH) {

            switchers.get(MAX_PIN_LENGTH - 1).showNext();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switchers.get(MAX_PIN_LENGTH - 1).showNext();
                }
            },150);
            return;
        }

        sb.append(view.getTag());

        password = sb.toString();
        Log.i("VALUES", "password: " + password);

        if(sb.length()>=1){
            switchers.get(sb.length()-1).showNext();
        }
    }

    public void cancClick(View view) {
        if (null == password || password.isEmpty()) {
            return;

        } else {
            sb.delete(sb.length() - 1, sb.length());
            //password.substring(password.length()-2, password.length()-1);
            password = sb.toString();
        }
        switchers.get(sb.length()).showPrevious();

        Log.i("VALUES", "password: " + password);
    }

    public void cancAllClick(View view) {
        if (sb.length() == 0) {
            return;
        } else if(sb.length() <= MAX_PIN_LENGTH-1){
            for (ViewSwitcher switcher : switchers){
                switcher.showPrevious();
            }
        }
        else if (sb.length() == 4) {
            disableAllCircles();
        }
        sb.delete(0, sb.length());
        password = sb.toString();
        Log.i("VALUES", "password: " + password);
    }

    public void disableAllCircles() {
        for (ViewSwitcher switcher : switchers){
            switcher.showNext();
        }
    }

    public void okClick(View view) {
        if (password.length() < 4) {
            return;
        } else if(!repeatPass){
            repeatPass = true;
            newPass = password;
            textMessage.setText(getResources().getString(R.string.reenter_PIN));
            textMessage.setContentDescription(getResources().getString(R.string.reenter_PIN));

        } else if (password.toString().equalsIgnoreCase(newPass)) {

            textMessage.setText(getResources().getString(R.string.new_PIN_saved));
            textMessage.setContentDescription(getResources().getString(R.string.new_PIN_saved));
            sessionManager.createLoginSession(newPass);
            goBack = true;
        } else {
            repeatPass = false;
            textMessage.setText(getResources().getString(R.string.error_PIN));
            textMessage.setContentDescription(getResources().getString(R.string.error_PIN));
        }
        sb.delete(0, sb.length());
        password = sb.toString();
        disableAllCircles();

        if (goBack) {
            final Intent intent = new Intent(this, MainActivity.class);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, 400);

        }
    }
}
