package com.lumpofcode.dotwo.todolist;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.lumpofcode.dotwo.model.Task;
import com.lumpofcode.dotwo.model.TaskList;
import com.lumpofcode.dotwo.model.TaskLists;
import com.lumpofcode.dotwo.model.TodayList;
import com.lumpofcode.dotwo.newtodo.TaskDetailsDialog;
import com.lumpofcode.dotwo.todolist.TaskListAdapter.TaskListListener;

public abstract class AbstractTaskListFragment extends Fragment implements TaskListListener
{

	//
	// handle when a checkbox on a task item is toggled
	//
	@Override
	public void onTaskDoneCheckedChanged(
			TaskListAdapter theAdapter,
			String theTaskListName,
			String theTaskName,
			boolean theCheckedState)
	{
		final TaskList theTaskList = TaskLists.getTaskListByName(theTaskListName);
		final Task theTask = theTaskList.getTaskByName(theTaskName);
		
		theTask.isDone(theCheckedState);
		if(theTask.isToday())
		{
			TodayList.notifyDataChanged();
		}
		theTaskList.notifyDataSetChanged();
		
		theTask.save();
	}

	@Override
	public void onTaskTodayCheckedChanged(
			TaskListAdapter theAdapter,
			String theTaskListName,
			String theTaskName,
			boolean theCheckedState)
	{
		final TaskList theTaskList = TaskLists.getTaskListByName(theTaskListName);
		final Task theTask = theTaskList.getTaskByName(theTaskName);
		
		theTask.isToday(theCheckedState);
		if(theTask.isToday())
		{
			TodayList.addTask(theTask);	// add the task to the today list
		}
		else
		{
			TodayList.removeTask(theTask);
		}
		// don't need to TodayList.notifyDataChanged(), add/RemoveTask does this
		// NOTE: notify though theTaskList rather than adapter,
		//       so we know it get's routed correctly.
		theTaskList.notifyDataSetChanged();	
		
		theTask.save();
	}


	@Override
	public void onTaskClick(
			final TaskListAdapter theParent, 
			final String theTaskListName, 
			final String theTaskName)
	{
		// we stashed the list name in the tag
		final TaskList theTaskList = TaskLists.getTaskListByName(theTaskListName);
		final Task theTask = theTaskList.getTaskByName(theTaskName);
		final TaskDetailsDialog theDialog = TaskDetailsDialog.newTaskDetailsDialog(theTask, this);
		theDialog.show(getFragmentManager(), null);
		// NOTE: the dlalog will call onActivityResult() when it finishes with Ok
		//       We will not get called at all if it is cancelled or if no changes are made
	}

	//
	// called when a dialog finishes
	//
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		// 
		// handle the result from dialogs
		//
		if(requestCode == TaskDetailsDialog.TASK_DETAILS_DIALOG)
		{
			//
			// a task has been edited, save it and notify the today list
			//
			final TaskList theList = TaskLists.getTaskListByName(data.getExtras().getString(TaskDetailsDialog.ARG_TASK_LIST_NAME));
			final Task theTask = theList.getTaskByName(data.getExtras().getString(TaskDetailsDialog.ARG_TASK_NAME));
			theTask.save();
			
			// notify the adapter so it redraws the item
			TodayList.notifyDataChanged();	// tell the today list to redraw itself.
			theList.notifyDataSetChanged();
		}
	}

}
