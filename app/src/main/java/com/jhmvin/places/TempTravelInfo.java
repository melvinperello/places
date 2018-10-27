package com.jhmvin.places;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.jhmvin.places.feature.tempTravel.TempTravelDirectory;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TempTravelInfo extends AppCompatActivity {

    public final static String FILE_NAME_INFO = "zzz_file_name_info";


    private File mMainFile;
    private File mMarkFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_travel_info);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Travel Information");

        Bundle extras = getIntent().getExtras();
        String mainFileName = extras.getString(FILE_NAME_INFO);
        String markFileName = mainFileName.replace(".temp", "_marker.temp");

        mMainFile = TempTravelDirectory.getFile(getApplicationContext(), mainFileName);
        mMarkFile = TempTravelDirectory.getFile(getApplicationContext(), markFileName);
    }


    @OnClick(R.id.btnDelete)
    public void onClickBtnDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Delete location data ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFiles();
                    }
                })
                .setNegativeButton("No", null)
                .create().show();
    }


    private void deleteFiles() {
        if (mMainFile != null) {
            mMainFile.delete();
        }
        if (mMarkFile != null) {
            mMarkFile.delete();
        }
        finish();
    }


}
