package com.zy.audio.audioex;

import android.graphics.Path;
import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.zy.audio.audioex.controller.AudioRecordManager;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "zytest" ;

    private  Button mRecord = null ;
    private  Button mPlay = null ;
    private boolean mIsRecording = false ;
    private boolean mIsPlaying = false ;
    public static final String PATH = Environment.getExternalStorageDirectory().toString();

    private AudioRecordManager audioRecordManager = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        if (init() == -1 ) {
            Log.e(TAG,"init failed...") ;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // ======================
    private int init() {
        mRecord = (Button) findViewById(R.id.start_record) ;
        mPlay = (Button) findViewById(R.id.play);
        audioRecordManager =  AudioRecordManager.getAudioRecordManagerInstance() ;
        ViewClickListener buttonListener = new ViewClickListener() ;
        if (null == mRecord || null == mPlay || null == audioRecordManager || null == buttonListener) return -1 ;
        mRecord.setOnClickListener(buttonListener);
        mPlay.setOnClickListener(buttonListener);
        Log.i(TAG,"init finished");
        return 0 ;
    }

    private  class ViewClickListener implements  View.OnClickListener{
        /**
         * Called when a view has been clicked.
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.start_record:
                    try{
                        if (!mIsRecording) {
                            Log.i(TAG,"path = "+PATH +"start record ...") ;
                            audioRecordManager.stePath(PATH);
                            audioRecordManager.startThread();
                            mIsRecording = true ;
                            mRecord.setText(R.string.stop_record);
                        }else {
                            Log.i(TAG,"stop record ...") ;
                            audioRecordManager.stopRecord();
                            mIsRecording = false ;
                            mRecord.setText(R.string.start_record);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break ;
                case R.id.play:
                    break ;
                default :
                    break ;
            }

        }
    }

    public void startRecord(){
        // todo  use audioRecord to record aduio


    }




}
