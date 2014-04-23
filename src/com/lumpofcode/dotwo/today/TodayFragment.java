package com.lumpofcode.dotwo.today;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.Task;
import com.lumpofcode.dotwo.model.TaskList;
import com.lumpofcode.dotwo.model.TaskLists;
import com.lumpofcode.dotwo.model.TodayList;
import com.lumpofcode.dotwo.newtodo.TaskDetailsDialog;
import com.lumpofcode.dotwo.todolist.TaskListAdapter;
import com.lumpofcode.dotwo.todolist.TaskListAdapter.TaskListListener;

public class TodayFragment extends Fragment implements TaskListListener, OnItemLongClickListener
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		final View theView = inflater.inflate(R.layout.fragment_today, container, false);
		
		final TaskListAdapter theAdapter = TodayList.taskListAdapter(theView.getContext(), R.layout.todo_item, this);
		final ListView theListView = (ListView)theView.findViewById(R.id.listTodoToday);
		theListView.setAdapter(theAdapter);
		theListView.setOnItemLongClickListener(this);
		
		return theView;
	}

	@Override
	public void onTaskDoneCheckedChanged(
			TaskListAdapter theAdapter,
			String theTaskListName,
			String theTaskName,
			boolean theSelectedState)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskTodayCheckedChanged(
			TaskListAdapter theAdapter,
			String theTaskListName,
			String theTaskName,
			boolean theSelectedState)
	{
		// TODO Auto-generated method stub
		
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
			// TODO : we want to notify the list that owns that task as well.
			//        we could make the lists be publishers of change events
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
}
