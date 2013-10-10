package com.noundla.smslisttracker.task;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.noundla.smslisttracker.OnCompleteListener;
import com.noundla.smslisttracker.SMSMessage;
import com.noundla.smslisttracker.db.SMSListDBHelper;

public class InsertSMSListTask extends AsyncTask<Void, Void, Void>{
	private ProgressDialog mDialog;
	private ArrayList<SMSMessage> mSMSList;
	private Activity mActivity;
	private OnCompleteListener mListener;
	private boolean mShowLoader = true;
	public InsertSMSListTask(Activity activity,ArrayList<SMSMessage> smsList, OnCompleteListener listener) {
		this(activity, smsList, true, listener);
	}
	public InsertSMSListTask(Activity activity,ArrayList<SMSMessage> smsList, boolean showLoader,OnCompleteListener listener) {
		mSMSList = smsList;
		mActivity = activity;
		mListener = listener;
		mShowLoader = showLoader;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(mShowLoader){
			if(mDialog==null || !mDialog.isShowing()){
				mDialog = ProgressDialog.show(mActivity, "", "Please wait...");
				mDialog.setCanceledOnTouchOutside(false);
				mDialog.show();
			}
		}
	}
	@Override
	protected Void doInBackground(Void... params) {
		if(mSMSList!=null){
			try{
				SMSListDBHelper dbHelper = new SMSListDBHelper(mActivity);
				//				dbHelper.insertBulkCallList(mSMSList);
				dbHelper.bulkInsertData(mSMSList);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if(mDialog!=null && mDialog.isShowing()){
			mDialog.dismiss();
		}
		mListener.onComplete(null);

		//		new FetchCalllListTask().execute();

	}

}
