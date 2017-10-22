package com.zy.audio.audioex.controller;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2017/10/22.
 */

public class AudioRecordManager {
    public static final String TAG = "zytest" ;
    private static final int SAMPLE_RATE =16000 ;
    private  int bufferSize  ;
    private AudioRecord mRecorder ;
    private DataOutputStream dos ;
    private Thread recordThread ;
    private boolean isStart = false ;
    private static AudioRecordManager mInstance ;

    public AudioRecordManager() {
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT) ;
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,SAMPLE_RATE,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,bufferSize*2) ;
        Log.i(TAG,"bufferSize = "+bufferSize);
    }

    public static AudioRecordManager getAudioRecordManagerInstance() {
        if (null == mInstance) {
            synchronized (AudioRecordManager.class){
                if (mInstance == null) {
                    mInstance = new AudioRecordManager() ;
                }
            }
        }
        return mInstance ;
    }
    /**
     * 保存文件
     * @param path 文件路径
     * @throws Exception
     */
    public void stePath(String path) throws  Exception{
        File file = new File(path+"audio.raw") ;
        Log.i("zytest","file = "+file.getAbsolutePath());
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            dos = new DataOutputStream(new FileOutputStream(file, true));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 启动录音线程
     */
    public  void startThread(){
        destroyThread();
        isStart = true ;
        if (recordThread == null) {
            recordThread = new Thread(recordRunnable);
            recordThread.start();
        }
    }
    /**
     * stop record
     */
    public void stopRecord() {
        try{
            destroyThread();
            if(mRecorder != null){
                if (mRecorder.getState() == AudioRecord.STATE_INITIALIZED){
                    mRecorder.stop();
                }
                if (mRecorder != null) {
                    mRecorder = null ;
                }
                if (dos != null) {
                    dos.flush();
                    dos.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * recording thread
     */
    Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            try{
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                int bytesRecord ;
                byte[] tempBuffer = new byte[bufferSize];
                if (mRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
                    Log.i(TAG,"state is not initialiazed");
                    stopRecord() ;
                    return ;
                }
                mRecorder.startRecording();
                while (isStart){
                    if (null != mRecorder) {
                        bytesRecord = mRecorder.read(tempBuffer,0,bufferSize);
                        Log.i(TAG,"byteRecord = "+bytesRecord) ;
                        if (bytesRecord == AudioRecord.ERROR_BAD_VALUE || bytesRecord== AudioRecord.ERROR_INVALID_OPERATION) {
                            Log.i(TAG,"state is not initialiazed");
                            continue;
                        }
                        if (bytesRecord != 0 && bytesRecord != -1) {
                            // 原始pcm数据，对数据进行处理，变声，压缩，降噪，增益等.处理后实时传送还需编解码发送。
                            // 暂时将pcm数据写入文件，可使用audioTrack 播放原始数据。
                            Log.i(TAG,"writdata to file");
                            dos.write(tempBuffer,0,bytesRecord);
                        }
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    /**
     * 销毁线程
     */
    private void destroyThread() {
        try{
            isStart = false ;
            if (null != recordThread && Thread.State.RUNNABLE == recordThread.getState()) {
                try{
                    Thread.sleep(500);
                    recordThread.interrupt();
                }catch (Exception e){
                    recordThread = null ;
                }
            }
            recordThread = null  ;

        }catch(Exception e ){
            e.printStackTrace();
        }finally {
            recordThread = null ;
        }
    }




}
