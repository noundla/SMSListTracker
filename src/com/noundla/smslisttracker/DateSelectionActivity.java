package com.noundla.smslisttracker;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;

import com.noundla.smslisttracker.DateDialogFragment.OnDateSetListener;
import com.noundla.smslisttracker.projectconfig.Constants;
import com.noundla.smslisttracker.projectconfig.Util;



public class DateSelectionActivity extends FragmentActivity implements OnClickListener{
	public static final String EXTRA_START_DATE_IN_MILLIS="EXTRA_START_DATE";
	public static final String EXTRA_END_DATE_IN_MILLIS="EXTRA_END_DATE";
	public static final String EXTRA_SELECTED_LIST_TYPE="EXTRA_SELECTED_LIST_TYPE";
	public static final String EXTRA_SMS_TYPE="EXTRA_SMS_TYPE";
	
	
	
	
	private EditText mStartDateEt;
	private EditText mEndDateEt;
	private Activity mActivity;
	
	/**stores the date to show on date picker*/
	protected Calendar mStartDate=Calendar.getInstance();
	
	protected Calendar mEndDate=Calendar.getInstance();
	
	private RadioButton mAllRB;
	private RadioButton mMobilePhonesRB;
	private RadioButton mFixedPhonesRB;
	private RadioButton mExclude2And3RB;
	
	
	private RadioButton mAllTypesRB;
	private RadioButton mSentRB;
	private RadioButton mReceivedRB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.date_selection);
		mActivity = this;
		
		mStartDateEt = (EditText)findViewById(R.id.startDateET);
		mStartDateEt.setOnClickListener(this);
		
		mEndDateEt = (EditText)findViewById(R.id.endDateET);
		mEndDateEt.setOnClickListener(this);
		
		mAllRB = (RadioButton)findViewById(R.id.all);
		mMobilePhonesRB = (RadioButton)findViewById(R.id.mobile_phones);
		mFixedPhonesRB = (RadioButton)findViewById(R.id.fixed_phones);
		mExclude2And3RB = (RadioButton)findViewById(R.id.exclude_above_two);
		
		mAllTypesRB = (RadioButton)findViewById(R.id.all_types);
		mSentRB = (RadioButton)findViewById(R.id.sent);
		mReceivedRB = (RadioButton)findViewById(R.id.received);
		
		
		
		findViewById(R.id.goBtn).setOnClickListener(this);
		
		//check whether start date has been set
		if(Util.getStringFromSP(mActivity, Constants.SP_START_DATE) == null){
			Calendar endDate = Calendar.getInstance();
			endDate.add(Calendar.DATE, -30);
			endDate.set(Calendar.HOUR_OF_DAY, 0);
			endDate.set(Calendar.MINUTE, 0);
			endDate.set(Calendar.SECOND, 0);
			
			Util.saveStringInSP(mActivity, Constants.SP_START_DATE, endDate.getTimeInMillis()+"");	
		}
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(Long.parseLong(Util.getStringFromSP(mActivity, Constants.SP_START_DATE)));
		mStartDateEt.setText(Util.getStringFromMillis(Constants.DATE_FORMAT,endCal.getTimeInMillis()));
		mStartDate = endCal;
		
		//set end date by default to current time
		mEndDate = Calendar.getInstance();
		mEndDateEt.setText(Util.getStringFromMillis(Constants.DATE_FORMAT,mEndDate.getTimeInMillis()));		
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startDateET:
			showDatePickerDialog(true);
			break;
			
		case R.id.endDateET:
			showDatePickerDialog(false);
			break;
		case R.id.goBtn:
			Intent intent = new Intent(mActivity, SmsListActivity.class);
			intent.putExtra(EXTRA_START_DATE_IN_MILLIS, mStartDate.getTimeInMillis());
			intent.putExtra(EXTRA_END_DATE_IN_MILLIS, mEndDate.getTimeInMillis());
			if(mAllRB.isChecked()){
				intent.putExtra(EXTRA_SELECTED_LIST_TYPE, Constants.ALL);	
			}else if(mMobilePhonesRB.isChecked()){
				intent.putExtra(EXTRA_SELECTED_LIST_TYPE, Constants.MOBILE_PHONES);
			}else if(mFixedPhonesRB.isChecked()){
				intent.putExtra(EXTRA_SELECTED_LIST_TYPE, Constants.FIXED_PHONES);
			}else{
				intent.putExtra(EXTRA_SELECTED_LIST_TYPE, Constants.EXCLUDE_ABOVE_2_AND_3);
			}
			
			if(mSentRB.isChecked()){
				intent.putExtra(EXTRA_SMS_TYPE, Constants.MESSAGE_TYPE_SENT);
			}else if(mReceivedRB.isChecked()){
				intent.putExtra(EXTRA_SMS_TYPE, Constants.MESSAGE_TYPE_INBOX);
			}else if(mAllTypesRB.isChecked()){
				intent.putExtra(EXTRA_SMS_TYPE, Constants.MESSAGE_TYPE_ALL);	
			}
			
			startActivity(intent);
			break;

		default:
			break;
		}
		
	}
	
	private void showDatePickerDialog(final boolean isStartDate){
		FragmentManager fm = getSupportFragmentManager();
		DateDialogFragment dateDialogFragment = new DateDialogFragment();
		dateDialogFragment.setData( isStartDate?"Start Date":"End Date", isStartDate?mStartDate:mEndDate);
		dateDialogFragment.setOnDateSetListener(new OnDateSetListener() {

			@Override
			public void onDateSet(Calendar date) {
				
				if(isStartDate){
					date.set(Calendar.HOUR_OF_DAY, 0);
					date.set(Calendar.MINUTE, 0);
					date.set(Calendar.SECOND, 0);
					mStartDate = date; 
					mStartDateEt.setText(Util.getStringFromMillis(Constants.DATE_FORMAT, date.getTimeInMillis()));
					Util.saveStringInSP(mActivity, Constants.SP_START_DATE, date.getTimeInMillis()+"");	
				}else{
					mEndDate = date;
					mEndDateEt.setText(Util.getStringFromMillis(Constants.DATE_FORMAT, date.getTimeInMillis()));
				}
			}
		});
		dateDialogFragment.show(fm, isStartDate?"start Date":"end date");
	}
}
