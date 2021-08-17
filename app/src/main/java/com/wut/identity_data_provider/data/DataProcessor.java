package com.wut.identity_data_provider.data;


import android.content.Context;

import com.wut.identity_data_provider.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class used for data processing - compressing the data and sending it to webhook in Azure.
 */

public class DataProcessor {

    static Semaphore busy = new Semaphore(1);

    /**
     * Method used to compress String data.
     *
     * @param dataToCompress    String that needs to be compressed.
     * @return                  String compressed data in form of an encoded string.
     */
    public static String compressString(String dataToCompress) throws IOException {
        byte[] bytes = dataToCompress.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream os = new GZIPOutputStream(baos);
        os.write(bytes, 0, bytes.length);
        os.close();
        byte[] outBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(outBytes);
    }

    /**
     * Method used to send the data entries in an HTTP POST request message.
     *
     * @param context   Context provided to the method to get secret API key and to get the API URL.
     */
    public static void uploadData(Context context){
        Thread thread = new Thread(() -> {
            try {
                busy.acquire();
                HashMap<Integer, String> entryMap = DataDBHandler.getReadyDataEntries();
                if (entryMap.size() > 0) {
                    for (Integer key : entryMap.keySet()) {
                        try {
                            URL url = new URL(context.getString(R.string.APIUrl));
                            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setRequestProperty("Accept", "application/json");
                            conn.setRequestProperty("x-functions-key", context.getString(R.string.APIKey));
                            conn.setDoOutput(true);
                            conn.setDoInput(true);
                            conn.connect();

                            JSONObject msg = new JSONObject();
                            msg.put("entry", entryMap.get(key));

                            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                            String outputMsg = msg.toString();
                            os.writeBytes(outputMsg);

                            os.flush();
                            os.close();

                            String response = String.valueOf(conn.getResponseCode());
                            if (response.equals("200")){
                                DataDBHandler.updateUploadedStatuses(key.toString());
                            }

                            conn.disconnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                busy.release();
            }
        });
        thread.start();
    }
}
