package com.eunan.tracey.etimefinalyearproject;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {

    private static final String TAG = "PropertyManager";
    private Context mContext;
    private Properties mProperties;

    public PropertyManager(Context context) {
        Log.d(TAG, "PropertyManager: called");
        this.mContext = context;
        //creates a new object ‘Properties’
        mProperties = new Properties();
    }

    public Properties getProperties(String file) {
        Log.d(TAG, "getProperties: starts" + file);
        try {
            //access to the folder ‘assets’
            AssetManager am = mContext.getAssets();
            //opening the file
            InputStream inputStream = am.open(file);
            //loading of the properties
            mProperties.load(inputStream);
        } catch (IOException e) {
            Log.e("PropertiesReader", e.toString());
        }
        return mProperties;
    }
}
