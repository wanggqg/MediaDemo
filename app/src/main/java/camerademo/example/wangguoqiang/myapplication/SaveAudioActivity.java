
package camerademo.example.wangguoqiang.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by wangguoqiang on 17-3-12.
 */
public class SaveAudioActivity extends Activity {
    private static String TAG = "SaveAudioActivity";
    private File file;
    private Button startAudio;
    private Button stopAudio;
    private Button playAudio;
    private Button deleteAudio;
    private boolean isRecording;
    private TextView tv_audio_succeess;
    private ScrollView mScrollView;
    // 48K采集率
    private static int sample = 48000;
    // 格式
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    int channelConfiguration_out = AudioFormat.CHANNEL_OUT_MONO;
    // 16Bit
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_audio);
        startAudio = (Button) findViewById(R.id.startAudio);
        stopAudio = (Button) findViewById(R.id.stopAudio);
        playAudio = (Button) findViewById(R.id.playAudio);
        deleteAudio = (Button) findViewById(R.id.deleteAudio);
        tv_audio_succeess = (TextView) findViewById(R.id.tv_audio_succeess);
        mScrollView = (ScrollView) findViewById(R.id.mScrollView);
        // 生成PCM文件
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/reverseme.pcm");
        startAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StartRecord();
                    }
                });
                thread.start();
                printLog("开始录音");
            }
        });
        stopAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = false;
                printLog("停止录音");
            }
        });
        playAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAudioTrack();
                printLog("播放录音");
            }
        });
    }

    // 打印log
    private void printLog(final String resultString) {
        tv_audio_succeess.post(new Runnable() {
            @Override
            public void run() {
                tv_audio_succeess.append(resultString + "\n");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    // 开始录音
    public void StartRecord() {
        Log.i(TAG, "开始录音");
        Log.i(TAG, "生成文件");
        // 如果存在，就先删除再创建
        if (file.exists())
            file.delete();
        Log.i(TAG, "删除文件");
        try {
            file.createNewFile();
            Log.i(TAG, "创建文件");
        } catch (IOException e) {
            Log.i(TAG, "未能创建");
            throw new IllegalStateException("未能创建" + file.toString());
        }
        try {
            // 输出流
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);
            int bufferSize = AudioRecord.getMinBufferSize(sample, channelConfiguration, audioEncoding);
            Log.d(TAG, "bufferSize: " + bufferSize);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, sample, channelConfiguration, audioEncoding, bufferSize);
//            AudioRecord audioRecord = new AudioRecord(15, frequency, channelConfiguration, audioEncoding, bufferSize);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferSize);
            audioRecord.startRecording();
            Log.i(TAG, "开始录音");
            isRecording = true;
            while (isRecording) {
                byteBuffer.clear();
                int bufferReadResult = audioRecord.read(byteBuffer, byteBuffer.capacity());
                byte[] bytes = byteBuffer.array();
                for (int i = 0; i < bufferReadResult; i++) {
                    dos.writeByte(bytes[i]);
                }
            }
            audioRecord.stop();
            dos.close();
        } catch (Throwable t) {
            Log.e(TAG, "录音失败");
        }
    }

    private void startAudioTrack() {
        if (file == null) {
            Log.e(TAG, "startAudioTrack fail file is null");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int audioLength = (int) file.length();
                    Log.d(TAG, "audioLength: " + audioLength);
                    byte[] audio = new byte[audioLength];
                    InputStream is = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    DataInputStream dis = new DataInputStream(bis);

                    int i = 0;
                    while (dis.available() > 0) {
                        audio[i] = dis.readByte();
                        i++;
                    }
                    dis.close();
                    AudioTrack audioTrack = new AudioTrack(
                            AudioManager.STREAM_MUSIC, sample,
                            channelConfiguration_out, audioEncoding, audioLength,
                            AudioTrack.MODE_STREAM);
                    Log.d(TAG, "audio track init " + audioTrack.getState());
                    if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                        Log.d(TAG, "audio track init success");
                        audioTrack.play();
                        audioTrack.write(audio, 0, audio.length);
                    }
//                    Log.d(TAG, "audioTrack stop");
//                    audioTrack.stop();
//                    audioTrack.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 删除文件
    private void deleFile() {
        if (file == null) {
            return;
        }
        file.delete();
        printLog("文件删除成功");
    }
}
