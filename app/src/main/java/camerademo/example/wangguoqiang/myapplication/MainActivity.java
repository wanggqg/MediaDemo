package camerademo.example.wangguoqiang.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by wanggq on 17-6-25.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private Button startAudio;
    private Button startCamera;
    private Button startWebView;
    private Button startScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startAudio = (Button) findViewById(R.id.startAudio);
        startCamera = (Button) findViewById(R.id.startCamera);
        startWebView = (Button) findViewById(R.id.startWebView);
        startScreen = (Button) findViewById(R.id.startScreen);
        startAudio.setOnClickListener(this);
        startCamera.setOnClickListener(this);
        startWebView.setOnClickListener(this);
        startScreen.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.startAudio:
                intent = new Intent(this, SaveAudioActivity.class);
                break;
            case R.id.startCamera:
                intent = new Intent(this, CameraActivity.class);
                break;
            case R.id.startWebView:
                intent = new Intent(this, WebViewActivity.class);
                break;
            case R.id.startScreen:
                intent = new Intent(this, ScreenCaptureImageActivity.class);
                break;
            default:
                break;
        }
        startActivity(intent);
    }
}
