package com.lumpofcode.dotwo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.lumpofcode.dotwo.todolist.TaskListAdapter;
import com.lumpofcode.dotwo.todolist.TaskListAdapter.TaskListListener;

/**
 * @author Ed
 * 
 * TaskList is actually a set, implemented using a ParseObject.
 * So the TaskList can be persisted using the Parse API.
 * 
 * A Task List has
 * String name, the name of the list, such as "Grocery List" or "Yard work"
 * String sharedBy, the identity of the user that shared the list, or null if not shared
 * A set of tasks that can be looked up by the name of the task.
 * 
 * To construct a TaskList, use the factory method of the TaskLists singleton;
 * 		TaskList theTaskList = TaskLists.newTaskList("testList");
 *
 */
@Table(name = "TaskLists")
public final class TaskList extends Model
{
	public static final String TASK_LIST_NAME = "TASK_LIST_NAME";
	private static final String SHARED_BY = "SHARED_BY";
	
	//
	// public properties required by ActiveAndroid
	//
	@Column(name = "name")
	public String _name;
	
	//
	// private collection, populated with call to ActiveAndroid
	//
	private ArrayList<Task> __tasks = null;

	private TaskSortOrder _sortOrder = TaskSortOrder.BY_PRIORITY;

	//
	// zero arg constructor required by ActiveAndroid
	public TaskList() 
	{
	}	
	
	/**
	 * Load the tasks for this list.
	 * 
	 * NOTE: This blocks while loading, so 
	 *       it should be used in an AsyncTask.
	 * 
	 * NOTE: any ArrayAdapters that are attached to the data
	 *       should be notifiedDataChanged when this completes.
	 */
	protected void load()
	{
		final List<Task> theTasks = new Select()
	        .from(Task.class)
	        .where("TaskList = ?", this.getId())
	        .execute();
	
		//
		// add the to underlying array,
		// so any ArrayAdapter does not get borked
		//
		_tasks().clear();
		_tasks().addAll(theTasks);
	}
	
	/**
	 * Package private factory for constructing TaskList.
	 * This should ONLY be called by the TaskLists singleton.
	 * Do NOT call this directly, instead use the factory method
	 * of the TaskLists singleton like this;
	 * 		TaskList theTaskList = TaskLists.newTaskList("testList");
	 * 
	 * @param theName
	 * @return
	 */
	/* package private */ static final TaskList _newTaskList(final String theName)
	{
		// TODO : validate structure
		if((null == theName) || theName.isEmpty()) throw new IllegalArgumentException();

		final TaskList theTaskList = new TaskList();
		
		theTaskList.name(theName);
		return theTaskList;
	}	
	
	/**
	 * Create a new task in this list.
	 * 
	 * @param theName
	 * @return the Task
	 */
	public Task newTask(final String theName)
	{
		if((null == theName) || theName.isEmpty()) throw new IllegalArgumentException("Task cannot be constructed; theName is null or empty.");

		// 
		// add the task to the list and the list to the task
		//
		final Task theTask = new Task(this, theName);
		_setTask(theTask);
		
		// 
		// set defaults
		// 
		theTask.importance1to5(3);	// 3 stars
		theTask.dueDateUTC(System.currentTimeMillis() + (1000L * 60L * 60L * 24L * 7L));	// due in one week
		
		return theTask;
	}
		
	private List<Task> _tasks()
	{
		if(null == __tasks)
		{
			// load task for this list from database
			__tasks = getMany(Task.class, "TaskList");
		}
		return __tasks;
	}
	public final int taskCount()
	{
		return _tasks().size();
	}
	

	
		
	public String name()
	{
		return _name;
	}
	public void name(final String theName)
	{
		// TODO : validate structure of name
		if((null == theName) || theName.isEmpty()) throw new IllegalArgumentException();
		
		_name = theName;
	}
			
	private final void _validateTaskName(final String theTaskName)
	{
		// don't allow reserved keys
		if(TASK_LIST_NAME.equals(theTaskName)) throw new IllegalArgumentException("getTask: use of reserved field name, " + SHARED_BY + ".");
		if(SHARED_BY.equals(theTaskName)) throw new IllegalArgumentException("getTask: use of reserved field name, " + SHARED_BY + ".");
	}
	

	/**
	 * Get a named task from the list of tasks.
	 * 
	 * @param theTaskName
	 * @return
	 */
	public Task getTaskByName(final String theTaskName)
	{
		return getTaskByIndex(getTaskIndexByName(theTaskName));
	}
	public Task getTaskByIndex(final int theIndex)
	{
		if((theIndex >= 0) && (theIndex < this.taskCount()))
		{
			return _tasks().get(theIndex);
		}
		return null;
	}
	private int getTaskIndexByName(final String theTaskName)
	{
		_validateTaskName(theTaskName);
		
		// check the map to see if it exists before searching for it
		for(int i = 0; i < taskCount(); i += 1)
		{
			final Task theTask = _tasks().get(i);
			if(theTask.name().equals(theTaskName))
			{
				return i;
			}
		}
		return -1;
	}

	/* package */ void _setTask(final Task theTask)
	{
		if(null != theTask)
		{
			// don't allow reserved keys
			final String theTaskName = theTask.name();
			_validateTaskName(theTaskName);
			
			final int i = getTaskIndexByName(theTaskName);
			if(i >= 0)
			{
				// overwrite existing task
				_tasks().set(i, theTask);
			}
			else
			{
				// add new task
				_tasks().add(theTask);
			}
		}
		
	}
	
	public void removeTask(final Task theTask)
	{
		if(null != theTask)
		{
			final String theTaskName = theTask.name();
			removeTaskByName(theTaskName);
		}
	}
	public void removeTaskByName(final String theTaskName)
	{
		if((null != theTaskName) && !theTaskName.isEmpty())
		{
			if(TASK_LIST_NAME.equals(theTaskName)) throw new IllegalArgumentException("getTask: use of reserved field name, " + SHARED_BY + ".");
			if(SHARED_BY.equals(theTaskName)) throw new IllegalArgumentException("getTask: use of reserved field name, " + SHARED_BY + ".");

			// remove the field if it is null or empty
			removeTaskByIndex(getTaskIndexByName(theTaskName));
		}
	}
	private void removeTaskByIndex(final int theIndex)
	{
		if((theIndex >= 0) && (theIndex < taskCount()))
		{
			// remove from map, then list
			final Task theTask = getTaskByIndex(theIndex);
			_tasks().remove(theIndex);
			
			// persist
			theTask.delete();	// remove from ActiveAndroid
		}
		
	}
		
	
	/**
	 * Detach the list from an adapter.
	 * 
	 * @return the previous adapter, or null if no adapter was attached.
	 */
	public final TaskListAdapter detachAdapter()
	{
		final TaskListAdapter theAdapter = __adapter;
		__adapter = null;
		return theAdapter;
	}
	
	/**
	 * Construct a new ArrayAdapter and attach it to the task list.
	 * 
	 * NOTE: the list must NOT be already attached to an adapter.
	 *       Use detachAdapter() to detach from a prior adapter.
	 * 
	 * @param context
	 * @param theItemLayoutId
	 * @param theListener
	 * @return
	 */
	public final TaskListAdapter attachAdapter(
			final Context context, 
			final int theItemLayoutId, 
			TaskListListener theListener)
	{
		if(null != __adapter) throw new IllegalStateException("TaskList=$name is already attached to an adapter.  Call TaskList.detach() before attaching a new adapter.");
		return __adapter = new TaskListAdapter(context, theItemLayoutId, _tasks(), theListener);
	}
	private TaskListAdapter __adapter = null;
	
	public final void notifyDataSetChanged()
	{
		_sort();
		if(null != __adapter)
		{
			__adapter.notifyDataSetChanged();
		}
	}
	
	public final TaskSortOrder sortOrder()
	{
		return _sortOrder;
	}
	public final void sortOrder(TaskSortOrder theSortOrder)
	{
		if(_sortOrder != theSortOrder)
		{
			// set order and do the sort
			_sortOrder = theSortOrder;
			notifyDataSetChanged();	// this will sort it
		}
	}
	
	private final void _sort()
	{
		Collections.sort(_tasks(), sortOrder().comparator());
	}

}
