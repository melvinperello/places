package com.melvinperello.places.feature.tempTravel;

import android.content.Context;
import android.util.Log;

import com.melvinperello.places.persistence.text.TextWriter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TempTravelCacheService {
    public static String TAG = TempTravelCacheService.class.getCanonicalName();

    /**
     * Text Writer Instance.
     */
    private TextWriter mCacheWriter;

    /**
     * Cache for writing in the files.
     */
    private List<TempTravelLocationBean> mForWriteCache;
    /**
     * In Memory cache for live report generation.
     */
    private List<TempTravelLocationBean> mInMemoryCache;

    public final static int CACHE_SIZE_PERSISTENCE = 5;

    private int mLocationCount = 0;


    public TempTravelCacheService(Context context, String fileName) {
        mCacheWriter = new TempTravelWriter(context, fileName);
        mInMemoryCache = new LinkedList<>();
        mForWriteCache = new LinkedList<>();
    }

    /**
     * Add to write cache.
     *
     * @param location
     */
    public void cache(TempTravelLocationBean location) {
        // add to cache for writing
        mForWriteCache.add(location);
        // increment
        this.incrementLocationCount();
        // check if needs to persist.
        if (mForWriteCache.size() > CACHE_SIZE_PERSISTENCE) {
            // write to cache when reach the cache size persistence.
            persistCache();
        }
        Log.d(TAG, String.format("Cache State: for-write[%s] in-memory[%s]", mForWriteCache.size(), mInMemoryCache.size()));
    }

    /**
     * Increments the location counter.
     */
    private void incrementLocationCount() {
        mLocationCount++;
    }

    /**
     * Get the current location count.
     *
     * @return
     */
    public int getLocationCount() {
        return mLocationCount;
    }

    /**
     * file write.
     *
     * @param text
     */
    public void write(String text) {
        try {
            mCacheWriter.open();
            mCacheWriter.write(text);
        } catch (IOException e) {
            Log.d(TAG, "Cannot persist cache", e);
        } finally {
            closeWriterQuietly();
        }
    }

    /**
     * Write the contents of mForWriteCache to file.
     */
    public void persistCache() {
        StringBuilder textBuffer = new StringBuilder();
        for (TempTravelLocationBean location : mForWriteCache) {
            textBuffer.append("\n");
            textBuffer.append(location.toTempCSV());
        }
        write(textBuffer.toString());
        // add the contents to normal cache
        mInMemoryCache.addAll(mForWriteCache);
        mForWriteCache.clear();
    }

    /**
     * Close the writer quietly.
     */
    private void closeWriterQuietly() {
        try {
            mCacheWriter.close();
        } catch (IOException e) {
            Log.d(TAG, "Cannot Close Writer", e);
        }
    }

    /**
     * release the service resources.
     */
    public void stopService() {
        mInMemoryCache.clear();
        mInMemoryCache = null;
        mForWriteCache.clear();
        mForWriteCache = null;
        mCacheWriter = null;
    }

}
