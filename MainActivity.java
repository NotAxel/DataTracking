package com.axel.datatracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MyRecyclerViewAdapter adapter;
    ArrayList<AppInfo> apps = new ArrayList<>();
    ArrayList<SimplifiedAppInfo>  selectedApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set up RecyclerView
        fillApps();
        RecyclerView recyclerView = findViewById(R.id.rvApps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, apps);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.item_seperator)));
        adapter.loadItems(apps);
        getSelectedApps();
        Log.d("selected", "apps selected = " + selectedApps.size());
    }

    private void fillApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<PackageInfo> appsList = getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for(int i=0;i<appsList.size();i++){
            if(!isSystemPackage(appsList.get(i))) {
                Log.d("app", "app: " + appsList.get(i).toString());
                AppInfo tempapp = new AppInfo(getPackageManager().getApplicationLabel(appsList.get(i).applicationInfo).toString(),
                        getPackageManager().getApplicationIcon(appsList.get(i).applicationInfo),
                        appsList.get(i).packageName);
                apps.add(tempapp);
            }
        }
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    private void getSelectedApps() {
        for(int i=0;i<apps.size();i++) {
            if(apps.get(i).getFlag()) {
                int uid = 0;
                try {
                    uid = this.getPackageManager().getApplicationInfo(apps.get(i).getAppFullName(), 0).uid;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                SimplifiedAppInfo tempapp = new SimplifiedAppInfo(apps.get(i).getAppName(),
                        apps.get(i).getAppFullName(), uid);
                selectedApps.add(tempapp);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menufile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.link_page_2) {
            getSelectedApps();
            Log.d("selected", "apps selected = " + selectedApps.size());
            Intent intent = new Intent(this, viewSelectedApps.class);
            intent.putParcelableArrayListExtra("KEY", selectedApps);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
