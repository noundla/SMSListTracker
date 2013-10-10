package com.noundla.smslisttracker.projectconfig;

public class Constants {

	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "CALL_LIST_DB.sqlite";

	//SMS list view type
	public static final int ALL = 1;
	public static final int MOBILE_PHONES = 2;
	public static final int FIXED_PHONES = 3;
	public static final int EXCLUDE_ABOVE_2_AND_3 = 4;

	//SHARED preferences
	public static final String SP_LAST_SMS_DATE = "LAST_SMS_DATE";
	public static final String SP_START_DATE = "START_DATE";

	//date and time patterns
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String SMS_DATE_FORMAT = "dd/MM/yyyy hh:mm:ss a";

	
	public static final int MESSAGE_TYPE_ALL    = 0;
	public static final int MESSAGE_TYPE_INBOX  = 1;
	public static final int MESSAGE_TYPE_SENT   = 2;
	public static final int MESSAGE_TYPE_DRAFT  = 3;
	public static final int MESSAGE_TYPE_OUTBOX = 4;
	public static final int MESSAGE_TYPE_FAILED = 5; // for failed outgoing messages
	public static final int MESSAGE_TYPE_QUEUED = 6; // for messages to send later
}
