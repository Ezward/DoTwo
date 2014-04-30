package com.lumpofcode.dotwo.model;

import com.lumpofcode.dotwo.R;

import android.content.Context;
import android.text.format.DateUtils;

public final class TaskUtils
{
	private TaskUtils() {};	// enforce singleton;
	
	public static final String getImportanceString(final Context theContext, final Task theTask)
	{
		final String[] theStrings = (theContext.getResources().getStringArray(R.array.importanceStars));
		return theStrings[theTask.importance1to5() - 1];
	}
	public static final CharSequence getDueDateString(final Context theContext, final Task theTask)
	{
		return getRelativeDateTimeString(theContext, theTask.dueDateUTC());
	}
	public static final String getPriorityString(final Context theContext, final Task theTask)
	{
		final String theTemplate = theContext.getString(R.string.priority_template);
		return theTemplate.replace("$PRIORITY", Long.toString(theTask.priority(TodayList.getToday())));
	}
	private static final CharSequence getRelativeDateTimeString(Context theContext, long theDueDate)
	{
		return DateUtils.getRelativeDateTimeString(
			theContext,					// Context 
			theDueDate,					// The time to display
	        DateUtils.DAY_IN_MILLIS,	// The resolution 
	        DateUtils.WEEK_IN_MILLIS,	// The maximum time at which the time will switch to default date instead of spans. 
	        0); 						// Eventual flags
}

}
