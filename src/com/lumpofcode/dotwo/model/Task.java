package com.lumpofcode.dotwo.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Task")
public final class Task extends ParseObject
{
	/* package private */ static final String LIST = "LIST";
	/* package private */ static final String NAME = "NAME";
	private static final String IS_DONE = "IS_DONE";
	
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
	
	
	public TaskList list()
	{
		return (TaskList)this.get("LIST");
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

	
}
