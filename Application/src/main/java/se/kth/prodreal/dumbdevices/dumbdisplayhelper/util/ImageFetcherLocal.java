/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.kth.prodreal.dumbdevices.dumbdisplayhelper.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import se.kth.prodreal.dumbdevices.dumbdisplayhelper.BuildConfig;
import se.kth.prodreal.dumbdevices.dumbdisplayhelper.Logger.Log;
import se.kth.prodreal.dumbdevices.dumbdisplayhelper.R;

/**
 * A simple subclass of {@link se.kth.prodreal.dumbdevices.dumbdisplayhelper.util.ImageResizer} that fetches and resizes images fetched from a URL.
 */
public class ImageFetcherLocal extends ImageResizerLocal {
    private static final String TAG = "ImageFetcher";
    private static final int HTTP_CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String HTTP_CACHE_DIR = "http";
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final int REQ_IMG_HEIGHT = 176;
    private static final int REQ_IMG_WIDTH = 264;

    private DiskLruCache mHttpDiskCache;
    private File mHttpCacheDir;
    private boolean mHttpDiskCacheStarting = true;
    private final Object mHttpDiskCacheLock = new Object();
    private static final int DISK_CACHE_INDEX = 0;

    //local storage for images
//    public static final String SDCARD_SCREENSHOTS = "/storage/emulated/0/Pictures/Screenshots";
    public static final String SDCARD_SCREENSHOTS = "/storage/emulated/0/Pictures/Lorena";
//    public static final String SDCARD_SCREENSHOTS = "/storage/emulated/0/Pictures/Artwork";
    private File[] files;

    /**
     * Initialize providing a target image width and height for the processing images.
     *
     * @param context
     * @param imageWidth
     * @param imageHeight
     */
    public ImageFetcherLocal(Context context, int imageWidth, int imageHeight) {
        super(context, imageWidth, imageHeight);
        init(context);
    }

    /**
     * Initialize providing a single target image size (used for both width and height);
     *
     * @param context
     * @param imageSize
     */
    public ImageFetcherLocal(Context context, int imageSize) {
        super(context, imageSize);
        retrieveImages();
        init(context);
    }

    private void init(Context context) {

    }

    @Override
    protected void initDiskCacheInternal() {
        super.initDiskCacheInternal();
    }


    @Override
    protected void clearCacheInternal() {
        super.clearCacheInternal();
        synchronized (mHttpDiskCacheLock) {
            if (mHttpDiskCache != null && !mHttpDiskCache.isClosed()) {
                try {
                    mHttpDiskCache.delete();
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "HTTP cache cleared");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "clearCacheInternal - " + e);
                }
                mHttpDiskCache = null;
                mHttpDiskCacheStarting = true;
            }
        }
    }

    @Override
    protected void flushCacheInternal() {
        super.flushCacheInternal();
        synchronized (mHttpDiskCacheLock) {
            if (mHttpDiskCache != null) {
                try {
                    mHttpDiskCache.flush();
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "HTTP cache flushed");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "flush - " + e);
                }
            }
        }
    }

    @Override
    protected void closeCacheInternal() {
        super.closeCacheInternal();
        synchronized (mHttpDiskCacheLock) {
            if (mHttpDiskCache != null) {
                try {
                    if (!mHttpDiskCache.isClosed()) {
                        mHttpDiskCache.close();
                        mHttpDiskCache = null;
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "HTTP cache closed");
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "closeCacheInternal - " + e);
                }
            }
        }
    }


    /**
     * The main process method, which will be called by the ImageWorker in the AsyncTask background
     * thread.
     *
     * @return The downloaded and resized bitmap
     */
    protected Bitmap processBitmap(int posOfImage) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "processBitmap - " + posOfImage);
        }



        File imgFile = this.files[posOfImage];
        Bitmap bitmap = null;
        if (imgFile != null) {
            bitmap = decodeSampledBitmapFromFile(imgFile.getAbsolutePath(), mImageWidth,
                    mImageHeight, getImageCache());
        }

        if(scaled) {
           Bitmap resizedBitmap = ImageManipulation.getResizedBitmap(bitmap, REQ_IMG_HEIGHT, REQ_IMG_WIDTH);
           Bitmap bwBitmap = ImageManipulation.createBlackAndWhite(resizedBitmap);
            return bwBitmap;
        }
        return bitmap;
    }

    public void retrieveImages() {

        // Find the latest file
        // Delete the info.txt file and config.dat to avoid
        File dir = new File(SDCARD_SCREENSHOTS);
        File f = new File(SDCARD_SCREENSHOTS + "/info.txt");
        f.delete();
        File g = new File(SDCARD_SCREENSHOTS + "/config.dat");
        g.delete();

        // check whether there are any files in the folder
        files = dir.listFiles();
        
        if (files.length == 0) {
            android.util.Log.d("TEST", "No files");
        }


        for (int i = 1; i < files.length; i++) {
        }


    }
}
