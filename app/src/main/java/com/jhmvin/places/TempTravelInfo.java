package com.jhmvin.places;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jhmvin.places.feature.tempTravel.TempTravelDirectory;
import com.jhmvin.places.feature.tempTravel.TempTravelFooterBean;
import com.jhmvin.places.feature.tempTravel.TempTravelHeaderBean;
import com.jhmvin.places.feature.tempTravel.TempTravelLocationBean;
import com.jhmvin.places.feature.tempTravel.TempTravelReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;

public class TempTravelInfo extends AppCompatActivity {
    public final static String TAG = TempTravelInfo.class.getCanonicalName();

    public final static String FILE_NAME_INFO = "zzz_file_name_info";

    // bundled files
    private File mMainFile;
    private File mMarkFile;
    // translated data.
    private TempTravelHeaderBean mLocationHeader;
    private List<TempTravelLocationBean> mLocationList;
    private TempTravelFooterBean mLocationFooter;
    //
    private long mWaitingTime;

    private int mIdleLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_travel_info);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(R.string.summary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // call
        this.getFiles();
        // read data
        this.readFiles();
        //
        sortTravelTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_temp_travel_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_item_delete:
                onClickBtnDelete();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void sortTravelTime() {
        mWaitingTime = 0;
        mIdleLocations = 0;
        mLocationList = new ArrayList<>(mLocationList);
        // Get the first location
        TempTravelLocationBean firstLoc = mLocationList.get(0);
        Log.d(TAG, String.format("Start Times: Travel-Start[%s] Location-First[%s]", mLocationHeader.getStartTime(), firstLoc.getTime()));
        if (firstLoc.getSpeed() == 0.0f) {
            long timeDiff = firstLoc.getTime() - mLocationHeader.getStartTime();
            Log.d(TAG, String.format("Start Times: Start-Lag[%s]", timeDiff));
            mWaitingTime += timeDiff;
            mIdleLocations++;
        }
        // get middle location
        for (int ctr = 1; ctr < mLocationList.size(); ctr++) {
            TempTravelLocationBean loc = mLocationList.get(ctr);
            if (loc.getSpeed() == 0.0f) {
                long diff = mLocationList.get(ctr).getTime() - mLocationList.get(ctr - 1).getTime();
                mWaitingTime += diff;
                mIdleLocations++;
            }
        }
        // get last location
        TempTravelLocationBean lastLocation = mLocationList.get(mLocationList.size() - 1);
        Log.d(TAG, String.format("Start Times: Travel-End[%s] Location-Last[%s]", mLocationFooter.getEndedTime(), lastLocation.getTime()));
        if (lastLocation.getSpeed() == 0.0f) {
            long timeDiff = mLocationFooter.getEndedTime() - lastLocation.getTime();
            Log.d(TAG, String.format("Start Times: End-Lag[%s]", timeDiff));
            mWaitingTime += timeDiff;
        }
        // get travel time
        long travelTime = mLocationFooter.getEndedTime() - mLocationHeader.getStartTime();

        Log.d(TAG, String.format("Start Times: Travel-Time[%s]", travelTime));
        Log.d(TAG, String.format("Start Times: Wait-Time[%s]", mWaitingTime));
        Log.d(TAG, String.format("Start Times: %s out of %s locations are idle", mIdleLocations, mLocationList.size()));
    }


    /**
     * Get files from extras.
     */
    private void getFiles() {
        Bundle extras = getIntent().getExtras();
        String mainFileName = extras.getString(FILE_NAME_INFO);
        Log.d(TAG, String.format("Loading [Main File] : %s", mainFileName));
        String markFileName = mainFileName.replace(".temp", "_marker.temp");
        Log.d(TAG, String.format("Loading [Mark File] : %s", markFileName));

        mMainFile = TempTravelDirectory.getFile(getApplicationContext(), mainFileName);
        mMarkFile = TempTravelDirectory.getFile(getApplicationContext(), markFileName);
    }

    /**
     * Translate the data from file to memory.
     */
    private void readFiles() {
        // create reader
        TempTravelReader reader = new TempTravelReader(mMainFile);
        // create location list
        this.mLocationList = new LinkedList<>();
        try {
            // open reader.
            reader.open();
            // get line.
            String data = reader.read();
            while (data != null) {
                if (data.isEmpty()) {
                    data = reader.read();  // next
                    continue;
                }
                if (data.startsWith("START")) {
                    this.mLocationHeader = new TempTravelHeaderBean();
                    this.mLocationHeader.fromTempCSV(data);
                } else if (data.startsWith("END")) {
                    this.mLocationFooter = new TempTravelFooterBean();
                    this.mLocationFooter.fromTempCSV(data);
                    break;
                } else {
                    TempTravelLocationBean location = new TempTravelLocationBean();
                    location.fromTempCSV(data);
                    this.mLocationList.add(location);
                }
                data = reader.read();
            }
        } catch (IOException ioex) {
            Log.e(TAG, "fileReadError", ioex);
            new AlertDialog.Builder(this)
                    .setMessage("Cannot load this travel information.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create().show();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                    Log.e(TAG, "cannot close reader");
                }
            }
        }
    }


    public void onClickBtnDelete() {
        new AlertDialog.Builder(this)
                .setMessage("Delete this record forever ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFiles();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .create().show();
    }


    /**
     * Delete selected travel information and their files in cache.
     */
    private void deleteFiles() {
        if (mMainFile != null) {
            mMainFile.delete();
        }
        if (mMarkFile != null) {
            mMarkFile.delete();
        }
    }


}
