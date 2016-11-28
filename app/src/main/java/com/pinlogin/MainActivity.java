package com.pinlogin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = getClass().getSimpleName();

    private static final int MAX_PIN_LENGTH = 4;

    private String password = "";
    StringBuilder sb = new StringBuilder();
    SessionManager sessionManager;
    private List<ViewSwitcher> switchers = new ArrayList<>(MAX_PIN_LENGTH);
    TextView textMessage;

    int attempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.createLoginSession(sessionManager.getUserPassword());

        LinearLayout linearLayoutPin = (LinearLayout) findViewById(R.id.linear_layout_small_circles);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i = 0; i < MAX_PIN_LENGTH; i++){
            ViewSwitcher view = (ViewSwitcher) inflater.inflate(R.layout.include_dot, null);
            linearLayoutPin.addView(view);
            switchers.add(view);
        }

        textMessage = (TextView) findViewById(R.id.message);
    }

    public void numberClick(View view) {
        //Log.i(LOG_TAG, "VALORE VIEW: " + view.toString() + "ID" + view.getId());
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
            deleteAllCircles();
        }
        sb.delete(0, sb.length());
        password = sb.toString();
        Log.i("VALUES", "password: " + password);
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(this, ForgotActivity.class);
        startActivity(intent);
        finish();
    }

    public void okClick(View view) {
        if (password.length() < 4) {
            return;
        } else {
            if (attempts == 4) {
                textMessage.setText(R.string.you_forgot_password);
                textMessage.setContentDescription(getResources().getString(R.string.you_forgot_password));
            } else {
                String pass = sessionManager.getUserPassword();
                if (pass.toString().equalsIgnoreCase(password)) {
                    sessionManager.checkLogin();
                } else {
                    if (attempts == 4) {
                        textMessage.setText(R.string.no_more_attempts);
                        textMessage.setContentDescription(getResources().getString(R.string.no_more_attempts));
                        deleteAllCircles();
                    }
                    if (1 <= attempts || attempts <= 3) {
                        textMessage.setText(4 - attempts + " attempts left");
                        textMessage.setContentDescription(4 - attempts + " attempts left");
                        deleteAllCircles();
                    }
                    attempts++;
                    sb.delete(0, sb.length());
                    password = sb.toString();
                }
            }
        }
    }

    public void deleteAllCircles() {
        for (ViewSwitcher switcher : switchers){
            switcher.showNext();
        }
    }
}
