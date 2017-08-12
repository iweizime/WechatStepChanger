package me.iweizi.wechatstepchanger;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by iweiz on 2017/8/2.
 * StepCounterCfg.java
 */

class StepCounterCfg {

    private enum RW {R, W}

    private class GetRW extends AsyncTask<File, Void, Void> {
        private Context mContext = null;
        private boolean mSuAvailable;
        private ProgressDialog mProgressDialog = null;
        private AlertDialog mAlertDialog = null;
        private RW rw;
        private File file;

        GetRW setContext(Context context) {
            mContext = context;
            return this;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setTitle(R.string.p_title);
            mProgressDialog.setMessage(mContext.getString(R.string.p_message));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(File... files) {
            if (files.length == 1) {
                rw = RW.R;
            } else {
                rw = RW.W;
            }
            file = files[0];
            mSuAvailable = Shell.SU.available();
            if (mSuAvailable) {
                Shell.SU.run("chmod o+rw " + file.getAbsolutePath());
                Shell.SU.run("chmod o+x " + file.getParent());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            mProgressDialog.dismiss();
            if (mSuAvailable) {
                if (rw == RW.R && file.canRead()) {
                    ((Activity)mContext).findViewById(R.id.load_button).performClick();
                } else if (rw == RW.W && file.canWrite()){
                    ((Activity)mContext).findViewById(R.id.store_button).performClick();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setTitle(R.string.a_title)
                        .setMessage(R.string.non_root)
                        .setCancelable(true);
                mAlertDialog = builder.create();
                mAlertDialog.show();
            }
        }
    }

    static final int CURRENT_TODAY_STEP = 201;
    static final int SAVE_TODAY_TIME = 202;
    static final int PRE_SENSOR_STEP = 203;
    static final int LAST_SAVE_STEP_TIME = 204;
    static final int SENSOR_TIME_STAMP = 209;

    static final int SUCCESS = 0;
    static final int FAIL = 1;
    static final int PENDING = 2;

    private static final String TAG = "StepCounterCfg";
    // private static final String STEP_COUNTER_CFG = "/data/local/tmp/stepcounter.cfg";
    private static final String STEP_COUNTER_CFG = "/data/data/com.tencent.mm/MicroMsg/stepcounter.cfg";
    private static final String WECHAT = "com.tencent.mm";
    private static final StepCounterCfg sStepCounterCfg = new StepCounterCfg();
    private HashMap<Integer, Long> mHashMap = null;

    private StepCounterCfg() {
    }

    static StepCounterCfg get() {
        return sStepCounterCfg;
    }

    HashMap getHashMap() {
        return mHashMap;
    }

    int loadCfg(Context context) {
        File f;
        FileInputStream fis;
        ObjectInputStream ois;

        try {
            f = new File(STEP_COUNTER_CFG);
            if (!f.canRead()) {
                (new GetRW()).setContext(context).execute(f);
                return PENDING;
            }
            fis = new FileInputStream(f);
            ois = new ObjectInputStream(fis);
            mHashMap = (HashMap<Integer, Long>) ois.readObject();
            ois.close();
            fis.close();
            return SUCCESS;
        } catch (Exception e) {
            Log.e(TAG, "Load Error: " + e.toString());
            return FAIL;
        }
    }

    int storeCfg(Context context) {
        File f;
        FileOutputStream fos;
        ObjectOutputStream oos;

        if (mHashMap == null) {
            return FAIL;
        }

        try {
            ActivityManager am = (ActivityManager)
                    context.getSystemService(Context.ACTIVITY_SERVICE);
            am.killBackgroundProcesses(WECHAT);
            f = new File(STEP_COUNTER_CFG);
            if (!f.canWrite()) {
                (new GetRW()).setContext(context).execute(f, f);
                return PENDING;
            }
            fos = new FileOutputStream(STEP_COUNTER_CFG);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(mHashMap);
            oos.close();
            fos.close();
            return SUCCESS;
        } catch (Exception e) {
            Log.e(TAG, "Store Error");
            return FAIL;
        }
    }

    void changeStep(long diff) {
        if (mHashMap == null) {
            return;
        }

        long newStep = mHashMap.get(CURRENT_TODAY_STEP) + diff;
        if (newStep >= 0) {
            mHashMap.put(CURRENT_TODAY_STEP, newStep);
        } else {
            mHashMap.put(CURRENT_TODAY_STEP, 0L);
        }

    }

    void setStep(long step) {
        if (mHashMap == null) {
            return;
        }

        mHashMap.put(CURRENT_TODAY_STEP, step);
    }
}
