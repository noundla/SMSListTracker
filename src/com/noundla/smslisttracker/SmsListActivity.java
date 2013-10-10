package com.noundla.smslisttracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.noundla.smslisttracker.db.SMSListDBHelper;
import com.noundla.smslisttracker.projectconfig.Constants;
import com.noundla.smslisttracker.projectconfig.Util;
import com.noundla.smslisttracker.task.GetSMSListTask;
import com.noundla.smslisttracker.task.InsertSMSListTask;

public class SmsListActivity extends Activity {
	
	private Activity mActivity;
	
	private int mSelectedListType;
	private int mSmsType;
	
	private long mEndDateInMillis;
	private long mStartDateInMillis;
	
	private TextView mTotalUnits;
	private ListView mListview;
	private TextView textView1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.sms_list_screen);
		mActivity = this;
		
		mListview = (ListView)findViewById(R.id.listView1);
		mTotalUnits = (TextView)findViewById(R.id.unitsTV);
		loadIntent(getIntent());
		SMSObserver observer = new SMSObserver(mHandler,this);  
        // REGISTER ContetObserver 
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, observer);
        
//        retriveSentSmsFromDefaultApp();
        
        new GetSMSListTask(this, new OnCompleteListener() {
			
			@Override
			public void onComplete(ArrayList<SMSMessage> smsList) {
				new InsertSMSListTask(mActivity, smsList, new OnCompleteListener() {
					
					@Override
					public void onComplete(ArrayList<SMSMessage> smsList) {
						new FetchSMSListTask().execute();
					}
				}).execute();
				
			}
		}).execute();
	}
	
	
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
		}
		
	};

	private void loadIntent(Intent intent){
		if(intent!=null){
			if(intent.hasExtra(DateSelectionActivity.EXTRA_START_DATE_IN_MILLIS)){
				mStartDateInMillis  = intent.getLongExtra(DateSelectionActivity.EXTRA_START_DATE_IN_MILLIS, -1);
			}
			if(intent.hasExtra(DateSelectionActivity.EXTRA_END_DATE_IN_MILLIS)){
				mEndDateInMillis  = intent.getLongExtra(DateSelectionActivity.EXTRA_END_DATE_IN_MILLIS, -1);
			}
			if(intent.hasExtra(DateSelectionActivity.EXTRA_SELECTED_LIST_TYPE)){
				mSelectedListType  = intent.getIntExtra(DateSelectionActivity.EXTRA_SELECTED_LIST_TYPE, -1);
			}
			if(intent.hasExtra(DateSelectionActivity.EXTRA_SMS_TYPE)){
				mSmsType  = intent.getIntExtra(DateSelectionActivity.EXTRA_SMS_TYPE, Constants.MESSAGE_TYPE_SENT);
			}
			
			
		}
	}
	
	private void retriveSentSmsFromDefaultApp(){
		Uri uriSMSURI = Uri.parse("content://sms");
//		String[] FROM = { "_id", "type", "date", "thread_id", "address","length(body) as sms_lenth"};
		Cursor cur = getContentResolver().query(uriSMSURI, null, null , null, null);
		int id = cur.getColumnIndex("_id");
		int type = cur.getColumnIndex( "type" );
		int date = cur.getColumnIndex( "date");
		int threadID = cur.getColumnIndex( "thread_id");
		int address = cur.getColumnIndex("address");
		int body = cur.getColumnIndex("body");
		if(cur!=null && cur.moveToFirst()){
			do {
				int[] ar = SmsMessage.calculateLength(cur.getString(body),true);
				textView1.append("id: "+cur.getInt(id)+ " \t address: "+cur.getString(address)+"\t msg length: "+ar[0] +"\nthread_id: "+cur.getString(threadID)+" \t type: "+cur.getString(type)+"\ndate: "+getStringFromMillis(Constants.SMS_DATE_FORMAT,cur.getLong(date)));
				textView1.append("\n====================\n");
				
				
			}while ( cur.moveToNext());
		}
	}
	
	public static String getStringFromMillis(String pattern, long timeInMillis){
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern,Locale.ENGLISH);
			Date date = new Date(timeInMillis);
			return dateFormat.format(date);
		}catch(Exception e){
			return null;
		}

	}
	
	
//	class MyContentObserver extends ContentObserver { 
//
//	    public MyContentObserver(Handler handler) { 
//
//	        super(handler); 
//
//	    }
//
//	@Override public boolean deliverSelfNotifications() { 
//	    return false; 
//	    }
//
//	@Override public void onChange(boolean arg0) { 
//	    super.onChange(arg0);
//
//	     Log.v("SMS", "Notification on SMS observer"); 
//
//	    Message msg = new Message(); 
//	    msg.obj = "xxxxxxxxxx";
//
//	    handler.sendMessage(msg);
//
//	    Uri uriSMSURI = Uri.parse("content://sms/");
//	    Cursor cur = getContentResolver().query(uriSMSURI, null, null,
//	                 null, null);
//	    cur.moveToNext();
//	    String protocol = cur.getString(cur.getColumnIndex("protocol"));
//	    if(protocol == null){
//	           Log.d("SMS", "SMS SEND"); 
//	           int threadId = cur.getInt(cur.getColumnIndex("thread_id"));
//
//	           Log.d("SMS", "SMS SEND ID = " + threadId); 
//	           Cursor c = getContentResolver().query(Uri.parse("content://sms/outbox/" + threadId), null, null,
//	                   null, null);
//	           c.moveToNext();
//	           int p = cur.getInt(cur.getColumnIndex("person"));
//	           Log.d("SMS", "SMS SEND person= " + p); 
//	           //getContentResolver().delete(Uri.parse("content://sms/conversations/" + threadId), null, null);
//
//	    }
//	    else{
//	        Log.d("SMS", "SMS RECIEVE");  
//	         int threadIdIn = cur.getInt(cur.getColumnIndex("thread_id"));
//
//	         getContentResolver().delete(Uri.parse("content://sms/conversations/" + threadIdIn), null, null);
//	    }
//
//	 }


	
	private class FetchSMSListTask extends AsyncTask<Void, Void, SMSListDetails>{
		private ProgressDialog mDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(mDialog==null || !mDialog.isShowing()){
				mDialog = ProgressDialog.show(mActivity, "", "Please wait...");
				mDialog.setCanceledOnTouchOutside(false);
				mDialog.show();
			}
		}

		@Override
		protected SMSListDetails doInBackground(Void... params) {
			try{
				SMSListDBHelper dbHelper = new SMSListDBHelper(mActivity);
				return dbHelper.getCallList(mStartDateInMillis,mEndDateInMillis,mSelectedListType,mSmsType);
			}catch(Exception e){
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(SMSListDetails result) {
			super.onPostExecute(result);
			if(result!=null){
				Util.saveLongInSP(mActivity, Constants.SP_LAST_SMS_DATE, result.getLatestSMSDate());
				mTotalUnits.setText(result.getTotalUnits()+"");
				SMSListAdapter adapter = new SMSListAdapter(result.getSMSList());
				mListview.setAdapter(adapter);
			}

			if(mDialog!=null && mDialog.isShowing()){
				mDialog.dismiss();
			}


		}

	}
	private class SMSListAdapter extends BaseAdapter{
		private ArrayList<SMSMessage> mSmsList;
		public SMSListAdapter(ArrayList<SMSMessage> smsList) {
			mSmsList = smsList;
		}
		@Override
		public int getCount() {
			return mSmsList==null?0:mSmsList.size();
		}

		@Override
		public SMSMessage getItem(int position) {
			return mSmsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SMSMessage smsInfo = mSmsList.get(position);

			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.log_item, null);
			}
			TextView number = (TextView)convertView.findViewById(R.id.number);
			TextView smsCount = (TextView)convertView.findViewById(R.id.smscount);
			TextView date = (TextView)convertView.findViewById(R.id.date);
			TextView type = (TextView)convertView.findViewById(R.id.type);
			
			smsCount.setText("Pages: "+smsInfo.getSmsParts());
			number.setText(smsInfo.getPersonName());
			date.setText(Util.getStringFromMillis(Constants.SMS_DATE_FORMAT, smsInfo.getDate()));
			switch (smsInfo.getType()) {
			case Constants.MESSAGE_TYPE_INBOX:
				type.setText("Received");
				type.setTextColor(Color.GREEN);
				break;
			case Constants.MESSAGE_TYPE_SENT:
				type.setText("Sent");
				type.setTextColor(Color.BLUE);
				break;
			

			default:
				break;
			}
			return convertView;
		}
	}
	
}
