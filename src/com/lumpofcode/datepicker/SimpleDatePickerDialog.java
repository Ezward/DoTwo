package com.lumpofcode.datepicker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.format.Time;

public final class SimpleDatePickerDialog extends DatePickerDialog 
{
	public SimpleDatePickerDialog(
			android.content.Context context, 
			android.app.DatePickerDialog.OnDateSetListener callBack, 
			int year, int monthOfYear, int dayOfMonth)
	{
		super(context, callBack, year, monthOfYear, dayOfMonth);
	}
	
	public static final SimpleDatePickerDialog newSimpleDatePickerDialog(
			final Context theContext, 
			final OnDateSetListener theListener,
			final long theInitialDateMs, 
			final String theTitle)
	{
		final Time theTime = new Time();
		theTime.set(theInitialDateMs);
		SimpleDatePickerDialog theDialog = new SimpleDatePickerDialog(theContext, theListener, theTime.year, theTime.month, theTime.monthDay);

		theDialog.setTitle(theTitle);
		
		return theDialog;
	}
}
