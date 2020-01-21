package com.darryncampbell.androidv2dialogflowclient;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DialogFlowCommsService extends IntentService {

    private final String FIREBASE_SERVER = "https://us-central1-androidv2dialogflow-xspkei.cloudfunctions.net";

    private static final String ACTION_DETECT_INTENT = "com.darryncampbell.androidv2dialogflowclient.action.DETECT_INTENT";
    private static final String EXTRA_QUESTION = "com.darryncampbell.androidv2dialogflowclient.extra.QUESTION";

    public DialogFlowCommsService() {
        super("DialogFlowCommsService");
    }

    public static void startActionDetectIntent(Context context, String question) {
        Intent intent = new Intent(context, DialogFlowCommsService.class);
        intent.setAction(ACTION_DETECT_INTENT);
        intent.putExtra(EXTRA_QUESTION, question);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DETECT_INTENT.equals(action)) {
                final String question = intent.getStringExtra(EXTRA_QUESTION);
                handleActionDetectIntent(question);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDetectIntent(String question) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = FIREBASE_SERVER + "/detectIntent";
        try {
            url += "?question=" + URLEncoder.encode(question, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(MainActivity.TAG, "Exception encoding question");
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // todo Do something with the response
                Log.i(MainActivity.TAG, "Response from DetectIntent: " + response);
                Intent intent = new Intent(MainActivity.DIALOGFLOW_RESPONSE);
                intent.putExtra("response", response);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e(MainActivity.TAG, "Error detecting intent ", error);
                    }
                });//  todo authentication headers
        queue.add(stringRequest);
    }
}
