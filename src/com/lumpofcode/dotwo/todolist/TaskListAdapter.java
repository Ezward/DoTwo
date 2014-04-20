package com.lumpofcode.dotwo.todolist;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.Task;

public class TaskListAdapter extends ArrayAdapter<Task> implements OnClickListener, OnCheckedChangeListener
{
	private final int itemLayoutId;
	private TaskListListener _listener;
	
	public interface TaskListListener
	{
		/**
		 * Handle click on a task.
		 * 
		 * @param theAdapter
		 * @param theTaskName
		 */
		public void onTaskClick(
				final TaskListAdapter theAdapter, 
				final String theTaskName);
		
		/**
		 * Handle check changes on done toggle.
		 * 
		 * @param theAdapter, the TaskListAdapter
		 * @param theTaskName, the name of the task
		 * @param theSelectedState, true if selected, false if not
		 */
		public void onTaskDoneCheckedChanged(
				final TaskListAdapter theAdapter, 
				final String theTaskName, 
				final boolean theSelectedState);
		
		/**
		 * Handle check changes on today toggle.
		 * 
		 * @param theAdapter, the TaskListAdapter
		 * @param theTaskName, the name of the task
		 * @param theSelectedState, true if selected, false if not
		 */
		public void onTaskTodayCheckedChanged(
				final TaskListAdapter theAdapter, 
				final String theTaskName, 
				final boolean theSelectedState);
	}
	
	/**
	 * constructor for an array adapter for an array of Task items.
	 * 
	 * @param context
	 * @param theItemLayoutId,	the layout to use for the task items
	 * @param theTaskArray, 	the array of tasks
	 * @param theListener,  the listener for events on the array items
	 */
	public TaskListAdapter(
			final Context context, 
			final int theItemLayoutId, 
			final ArrayList<Task> theTaskArray,
			final TaskListListener theListener)
	{
		super(context, 0, theTaskArray);
		itemLayoutId = theItemLayoutId;
		_listener = theListener;
	}
	
	

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent)
	{
		View theItemView = convertView;
		if(null == theItemView)
		{
			final LayoutInflater theInflator = LayoutInflater.from(getContext());
			theItemView = theInflator.inflate(itemLayoutId, null);
		}
		theItemView.setOnClickListener(this);
		
		// fill in the view
		final Task theTask = this.getItem(position);
		final TextView theNameView = (TextView)theItemView.findViewById(R.id.textTask);
		theNameView.setText(theTask.name());
		
		ToggleButton theToggleDone = (ToggleButton)theItemView.findViewById(R.id.toggleDone);
		theToggleDone.setSelected(theTask.isDone());
		theToggleDone.setOnCheckedChangeListener(this);
		theToggleDone.setTag(theTask.name());	// so we can get task name is onCheckedChanged
		
		ToggleButton theToggleToday = (ToggleButton)theItemView.findViewById(R.id.toggleToday);
		theToggleToday.setSelected(theTask.isToday());
		theToggleToday.setOnCheckedChangeListener(this);
		theToggleToday.setTag(theTask.name());	// so we can get task name is onCheckedChanged
		
		// stash the task name in the item's tag so we can quickly look it up
		theItemView.setTag(theTask.name());
		return theItemView;
	}
	
	@Override
	public void onClick(View theView)
	{
		if(null != _listener)
		{
			_listener.onTaskClick(this, (String)theView.getTag());
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton theButton, boolean theSelectedState)
	{
		if(null != _listener)
		{
			switch(theButton.getId())
			{
				case R.id.toggleDone:
				{
					_listener.onTaskDoneCheckedChanged(
							this, 
							(String)theButton.getTag(), 
							theSelectedState);
					return;
				}
				case R.id.toggleToday:
				{
					_listener.onTaskTodayCheckedChanged(
							this, 
							(String)theButton.getTag(), 
							theSelectedState);
					return;
				}
			}
		}
	}
	
}
