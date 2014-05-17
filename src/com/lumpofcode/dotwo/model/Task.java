package com.lumpofcode.dotwo.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.lumpofcode.date.TimeUtils;

@Table(name = "Tasks")
public final class Task extends Model implements Comparable<Task>
{
	/* package private */ static final String LIST = "LIST";
	/* package private */ static final String NAME = "NAME";
	
	@Column(name = "TaskList")
	public TaskList _list; 
	
	@Column(name = "name")
	public String _name;
	
	@Column(name = "done")
	public Boolean _done = false;
	
	@Column(name = "description")
	public String _description = "";
	
	@Column(name = "importance")
	public Integer _importance = 3;
	
	@Column(name = "today")
	public Boolean _today = false;
	
	@Column(name = "due")
	public Long _due = System.currentTimeMillis() + TimeUtils.WEEK_IN_MILLIS;;
 		
	//
	// our serialized copy of the persisted id;
	//
	private String _id_ = null;

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
	
	public Task(final TaskList theList, final String theName)
	{
		super();
		
		if(null == theList) throw new IllegalArgumentException("theList is null.");
		if((null == theName) || theName.isEmpty()) throw new IllegalArgumentException("theName is null or empty.");
		_list = theList;
		_name = theName;
	}
	
	/**
	 * @return the primary key for this list or null if task is not persisted.
	 */
	public final String id()
	{
		if(null == _id_)
		{
			//
			// if we have been persisted, then the id is
			// immutable and we can save a locally serialized
			// copy of it so users of the class always
			// treat it as a string.
			//
			final Long theId = this.getId();
			if(null != theId)
			{
				_id_ = theId.toString();
			}
		}
		return _id_;
	}

	public TaskList list()
	{
		return _list;
	}
	
	public String name()
	{
		return _name;
	}
	public void name(final String theName)
	{
		if((null == theName) || theName.isEmpty()) throw new IllegalArgumentException("Task.name cannot be empty or null.");
		_name = theName;
	}

	public boolean isDone()
	{
		return (null != _done) ? _done : false;
	}

	public void isDone(boolean done)
	{
		_done = done;
	}
	
	public String description()
	{
		return (null != _description) ? _description : "";
	}
	
	public void description(final String theDescription)
	{
		_description = theDescription;
	}
	
	public int importance1to5()
	{
		return _importance;
	}
	
	public void importance1to5(final int theImportance1to5)
	{
		_importance = theImportance1to5;
	}
	

	public boolean isToday()
	{
		return _today;
	}

	public void isToday(boolean today)
	{
		_today = today;
	}
	
	public long dueDateUTC()
	{
		return _due;
	}
	
	public void dueDateUTC(final long theDueDateUTC)
	{
		_due = theDueDateUTC;
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
			return 1;	// highest urgency;
		}
		if(theDaysToGo <= 2)
		{
			return 2;
		}
		if(theDaysToGo <= 5)
		{
			return 3;
		}
		if(theDaysToGo <= 10)
		{
			return 4;
		}
		return 5;
	}

	@Override
	public int compareTo(Task that)
	{
		// default compare is by name
		if(null == that) return 1;
		return this.name().compareTo(that.name());
	}
}
