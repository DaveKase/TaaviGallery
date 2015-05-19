package taavi.kase.gallery;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    public static ProgressBar progress;

    private float oldTouchValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);
        download();
    }

    private void download() {
        new Thread("Download JSON") {
            public void run() {
                Downloader downloader = new Downloader();
                downloader.getJsonArray();
                showUI();
            }
        }.start();
    }

    private void showUI() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                progress.setVisibility(View.INVISIBLE);
                showImages();
            }
        });
    }

    private void showImages() {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        String dir = Downloader.getDirectory();
        Uri uri = Uri.parse(dir + "/PIC-0.jpg");
        imageView.setImageURI(uri);
    }

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
                    Log.i(TAG, "left");
                } if (oldTouchValue > currentX ) {
                    Log.i(TAG, "right");
                }
        }

        return super.onTouchEvent(event);
    }
}
