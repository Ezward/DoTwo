package com.lumpofcode.dotwo.todolist;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
			final View theTaskView,
			TaskListAdapter theAdapter,
			String theTaskListName,
			String theTaskName,
			boolean theCheckedState)
	{
		// toast to the user
		_toast(theCheckedState ? _toastDone() : _toastNotDone());

		if (theCheckedState)
		{
			animateTaskDone(theTaskView, theAdapter, theTaskListName, theTaskName);
		}
		else
		// unchecked
		{
			setTaskDoneChecked(theAdapter, theTaskListName, theTaskName, theCheckedState);
		}
	}

	/**
	 * Run an animation and then make the task done.
	 * 
	 * @param theTaskView
	 * @param theAdapter
	 * @param theTaskListName
	 * @param theTaskName
	 * @return
	 */
	private final TaskAnimationHandler animateTaskDone(
			final View theTaskView,
			final TaskListAdapter theAdapter,
			final String theTaskListName,
			final String theTaskName)
	{
		final TaskAnimationHandler theHandler = new TaskAnimationHandler()
		{
			@Override
			public void onAnimationFinished()
			{
				setTaskDoneChecked(theAdapter, theTaskListName, theTaskName, true);
			}
		};
		return theHandler.startAnimation(theTaskView, R.anim.fade_out);
	}

	/**
	 * Set the task done state and save it.
	 * 
	 * @param theAdapter
	 * @param theTaskListName
	 * @param theTaskName
	 * @param theCheckedState
	 */
	private final void setTaskDoneChecked(
			TaskListAdapter theAdapter,
			String theTaskListName,
			String theTaskName,
			boolean theCheckedState)
	{
		final TaskList theTaskList = TaskLists.getTaskListByName(theTaskListName);
		if (null != theTaskList)
		{
			final Task theTask = theTaskList.getTaskByName(theTaskName);
			if (null != theTask)
			{

				theTask.isDone(theCheckedState);
				if (theTask.isToday())
				{
					TodayList.removeTask(theTask);
					TodayList.notifyDataChanged();
				}
				theTaskList.removeTask(theTask);
				theTaskList.notifyDataSetChanged();

				theTask.save();
			}
		}
	}

	@Override
	public void onTaskTodayCheckedChanged(
			final View theTaskView,
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
		if (theTask.isToday())
		{
			TodayList.addTask(theTask);	// add the task to the today list
			TodayList.notifyDataChanged();
		}
		else
		{
			TodayList.removeTask(theTask);
			TodayList.notifyDataChanged();
		}
		// don't need to TodayList.notifyDataChanged(), add/RemoveTask does this
		// NOTE: notify though theTaskList rather than adapter,
		// so we know it get's routed correctly.
		theTaskList.notifyDataSetChanged();

		theTask.save();
	}

	@Override
	public void onTaskClick(
			final View theTaskView,
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
		// We will not get called at all if it is cancelled or if no changes are made
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
		if (requestCode == TaskDetailsDialog.TASK_DETAILS_DIALOG)
		{
			//
			// a task has been edited, save it and notify the today list
			//
			final TaskList theList = TaskLists.getTaskListByName(data.getExtras().getString(
					TaskDetailsDialog.ARG_TASK_LIST_NAME));
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
	private int			_toastTodayIndex	= Integer.MAX_VALUE;
	private String[]	_toastToday			= null;

	private final String _toastToday()
	{
		if (null == _toastToday)
		{
			_toastToday = getResources().getStringArray(R.array.toast_task_today);
		}
		if (_toastTodayIndex >= _toastToday.length)
		{
			ArrayUtils.stringShuffle(_toastToday);
			_toastTodayIndex = 0;
		}
		return _toastToday[_toastTodayIndex++];
	}

	private int			_toastNotTodayIndex	= Integer.MAX_VALUE;
	private String[]	_toastNotToday		= null;

	private final String _toastNotToday()
	{
		if (null == _toastNotToday)
		{
			_toastNotToday = getResources().getStringArray(R.array.toast_task_not_today);
		}
		if (_toastNotTodayIndex >= _toastNotToday.length)
		{
			ArrayUtils.stringShuffle(_toastNotToday);
			_toastNotTodayIndex = 0;
		}
		return _toastNotToday[_toastNotTodayIndex++];
	}

	private int			_toastDoneIndex	= Integer.MAX_VALUE;
	private String[]	_toastDone		= null;

	private final String _toastDone()
	{
		if (null == _toastDone)
		{
			_toastDone = getResources().getStringArray(R.array.toast_task_done);
		}
		if (_toastDoneIndex >= _toastDone.length)
		{
			ArrayUtils.stringShuffle(_toastDone);
			_toastDoneIndex = 0;
		}
		return _toastDone[_toastDoneIndex++];
	}

	private int			_toastNotDoneIndex	= Integer.MAX_VALUE;
	private String[]	_toastNotDone		= null;

	private final String _toastNotDone()
	{
		if (null == _toastNotDone)
		{
			_toastNotDone = getResources().getStringArray(R.array.toast_task_not_done);
		}
		if (_toastNotDoneIndex >= _toastNotDone.length)
		{
			ArrayUtils.stringShuffle(_toastNotDone);
			_toastNotDoneIndex = 0;
		}
		return _toastNotDone[_toastNotDoneIndex++];
	}

	/**
	 * Show a toast. Interupt current toast if necessary.
	 * 
	 * @param theMessage
	 *            string to show
	 */
	private final void _toast(final String theMessage)
	{
		if (null == _lastToast_)
		{
			_lastToast_ = Toast.makeText(getActivity(), theMessage, Toast.LENGTH_SHORT);
		}
		if (null != theMessage)
		{
			_lastToast_.setText(theMessage);
			_lastToast_.show();
		}
	}

	private Toast	_lastToast_	= null;

	/**
	 * @author Ed
	 * 
	 *         Class to handle the animation of a task item.
	 * 
	 */
	private class TaskAnimationHandler extends Handler
	{
		private View			_taskView	= null;
		private final Handler	_handler	= new Handler();
		private final Runnable	_runnable	= new Runnable()
											{
												public void run()
												{
													TaskAnimationHandler.this.finishAnimation();
												}
											};

		/**
		 * Start an animation. Any previous animation will be cancelled.
		 * 
		 * NOTE: if you want a previous animation to be finalized before starting the new animation, call finishAnimation();
		 * 
		 * @param theTaskView
		 * @param theAnimationId
		 */
		public TaskAnimationHandler startAnimation(final View theTaskView, final int theAnimationId)
		{
			// cancel any previous animation
			cancelAnimation();

			final Animation theAnimation = AnimationUtils.loadAnimation(theTaskView.getContext(), theAnimationId);
			_taskView = theTaskView;
			_taskView.startAnimation(theAnimation);
			_handler.postDelayed(_runnable, theAnimation.getDuration());

			return this;
		}

		public TaskAnimationHandler cancelAnimation()
		{
			if (null != _taskView)
			{
				_stopAnimation();
				_taskView = null;
			}

			return this;
		}

		public TaskAnimationHandler finishAnimation()
		{
			if (null != _taskView)
			{
				_stopAnimation();
				onAnimationFinished();
				_taskView = null;
			}

			return this;
		}

		/**
		 * @return the View that is animating or null if the animation if finished or cancelled.
		 */
		public View getTaskView()
		{
			return _taskView;
		}

		/**
		 * called when animation finished. NOTE: NOT called is animation is cancelled.
		 */
		public void onAnimationFinished()
		{

		}

		private void _stopAnimation()
		{
			if (null != _taskView)
			{
				_handler.removeCallbacks(_runnable);
				_taskView.getAnimation().cancel();
			}
		}
	}

}
