package com.uqu.sensysframework.commons;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.format.Time;

public class DateTime {
	
	private class _DateTime {
		private Time _time;
		private Date _date;
		private Boolean _ignoreDst;
		
		public _DateTime() {
			_time = new Time(Time.getCurrentTimezone());
			//_time = new Time();
			_time.setToNow();
			
			_date = new Date();
			_ignoreDst = true;
		}
		
		public _DateTime(long timestamp) {
			_time = new Time(Time.getCurrentTimezone());
			//_time = new Time();
			_time.set(timestamp);
			
			_date = new Date(timestamp);
			_ignoreDst = true;
		}
		
		@SuppressWarnings("unused")
		public void setIgnoreDst(Boolean bool) {
			_ignoreDst = bool;
		}
		
		public Time getTime() {
			return _time;
		}
		
		public Date getDate() {
			return _date;
		}
		
		public long getMillis() {
			if(_time!=null)
				return _time.toMillis(_ignoreDst);
			else
				return 0;
		}
		
		public String toString() {
			return toString("yyyyMMddHHmmss");
			//return _time.toString();
		}
		
		public String toString(String format) {
			SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
			return sdf.format(_date);
			//return _time.format(format);
		}
	}
	
	public long getLongMillis() {
		return (new _DateTime()).getMillis();
		//return System.currentTimeMillis();
	}
	
	public String getStringMillis() {
		return String.valueOf((new _DateTime()).getMillis());
		//return String.valueOf(System.currentTimeMillis());
	}
	
	public String getNowPlain() {
		return (new _DateTime()).toString();
	}
	
	public String getNowStandard() {
		return (new _DateTime()).toString("dd/MM/yyyy HH:mm:ss");
	}
	
	public String getNow(String format) {
		return (new _DateTime()).toString(format);
	}
	
	public String getDateTimePlain(long timestamp) {
		return (new _DateTime(timestamp)).toString();
	}
	
	public String getDateTimeStandard(long timestamp) {
		return (new _DateTime(timestamp)).toString("dd/MM/yyyy HH:mm:ss");
	}
	
	public String getDateTime(long timestamp, String format) {
		return (new _DateTime(timestamp)).toString(format);
	}
	
	public Time getTime() {
		return (new _DateTime()).getTime();
	}
	
	public Time getTime(long timestamp) {
		return (new _DateTime(timestamp)).getTime();
	}
	
	public String getTime(String format) {
		return (new _DateTime()).toString(format);
	}
	
	public String getTime(long timestamp, String format) {
		return (new _DateTime(timestamp)).toString(format);
	}
	
	public String getTimePlain() {
		return (new _DateTime()).toString("HHmmss");
	}
	
	public String getTimePlain(long timestamp) {
		return (new _DateTime(timestamp)).toString("HHmmss");
	}
	
	public String getTimeStandard() {
		return (new _DateTime()).toString("HH:mm:ss");
	}
	
	public String getTimeStandard(long timestamp) {
		return (new _DateTime(timestamp)).toString("HH:mm:ss");
	}
	
	public Date getDate() {
		return (new _DateTime()).getDate();
	}
	
	public Date getDate(long timestamp) {
		return (new _DateTime(timestamp)).getDate();
	}
	
	public String getDate(String format) {
		return (new _DateTime()).toString(format);
	}
	
	public String getDate(long timestamp, String format) {
		return (new _DateTime(timestamp)).toString(format);
	}
	
	public String getDatePlain() {
		return (new _DateTime()).toString("yyyyMMdd");
	}
	
	public String getDatePlain(long timestamp) {
		return (new _DateTime(timestamp)).toString("yyyyMMdd");
	}
	
	public String getDateStandard() {
		return (new _DateTime()).toString("dd/MM/yyyy");
	}
	
	public String getDateStandard(long timestamp) {
		return (new _DateTime(timestamp)).toString("dd/MM/yyyy");
	}
}
