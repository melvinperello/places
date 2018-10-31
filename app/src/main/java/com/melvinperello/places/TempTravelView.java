package com.melvinperello.places;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.melvinperello.places.domain.TravelStreamListData;
import com.melvinperello.places.domain.TravelStreamListDataAdapter;
import com.melvinperello.places.feature.tempTravel.TempTravelDirectory;
import com.melvinperello.places.feature.tempTravel.TempTravelHeaderBean;
import com.melvinperello.places.feature.tempTravel.TempTravelReader;
import com.melvinperello.places.persistence.text.TextReader;
import com.melvinperello.places.util.ToastAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TempTravelView extends AppCompatActivity implements TravelStreamListDataAdapter.ItemClicked {
    /**
     * Logging tag.
     */
    public final static String TAG = TempTravelView.class.getCanonicalName();
    /**
     * formatter.
     */
    private final SimpleDateFormat mDateFormatter = new SimpleDateFormat("MMMMMMMMMMMMMMMM dd, yyyy hh:mm:ss a");


    @BindView(R.id.rvFiles)
    RecyclerView rvFiles;


    private RecyclerView.Adapter adapter;

    private final List<File> mDisplayUnsavedFiles = new ArrayList<>();
    private final ArrayList<TravelStreamListData> dataList = new ArrayList<>();

    private boolean mViewEmpty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_travel_view);
        ButterKnife.bind(this);
        // change title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.recent_places);


        rvFiles.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvFiles.setLayoutManager(manager);

        this.adapter = new TravelStreamListDataAdapter(this, dataList);
        this.rvFiles.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_item_clear:
                onClearCache();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_temp_travel_view, menu);

        if (this.mViewEmpty) {
            MenuItem action_item_clear = menu.findItem(R.id.action_item_clear);
            action_item_clear.setVisible(false);
        }


        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh.
        this.refreshData();
        Log.d(TAG, "Refresh Data . . .");

        if (this.dataList.isEmpty()) {
            mViewEmpty = true;
            invalidateOptionsMenu(); // recall the onCreateOptionsMenu
            Log.d(TAG, "No data to display.");
        }
    }

    private void refreshData() {
        this.refreshUnsavedFiles();
        this.refreshAdapterData();
    }


    private void refreshUnsavedFiles() {
        List<File> files = new ArrayList<>();
        File tempDir = TempTravelDirectory.getWorkingDirectory(getApplicationContext());
        String[] fileNames = tempDir.list();
        for (String fileName : fileNames) {
            if (fileName.contains("_marker.temp")) {
                continue;
            }
            files.add(new File(tempDir, fileName));
        }
        mDisplayUnsavedFiles.clear();
        mDisplayUnsavedFiles.addAll(files);
    }

    /**
     * Refresh adapter data.
     */
    private void refreshAdapterData() {
        this.dataList.clear();
        for (File placeFile : mDisplayUnsavedFiles) {
            // read first line
            String firstLine = readFirstLine(placeFile);
            if (firstLine == null) {
                continue;
            }
            // translate header
            TempTravelHeaderBean header = new TempTravelHeaderBean();
            header.fromTempCSV(firstLine);
            // create item.
            TravelStreamListData data = new TravelStreamListData();
            data.setOrigin(header.getStartPlace());
            data.setDestination(header.getEndPlace());
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(header.getStartTime());
            data.setStartTime(mDateFormatter.format(c.getTime()));
            this.dataList.add(data);
        }
        this.adapter.notifyDataSetChanged();
    }

    private String readFirstLine(File placeFile) {
        // create reader
        TextReader reader = null;
        String firstLine;
        try {
            // instantiate reader.
            reader = new TempTravelReader(getApplicationContext(), placeFile.getName());
            // open reader.
            reader.open();
            // get only the first line which containes the header.
            firstLine = reader.read();
        } catch (IOException ex) {
            firstLine = null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        return firstLine;
    }


    private void onClearCache() {
        if (mDisplayUnsavedFiles.size() == 0) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.there_are_no_items_to_clear)
                    .setPositiveButton(R.string.ok, null)
                    .create()
                    .show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(R.string.are_you_sure_to_delete_unsaved_places)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearCache();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create().show();
    }

    private void clearCache() {
        for (File f : mDisplayUnsavedFiles) {
            f.delete();
        }
        this.dataList.clear();
        this.adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClicked(int index) {
        try {
            File selectedFile = this.mDisplayUnsavedFiles.get(index);
            Log.d(TAG, String.format("Selected Item [%s] %s", index, selectedFile.getName()));
            // start next
            Intent intent = new Intent(this, TempTravelInfo.class);
            intent.putExtra(TempTravelInfo.FILE_NAME_INFO, selectedFile.getName());
            startActivity(intent);
        } catch (Exception ex) {
            ToastAdapter.show(getApplicationContext(), "Cannot Display", ToastAdapter.ERROR);
        }
    }


}
