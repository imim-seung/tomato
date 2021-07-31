package com.example.tomato;

import android.os.CountDownTimer;
import android.widget.TextView;

class MyTimer extends CountDownTimer
{

   TextView textView;

    public MyTimer(long millisInFuture, long countDownInterval)
    {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        textView.setText(millisUntilFinished/1000 + " 초");
    }

    @Override
    public void onFinish() {
        textView.setText("0 초");
    }
}