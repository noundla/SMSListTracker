package com.noundla.smslisttracker.projectconfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;



public class Util {

	 private static final String SHARED_PREFERENCES_NAME = "NOUNDLA_CALL_LIST";

	/**
     * Save string value from SharedPreference for the given key
     */
    public static void saveStringInSP(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                SHARED_PREFERENCES_NAME, android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    
    /**
     * Retrieve Integer value from SharedPreference for the given key
     */
    public static void saveIntInSP(Context context, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(
                SHARED_PREFERENCES_NAME, android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    
    /**
     * Retrieve Long value from SharedPreference for the given key
     */
    public static void saveLongInSP(Context context, String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences(
                SHARED_PREFERENCES_NAME, android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }
    
    /**
     * Retrieve integer value from SharedPreference for the given key
     */
    public static int getIntFromSP(Context _activity, String key) {
        SharedPreferences preferences = _activity.getSharedPreferences(
                SHARED_PREFERENCES_NAME, android.content.Context.MODE_PRIVATE);
        return preferences.getInt(key, -1);
    }
    
    /**
     * Retrieve long value from SharedPreference for the given key
     */
    public static long getLongFromSP(Context _activity, String key) {
        SharedPreferences preferences = _activity.getSharedPreferences(
                SHARED_PREFERENCES_NAME, android.content.Context.MODE_PRIVATE);
        return preferences.getLong(key, -1);
    }

    /**
     * Retrieve string value from SharedPreference for the given key
     */
    public static String getStringFromSP(Context _activity, String key) {
        SharedPreferences preferences = _activity.getSharedPreferences(
                SHARED_PREFERENCES_NAME, android.content.Context.MODE_PRIVATE);
        return preferences.getString(key, null);
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
    
    /**
	 * Displays an alert to show some information to user and this alert can be cancellable by user.
	 * 
	 * @param activity {@link Activity} reference to create alert
	 * @param title Title for the alert dialog
	 * @param alertMsg Message to display on alert dialog
	 * @throws IllegalArgumentException when button is not having valid text to
	 *             show.
	 */
	public static void showAlert(final Activity activity, String title, String alertMsg	) {
		showAlert(activity, title, alertMsg, activity.getString(android.R.string.ok), true, null);
	}

	/**
	 * Displays an alert to show some information to user and this alert can be cancellable by user.
	 * 
	 * @param activity {@link Activity} reference to create alert
	 * @param title Title for the alert dialog
	 * @param alertMsg Message to display on alert dialog
	 * @param buttonText Text should be displayed on button
	 * @throws IllegalArgumentException when button is not having valid text to
	 *             show.
	 */
	public static void showAlert(final Activity activity, String title, String alertMsg,
			String buttonText) {
		showAlert(activity, title, alertMsg, buttonText, true, null);
	}

	/**
	 * Displays an alert to show some information to user.
	 * 
	 * @param activity {@link Activity} reference to create alert
	 * @param title Title for the alert dialog
	 * @param alertMsg Message to display on alert dialog
	 * @param buttonText Text should be displayed on button
	 * @param listener callback to call on button click.
	 * @throws IllegalArgumentException when button is not having valid text to
	 *             show.
	 */
	public static void showAlert(final Activity activity, String title, String alertMsg,
			String buttonText, final OnAlertDialogButtonClickListener listener) {
		showAlert(activity, title, alertMsg, buttonText,true,listener);
	}

	/**
	 * Displays an alert to show some information to user.
	 * 
	 * @param activity {@link Activity} reference to create alert
	 * @param title Title for the alert dialog
	 * @param alertMsg Message to display on alert dialog
	 * @param buttonText Text should be displayed on button
	 * @param isCancelable <code>true</code> indicates that this dialog will be
	 *            dismissed when device back button pressed ,otherwise not.
	 * @throws IllegalArgumentException when button is not having valid text to
	 *             show.
	 */
	public static void showAlert(final Activity activity, String title, String alertMsg,
			String buttonText, boolean isCancelable) {
		showAlert(activity, title, alertMsg, buttonText, isCancelable, null);
	}

	/**
	 * Displays an alert to show some information to user.
	 * 
	 * @param activity {@link Activity} reference to create alert
	 * @param title Title for the alert dialog
	 * @param alertMsg Message to display on alert dialog
	 * @param buttonText Text should be displayed on button
	 * @param isCancelable <code>true</code> indicates that this dialog will be
	 *            dismissed when device back button pressed ,otherwise not.
	 * @param listener callback to call on button click.
	 * @throws IllegalArgumentException when button is not having valid text to
	 *             show.
	 */
	public static void showAlert(final Activity activity, String title, String alertMsg,
			String buttonText, boolean isCancelable,
			final OnAlertDialogButtonClickListener listener) {

		if (buttonText == null || "".equalsIgnoreCase(buttonText.trim())) {
			throw new IllegalArgumentException(
					"Button text must be supplied to show the alert dialog");
		}

		final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		alert.setTitle(title);
		alert.setCancelable(isCancelable);
		alert.setMessage(alertMsg);
		alert.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (listener != null) {
					listener.onAlertDialogButtonClick();
				}
			}
		});
		AlertDialog dialog = alert.create();
		dialog.setCanceledOnTouchOutside(false);

		dialog.show();
	}// showAlert()
    
	public static int getDurationInMinutes(String durationInSec){
		try{
			int iDurationInSec = Integer.parseInt(durationInSec);

			int durationInMins  = iDurationInSec/60;

			if(iDurationInSec%60>0){
				durationInMins++;
			}
			return durationInMins;
		}catch(Exception nfe){}
		return 0;
	}
}
