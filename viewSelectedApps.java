package com.axel.datatracking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class viewSelectedApps extends AppCompatActivity {

    private Handler mHandler = new Handler();
    ArrayList<SimplifiedAppInfo> selectedApps = new ArrayList<>();
    secondViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_data_info);
        Intent intent = getIntent();
        selectedApps = intent.getParcelableArrayListExtra("KEY");
        RecyclerView rView = findViewById(R.id.rv_apps_data);
        rView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new secondViewAdapter(this, selectedApps);
        rView.setAdapter(adapter);
        rView.addItemDecoration(new DividerItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.item_seperator)));
        adapter.loadItems(selectedApps);
        mHandler.postDelayed(mRunnable, 1000);
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            for(int i=0;i<selectedApps.size();i++) {
                selectedApps.get(i).setDownbytes();
                selectedApps.get(i).setUpbyts();
            }
            adapter.loadItems(selectedApps);
            mHandler.postDelayed(mRunnable, 1000);
        }
    };
}
