package com.lumpofcode.dotwo.model;

import org.json.JSONException;
import org.json.JSONObject;

public final class Task
{
	private static final String NAME = "name";
	private static final String DONE = "done";
	private String name;
	private boolean done = false;
	
	// TODO: add other fields
	
	// private constructor enforces use of factory
	private Task(final String theName) 
	{
		if((null == theName) || theName.isEmpty()) throw new IllegalArgumentException("Task cannot be constructed; theName is null or empty.");
		name=theName;
	}	
	
	/**
	 * Factory constructor
	 * @param theName must be non-null and non-empty
	 * @return
	 */
	public static final Task newTask(final String theName)
	{
		return new Task(theName);
	}
	
	public String getName()
	{
		return name;
	}

	public boolean isDone()
	{
		return done;
	}

	public void setDone(boolean done)
	{
		this.done = done;
	}

	/**
	 * Factory to construct a Task from a JSONObject
	 * 
	 * @param theJSONObject
	 * @return a Task constructed from the fields of the JSONObject
	 * @throws IllegalArgumentException if JSONObject does not have a "name" field
	 */
	public static final Task fromJSONObject(final JSONObject theJSONObject)
	{
		try
		{
			Task theTask = newTask(theJSONObject.getString(NAME));
			if(theJSONObject.has(DONE)) theTask.setDone(theJSONObject.getBoolean(DONE));
			
			return theTask;
		}
		catch (JSONException e)
		{
			throw new IllegalArgumentException(e);
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
			theJSONObject.put("name", this.name);
			theJSONObject.put("done", this.done);
			
			return theJSONObject;
		}
		catch (JSONException e)
		{
			throw new RuntimeException(e);
		}
	}

	
}
