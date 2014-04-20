package com.lumpofcode.dotwo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.lumpofcode.dotwo.todolist.TaskListAdapter;
import com.lumpofcode.dotwo.todolist.TaskListAdapter.TaskListListener;
import com.parse.ParseException;
import com.parse.ParseObject;

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
public class TaskList
{
	private static final String PARSE_CLASS = "TaskList";
	public static final String TASK_LIST_NAME = "TASK_LIST_NAME";
	private static final String SHARED_BY = "SHARED_BY";
	
	private ArrayList<Task> _tasks;
	private Map<String, Task> _taskMap;
	private ParseObject _state;
	
	private TaskList () {};	// enforce use of factory constructor
	
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
		theTaskList._tasks = new ArrayList<Task>();
		theTaskList._taskMap = new HashMap<String, Task>();
		theTaskList._state = new ParseObject(PARSE_CLASS);
		
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

		final Task theTask = new Task();
		theTask.put(Task.NAME, theName);
		
		// 
		// add the task to the list and the list to the task
		//
		_setTask(theTask);
		theTask.put(Task.LIST, _state);
		
		// 
		// set defaults
		// 
		theTask.importance1to5(3);	// 3 stars
		theTask.dueDateUTC(new Date().getTime() + (1000L * 60L * 60L * 24L * 7L));	// due in one week
		
		return theTask;
	}
	
	
	public void save()
	{
		try
		{
			_state.save();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public String id()
	{
		return _state.getObjectId();
	}
		
	public String name()
	{
		return _state.getString(TASK_LIST_NAME);
	}
	public void name(final String theName)
	{
		// TODO : validate structure
		if((null == theName) || theName.isEmpty()) throw new IllegalArgumentException();
		
		_state.put(TASK_LIST_NAME, theName);
	}
		
	public String sharedBy()
	{
		return _state.getString(SHARED_BY);
	}
	public void sharedBy(final String theSharedBy)
	{
		if((null != theSharedBy) && !theSharedBy.isEmpty())
		{
			_state.put(SHARED_BY, theSharedBy);
		}
		else	// null or empty
		{
			// remove the field if it is null or empty
			_state.remove(SHARED_BY);
		}
		
	}
	
	private final void _validateTaskName(final String theTaskName)
	{
		// don't allow reserved keys
		if(TASK_LIST_NAME.equals(theTaskName)) throw new IllegalArgumentException("getTask: use of reserved field name, " + SHARED_BY + ".");
		if(SHARED_BY.equals(theTaskName)) throw new IllegalArgumentException("getTask: use of reserved field name, " + SHARED_BY + ".");
	}
	
	public final int taskCount()
	{
		return _tasks.size();
	}

	/**
	 * Get a named task from the list of tasks.
	 * 
	 * @param theTaskName
	 * @return
	 */
	public Task getTaskByName(final String theTaskName)
	{
		return getTaskByIndex(getTaskIndex(theTaskName));
	}
	public Task getTaskByIndex(final int theIndex)
	{
		if((theIndex >= 0) && (theIndex < this.taskCount()))
		{
			return _tasks.get(theIndex);
		}
		return null;
	}
	private int getTaskIndex(final String theTaskName)
	{
		_validateTaskName(theTaskName);
		
		final Task theTask = _taskMap.get(theTaskName);
		if(null != theTask)
		{
			return _tasks.indexOf(theTask);
		}
		return -1;
	}

	/* package */ void _setTask(final Task theTask)
	{
		if(null != theTask)
		{
			// don't allow reserved keys
			final String theTaskName = theTask.name();
			if(TASK_LIST_NAME.equals(theTaskName)) throw new IllegalArgumentException("getTask: use of reserved field name, " + SHARED_BY + ".");
			if(SHARED_BY.equals(theTaskName)) throw new IllegalArgumentException("getTask: use of reserved field name, " + SHARED_BY + ".");
			
			final int i = getTaskIndex(theTask.name());
			if(i >= 0)
			{
				_tasks.set(i, theTask);
			}
			else
			{
				_tasks.add(theTask);
			}
			_taskMap.put(theTask.name(), theTask);
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
			removeTaskByIndex(getTaskIndex(theTaskName));
		}
	}
	private void removeTaskByIndex(final int theIndex)
	{
		if((theIndex >= 0) && (theIndex < taskCount()))
		{
			_taskMap.remove(_tasks.get(theIndex).name());
			_tasks.remove(theIndex);
		}
		
		// TODO: PERSIST
	}
		
	
	/**
	 * Create a new ArrayAdapter attached to the task list
	 * 
	 * @param context
	 * @param theItemLayoutId
	 * @param theListener
	 * @return
	 */
	public TaskListAdapter newTaskListAdapter(
			final Context context, 
			final int theItemLayoutId, 
			TaskListListener theListener)
	{
		return new TaskListAdapter(context, theItemLayoutId, _tasks, theListener);
	}
	
}
