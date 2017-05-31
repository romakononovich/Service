package xyz.romakononovich.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private static final String TAG = MainActivity.class.getSimpleName();
    private long ServiceBindTime;
    private boolean isBound;
    private IBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"service connection "+(System.currentTimeMillis()-ServiceBindTime));
            isBound = true;
            binder = service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"service disconnection");
            isBound = false;
            binder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((StreamService.PlayerBinder) binder).getService().startPlay();
                Intent intent = new Intent(MainActivity.this, StreamService.class);
                intent.setAction(StreamService.ACTION_PLAY);
                startService(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"OnStart");
        Intent intent = new Intent(this, StreamService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
        ServiceBindTime = System.currentTimeMillis();
        Log.d(TAG, "Binding Service");
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume"+System.currentTimeMillis());
    }
}
