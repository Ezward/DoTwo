package com.lumpofcode.dotwo.model;

import java.util.ArrayList;

import android.content.Context;

import com.lumpofcode.dotwo.todolist.TaskListAdapter;
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
	public Task getTask(final String theTaskName)
	{
		_validateTaskName(theTaskName);

		final int theCount = _tasks.size();
		for(int i = 0; i < theCount; i += 1)
		{
			final Task theCandidate = _tasks.get(i);
			if(theTaskName.equals(theCandidate.name())) return theCandidate;
		}
		
		return null;
	}
	private int getTaskIndex(final String theTaskName)
	{
		_validateTaskName(theTaskName);

		final int theCount = _tasks.size();
		for(int i = 0; i < theCount; i += 1)
		{
			final Task theCandidate = _tasks.get(i);
			if(theTaskName.equals(theCandidate.name())) return i;
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
			_tasks.remove(theIndex);
		}
		
		// TODO: PERSIST
	}
		
	
	/**
	 * Create a new ArrayAdapter attached to the task list
	 * 
	 * @param context
	 * @param theItemLayoutId
	 * @return
	 */
	public TaskListAdapter newTaskListAdapter(final Context context, final int theItemLayoutId)
	{
		return new TaskListAdapter(context, theItemLayoutId, _tasks);
	}
	
}
