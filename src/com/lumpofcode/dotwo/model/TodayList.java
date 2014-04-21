package com.lumpofcode.dotwo.model;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;

import com.lumpofcode.date.TimeUtils;
import com.lumpofcode.dotwo.todolist.TaskListAdapter;
import com.lumpofcode.dotwo.todolist.TaskListAdapter.TaskListListener;

public final class TodayList
{
	private static ArrayList<Task> _todayList = new ArrayList<Task>();
	private static TaskSortOrder _sortOrder = TaskSortOrder.BY_PRIORITY;
	
	private TodayList() {super();}	// private constructor to enforce singleton
		
	public static final TaskSortOrder sortOrder()
	{
		return _sortOrder;
	}
	public static final void sortOrder(TaskSortOrder theSortOrder)
	{
		if(_sortOrder != theSortOrder)
		{
			// set order and do the sort
			_sortOrder = theSortOrder;
			sort();
		}
	}
	
	public static final void sort()
	{
		Collections.sort(_todayList, sortOrder().comparator());
		
		// TODO : after sorting is complete, notify other tasklists or new sort order
	}
	
    /**
     * Insert a new task into the list in the current sort order.
     * 
     * @param theTask
     */
    public static void addTask(final Task theTask) 
    {
    	if(null != theTask)
    	{
	        int index = Collections.binarySearch(_todayList, theTask, sortOrder().comparator());
	        if (index < 0) index = ~index;
	        _todayList.add(index, theTask);
	        notifyDataChanged();	// tell attached adapter that the data changed.
    	}
    }

	

	private static long _today = 0;
	public static final long getToday()
	{
		if(0 == _today)
		{
			_today = TimeUtils.timeSpanInDays(System.currentTimeMillis());
		}
		return _today;
	}
	
	public static final long updateToday()
	{
		final long today = TimeUtils.timeSpanInDays(System.currentTimeMillis());
		if(today > _today) 
		{
			_today = today;
			// TODO : notify systems of day change
		}
		return _today;
	}
	
	/**
	 * notify the TodayList that it's underlying data has changed.
	 * This will, in turn, notify the attached ArrayAdapter (if there is one)
	 */
	public static final void notifyDataChanged()
	{
		if(null != _adapter)
		{
			sort();	// resort
			_adapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * Create a new ArrayAdapter attached to the task list
	 * 
	 * @param context
	 * @param theItemLayoutId
	 * @param theListener
	 * @return
	 */
	public static final TaskListAdapter taskListAdapter(
			final Context context, 
			final int theItemLayoutId, 
			TaskListListener theListener)
	{
		if(null == _adapter)
		{
			_adapter = new TaskListAdapter(context, theItemLayoutId, _todayList, theListener);
		}
		return _adapter;
	}
	private static TaskListAdapter _adapter = null;
	
}
