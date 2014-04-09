package com.lumpofcode.dotwo.app;

import com.lumpofcode.dotwo.model.Task;
import com.lumpofcode.dotwo.model.TaskList;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Application;

public class DoTwoApplication extends Application
{

	@Override
	public void onCreate()
	{
		super.onCreate();

		// intialize parse with out application key
		Parse.initialize(this, "GiQ8f7CA2w6xC1Hh8UD4loGGDvwP5JcAKVDLyn0d", "gGxjXOUu8b9sM1wpOztI41MOfZVAn0w7hBSwPnBB");

		// register our models
		ParseObject.registerSubclass(TaskList.class);

		//
		// TODO: remove this test
		//
		TaskList theTaskList = new TaskList();
		theTaskList.setTask("testList", Task.newTask("testTask"));
		try
		{
			theTaskList.save();
		}
		catch (ParseException e1)
		{
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}

		ParseQuery<TaskList> theTaskListQuery = ParseQuery.getQuery("TaskList");
		theTaskListQuery.getInBackground(
			theTaskList.getObjectId(),
			new GetCallback<TaskList>()
			{
				public void done(TaskList object, ParseException e)
				{
					if (e == null)
					{
						// object is the task
						Task theTask = object.getTask("testList");
						if(!"testTask".equals(theTask.getName()))
						{
							throw new RuntimeException();
						}
						
					}
					else
					{
						// something went wrong
					}
				}
			});
	}

}
