package me.iweizi.wechatstepchanger;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.ChineseCalendar;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class DetailsActivity extends AppCompatActivity {


    private Button mLoadButton;
    private Button mBackButton;
    private Button mStoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mLoadButton = (Button) findViewById(R.id.load_button);
        mLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int result = StepCounterCfg.get().loadCfg(DetailsActivity.this);
                if (result == StepCounterCfg.SUCCESS) {
                    updateUI();
                    Toast.makeText(DetailsActivity.this, R.string.loaded, Toast.LENGTH_SHORT).show();
                } else if (result == StepCounterCfg.FAIL) {
                    Toast.makeText(DetailsActivity.this, R.string.load_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBackButton = (Button) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mStoreButton = (Button) findViewById(R.id.store_button);
        mStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int result = StepCounterCfg.get().storeCfg(DetailsActivity.this);
                if (result == StepCounterCfg.SUCCESS) {
                    Toast.makeText(DetailsActivity.this, R.string.stored, Toast.LENGTH_SHORT).show();
                } else if (result == StepCounterCfg.FAIL){
                    Toast.makeText(DetailsActivity.this, R.string.store_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (StepCounterCfg.get().getHashMap() == null) {
            return;
        }
        setText(R.id.current_today_step_text_view, StepCounterCfg.CURRENT_TODAY_STEP);

        long saveTodayTime = (long) StepCounterCfg.get().getHashMap()
                .get(StepCounterCfg.SAVE_TODAY_TIME);
        StringBuilder builder = new StringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
            Calendar calendar = ChineseCalendar.getInstance();

            calendar.setTimeInMillis(saveTodayTime * 10000);
            builder.append(dateFormat.format(calendar.getTime()));
            builder.append("\n");
        }
        builder.append(saveTodayTime);
        ((TextView) findViewById(R.id.save_today_time_text_view))
                .setText(builder.toString());


        setText(R.id.pre_sensor_step_text_view, StepCounterCfg.PRE_SENSOR_STEP);

        long lastSaveStepTime = (long) StepCounterCfg.get().getHashMap()
                .get(StepCounterCfg.LAST_SAVE_STEP_TIME);
        builder = new StringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
            Calendar calendar = ChineseCalendar.getInstance();
            calendar.setTimeInMillis(lastSaveStepTime);
            builder.append(dateFormat.format(calendar.getTime()));
            builder.append("\n");
        }
        builder.append(lastSaveStepTime);
        ((TextView) findViewById(R.id.last_save_step_time_text_view))
                .setText(builder.toString());

        setText(R.id.sensor_time_stamp_text_view, StepCounterCfg.SENSOR_TIME_STAMP);

    }

    private void setText(int view_id, int data_id) {
        TextView textView;

        textView = (TextView) findViewById(view_id);
        textView.setText(StepCounterCfg.get().getHashMap().get(data_id).toString());
    }

}
