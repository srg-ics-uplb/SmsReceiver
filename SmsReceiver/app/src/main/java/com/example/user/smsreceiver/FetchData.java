package com.example.user.smsreceiver;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchData extends AsyncTask<Void, Void, Void> {
    private Context context;
    private String num="";
    private static String body;

    public FetchData(Context context, String num, String body){
        this.context=context;
        this.num=num;
        this.body=body;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //==================================using save()=========================================//
        String[] data=body.split(", "); //split the message to get the point id

        if(data.length!=2){
            body=data[1]+", "+data[2]+", "+data[3]+", "+data[4]+", "+data[5]+", "+data[6]+", "+data[7]+", "+data[8];
        }else{
            body="done";
        }
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this.context);
        String url = "http://10.0.3.57:6200/points/"+data[0]+"/logs";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(body.equals("done")){
                            sendMessage(response);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("message", body);
                return params;
            }
        };
        MyRequestQueue.add(postRequest);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    private void sendMessage(String result){
        //Sending of message
        SmsManager smsManager = SmsManager.getDefault();
        List<String> messages = smsManager.divideMessage(result);
        for (String msg : messages) {
            smsManager.sendTextMessage(this.num, null, msg, null, null);    //TODO this.num will be changed to those of the fishermen's
        }
        Toast.makeText(context, "Message forwarded.", Toast.LENGTH_LONG).show();
    }
}