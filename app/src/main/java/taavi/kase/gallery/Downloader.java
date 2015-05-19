package taavi.kase.gallery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * Created by Taavi on 19.05.2015.
 */
public class Downloader {
    private static final String TAG = "JSON";

    public void getJsonArray() {
        new Thread("Download JSON") {
            public void run() {
                try {
                    JSONArray jArray = new JSONArray(getJson());
                    //Log.i(TAG, "jArray = " + jArray.toString());
                    getUrl(jArray);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON download error: " + e.toString());
                }
            }
        }.start();
    }

    private String getJson() {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://portal.huntloc.com/galery_static.json#");

        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("", "Failed to download file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    private String getUrl(JSONArray jArray) {
        try {
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String url = jObject.getString("url");
                Log.i(TAG, "url = " + url);
                getImageFromUrl(url, i);
            }
        } catch(JSONException e) {
            Log.e(TAG, "Error in parsing JSON: " + e.toString());
        }

        return "";
    }

    private void getImageFromUrl (String src, int index) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            saveBitmap(bitmap, index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBitmap(Bitmap bitmapToSave, int index) {
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/taavi_gallery");
        dir.mkdirs();

        String fileName = "PIC-" + index + ".jpg";
        File file = new File (dir, fileName);

        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
