package com.bananaplan.workflowandroid.login;

import android.util.Log;

import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.loading.RestfulUtils;
import com.bananaplan.workflowandroid.data.loading.URLUtils;
import com.bananaplan.workflowandroid.data.network.IGetRequestStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/20/15.
 */
public class CheckLoggedInStatusStrategy implements IGetRequestStrategy {

    private static final String TAG = CheckLoggedInStatusStrategy.class.toString();

    @Override
    public JSONObject get() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            String urlString = URLUtils.buildURLString(LoadingDataUtils.WorkingDataUrl.BASE_URL, LoadingDataUtils.WorkingDataUrl.EndPoints.LOGIN_STATUS, null);
            String responseJSONString = RestfulUtils.restfulGetRequest(urlString, headers);
            JSONObject responseJSON = new JSONObject(responseJSONString);
            if (responseJSON.getString("status").equals("success")) {
                return responseJSON.getJSONObject("result");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in CheckLoggedInStatusStrategy()");
            e.printStackTrace();
        }
        return null;
    }
}
