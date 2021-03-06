package com.bananaplan.workflowandroid.data.activity.actions;

import android.util.Log;

import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.loading.RestfulUtils;
import com.bananaplan.workflowandroid.data.loading.URLUtils;
import com.bananaplan.workflowandroid.data.network.IPostRequestStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/13/15.
 */
public class UploadingFileStrategy implements IPostRequestStrategy {

    private static final String TAG = UploadingFileStrategy.class.toString();

    private String mFilePath;
    private String mEndPoint;
    private HashMap<String, String> mHeaders;

    public UploadingFileStrategy(String filePath, String endPoint, HashMap<String, String> headers) {
        mFilePath = filePath;
        mEndPoint = endPoint;
        mHeaders = headers;
    }

    @Override
    public JSONObject post() {
        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.WorkingDataUrl.BASE_URL, mEndPoint, null);
            String responseString = RestfulUtils.restfulPostFileRequest(urlString, mHeaders, mFilePath);
            JSONObject jsonObject = new JSONObject(responseString);
            if (jsonObject.getString("status").equals("success")) {
                return jsonObject.getJSONObject("result");
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in LoadingWorkerActivitiesStrategy()");
            e.printStackTrace();
        }

        return null;
    }
}
