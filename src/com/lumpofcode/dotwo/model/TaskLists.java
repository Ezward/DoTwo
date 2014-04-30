package com.lumpofcode.dotwo.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.activeandroid.query.Select;
import com.lumpofcode.dotwo.todolists.OnTaskListClickListener;
import com.lumpofcode.dotwo.todolists.TaskListsAdapter;

/**
 * @author Ed
 * 
 * A singleton collection of TaskList
 *
 */
public final class TaskLists
{
	private static final ArrayList<TaskList> _taskListArray = new ArrayList<TaskList>();
	
	private TaskLists(){}	// private constructor enforces singleton.
	
	/**
	 * Load all the lists.
	 * 
	 * NOTE: this is a long running task that will block,
	 *       so it should be used within an AsyncTask.
	 * 
	 * NOTE: any ArrayAdapters that are attached to the data
	 *       should be notifiedDataChanged when this completes.
	 */
	public static final void load()
	{
		final List<TaskList> theLists = new Select()
	        .from(TaskList.class)
	        .execute();
		
		//
		// add the to underlying array,
		// so any ArrayAdapter does not get borked
		//
		_taskListArray.clear();
		_taskListArray.addAll(theLists);
		
		
		//
		// now iterate though and load the items for each list
		//
		for(TaskList theList : theLists)
		{
			theList.load();
		}
	}
	
	/**
	 * Create a new TaskList in the collection of lists.
	 * 
	 * @param theName
	 * @return the TaskList
	 */
	public static final TaskList newTaskList(final String theName)
	{
		final TaskList theTaskList = TaskList._newTaskList(theName);
		putTaskList(theTaskList);
		return theTaskList;
	}
	
	public static final int taskListCount()
	{
		return _taskListArray.size();
	}
	public static final TaskList getTaskListByIndex(final int theIndex)
	{
		if((theIndex >= 0) && (theIndex < _taskListArray.size()))
		{
			return _taskListArray.get(theIndex);
		}
		return null;
	}
	
	public static final TaskList getTaskListByName(final String theName)
	{
		final int i = _getTaskListIndexByName(theName);
		if(i >= 0)
		{
			return _taskListArray.get(i);
		}
		return null;
	}
	private static final int _getTaskListIndexByName(final String theName)
	{
		final int n = _taskListArray.size();
		for(int i = 0; i < n; i += 1)
		{
			final TaskList theExistingTaskList = _taskListArray.get(i);
			
			// TODO : should to a case insensitive, igore white space compare
			if(theName.equals(theExistingTaskList.name()))
			{
				return i;
			}
		}
		return -1;
	}
			
	private static final void putTaskList(final TaskList theTaskList)
	{
		if(null != getTaskListByName(theTaskList.name()))
		{
			throw new IllegalStateException("TaskLists.putTaskList() attempt to add a list with a duplicate name = " + theTaskList.name());
		}
		_taskListArray.add(theTaskList);
	}
	
	/**
	 * Factory to create a TaskListsAdapter and attach it to TaskLists
	 * 
	 * @param theContext
	 * @param theItemLayout
	 * @param theListener
	 * @return
	 */
	public static final TaskListsAdapter newTaskListsAdapter(
			final Context theContext,
			final OnTaskListClickListener theListener)
	{
		return __adapter = new TaskListsAdapter(theContext, _taskListArray, theListener);
	}
	private static TaskListsAdapter __adapter = null;
	
	public static final void notifyDataSetChanged()
	{
		if(null != __adapter)
		{
			__adapter.notifyDataSetChanged();
		}
	}
	
	public static final void sort(final TaskSortOrder theOrder)
	{
		for(int i = 0; i < taskListCount(); i += 1)
		{
			final TaskList theList = getTaskListByIndex(i);
			theList.sortOrder(theOrder);
		}
	}
	
}
