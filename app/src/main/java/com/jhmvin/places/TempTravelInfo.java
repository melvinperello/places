package com.jhmvin.places;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.jhmvin.places.domain.TempTravelSummaryData;
import com.jhmvin.places.feature.tempTravel.TempTravelDirectory;
import com.jhmvin.places.feature.tempTravel.TempTravelFooterBean;
import com.jhmvin.places.feature.tempTravel.TempTravelHeaderBean;
import com.jhmvin.places.feature.tempTravel.TempTravelLocationBean;
import com.jhmvin.places.feature.tempTravel.TempTravelReader;
import com.jhmvin.places.util.TimeTool;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TempTravelInfo extends AppCompatActivity {
    public final static String TAG = TempTravelInfo.class.getCanonicalName();
    //----------------------------------------------------------------------------------------------
    // Widgets.
    //----------------------------------------------------------------------------------------------
    @BindView(R.id.tvFrom)
    TextView tvFrom;

    @BindView(R.id.tvTo)
    TextView tvTo;

    @BindView(R.id.tvTimeStart)
    TextView tvTimeStart;

    @BindView(R.id.tvTimeEnd)
    TextView tvTimeEnd;

    @BindView(R.id.tvActiveTime)
    TextView tvActiveTime;

    @BindView(R.id.tvIdleTime)
    TextView tvIdleTime;

    @BindView(R.id.tvTravelTime)
    TextView tvTravelTime;

    @BindView(R.id.tvPrecision)
    TextView tvPrecision;

    //----------------------------------------------------------------------------------------------
    // Activity.
    //----------------------------------------------------------------------------------------------
    /**
     * Bundle key for file.
     */
    public final static String FILE_NAME_INFO = "zzz_file_name_info";

    /**
     * Date Formatter.
     */
    private final SimpleDateFormat mDateFormatter = new SimpleDateFormat("MMMMMMMMMMMMMMMM dd, yyyy hh:mm:ss a");
    private final DecimalFormat mDecimalFormat = new DecimalFormat("#,##0.00");
    // bundled files
    private File mMainFile;
    private File mMarkFile;
    // translated data.
    private TempTravelHeaderBean mLocationHeader;
    private List<TempTravelLocationBean> mLocationList;
    private TempTravelFooterBean mLocationFooter;
    //


    //----------------------------------------------------------------------------------------------
    // Creation.
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_travel_info);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(R.string.summary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // clear text
        clearText();
        // call
        this.getFiles();
        // read data
        this.readFiles();
        //
        this.createSummary();
        //
    }


    private void clearText() {
        tvFrom.setText("");
        tvTo.setText("");
        tvTimeStart.setText("");
        tvTimeEnd.setText("");
        tvActiveTime.setText("");
        tvIdleTime.setText("");
        tvTravelTime.setText("");
    }

    private void displayData() {
        if (mLocationHeader != null) {

        }
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

    /**
     * Generate useful information from the data gathered.
     */
    private void createSummary() {
        Log.d(TAG, "Summary: Creating Summary");
        // create summary object.
        TempTravelSummaryData summary = new TempTravelSummaryData();
        //
        // Declarations.
        //
        // waiting time.
        long mWaitingTime = 0;
        // idle locations
        int mIdleLocations = 0;
        // convert linked list to array list location list for get/set performance
        mLocationList = new ArrayList<>(mLocationList);
        //
        // Get the first location
        //
        TempTravelLocationBean firstLoc = mLocationList.get(0);
        Log.d(TAG, String.format("Start Times: Travel-Start[%s] Location-First[%s]", mLocationHeader.getStartTime(), firstLoc.getTime()));
        if (firstLoc.getSpeed() == 0.0f) {
            long timeDiff = firstLoc.getTime() - mLocationHeader.getStartTime();
            Log.d(TAG, String.format("Start Times: Start-Lag[%s]", timeDiff));
            mWaitingTime += timeDiff;
            mIdleLocations++;
        }
        //
        // get middle location
        //
        for (int ctr = 1; ctr < mLocationList.size(); ctr++) {
            TempTravelLocationBean loc = mLocationList.get(ctr);
            if (loc.getSpeed() == 0.0f) {
                long diff = mLocationList.get(ctr).getTime() - mLocationList.get(ctr - 1).getTime();
                mWaitingTime += diff;
                mIdleLocations++;
            }
        }
        //
        // get location footer.
        //
        long localEndedTime;
        if (mLocationFooter != null) {
            // get last location
            TempTravelLocationBean lastLocation = mLocationList.get(mLocationList.size() - 1);
            Log.d(TAG, String.format("Start Times: Travel-End[%s] Location-Last[%s]", mLocationFooter.getEndedTime(), lastLocation.getTime()));
            if (lastLocation.getSpeed() == 0.0f) {
                long timeDiff = mLocationFooter.getEndedTime() - lastLocation.getTime();
                Log.d(TAG, String.format("Start Times: End-Lag[%s]", timeDiff));
                mWaitingTime += timeDiff;
            }
            localEndedTime = mLocationFooter.getEndedTime();
            summary.setTimeEnd("(Complete)");
        } else {
            localEndedTime = mLocationList.get(mLocationList.size() - 1).getTime();
            summary.setTimeEnd("(Last Location)");
        }


        // get travel time
        long travelTime = localEndedTime - mLocationHeader.getStartTime();

        Log.d(TAG, String.format("Start Times: Travel-Time[%s]", travelTime));
        Log.d(TAG, String.format("Start Times: Wait-Time[%s]", mWaitingTime));
        Log.d(TAG, String.format("Start Times: %s out of %s locations are idle", mIdleLocations, mLocationList.size()));

        // load summary data.
        summary.setFrom(mLocationHeader.getStartPlace());
        summary.setTo(mLocationHeader.getEndPlace());
        // start
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(mLocationHeader.getStartTime());
        summary.setTimeStart(mDateFormatter.format(startCal.getTime()));
        // end
        Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis(localEndedTime);
        summary.setTimeEnd(mDateFormatter.format(endCal.getTime()) + " " + summary.getTimeEnd());
        // set idle time
        summary.setTimeIdle(TimeTool.convertMillsToString(mWaitingTime));
        // get total
        summary.setTimeTotalTravelled(TimeTool.convertMillsToString(travelTime));
        // get active
        summary.setTimeActive(TimeTool.convertMillsToString(travelTime - mWaitingTime));
        // get points
        summary.setLocationCount(mLocationList.size() + " Location Points");


        double idlePercent = (((double) mWaitingTime) / ((double) travelTime));
        Log.d(TAG, "Summary: idle-percent[" + idlePercent + "]");
        double activePercent = (1.0 - idlePercent);

        tvFrom.setText(summary.getFrom());
        tvTo.setText(summary.getTo());
        tvTimeStart.setText(summary.getTimeStart());
        tvTimeEnd.setText(summary.getTimeEnd());
        tvActiveTime.setText(summary.getTimeActive() + " [ " + mDecimalFormat.format(activePercent * 100) + "% ]");
        tvIdleTime.setText(summary.getTimeIdle() + " [ " + mDecimalFormat.format(idlePercent * 100) + "% ]");
        tvTravelTime.setText(summary.getTimeTotalTravelled());
        tvPrecision.setText(summary.getLocationCount());


    }

    //----------------------------------------------------------------------------------------------
    // Menu Options.
    //----------------------------------------------------------------------------------------------

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


    //----------------------------------------------------------------------------------------------
    // Activity Methods.
    //----------------------------------------------------------------------------------------------

    /**
     * Invoke a click on button delete.
     */
    public void onClickBtnDelete() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_forever_without_saving)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFiles();
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, null)
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
