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
		
	/**
	 * Load the contents of the today list.
	 * It does this by copying tasks from the other lists,
	 * so those lists must be fully loaded first.
	 * 
	 * NOTE: this blocks, so it should be done within an AsyncTask
	 * 
	 * NOTE: any ArrayAdapters that are attached to the data
	 *       should be notifiedDataChanged when this completes.
	 */
	public static final void load()
	{
		_todayList.clear();
		for(int i = 0; i < TaskLists.taskListCount(); i += 1)
		{
			final TaskList theList = TaskLists.getTaskListByIndex(i);
			for(int j = 0; j < theList.taskCount(); j += 1)
			{
				final Task theTask = theList.getTaskByIndex(j);
				if(theTask.isToday())
				{
					_todayList.add(theTask);	// adds end (NOT in sorted order)
				}
			}
		}
		sort();
	}
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
			notifyDataChanged();
		}
	}
	
	private static final void sort()
	{
		Collections.sort(_todayList, sortOrder().comparator());
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
    		if(theTask.isToday())
    		{
		        _innerAddTask(_todayList, theTask);
		        notifyDataChanged();	// tell attached adapter that the data changed.
    		}
    	}
    }
    private static void _innerAddTask(final ArrayList<Task> theList, final Task theTask) 
    {
    	if(null != theTask)
    	{
	        int index = Collections.binarySearch(theList, theTask, sortOrder().comparator());
	        if (index < 0) index = ~index;
	        if((index == theList.size()) || (theTask != theList.get(index)))
	        {
	        	// add it if it is not aleady there
	        	theList.add(index, theTask);
	        }
    	}
    }
	
    public static void removeTask(final Task theTask) 
    {
    	if(null != theTask)
    	{
			_innerRemoveTask(_todayList, theTask);
	        notifyDataChanged();	// tell attached adapter that the data changed.
    	}
    }
    private static void _innerRemoveTask(final ArrayList<Task> theList, final Task theTask) 
    {
    	if(null != theTask)
    	{
    		int index = theList.indexOf(theTask);
    		if(index >= 0)
    		{
    			theList.remove(index);
    		}
    	}
    }
	

	private static long _today = 0;
	public static final long getToday()
	{
		if(0 == _today)
		{
			_today = TimeUtils.timeSpanInDays(System.currentTimeMillis()) * TimeUtils.DAY_IN_MILLIS;
		}
		return _today;
	}
	
	public static final long updateToday()
	{
		final long today = TimeUtils.timeSpanInDays(System.currentTimeMillis()) * TimeUtils.DAY_IN_MILLIS;
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
	 * Detach from the adapter.
	 * 
	 * @return the detached adapter or null if not adapter was attached.
	 */
	public static final TaskListAdapter detachAdapter()
	{
		final TaskListAdapter theAdapter = _adapter;
		_adapter = null;
		return theAdapter;
	}
	
	/**
	 * Construct a new ArrayAdapter attached to the task list
	 * 
	 * NOTE: The list must NOT be attached already.  To detach
	 *       from a prior adapter, call detachAdapter().
	 * 
	 * @param context
	 * @param theItemLayoutId
	 * @param theListener
	 * @return
	 */
	public static final TaskListAdapter attachAdapter(
			final Context context, 
			final int theItemLayoutId, 
			TaskListListener theListener)
	{
		if(null != _adapter) throw new IllegalStateException("TaskLists is already attached to an adapter.  Call detachAdapter detach from prior adapter.");
		
		return _adapter = new TaskListAdapter(context, theItemLayoutId, _todayList, theListener);
	}
	private static TaskListAdapter _adapter = null;
	
}
