package com.lumpofcode.dotwo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private static final Map<String, TaskList> _taskListMappedByName = new HashMap<String, TaskList>();
	
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
		return _taskListMappedByName.get(theName);
	}
		
	public static final void putTaskList(final TaskList theTaskList)
	{
		final String theName = theTaskList.name();
		
		if(null != _taskListMappedByName.get(theName))
		{
			// replace it
			final int n = _taskListArray.size();
			for(int i = 0; i < n; i += 1)
			{
				final TaskList theExistingTaskList = _taskListArray.get(i);
				
				// TODO : should to a case insensitive, igore white space compare
				if(theName.equals(theExistingTaskList.name()))
				{
					_taskListArray.set(i, theTaskList);
					break;
				}
			}
			
		}
		else	// just add it to the array
		{
			_taskListArray.add(theTaskList);
		}
		_taskListMappedByName.put(theName, theTaskList);
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
	
}
