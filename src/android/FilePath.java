package com.meeco.cordova.filepath;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PermissionHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class FilePath extends CordovaPlugin {

    private static final String TAG = "[FilePath plugin]: ";

    private static final int INVALID_ACTION_ERROR_CODE = -1;

    private static final int GET_PATH_ERROR_CODE = 0;
    private static final String GET_PATH_ERROR_ID = null;

    private static final int GET_CLOUD_PATH_ERROR_CODE = 1;
    private static final String GET_CLOUD_PATH_ERROR_ID = "cloud";

    // private static final int RC_READ_EXTERNAL_STORAGE = 5;

    private static CallbackContext callback;
    private static String uriStr;

    public static final int READ_REQ_CODE = 0;

    public static final String READ = Manifest.permission.READ_EXTERNAL_STORAGE;

    protected void getReadPermission(int requestCode) {
        PermissionHelper.requestPermission(this, requestCode, READ);
    }

    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action        The action to execute.
     * @param args          JSONArry of arguments for the plugin.
     * @param callbackContext The callback context through which to return stuff to caller.
     * @return              A PluginResult object with a status and message.
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callback = callbackContext;
        this.uriStr = args.getString(0);

        if (action.equals("resolveNativePath")) {
            if (PermissionHelper.hasPermission(this, READ)) {
                resolveNativePath();
            }
            else {
                getReadPermission(READ_REQ_CODE);
            }

            return true;
        }
        else {
            JSONObject resultObj = new JSONObject();

            resultObj.put("code", INVALID_ACTION_ERROR_CODE);
            resultObj.put("message", "Invalid action.");

            callbackContext.error(resultObj);
        }

        return false;
    }

    public void resolveNativePath() throws JSONException {
        JSONObject resultObj = new JSONObject();
        /* content:///... */
        Uri pvUrl = Uri.parse(this.uriStr);

        Log.d(TAG, "URI: " + this.uriStr);

        Context appContext = this.cordova.getActivity().getApplicationContext();
        String filePath = FileUtils.getFileUrlForUri(appContext, pvUrl);

        //check result; send error/success callback
        if (filePath == GET_PATH_ERROR_ID) {
            resultObj.put("code", GET_PATH_ERROR_CODE);
            resultObj.put("message", "Unable to resolve filesystem path.");

            this.callback.error(resultObj);
        }
        else if (filePath.equals(GET_CLOUD_PATH_ERROR_ID)) {
            resultObj.put("code", GET_CLOUD_PATH_ERROR_CODE);
            resultObj.put("message", "Files from cloud cannot be resolved to filesystem, download is required.");

            this.callback.error(resultObj);
        }
        else {
            Log.d(TAG, "Filepath: " + filePath);
            this.callback.success("file://" + filePath);
        }
    }


    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                JSONObject resultObj = new JSONObject();
                resultObj.put("code", 3);
                resultObj.put("message", "Filesystem permission was denied.");

                this.callback.error(resultObj);
                return;
            }
        }

        if (requestCode == READ_REQ_CODE) {
            resolveNativePath();
        }
    }
}
