package taavi.kase.gallery;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadJson();
    }

    private void downloadJson() {
        Downloader downloader = new Downloader();
        downloader.getJsonArray();

        //Log.i(TAG, "JARRAY = " + jArray.toString());
    }
}
