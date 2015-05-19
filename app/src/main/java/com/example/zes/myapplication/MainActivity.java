package com.example.zes.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends Activity {

    private ImageView imageView;
    private MyView myView;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myView=(MyView)findViewById(R.id.myView);
        imageView=(ImageView)findViewById(R.id.image);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myView.isInEndge()) {

                    bitmap=myView.getClipBitmap();
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
    }

}
