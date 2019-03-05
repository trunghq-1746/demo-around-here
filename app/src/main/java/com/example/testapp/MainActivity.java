package com.example.testapp;

import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements DownloadCallback<String> {
    private static final String TAG = "MainActivity";
    private NetworkFragment mNetworkFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(),
            "https://www.foody.vn/ha-noi/dia-diem?ds=Restaurant&vt=row&st=1&q=canh%20c%C3%A1%20r%C3%B4&page=1&provinceId=218&categoryId=&append=false");
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNetworkFragment.startDownload();
            }
        });
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        return null;
    }

    @Override
    public void onProgressUpdate(int processCode, int percentComplete) {
    }

    @Override
    public void finishDownloading() {
    }

    @Override
    public void updateFromDownload(String result) {
        TextView mTextView = findViewById(R.id.textView);
        mTextView.setText(result);
    }
}
