package com.noundla.smslisttracker.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

import com.noundla.smslisttracker.SMSListDetails;
import com.noundla.smslisttracker.SMSMessage;
import com.noundla.smslisttracker.projectconfig.Constants;
import com.noundla.smslisttracker.projectconfig.Util;



public class SMSListDBHelper extends SQLiteOpenHelper{
	private Context mContext;
	public SMSListDBHelper(Context context) {
		super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
		mContext = context;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		String query;
		query = "CREATE TABLE "+SMSListTable.TABLE_NAME+ "( "+
				SMSListTable.smsId+" INTEGER PRIMARY KEY, "+
				SMSListTable.phoneNumber+" TEXT NOT NULL, "+
				SMSListTable.threadID+" INTEGER NOT NULL, "+
				SMSListTable.date+" INTEGER NOT NULL, "+
				SMSListTable.type+" INTEGER NOT NULL, "+
				SMSListTable.smsParts+" INTEGER NOT NULL "+
				")";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//backup old data before upgrate
		db.execSQL("create table "+SMSListTable.TABLE_NAME+"_"+oldVersion+" as select "+ SMSListTable.smsId+","+SMSListTable.phoneNumber+","+SMSListTable.threadID+","+SMSListTable.date+","+SMSListTable.type+","+SMSListTable.smsParts  +" from "+SMSListTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SMSListTable.TABLE_NAME);
		onCreate(db);
	}


	public void insertSMSMessage(SMSMessage smsMessage) {
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			ContentValues values = new ContentValues();

			values.put(SMSListTable.smsId, smsMessage.getId()); 
			values.put(SMSListTable.phoneNumber, smsMessage.getNumber());
			values.put(SMSListTable.threadID, smsMessage.getThreadID());
			values.put(SMSListTable.type, smsMessage.getType());
			values.put(SMSListTable.date, smsMessage.getDate());
			values.put(SMSListTable.smsParts, smsMessage.getSmsParts());

			// Inserting Row
			db.insert(SMSListTable.TABLE_NAME, null, values);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeDatabase(db);
		}

	}



	public void insertBulkCallList(ArrayList<SMSMessage> smsList){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			// Create a single InsertHelper to handle this set of insertions.
			InsertHelper ih = new InsertHelper(db, SMSListTable.TABLE_NAME);

			// Get the numeric indexes for each of the columns that we're updating
			final int smsIdColumn = ih.getColumnIndex(SMSListTable.smsId);
			final int phoneColumn = ih.getColumnIndex(SMSListTable.phoneNumber);
			final int threadIDColumn = ih.getColumnIndex(SMSListTable.threadID);
			final int typeColumn = ih.getColumnIndex(SMSListTable.type);
			final int dateColumn = ih.getColumnIndex(SMSListTable.date);
			final int smsPartsColumn = ih.getColumnIndex(SMSListTable.smsParts);
			try {
				for (SMSMessage smsMessage : smsList) {
					if(smsMessage!=null){
						// ... Create the data for this row (not shown) ...

						// Get the InsertHelper ready to insert a single row
						ih.prepareForInsert();

						// Add the data for each column
						ih.bind(smsIdColumn, smsMessage.getId());
						ih.bind(phoneColumn, smsMessage.getNumber());
						ih.bind(threadIDColumn, smsMessage.getThreadID());
						ih.bind(typeColumn, smsMessage.getType());
						ih.bind(dateColumn, smsMessage.getDate());
						ih.bind(smsPartsColumn, smsMessage.getSmsParts());

						// Insert the row into the database.
						ih.execute();		
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally {
				if(ih!=null )
					ih.close();  // See comment below from Stefan Anca
			}
		}finally{
			closeDatabase(db);
		}
	}


	public boolean bulkInsertData(ArrayList<SMSMessage> smsList) {
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();

			SQLiteStatement insStmt;
			insStmt = db.compileStatement("INSERT INTO " + SMSListTable.TABLE_NAME + " ("+SMSListTable.smsId+","+SMSListTable.phoneNumber+","+SMSListTable.threadID+","+SMSListTable.type+","+SMSListTable.date+","+SMSListTable.smsParts+") VALUES (?, ?, ?, ?, ?, ?);");
			db.beginTransaction();
			try {
				if(smsList!=null && smsList.size()>0){
					//saving the date of last message
					Util.saveLongInSP(mContext, Constants.SP_LAST_SMS_DATE, smsList.get(0).getDate());
				}
				for(SMSMessage smsMessage : smsList) {
					try{
						
						// Add the data for each column
						insStmt.bindLong(1, smsMessage.getId());
						insStmt.bindString(2, smsMessage.getNumber());
						insStmt.bindLong(3, smsMessage.getThreadID());
						insStmt.bindLong(4, smsMessage.getType());
						insStmt.bindLong(5, smsMessage.getDate());
						insStmt.bindLong(6, smsMessage.getSmsParts());
						
						insStmt.executeInsert();    //  should really check value here!
						
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();    
			}
		} catch(SQLException se) {
			return false;
		}finally{
			closeDatabase(db);
		}
		return true;
	}



	public SMSListDetails getCallList(long startDate,long endDate, int listType, int smsType){
		SMSListDetails listDetails = new SMSListDetails();
		ArrayList<SMSMessage> smsList = new ArrayList<SMSMessage>();
		long totalUnits=0;
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			Cursor cursor = null;
			try{
				String listTypeStr = null;
				switch (listType) {
				case Constants.FIXED_PHONES:
					listTypeStr = " AND ( "+SMSListTable.phoneNumber+" LIKE \"+9140%\" OR "+
							SMSListTable.phoneNumber+" LIKE \"9140%\" OR "+
							SMSListTable.phoneNumber+" LIKE \"040%\" OR "+
							SMSListTable.phoneNumber+" LIKE \"+040%\" )";

					break;
				case Constants.MOBILE_PHONES:


					listTypeStr =" AND ("+

							"(("+SMSListTable.phoneNumber+" LIKE \"+919%\" OR "+SMSListTable.phoneNumber+" LIKE \"+918%\" OR "+SMSListTable.phoneNumber+" LIKE \"+917%\") AND length("+SMSListTable.phoneNumber+") = 13 ) OR "+
							"(("+SMSListTable.phoneNumber+" LIKE \"919%\" OR "+SMSListTable.phoneNumber+" LIKE \"918%\" OR "+SMSListTable.phoneNumber+" LIKE \"917%\") AND length("+SMSListTable.phoneNumber+") = 12 ) OR "+
							"(("+SMSListTable.phoneNumber+" LIKE \"09%\" OR "+SMSListTable.phoneNumber+" LIKE \"08%\" OR "+SMSListTable.phoneNumber+" LIKE \"07%\") AND length("+SMSListTable.phoneNumber+") = 11 ) OR "+
							"(("+SMSListTable.phoneNumber+" LIKE \"9%\" OR "+SMSListTable.phoneNumber+" LIKE \"8%\" OR "+SMSListTable.phoneNumber+" LIKE \"7%\") AND length("+SMSListTable.phoneNumber+") = 10 ) "+
							" ) ";
					break;
				case Constants.EXCLUDE_ABOVE_2_AND_3:
					listTypeStr = " AND NOT ( "+SMSListTable.phoneNumber+" LIKE \"+9140%\" OR "+
							SMSListTable.phoneNumber+" LIKE \"9140%\" OR "+
							SMSListTable.phoneNumber+" LIKE \"040%\" OR "+
							SMSListTable.phoneNumber+" LIKE \"+040%\" ) AND NOT ("+
							"(("+SMSListTable.phoneNumber+" LIKE \"+919%\" OR "+SMSListTable.phoneNumber+" LIKE \"+918%\" OR "+SMSListTable.phoneNumber+" LIKE \"+917%\") AND length("+SMSListTable.phoneNumber+") = 13 ) OR "+
							"(("+SMSListTable.phoneNumber+" LIKE \"919%\" OR "+SMSListTable.phoneNumber+" LIKE \"918%\" OR "+SMSListTable.phoneNumber+" LIKE \"917%\") AND length("+SMSListTable.phoneNumber+") = 12 ) OR "+
							"(("+SMSListTable.phoneNumber+" LIKE \"09%\" OR "+SMSListTable.phoneNumber+" LIKE \"08%\" OR "+SMSListTable.phoneNumber+" LIKE \"07%\") AND length("+SMSListTable.phoneNumber+") = 11 ) OR "+
							"(("+SMSListTable.phoneNumber+" LIKE \"9%\" OR "+SMSListTable.phoneNumber+" LIKE \"8%\" OR "+SMSListTable.phoneNumber+" LIKE \"7%\") AND length("+SMSListTable.phoneNumber+") = 10 ) "+
							" ) ";;
							break;
				case Constants.ALL:
					listTypeStr = null;
					break;	
				default:
					break;
				}

				cursor = db.query(SMSListTable.TABLE_NAME, null, SMSListTable.date + " >= " + startDate+" AND "+SMSListTable.date+" <= "+endDate + (listTypeStr!=null ? listTypeStr:"")+ (smsType>0 ?(" AND "+SMSListTable.type+" = "+smsType):""), null, null, null, SMSListTable.date+" DESC");

				int idColumn = cursor.getColumnIndex(SMSListTable.smsId);
				int numberColumn = cursor.getColumnIndex(SMSListTable.phoneNumber);
				int dateColumn = cursor.getColumnIndex( SMSListTable.date);
				int threadIDColumn = cursor.getColumnIndex( SMSListTable.threadID);
				int smsPartsColumn = cursor.getColumnIndex( SMSListTable.smsParts);
				int typeColumn = cursor.getColumnIndex( SMSListTable.type);
				// looping through all rows and adding to list
				if (cursor!=null && cursor.moveToFirst()) {
					do {
						SMSMessage smsMessage = new SMSMessage();
						smsMessage.setId(cursor.getLong(idColumn));
						smsMessage.setNumber(cursor.getString(numberColumn));
						smsMessage.setDate(cursor.getLong(dateColumn));
						smsMessage.setThreadID(cursor.getInt( threadIDColumn ));
						smsMessage.setSmsParts(cursor.getInt( smsPartsColumn ));
						smsMessage.setType(cursor.getInt( typeColumn ));
						smsMessage.setPersonName(getContactNameIfAvailable(smsMessage.getNumber()));
						smsList.add(smsMessage);
						totalUnits = totalUnits+smsMessage.getSmsParts();
					} while (cursor.moveToNext());
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null){
					cursor.close();
				}
			}

			long maxDate = getMaxDateOfSMSList(db);
			if(maxDate!=0){
				listDetails.setLatestSMSDate(maxDate);
			}/*else{
				if(smsList.size()>0){
					listDetails.setLatestSMSDate(smsList.get(0).getDate());
				}
			}*/
		}finally{
			closeDatabase(db);
		}
		listDetails.setTotalUnits(totalUnits);
		listDetails.setSMSList(smsList);

		return listDetails;
	}

	/**Fetch the contact name for the given number. 
	 * @param number Phone number to get the contact name
	 * @return Returns the contact name for the number if available. Otherwise just returns the given number itself*/
	private String getContactNameIfAvailable(String number) { 
		String result = null;
		if(number!=null && !"".equalsIgnoreCase(number)){
			Cursor cursor=null;
			try{
				Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
				cursor= mContext.getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME},null,null,null);
				if (cursor.moveToFirst()){
					result = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(cursor!=null){
					cursor.close();
				}
			}
		}
		if(result==null || "".equalsIgnoreCase(result)){
			result = number;
		}
		return result;
	}

	public long getMaxDateOfSMSList(SQLiteDatabase db){
		//get the max id from the table
		Cursor maxCursor =null;
		try{
			maxCursor = db.rawQuery("SELECT MAX(" + SMSListTable.date + ") FROM " + SMSListTable.TABLE_NAME +";", null);
			if (maxCursor != null) {
				if (maxCursor.moveToFirst() && !maxCursor.isNull(0)) {
					return maxCursor.getLong(0);
				}else{
					return 0;
				}
			}else{
				return 0;

			}

		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}finally{
			if(maxCursor!=null){
				maxCursor.close();
			}
		}
	}

	public void closeDatabase(SQLiteDatabase db){
		if(db!=null && db.isOpen()){
			db.close();
		}
	}

}
