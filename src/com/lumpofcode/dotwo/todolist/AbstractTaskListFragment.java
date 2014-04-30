package com.lumpofcode.dotwo.todolist;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.lumpofcode.array.ArrayUtils;
import com.lumpofcode.dotwo.R;
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
		
		// toast to the user
		_toast(theCheckedState ? _toastDone() : _toastNotDone());

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
		
		// toast to the user
		_toast(theCheckedState ? _toastToday() : _toastNotToday());

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
	
	//
	// Helpers to get shuffled message 
	// to make the user feedback more interesting
	//
	private int _toastTodayIndex = Integer.MAX_VALUE;
	private String[] _toastToday = null;
	private final String _toastToday()
	{
		if(null == _toastToday)
		{
			_toastToday = getResources().getStringArray(R.array.toast_task_today);
		}
		if(_toastTodayIndex >= _toastToday.length)
		{
			ArrayUtils.stringShuffle(_toastToday);
			_toastTodayIndex = 0;
		}
		return _toastToday[_toastTodayIndex++];
	}
	
	private int _toastNotTodayIndex = Integer.MAX_VALUE;
	private String[] _toastNotToday = null;
	private final String _toastNotToday()
	{
		if(null == _toastNotToday)
		{
			_toastNotToday = getResources().getStringArray(R.array.toast_task_not_today);
		}
		if(_toastNotTodayIndex >= _toastNotToday.length)
		{
			ArrayUtils.stringShuffle(_toastNotToday);
			_toastNotTodayIndex = 0;
		}
		return _toastNotToday[_toastNotTodayIndex++];
	}

	private int _toastDoneIndex = Integer.MAX_VALUE;
	private String[] _toastDone = null;
	private final String _toastDone()
	{
		if(null == _toastDone)
		{
			_toastDone = getResources().getStringArray(R.array.toast_task_done);
		}
		if(_toastDoneIndex >= _toastDone.length)
		{
			ArrayUtils.stringShuffle(_toastDone);
			_toastDoneIndex = 0;
		}
		return _toastDone[_toastDoneIndex++];
	}
	
	private int _toastNotDoneIndex = Integer.MAX_VALUE;
	private String[] _toastNotDone = null;
	private final String _toastNotDone()
	{
		if(null == _toastNotDone)
		{
			_toastNotDone = getResources().getStringArray(R.array.toast_task_not_done);
		}
		if(_toastNotDoneIndex >= _toastNotDone.length)
		{
			ArrayUtils.stringShuffle(_toastNotDone);
			_toastNotDoneIndex = 0;
		}
		return _toastNotDone[_toastNotDoneIndex++];
	}

	/**
	 * Show a toast.  Interupt current toast if necessary.
	 * 
	 * @param theMessage string to show
	 */
	private final void _toast(final String theMessage)
	{
		if(null == _lastToast_)
		{
			_lastToast_ = Toast.makeText(getActivity(), theMessage, Toast.LENGTH_SHORT);
		}
		if(null != theMessage)
		{
			_lastToast_.setText(theMessage);
			_lastToast_.show();
		}
	}
	private Toast _lastToast_ = null;
}
