package com.subramanyavmpolu.miniproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void temperature(View view) {
        Intent intent = new Intent(this,Temperature.class);
        startActivity(intent);
    }

    public void humidity(View view) {
        Intent intent = new Intent(this,Humidity.class);
        startActivity(intent);
    }

    public void temp_vs_hum(View view) {
        Intent intent = new Intent(this,TempvsHum.class);
        startActivity(intent);
    }
}
