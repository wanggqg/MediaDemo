
package camerademo.example.wangguoqiang.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

import static android.view.SurfaceHolder.*;

public class CameraActivity extends Activity implements Callback, Camera.PreviewCallback {
    private Camera camera;
    private SurfaceView msurfaceview;
    private Button buttonstart;
    private SurfaceHolder surfaceholder;
    private int mFromat = ImageFormat.NV21;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_camera);
        msurfaceview = (SurfaceView) findViewById(R.id.mysurfaceview);
        buttonstart = (Button) findViewById(R.id.start);
        buttonstart.setText("start");
        surfaceholder = msurfaceview.getHolder();
        surfaceholder.addCallback(this);
        surfaceholder.setType(SURFACE_TYPE_PUSH_BUFFERS);
        audioManager = (AudioManager) this.getSystemService(
                Context.AUDIO_SERVICE);
        findViewById(R.id.NV21).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFromat = ImageFormat.NV21;
            }
        });
        findViewById(R.id.YV12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFromat = ImageFormat.YV12;
            }
        });
        findViewById(R.id.bt_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, SaveAudioActivity.class);
                startActivity(intent);
            }
        });
        buttonstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        camera = Camera.open(0);
                        if(camera == null){
                            Log.d("CameraActivity", "camera is null");
                            return;
                        }
                        try {
                            camera.setPreviewDisplay(surfaceholder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mParameters = camera.getParameters();
                        List<Integer> s = mParameters.getSupportedPreviewFormats();
                        for (int i : s) {
                            Log.d("CameraActivity", "wgq formt: " + i);
                            if(i == ImageFormat.NV21){
                                Log.d("CameraActivity", "wgq formt: NV21" + i);
                            }
                            if(i == ImageFormat.YV12){
                                Log.d("CameraActivity", "wgq formt: YV12" + i);
                            }
                        }
                        Camera.Size size = mParameters.getPreviewSize();
                        int previewWidth = size.width;
                        int previewHeight = size.height;
                        int sizePreview = previewWidth * previewHeight * ImageFormat.getBitsPerPixel(mParameters.getPreviewFormat()) / 8;
                        Log.d("CameraActivity", "wgq sizePreview: " + sizePreview);
                        mParameters.setPreviewFormat(mFromat);
                        camera.setParameters(mParameters);
                        camera.addCallbackBuffer(new byte[sizePreview]);
                        camera.setPreviewCallbackWithBuffer(CameraActivity.this);
                        camera.startPreview();
                    }
                }).start();
            }
        });
    }

    private Camera.Parameters mParameters = null;

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.d("CameraActivity", "wgq onPreviewFrame");
        Camera.Size size = mParameters.getPreviewSize();
        int previewWidth = size.width;
        int previewHeight = size.height;
        int sizePreview = previewWidth * previewHeight * ImageFormat.getBitsPerPixel(mParameters.getPreviewFormat()) / 8;
        camera.addCallbackBuffer(new byte[sizePreview]);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

}
