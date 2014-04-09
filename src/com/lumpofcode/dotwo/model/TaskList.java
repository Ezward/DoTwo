package com.lumpofcode.dotwo.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * @author Ed
 * 
 * TaskList is actually a set, implemented using a ParseObject.
 *
 */
@ParseClassName("TaskList")
public class TaskList extends ParseObject
{
	private static final String SHARED_BY = "sharedBy";
		
	public String getSharedBy()
	{
		return getString(SHARED_BY);
	}
	public void setSharedBy(final String theSharedBy)
	{
		if((null != theSharedBy) && !theSharedBy.isEmpty())
		{
			put(SHARED_BY, theSharedBy);
		}
		else	// null or empty
		{
			// remove the field if it is null or empty
			remove(SHARED_BY);
		}
		
	}
	
	
	/**
	 * Get a named task from the list of tasks.
	 * 
	 * @param theTaskName
	 * @return
	 */
	public Task getTask(final String theTaskName)
	{
		if(SHARED_BY.equals(theTaskName)) throw new IllegalArgumentException("getTask: use of reserved field name, " + SHARED_BY + ".");
		
		//
		// tasks are stored as JSONObjects
		// we make a defensive copy
		//
		return Task.fromJSONObject(getJSONObject(theTaskName));
	}
	public void setTask(final String theTaskName, final Task theTask)
	{
		if(SHARED_BY.equals(theTaskName)) throw new IllegalArgumentException("getTask: use of reserved field name, " + SHARED_BY + ".");
		if(null != theTask)
		{
			put(theTaskName, theTask.toJSONObject());
		}
		else	// null or empty
		{
			// remove the field if it is null or empty
			remove(theTaskName);
		}
		
	}
	
}
