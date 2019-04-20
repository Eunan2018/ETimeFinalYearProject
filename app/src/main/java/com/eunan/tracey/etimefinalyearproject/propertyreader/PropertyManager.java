package com.eunan.tracey.etimefinalyearproject.propertyreader;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {

    private static final String TAG = "PropertyManager";
    private Context context;
    private Properties properties;

    public PropertyManager(Context context) {
        Log.d(TAG, "PropertyManager: called");
        this.context = context;
        //creates a new object ‘Properties’
        properties = new Properties();
    }

    public Properties getProperties(String file) {
        Log.d(TAG, "getProperties: starts" + file);
        try {
            //access to the folder ‘assets’
            AssetManager am = context.getAssets();
            //opening the file
            InputStream inputStream = am.open(file);
            //loading of the prop.properties
            properties.load(inputStream);
        } catch (IOException e) {
            Log.e("PropertiesReader", e.toString());
        }
        return properties;
    }
}
