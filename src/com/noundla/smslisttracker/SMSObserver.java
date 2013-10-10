package com.noundla.smslisttracker;


import java.util.ArrayList;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.noundla.smslisttracker.task.GetSMSListTask;
import com.noundla.smslisttracker.task.InsertSMSListTask;

public class SMSObserver extends ContentObserver {
	private Handler m_handler = null;
	private Activity mActivity;
	private long mLastInsertTime;
	public SMSObserver(Handler handler, Activity activity){
		super(handler);
		m_handler = handler;
		mActivity = activity;
	}

	@Override
	public void onChange(boolean selfChange) {
		
		if(System.currentTimeMillis()-mLastInsertTime > 3000){
			mLastInsertTime = System.currentTimeMillis();
			Log.d("Onchange:", selfChange+"");
			new GetSMSListTask(mActivity,false, new OnCompleteListener() {
				@Override
				public void onComplete(ArrayList<SMSMessage> smsList) {
					new InsertSMSListTask(mActivity, smsList,false, new OnCompleteListener() {

						@Override
						public void onComplete(ArrayList<SMSMessage> smsList) {
							Toast.makeText(mActivity, "Inserted", 500).show();
						}
					}).execute();

				}
			}).execute();
		}
		super.onChange(selfChange);
	}

}
