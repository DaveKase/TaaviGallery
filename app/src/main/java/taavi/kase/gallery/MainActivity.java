package taavi.kase.gallery;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private ProgressBar progress;
    private TextView initText;

    private float oldTouchValue;
    private int index = 0;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);

        initText = (TextView) findViewById(R.id.init_textView);
        initText.setVisibility(View.VISIBLE);
        download();
    }

    private void download() {
        new Thread("Download JSON") {
            public void run() {
                Downloader downloader = new Downloader();
                downloader.startDownloading();
                showUI();
            }
        }.start();
    }

    private void showUI() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                progress.setVisibility(View.INVISIBLE);
                initText.setVisibility(View.INVISIBLE);
                showImageAndText();
            }
        });
    }

    private void showImageAndText() {
        if (imageView != null) {
            imageView = null;
        }

        imageView = (ImageView) findViewById(R.id.imageView);
        String dir = Downloader.getDirectory();
        Uri uri = Uri.parse(dir + "/PIC-" + index + ".jpg");
        imageView.setImageURI(uri);

        String created = "";

        try {
            JSONObject jObject = Downloader.jArray.getJSONObject(index);
            created = jObject.getString("created");
        } catch (JSONException e) {
            Toast.makeText(this, "Could not get created time", Toast.LENGTH_SHORT).show();
        }

        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText("Created: " + created);
    }

    /*
    * Found on:
    * http://stackoverflow.com/questions/6645537/how-to-detect-the-swipe-left-or-right-in-android
    */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldTouchValue = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float currentX = event.getX();

                if (oldTouchValue < currentX) {
                    showPreviousImage();
                } if (oldTouchValue > currentX ) {
                    showNextImage();
                }
        }

        return super.onTouchEvent(event);
    }

    private void showPreviousImage() {
        if (index <= 0) {
            Toast.makeText(this, "Already showing first image", Toast.LENGTH_SHORT).show();
        } else {
            index--;
            showImageAndText();
        }
    }

    private void showNextImage() {
        if (index == Downloader.maxIndex) {
            Toast.makeText(this, "Already showing last image", Toast.LENGTH_SHORT).show();
        } else {
            index++;
            showImageAndText();
        }
    }
}
