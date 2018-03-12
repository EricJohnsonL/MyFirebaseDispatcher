package com.ericjohnson.myfirebasedispatcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnSetScheduler, btnCancelScheduler;
    private String DISPATCHER_TAG = "mydispatcher";
    private String CITY = "Jakarta";
    FirebaseJobDispatcher dispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCancelScheduler = findViewById(R.id.btn_cancel_scheduler);
        btnSetScheduler = findViewById(R.id.btn_set_scheduler);
        btnSetScheduler.setOnClickListener(this);
        btnCancelScheduler.setOnClickListener(this);
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_set_scheduler) {
            startDispatcher();
            Toast.makeText(this, "Dispatcher Created", Toast.LENGTH_SHORT).show();
        }

        if (v.getId() == R.id.btn_cancel_scheduler) {
            cancelDispatcher();
            Toast.makeText(this, "Dispatcher Cancelled", Toast.LENGTH_SHORT).show();
        }
    }


    public void startDispatcher() {
        Bundle myExtrasBundle = new Bundle();
        myExtrasBundle.putString(MyJobService.EXTRAS_CITY, CITY);

        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class) // kelas service yang akan dipanggil
                .setTag(DISPATCHER_TAG) // unique tag untuk identifikasi job
                .setRecurring(true) // one-off job, true job tersebut akan diulang, dan false job tersebut tidak diulang
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT) // until_next_boot berarti hanya sampai next boot,  forever berarti akan berjalan meskipun sudah reboot
                .setTrigger(Trigger.executionWindow(0, 60)) // waktu trigger 0 sampai 60 detik
                .setReplaceCurrent(true)  // overwrite job dengan tag sama
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL) // set waktu kapan akan dijalankan lagi jika gagal
                .setConstraints( // set kondisi dari device
                        Constraint.ON_UNMETERED_NETWORK,
                        Constraint.ON_ANY_NETWORK,
                        Constraint.DEVICE_IDLE
                )
                .setExtras(myExtrasBundle)
                .build();
        dispatcher.mustSchedule(myJob);
    }

    public void cancelDispatcher(){
        dispatcher.cancel(DISPATCHER_TAG);
    }


}
