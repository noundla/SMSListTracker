package com.noundla.smslisttracker.task;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.SmsMessage;

import com.noundla.smslisttracker.OnCompleteListener;
import com.noundla.smslisttracker.SMSMessage;
import com.noundla.smslisttracker.projectconfig.Constants;
import com.noundla.smslisttracker.projectconfig.Util;

public class GetSMSListTask extends AsyncTask<Void, Void, ArrayList<SMSMessage>>{
	private ProgressDialog mDialog;
	private Activity mActivity;
	private OnCompleteListener mListener;
	private boolean mShowLodaer = true;
	public GetSMSListTask(Activity activity, OnCompleteListener listener) {
		this(activity, true, listener);

	}
	public GetSMSListTask(Activity activity, boolean showLoader, OnCompleteListener listener) {
		mActivity = activity;
		mListener = listener;
		mShowLodaer = showLoader;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(mShowLodaer){
			mDialog = ProgressDialog.show(mActivity, "", "Please wait...");
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.show();
		}
	}
	@Override
	protected ArrayList<SMSMessage> doInBackground(Void... params) {

		return getSmsListDetailsFromDeviceApp();
	}

	@Override
	protected void onPostExecute(ArrayList<SMSMessage> result) {
		super.onPostExecute(result);
		if(mDialog!=null && mDialog.isShowing()){
			mDialog.dismiss();
		}
		if(result!=null){
			mListener.onComplete(result);
		}
	}


	private ArrayList<SMSMessage> getSmsListDetailsFromDeviceApp() {
		ArrayList<SMSMessage> smsList = new ArrayList<SMSMessage>();
		Cursor managedCursor = null;
		try{
			long lastSMSDate = Util.getLongFromSP(mActivity, Constants.SP_LAST_SMS_DATE);

			Uri uriSMSURI = Uri.parse("content://sms");
			//fetch the messages which are sent and received
			managedCursor = mActivity.getContentResolver().query(uriSMSURI, null, " date > "+lastSMSDate +" AND ( type = 1 OR type = 2 )", null, null);

			mActivity.stopManagingCursor(managedCursor);

			int id = managedCursor.getColumnIndex("_id");
			int type = managedCursor.getColumnIndex( "type" );
			int date = managedCursor.getColumnIndex( "date");
			int threadID = managedCursor.getColumnIndex( "thread_id");
			int address = managedCursor.getColumnIndex("address");
			int body = managedCursor.getColumnIndex("body");

			if(managedCursor!=null && managedCursor.moveToFirst()){
				do {
					SMSMessage smsMessage = new SMSMessage();

					smsMessage.setId(managedCursor.getLong(id));
					smsMessage.setNumber(managedCursor.getString(address));
					smsMessage.setDate(Long.parseLong(managedCursor.getString(date)));
					smsMessage.setThreadID(Long.parseLong(managedCursor.getString(threadID)));
					smsMessage.setType(Integer.parseInt(managedCursor.getString(type)));
					int parts[] = SmsMessage.calculateLength(managedCursor.getString(body), true);//parts of a message will come this
					smsMessage.setSmsParts(parts[0]);
					smsList.add(smsMessage);
				}while ( managedCursor.moveToNext());
			}

			return smsList;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(managedCursor!=null){
				managedCursor.close();
				/*Warning: Do not call close() on a cursor obtained using this method, because the activity will do that for you at the appropriate time. 
				 * However, if you call stopManagingCursor(Cursor) on a cursor from a managed query, 
				 * the system will not automatically close the cursor and, in that case, you must call close().*/


			}
		}
		return null;
	}



}
