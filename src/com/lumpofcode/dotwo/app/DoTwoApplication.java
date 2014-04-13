package com.lumpofcode.dotwo.app;

import android.app.Application;

import com.lumpofcode.dotwo.model.Task;
import com.lumpofcode.dotwo.model.TaskList;
import com.lumpofcode.dotwo.model.TaskLists;
import com.parse.Parse;
import com.parse.ParseObject;

public class DoTwoApplication extends Application
{

	@Override
	public void onCreate()
	{
		super.onCreate();

		// intialize parse with out application key
		Parse.initialize(this, "GiQ8f7CA2w6xC1Hh8UD4loGGDvwP5JcAKVDLyn0d", "gGxjXOUu8b9sM1wpOztI41MOfZVAn0w7hBSwPnBB");

		// register our models
		ParseObject.registerSubclass(Task.class);

		//
		// TODO: remove this test
		//
		// setup some tasks in the singleton to test the views
		TaskList theTaskList = TaskLists.newTaskList("testList");
		theTaskList.newTask("testTask");
		theTaskList.newTask("anotherTestTask");
//		
//		try
//		{
//			theTaskList.save();
//		}
//		catch (Exception e1)
//		{
//			e1.printStackTrace();
//			throw new RuntimeException(e1);
//		}
//
//		ParseQuery<TaskList> theTaskListQuery = ParseQuery.getQuery("TaskList");
//		theTaskListQuery.getInBackground(
//			theTaskList.getObjectId(),
//			new GetCallback<TaskList>()
//			{
//				public void done(TaskList object, ParseException e)
//				{
//					if (e == null)
//					{
//						// object is the task
//						Task theTask = object.getTask("testList");
//						if(!"testTask".equals(theTask.name()))
//						{
//							throw new RuntimeException();
//						}
//						
//					}
//					else
//					{
//						// something went wrong
//					}
//				}
//			});
		
		
	}

}
