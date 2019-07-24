package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SubwayMain extends AppCompatActivity {


    Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subway_main);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle("Get Off");
        /* MyClick myclick = new MyClick(); */


        findViewById(R.id.bus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.bus_main);
            }
        });

    /*    findViewById(R.id.subway).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.subway_main);
            }
        });*/
    }

    private class DefaultInput {
    }
    /* class MyClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {

        }
    }*/

}