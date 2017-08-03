package me.iweizi.wechatstepchanger;

import android.app.ActivityManager;
import android.content.Context;
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

    static final int CURRENT_TODAY_STEP = 201;
    static final int SAVE_TODAY_TIME = 202;
    static final int PRE_SENSOR_STEP = 203;
    static final int LAST_SAVE_STEP_TIME = 204;
    static final int SENSOR_TIME_STAMP = 209;
    private static final String TAG = "StepCounterCfg";
    // private static final String STEP_COUNTER_CFG = "/data/local/tmp/stepcounter.cfg";
    private static final String STEP_COUNTER_CFG = "/data/data/com.tencent.mm/MicroMsg/stepcounter.cfg";
    private static final String WECHAT = "com.tencent.mm";
    private static final StepCounterCfg sStepCounterCfg = new StepCounterCfg();
    private HashMap<Integer, Integer> mHashMap = null;

    private StepCounterCfg() {
    }

    static StepCounterCfg get() {
        return sStepCounterCfg;
    }

    HashMap getHashMap() {
        return mHashMap;
    }

    void loadCfg(Context context) {
        File f;
        FileInputStream fis;
        ObjectInputStream ois;

        try {
            f = new File(STEP_COUNTER_CFG);
            if (!f.canRead() && Shell.SU.available()) {
                Shell.SU.run("chmod o+rw " + f.getAbsolutePath());
                Shell.SU.run("chmod o+x " + f.getParent());
            }
            fis = new FileInputStream(f);
            ois = new ObjectInputStream(fis);
            mHashMap = (HashMap<Integer, Integer>) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            Log.e(TAG, "Load Error: " + e.toString());
        }
    }

    void storeCfg(Context context) {
        File f;
        FileOutputStream fos;
        ObjectOutputStream oos;

        if (mHashMap == null) {
            return;
        }

        try {
            ActivityManager am = (ActivityManager)
                    context.getSystemService(Context.ACTIVITY_SERVICE);
            am.killBackgroundProcesses(WECHAT);
            f = new File(STEP_COUNTER_CFG);
            if (!f.canRead() && Shell.SU.available()) {
                Shell.SU.run("chmod o+rw " + f.getAbsolutePath());
                Shell.SU.run("chmod o+x" + f.getParent());
            }
            fos = new FileOutputStream(STEP_COUNTER_CFG);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(mHashMap);
            oos.close();
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "Store Error");
        }
    }

    void changeStep(int diff) {
        if (mHashMap == null) {
            return;
        }

        int newStep = (int) mHashMap.get(CURRENT_TODAY_STEP) + diff;
        if (newStep >= 0) {
            mHashMap.put(CURRENT_TODAY_STEP, newStep);
        } else {
            mHashMap.put(CURRENT_TODAY_STEP, 0);
        }

    }

    void setStep(int step) {
        if (mHashMap == null) {
            return;
        }

        mHashMap.put(CURRENT_TODAY_STEP, step);
    }
}
