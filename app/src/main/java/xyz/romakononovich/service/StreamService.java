package xyz.romakononovich.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;


public class StreamService extends Service {
    private static final String TAG = StreamService.class.getSimpleName();
    private static final String SOURCE = "http://uk3.internet-radio.com:11128";
    private IBinder binder = new PlayerBinder();
    private StreamService object;
    public boolean isPlaying;
    public static final String ACTION_PLAY = "action_play";
    private MediaPlayer mediaPlayer;
    private static final int ID = 1;
    private static final int REQUESTID = 1;
    public StreamService() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(SOURCE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent==null) {
            Log.d(TAG,"service started with null");
        } else if(intent.getAction()==null) {
            Log.d(TAG,"Action NULL!!");
        } else if(intent.getAction().equals(ACTION_PLAY)) {
            if(!isPlaying){
                new StartPlayTask().execute();

            }
            else {
                mediaPlayer.stop();
                isPlaying=false;
                stopForeground(true);
            }
            Log.d(TAG, "action: "+ intent.getAction());
        }
        return super.onStartCommand(intent, flags, startId);
    }
    public void startPlay() {
        Log.d(TAG, "something");
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),REQUESTID, new Intent(getApplicationContext(),MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setAutoCancel(false);
            builder.setContentTitle("Title");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentText("Now play");
            builder.setContentIntent(pendingIntent);
            startForeground(ID,builder.build());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class PlayerBinder extends Binder{
      public StreamService getService() {
          Log.d(TAG, Boolean.toString(StreamService.this == object));
          return StreamService.this;
      }
    }

    private class StartPlayTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            startPlay();
            return null;
        }
    }
}
