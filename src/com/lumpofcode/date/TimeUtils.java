package com.lumpofcode.date;

public class TimeUtils
{
	public static final long SECOND_IN_MILLIS = 1000L;
	public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
	public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
	public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
	public static final long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;

	public static final long timeSpanInHSeconds(final long theTimeSpanMs)
	{
		return theTimeSpanMs / SECOND_IN_MILLIS;
	}
	public static final long timeSpanInRoundedSeconds(final long theTimeSpanMs)
	{
		return (theTimeSpanMs + (SECOND_IN_MILLIS / 2)) / SECOND_IN_MILLIS;
	}
	public static final long timeSpanInHMinutes(final long theTimeSpanMs)
	{
		return theTimeSpanMs / MINUTE_IN_MILLIS;
	}
	public static final long timeSpanInRoundedMinutes(final long theTimeSpanMs)
	{
		return (theTimeSpanMs + (MINUTE_IN_MILLIS / 2)) / MINUTE_IN_MILLIS;
	}
	public static final long timeSpanInHours(final long theTimeSpanMs)
	{
		return theTimeSpanMs / HOUR_IN_MILLIS;
	}
	public static final long timeSpanInRoundedHours(final long theTimeSpanMs)
	{
		return (theTimeSpanMs + (HOUR_IN_MILLIS / 2)) / HOUR_IN_MILLIS;
	}
	public static final long timeSpanInDays(final long theTimeSpanMs)
	{
		return theTimeSpanMs / DAY_IN_MILLIS;
	}
	public static final long timeSpanInRoundedDays(final long theTimeSpanMs)
	{
		return (theTimeSpanMs + (DAY_IN_MILLIS / 2)) / DAY_IN_MILLIS;
	}
	public static final long timeSpanInWeeks(final long theTimeSpanMs)
	{
		return theTimeSpanMs / WEEK_IN_MILLIS;
	}
	public static final long timeSpanInRoundedWeeks(final long theTimeSpanMs)
	{
		return (theTimeSpanMs + (WEEK_IN_MILLIS / 2)) / WEEK_IN_MILLIS;
	}
}
