package com.noundla.smslisttracker;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DateDialogFragment extends DialogFragment {
	private Context mContext;
	private Calendar mDate;
	private OnDateSetListener mListener;

	public void setData(String titleResource, Calendar date){
		mDate = Calendar.getInstance();
		mDate = date;

		Bundle args = new Bundle();
		args.putString("title", titleResource);
		setArguments(args);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new DatePickerDialog(getActivity(), dateSetListener, mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH));
	}

	public void setOnDateSetListener(OnDateSetListener listener){
		mListener = listener;
	}

	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		boolean fired = false;
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar newDate = Calendar.getInstance();
			newDate.set(year, monthOfYear, dayOfMonth);
			mListener.onDateSet(newDate);
		}
	};


	public interface OnDateSetListener{
		public void onDateSet(Calendar date);
	}

}
