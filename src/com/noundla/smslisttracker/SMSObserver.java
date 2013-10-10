package com.noundla.smslisttracker;


import java.util.ArrayList;

import com.noundla.smslisttracker.projectconfig.Constants;
import com.noundla.smslisttracker.task.GetSMSListTask;
import com.noundla.smslisttracker.task.InsertSMSListTask;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.widget.Toast;

public class SMSObserver extends ContentObserver {
	
	
	
	private Handler m_handler = null;
	private Activity mActivity;
	public SMSObserver(Handler handler, Activity activity){
		super(handler);
		m_handler = handler;
		mActivity = activity;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		new GetSMSListTask(mActivity,false, new OnCompleteListener() {
			@Override
			public void onComplete(ArrayList<SMSMessage> smsList) {
				new InsertSMSListTask(mActivity, smsList,false, new OnCompleteListener() {
					
					@Override
					public void onComplete(ArrayList<SMSMessage> smsList) {
						Toast.makeText(mActivity, "Inserted", 500).show();
					}
				});
				
			}
		});
	}
}
