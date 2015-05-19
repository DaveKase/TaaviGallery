package taavi.kase.gallery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

public class Downloader {
    private static final String TAG = "JSON";
    public boolean isDownloading = true;
    public static int maxIndex;
    public static JSONArray jArray;

    public void startDownloading() {
        try {
            jArray = new JSONArray(getJson());
            getUrl(jArray);
        } catch (JSONException e) {
            Log.e(TAG, "JSON download error: " + e.toString());
        }
    }

    /**
     * Downloads JSON Array as a String
     *
     * Found on:
     * http://www.vogella.com/tutorials/AndroidJSON/article.html
     */
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
                getImageFromUrl(url, i);
                maxIndex = i;
            }
        } catch(JSONException e) {
            Log.e(TAG, "Error in parsing JSON: " + e.toString());
        }

        return "";
    }

    /*
    * Found on:
    * http://stackoverflow.com/questions/18210700/best-method-to-download-image-from-url-in-android
    */
    private void getImageFromUrl (String src, int index) {
        try {
            URL url = new URL(src);
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

    /*
    * Found on:
    * http://stackoverflow.com/questions/11846108/android-saving-bitmap-to-sd-card
    */
    private void saveBitmap(Bitmap bitmapToSave, int index) {
        File dir = new File(getDirectory());
        boolean deleted = dir.mkdirs();

        if (deleted) {
            String fileName = "PIC-" + index + ".jpg";
            File file = new File (dir, fileName);

            if (file.exists()) {
                boolean isDeleted = file.delete();
                Log.d(TAG, "file deleted = " + isDeleted);
            }

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

                isDownloading = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getDirectory() {
        String root = Environment.getExternalStorageDirectory().toString();
        return root + "/taavi_gallery";
    }
}
