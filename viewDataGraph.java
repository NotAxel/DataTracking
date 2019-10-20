package com.axel.datatracking;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class viewDataGraph extends AppCompatActivity implements View.OnClickListener {

    long startingTime;
    long startingUp;
    long startingDown;
    SimplifiedAppInfo chosenOne;
    TextView downBytes;
    TextView upBytes;
    private Handler mHandler = new Handler();
    LineGraphSeries<DataPoint> upValues =  new LineGraphSeries<>();
    LineGraphSeries<DataPoint> downValues = new LineGraphSeries<>();
    int tracker = 0;
    GraphView graph;
    GridLabelRenderer label;
    File testFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_page);
        Intent intent = getIntent();
        chosenOne = intent.getParcelableExtra("CHOOSEN");
        TextView APPNAME = findViewById(R.id.selected_app_name);
        APPNAME.setText(chosenOne.getAppName());
        graph = findViewById(R.id.graph_view);
        // experimenting with graph settings lead to weird settings
        graph.getViewport().setScalable(true);
        //graph.getViewport().setScrollable(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(60);
        label = graph.getGridLabelRenderer();
        labelGraph();
        chosenOne.setUpbyts();
        chosenOne.setDownbytes();
        // set 0 values
        downBytes = findViewById(R.id.down_bytes);
        upBytes = findViewById(R.id.up_bytes);
        downBytes.setText(Long.toString(0));
        upBytes.setText(Long.toString(0));
        // Button set up
        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(this);
        Button pauseButton = findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(this);
    }

    // graph label
    public void labelGraph() {
        label.setHorizontalAxisTitle("Seconds");
        label.setVerticalAxisTitle("Bytes");
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.start_button) {
            Log.d("start", "button has been pressed" + chosenOne.getDownbytes());
            startLog(this, chosenOne);
            mHandler.postDelayed(mRun, 1000);
        } else if(v.getId() ==  R.id.pause_button) {
            Log.d("pause", "button has been pressed" + chosenOne.getDownbytes());
            long tempDown = chosenOne.getDownbytes();
            long tempUp = chosenOne.getUpbyts();
            upBytes.setText(Long.toString(tempUp));
            downBytes.setText(Long.toString(tempDown));
            endLog(this, chosenOne);
            mHandler.removeCallbacks(mRun);
        }
    }

    private final Runnable mRun = new Runnable() {
        @Override
        public void run() {
            chosenOne.setDownbytes();
            chosenOne.setUpbyts();
            upValues.appendData(new DataPoint(tracker, chosenOne.getUpbyts()), true, 100000);
            downValues.appendData(new DataPoint(tracker, chosenOne.getDownbytes()), true, 100000);
            tracker++;
            downBytes.setText(Long.toString(chosenOne.getDownbytes()));
            upBytes.setText(Long.toString(chosenOne.getUpbyts()));
            downValues.setColor(Color.rgb(231, 76, 60));
            upValues.setColor(Color.rgb(255, 195, 0));
            graph.addSeries(downValues);
            graph.addSeries(upValues);
            mHandler.postDelayed(mRun, 1000);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.graph_menufile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.refresh_button) {
            Log.d("Refresh button", "refreshed byte count");
            downBytes.setText("0");
            upBytes.setText("0");
        } else if(item.getItemId() == R.id.run_it_back) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static void endLog(viewDataGraph dataGraph, SimplifiedAppInfo selectedApp) {
        // write end results to file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dataGraph.testFile, true));
            writer.write("End,"+selectedApp.getUpbyts()+","+selectedApp.getDownbytes()+"\n");
            float effectiveDown = selectedApp.getDownbytes() - dataGraph.startingDown;
            float effectiveUp = selectedApp.getUpbyts() - dataGraph.startingUp;
            writer.write("Effective,"+effectiveUp+","+effectiveDown+"\n");
            float timePassed = ((float) ((System.currentTimeMillis() - dataGraph.startingTime)/1000));
            float avgUp = effectiveUp/timePassed;
            float avgDown = effectiveDown/timePassed;
            writer.write("Average bytes/sec,"+avgUp+","+avgDown+"\n");
            writer.close();
            MediaScannerConnection.scanFile(dataGraph, new String[]{dataGraph.testFile.toString()},
                    null, null);
        } catch (IOException e) {
            Log.d("broke", "the write dont work");
        }
    }

    public static void startLog(viewDataGraph dataGraph, SimplifiedAppInfo selectedApp) {
        int i = 0;
        String name = "dataFile.csv";
        Log.d("update", "sort of works maybe");
        // make the file if it already exists increment the name by 1
        try {
            dataGraph.testFile = new File(dataGraph.getExternalFilesDir(null), name);
            while(dataGraph.testFile.exists()) {
                i++;
                name = dataGraph.makeFileName(i);
                dataGraph.testFile = new File(dataGraph.getExternalFilesDir(null), name);
            }
            Log.d("filename", name);
            dataGraph.testFile.createNewFile();
        } catch (IOException e) {
            Log.d("broke", "Unable to write my dood");
        }
        // try to write to the file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dataGraph.testFile, true));
            dataGraph.startingDown = selectedApp.getDownbytes();
            dataGraph.startingUp = selectedApp.getUpbyts();
            dataGraph.startingTime = System.currentTimeMillis();
            writer.write("data,up,down\n");
            writer.write("Initial,"+selectedApp.getUpbyts()+","+selectedApp.getDownbytes()+"\n");
            writer.close();
            // refresh the data file
            MediaScannerConnection.scanFile(dataGraph, new String[]{dataGraph.testFile.toString()},
                                         null, null);
        } catch (IOException e) {
            Log.d("broke", "cant write to the file");
        }
    }

    public String makeFileName(int i) {
        return "dataFile" + i + ".csv";
    }
}
