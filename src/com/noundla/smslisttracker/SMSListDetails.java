package com.noundla.smslisttracker;

import java.util.ArrayList;

public class SMSListDetails {
	private ArrayList<SMSMessage> smsList;
	private long latestSMSDate;
	private long totalUnits;
	
	public ArrayList<SMSMessage> getSMSList() {
		return smsList;
	}
	public void setSMSList(ArrayList<SMSMessage> smsList) {
		this.smsList = smsList;
	}
	public long getLatestSMSDate() {
		return latestSMSDate;
	}
	public void setLatestSMSDate(long latestSMSId) {
		this.latestSMSDate = latestSMSId;
	}
	public long getTotalUnits() {
		return totalUnits;
	}
	public void setTotalUnits(long totalUnits) {
		this.totalUnits = totalUnits;
	}
	
	
}
