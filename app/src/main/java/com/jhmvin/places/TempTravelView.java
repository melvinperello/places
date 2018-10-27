package com.jhmvin.places;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jhmvin.places.domain.TravelStreamListData;
import com.jhmvin.places.domain.TravelStreamListDataAdapter;
import com.jhmvin.places.feature.tempTravel.TempTravelDirectory;
import com.jhmvin.places.feature.tempTravel.TempTravelHeaderBean;
import com.jhmvin.places.feature.tempTravel.TempTravelReader;
import com.jhmvin.places.persistence.text.TextReader;
import com.jhmvin.places.util.ToastAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TempTravelView extends AppCompatActivity implements TravelStreamListDataAdapter.ItemClicked {


    @BindView(R.id.rvFiles)
    RecyclerView rvFiles;


    private final ArrayList<TravelStreamListData> dataList = new ArrayList<>();
    RecyclerView.Adapter adapter;


    @OnClick(R.id.btnClear)
    public void onClickBtnClear() {
        this.files = TempTravelDirectory.getWorkingDirectory(getApplicationContext()).listFiles();
        if (files.length == 0) {
            new AlertDialog.Builder(this)
                    .setMessage("There are no items to clear.")
                    .setPositiveButton("Ok", null)
                    .create()
                    .show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Are you sure you want to permanently delete unsaved places?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearCache();
                    }
                })
                .setNegativeButton("No", null)
                .create().show();
    }

    private void clearCache() {
        this.files = TempTravelDirectory.getWorkingDirectory(getApplicationContext()).listFiles();
        for (File f : files) {
            f.delete();
        }
        this.dataList.clear();
        this.adapter.notifyDataSetChanged();
        ToastAdapter.show(getApplicationContext(), "Cache Cleared", ToastAdapter.SUCCESS);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private File[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_travel_view);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Location Record");


        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMMMMMMMMMMMMM dd, yyyy hh:mm:ss a");

        rvFiles.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvFiles.setLayoutManager(manager);

        this.adapter = new TravelStreamListDataAdapter(this, dataList);
        this.rvFiles.setAdapter(adapter);
        this.files = TempTravelDirectory.getWorkingDirectory(getApplicationContext()).listFiles();
        for (File f : files) {

            if (f.getName().contains("_marker")) {
                continue;
            }
            TravelStreamListData data = new TravelStreamListData();
            TextReader reader = null;
            try {
                reader = new TempTravelReader(getApplicationContext(), f.getName());
                reader.open();
                String firstLine = reader.read();
                TempTravelHeaderBean header = new TempTravelHeaderBean();
                header.fromTempCSV(firstLine);
                data.setOrigin(header.getStartPlace());
                data.setDestination(header.getEndPlace());
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(header.getStartTime());
                data.setStartTime(dateFormat.format(c.getTime()));

            } catch (IOException ex) {
                continue;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //ignore
                    }
                }
            }
            this.dataList.add(data);
            this.adapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onItemClicked(int index) {
        try {
            if (this.files != null) {
                File selectedFile = this.files[index];
                Intent intent = new Intent(this, TempTravelInfo.class);
                intent.putExtra(TempTravelInfo.FILE_NAME_INFO, selectedFile.getName());
                startActivity(intent);

//                String content = readFileToString(selectedFile);
//                Intent intent = new Intent(this, PlacesViewStream.class);
//                intent.putExtra("file_content", content);
//                startActivity(intent);
            }
        } catch (Exception ex) {
            ToastAdapter.show(getApplicationContext(), "Cannot Display", ToastAdapter.ERROR);
        }
    }


    @Deprecated
    private String readFileToString(File file) {
        String content = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                content += (line + "\n");
                line = reader.readLine();
            }
            return content;
        } catch (Exception ex) {
            return "read error !";
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioex) {
                    // ignore
                }
                reader = null;
            }
        }
    }


}
