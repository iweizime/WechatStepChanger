package me.iweizi.wechatstepchanger;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "WechatStepChanger";
    private static final int COUNTER_DIFF = 1000;

    private Button mLoadButton;
    private Button mStoreButton;
    private Button mDetailsButton;

    private EditText mCurrentTodayStepEditText;
    private ImageButton mStepIncImageButton;
    private ImageButton mStepDecImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadButton = (Button) findViewById(R.id.load_button);
        mLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StepCounterCfg.get().loadCfg(MainActivity.this)) {
                    updateUI();
                    Toast.makeText(MainActivity.this, R.string.loaded, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.load_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mStoreButton = (Button) findViewById(R.id.store_button);
        mStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StepCounterCfg.get().storeCfg(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, R.string.stored, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.store_failed, Toast.LENGTH_SHORT).show();
                }

            }
        });

        mCurrentTodayStepEditText = (EditText) findViewById(R.id.current_today_step_edit_text);
        mCurrentTodayStepEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    s = "0";
                }
                StepCounterCfg.get().setStep(Integer.valueOf(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mStepIncImageButton = (ImageButton) findViewById(R.id.current_today_step_inc_image_button);
        mStepIncImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StepCounterCfg.get().changeStep(COUNTER_DIFF);
                updateUI();
            }
        });

        mStepDecImageButton = (ImageButton) findViewById(R.id.current_today_step_dec_image_button);
        mStepDecImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StepCounterCfg.get().changeStep(-COUNTER_DIFF);
                updateUI();
            }
        });

        mDetailsButton = (Button) findViewById(R.id.details_button);
        mDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
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
            mCurrentTodayStepEditText.setText("");
            return;
        }

        mCurrentTodayStepEditText.setText(StepCounterCfg.get().getHashMap().get(StepCounterCfg.CURRENT_TODAY_STEP).toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }
}
