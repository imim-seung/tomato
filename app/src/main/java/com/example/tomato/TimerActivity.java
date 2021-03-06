package com.example.tomato;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TimerActivity extends AppCompatActivity {
    final static String TAG = "timeRecordDB";
    private Button startBtn, stopBtn, recordBtn;
    private TextView timeTextView, today, tomatoCount;
    private Thread timeThread = null;
    private Boolean isRunning = true;
    String duplicateDate, afterTime;
    int beforeCount;
    SQLiteDatabase db;
    TomatoRecordOpenHelper helper;
    LinearLayout randomLayout;
    AlertDialog.Builder dlg;
    Tomato todayTomato;
    MyTimer myTimer;
    TimerTask startTask, stopTask;
    Timer startTimer = new Timer();
    Timer stopTimer = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        todayTomato = new Tomato();
        startBtn = findViewById(R.id.btn_start);
        stopBtn = findViewById(R.id.btn_stop);
        recordBtn = findViewById(R.id.btn_record);
//        pauseBtn = findViewById(R.id.btn_pause);
        timeTextView = findViewById(R.id.timeView);
        tomatoCount = findViewById(R.id.tomato_count);
        today = findViewById(R.id.today);
        helper = new TomatoRecordOpenHelper(this);
        setCalendar(today);
        myTimer = new MyTimer(1500000, 1000);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "start");
                view.setVisibility(View.GONE);
                stopBtn.setVisibility(View.VISIBLE);
                recordBtn.setVisibility(View.VISIBLE);
//                pauseBtn.setVisibility(View.VISIBLE);
                startTimerTask();

            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "stop");
                v.setVisibility(View.GONE);
                recordBtn.setVisibility(View.GONE);
                startBtn.setVisibility(View.VISIBLE);
//                pauseBtn.setVisibility(View.GONE);
                stopTimerTask();
            }
        });

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                // ??????????????? ????????? ????????????????????? ????????? ????????? ????????????.
                db = helper.getWritableDatabase();
                if (db == null) {
                    Log.d(TAG, "db??? ?????? ???????????????");
                    return;
                }
                try {
                    duplicateDate = null;
                    Cursor c = db.query("tomatoRecord", new String[]{"_id", "count", "rdate"}, null, null, null, null, null);
                    Log.d(TAG, "db ??????");
                    while (c.moveToNext()) {
//                        if (c.getString(c.getColumnIndex("rdate")).equals(today.getText().toString())) {
//                            duplicateDate = c.getString(c.getColumnIndex("rdate"));
//                            beforeTime = c.getString(c.getColumnIndex("rtime"));
//                        }
                        if (c.getString(2).equals(today.getText().toString())) {
                            duplicateDate = c.getString(2);
                            beforeCount = c.getInt(1);
                        }
                    }
                    if (duplicateDate == null) {
                        db.execSQL(String.format(Locale.KOREA,"insert into timeRecord(count,rdate) values ('%d' , '%s');",
                                todayTomato.count, today.getText().toString()));
                        Log.d(TAG, "????????? ?????????.");


                    } else {
                        Log.d(TAG, "?????? ????????? ??????.");
                        todayTomato.count+=beforeCount;

                        Log.d(TAG, "exception ??????111");

                        String updateSql = String.format(Locale.KOREA,"update timeRecord set count = \"%d\" where rdate =\"%s\"",
                                todayTomato, today.getText().toString());
                        db.execSQL(updateSql);
                        Log.d(TAG, "????????? ?????????.");
                    }
                    db.close();
                    c.close();
                    setResult(RESULT_OK);
                    finish();
                } catch (NullPointerException e2) {
                    Log.d(TAG, "getTime ??????");
                } catch (Exception e) {
                    Log.d(TAG, "insert ??????");
                }


            }
        });



    }


    private void startTimerTask() {
        stopTimerTask();
        startTask = new TimerTask() {
            int count = 5;

            @Override
            public void run() {
                if (count > 0) {
                    count--;
                    timeTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            timeTextView.setText(count / 60 + ":" + count % 60);
                        }
                    });
                } else {
                    todayTomato.count++;
                    stopTimerTask();
                    fiveMinuteBreak(0);
                    isRunning = true;

                }
            }

        };


        startTimer.schedule(startTask, 0, 1000);
//        stopTimer.schedule(stopTask,0,1000);


    }


    private void stopTimerTask() {
        if (startTask != null) {
            timeTextView.setText("25:00");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tomatoCount.setText(String.valueOf(todayTomato.count));
                }
            });
            startTask.cancel();
            startTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        startTimer.cancel();
        super.onDestroy();
    }

    public void fiveMinuteBreak(final int status) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(TimerActivity.this);
                                    if (status == 0) {
                                        builder.setTitle("5??? ??????").setMessage("??????????????? 5?????? ?????????.");
                                    } else {
                                        builder.setTitle("????????????").setMessage("?????? ????????? ???????????? ????????????");
                                    }

                                    //?????? ??????
                                    builder.setPositiveButton("?????? ??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                            startTimerTask();
                                            isRunning = true;
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();

                                    alertDialog.show();

                                }
                            }
                , 0);

    }


    public void setCalendar(TextView tv) {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        String now = year + "-" + month + "- " + day;
        tv.setText(now);
    }


//    @SuppressLint("HandlerLeak")
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            int mSec = msg.arg1 % 100;
//            int sec = (msg.arg1 / 100) % 60;
//            int min = (msg.arg1 / 100) / 60;
//            int hour = (msg.arg1 / 100) / 360;
//            //1000??? 1??? 1000*60 ??? 1??? 1000*60*10??? 10??? 1000*60*60??? ?????????
//
//            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);
//            timeTextView.setText(result);
//        }
//    };

//    public class TimeThread implements Runnable {
//        @Override
//        public void run() {
//            int i = 25;
//
//            while (true) {
//                while (isRunning) { //??????????????? ????????? ??????
//                    Message msg = new Message();
//                    msg.arg1 = 00;
//                    handler.sendMessage(msg);
//
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                timeTextView.setText("");
//                                timeTextView.setText("00:00:00:00");
//                            }
//                        });
//                        return; // ???????????? ?????? ?????? return
//                    }
//                }
//            }
//        }
//    }
}
