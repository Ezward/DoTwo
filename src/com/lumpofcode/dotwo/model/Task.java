package com.lumpofcode.dotwo.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.lumpofcode.date.TimeUtils;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Task")
public final class Task extends ParseObject implements Comparable<Task>
{
	/* package private */ static final String LIST = "LIST";
	/* package private */ static final String NAME = "NAME";
	private static final String IS_DONE = "IS_DONE";
	private static final String IS_TODAY = "IS_TODAY";
	private static final String DESCRIPTION = "DESCRIPTION";
	private static final String IMPORTANCE = "IMPORTANCE";
	private static final String DUE_DATE = "DUE_DATE";
 	
	// TODO: add other fields
	
	/**
	 * Do NOT call this constructor directly.
	 * The public no-arg constructor is required by Parse.
	 * Instead, use the factory method of the TaskList that
	 * you want the Task to be part of, like this;
	 *     TaskList theTaskList = TaskLists.newTaskList("testList");
	 *     Task theTask = theTaskList.newTask("testTask");
	 */
	public Task() 
	{
		super();
	}	
	
	
	public String list()
	{
		return ((ParseObject)this.get("LIST")).getString(TaskList.TASK_LIST_NAME);
	}
	
	public String id()
	{
		return getObjectId();
	}
	
	public String name()
	{
		return this.getString(NAME);
	}

	public boolean isDone()
	{
		return this.getBoolean(IS_DONE);
	}

	public void isDone(boolean done)
	{
		this.put(IS_DONE, done);
	}
	
	public String description()
	{
		return this.getString(DESCRIPTION);
	}
	
	public void description(final String theDescription)
	{
		this.put(DESCRIPTION, theDescription);
	}
	
	public int importance1to5()
	{
		return this.getInt(IMPORTANCE);
	}
	
	public void importance1to5(final int theImportance1to5)
	{
		this.put(IMPORTANCE, Integer.valueOf(theImportance1to5));
	}
	

	public boolean isToday()
	{
		return this.getBoolean(IS_TODAY);
	}

	public void isToday(boolean today)
	{
		this.put(IS_TODAY, today);
	}
	
	public long dueDateUTC()
	{
		return this.getLong(DUE_DATE);
	}
	
	public void dueDateUTC(final long theDueDateUTC)
	{
		this.put(DUE_DATE, Long.valueOf(theDueDateUTC));
	}
	
	
	public final int priority(final long theCurrentTime)
	{
		return this.importance1to5() * this.urgency1to5(theCurrentTime);
	}
	private final int urgency1to5(final long theCurrentTime)
	{
		final long theDaysToGo = TimeUtils.timeSpanInDays(dueDateUTC() - theCurrentTime);
		if(theDaysToGo <= 1)
		{
			return 5;	// highest urgency;
		}
		if(theDaysToGo <= 2)
		{
			return 4;
		}
		if(theDaysToGo <= 5)
		{
			return 3;
		}
		if(theDaysToGo <= 10)
		{
			return 2;
		}
		return 1;
	}
	
	

	/**
	 * Factory to construct a Task from a JSONObject
	 * 
	 * @param theJSONObject
	 * @return a Task constructed from the fields of the JSONObject
	 * @throws IllegalArgumentException if JSONObject does not have a "name" field
	 */
	public static final Task fromJSONObject(final JSONObject theJSONObject, final TaskList theParentList)
	{
		if(null == theJSONObject) throw new IllegalArgumentException();
		if(null == theParentList) throw new IllegalArgumentException();
		try
		{
			// list in the task must match theParentList
			if(!theParentList.id().equals(theJSONObject.getString(LIST))) throw new IllegalStateException();
			
			Task theTask = theParentList.newTask(theJSONObject.getString(NAME));
			if(theJSONObject.has(IS_DONE)) theTask.isDone(theJSONObject.getBoolean(IS_DONE));
			
			return theTask;
		}
		catch (JSONException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @return a JSONObject representation of the Task
	 */
	public final JSONObject toJSONObject()
	{
		try
		{
			final JSONObject theJSONObject = new JSONObject();
			theJSONObject.put(NAME, this.name());
			theJSONObject.put(IS_DONE, this.isDone());
			
			return theJSONObject;
		}
		catch (JSONException e)
		{
			throw new RuntimeException(e);
		}
	}


	@Override
	public int compareTo(Task that)
	{
		// default compare is by name
		if(null == that) return 1;
		return this.name().compareTo(that.name());
	}
}
