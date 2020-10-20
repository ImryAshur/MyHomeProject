package com.example.project.Others;
/*
    Student - Imry Ashur
*/
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

public class MySignalV2 {

    private static MySignalV2 instance;
    private static Context appContext;

    public static MySignalV2 getInstance() {
        return instance;
    }

    private MySignalV2(Context context) {
        appContext = context;
    }

    public static MySignalV2 initHelper(Context context) {
        if (instance == null)
            instance = new MySignalV2(context);
        return instance;
    }

    public void showToast(final String message) {
        // If we put it into handler - we can call in from asynctask outside of main uithread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void makeSound(int sound) {
        MediaPlayer mediaPlayer = MediaPlayer.create(appContext, sound);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer arg0) {
                arg0.release();
            }
        });
    }

    public void touched() {
        vibrate(10);
    }

    public void vibrate(long milliSeconds) {
        Vibrator v = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(milliSeconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(milliSeconds);
        }
    }

    public void vibratePattern(int numOfWaves) {
        Vibrator v = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);

        //long[] pattern = {0, 250, 250, 250, 250, 250};
        long[] pattern = new long[2 * numOfWaves];
        pattern[0] = 0;
        for (int i = 1; i < pattern.length; i++) {
            pattern[i] = 330;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(pattern, -1));
        } else {
            //deprecated in API 26
            v.vibrate(pattern, -1);
        }
    }

    public void vibratePattern(long[] pattern) {
        Vibrator v = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(pattern, -1));
        } else {
            //deprecated in API 26
            v.vibrate(pattern, -1);
        }
    }


    private ProgressDialog mProgressDialog;

    //For start progrss dialog
    public void nbStartDialog(Context activityContext, String message, boolean isCancelable) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        mProgressDialog = new ProgressDialog(activityContext);
        mProgressDialog.setCancelable(isCancelable);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();

//        TextView dialog_title = (TextView) dialog.findViewById(R.id.text_dialog_title);
//        dialog_title.setText(mVal);
    }

    //For close dialog
    public void nbCloseDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
}
