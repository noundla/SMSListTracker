package com.noundla.smslisttracker;

public class SMSMessage {
	private long id;
	private long threadID;
	private String phoneNumber;
	private int type;
	private long date;
	private int smsParts;
	private String personName;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getThreadID() {
		return threadID;
	}
	public void setThreadID(long threadID) {
		this.threadID = threadID;
	}
	public String getNumber() {
		return phoneNumber;
	}
	public void setNumber(String number) {
		this.phoneNumber = number;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public int getSmsParts() {
		return smsParts;
	}
	public void setSmsParts(int smsParts) {
		this.smsParts = smsParts;
	}
	/**
	 * @return the personName
	 */
	public String getPersonName() {
		return personName;
	}
	/**
	 * @param personName the personName to set
	 */
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	
}
