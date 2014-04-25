package com.lumpofcode.dotwo.todolist;

import java.util.List;

import android.content.Context;
import android.util.SparseArray;
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
	public static final int TASK_LIST_NAME = "TASK_LIST_NAME".hashCode();
	public static final int TASK_NAME = "TASK_NAME".hashCode();
	
	private final int itemLayoutId;
	private TaskListListener _listener;
	
	public interface TaskListListener
	{
		/**
		 * Handle click on a task.
		 * 
		 * @param theAdapter, the TaskListAdapter
		 * @param theTaskListName the name of the list that owns the task
		 * @param theTaskName, the name of the task
		 */
		public void onTaskClick(
				final TaskListAdapter theAdapter, 
				final String theTaskListName,
				final String theTaskName);
		
		/**
		 * Handle check changes on done toggle.
		 * 
		 * @param theAdapter, the TaskListAdapter
		 * @param theTaskListName the name of the list that owns the task
		 * @param theTaskName, the name of the task
		 * @param theCheckedState, true if checked, false if not
		 */
		public void onTaskDoneCheckedChanged(
				final TaskListAdapter theAdapter, 
				final String theTaskListName,
				final String theTaskName, 
				final boolean theCheckedState);
		
		/**
		 * Handle check changes on today toggle.
		 * 
		 * @param theAdapter, the TaskListAdapter
		 * @param theTaskListName the name of the list that owns the task
		 * @param theTaskName, the name of the task
		 * @param theCheckedState, true if selected, false if not
		 */
		public void onTaskTodayCheckedChanged(
				final TaskListAdapter theAdapter, 
				final String theTaskListName,
				final String theTaskName, 
				final boolean theCheckedState);
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
			final List<Task> theTaskArray,
			final TaskListListener theListener)
	{
		super(context, 0, theTaskArray);
		
		// adding and inserting into the list does not automatically call notifyDataSetChanged.  We do that ourselves.
		this.setNotifyOnChange(false);	
		
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
			theItemView.setTag(new SparseArray<String>());
		}
		theItemView.setOnClickListener(this);
		
		// fill in the view
		final Task theTask = this.getItem(position);
		final TextView theNameView = (TextView)theItemView.findViewById(R.id.textTask);
		theNameView.setText(theTask.name());
		
		//
		// NOTE: setChecked() calls the onCheckChanged listener, so we make sure
		//       that there is no listener when we call it to avoid unwanted callbacks
		//
		ToggleButton theToggleDone = (ToggleButton)theItemView.findViewById(R.id.toggleDone);
		theToggleDone.setOnCheckedChangeListener(null);	// avoid the onCheckedChange() that happens when setChecked() is called
		theToggleDone.setChecked(theTask.isDone());
		theToggleDone.setOnCheckedChangeListener(this);
		theToggleDone.setTag(theItemView.getTag());	// so we can get task name is onCheckedChanged
		
		ToggleButton theToggleToday = (ToggleButton)theItemView.findViewById(R.id.toggleToday);
		theToggleToday.setOnCheckedChangeListener(null); // avoid the onCheckedChange() that happens when setChecked() is called
		theToggleToday.setChecked(theTask.isToday());
		theToggleToday.setOnCheckedChangeListener(this);
		theToggleToday.setTag(theItemView.getTag());	// so we can get task name is onCheckedChanged
		
		// stash the task name in the item's tag so we can quickly look it up
		// this will also copy it into the toggle buttons' tag
		setFieldIntoTag(theItemView, TASK_LIST_NAME, theTask.list().name());
		setFieldIntoTag(theItemView, TASK_NAME, theTask.name());
		
		return theItemView;
	}
	
	/**
	 * This gets a field from the View's getTag() with the assumption
	 * that the tag contains a SparseArray.
	 * 
	 * @param theView, the View whose getTag contains a SparseArray
	 * @param theKey, the integer key to lookup in the SparseArray
	 * @return the value or null if it is ot in the SparseArray
	 *         or null if the getTag does not contain a SparseArray.
	 */
	public final Object getFieldFromTag(final View theView, final int theKey)
	{
		if(null != theView)
		{
			final Object theTag = theView.getTag();
			if(theTag instanceof SparseArray)
			{
				@SuppressWarnings("unchecked")
				final SparseArray<String> theArray = (SparseArray<String>)theTag;
				return theArray.get(theKey);
			}
		}
		return null;
	}
	public final void setFieldIntoTag(final View theView, final int theKey, final String theValue)
	{
		if(null != theView)
		{
			final Object theTag = theView.getTag();
			if(theTag instanceof SparseArray)
			{
				@SuppressWarnings("unchecked")
				final SparseArray<String> theArray = (SparseArray<String>)theTag;
				theArray.put(theKey, theValue);
			}
		}
	}
	
	@Override
	public void onClick(View theView)
	{
		if(null != _listener)
		{
			_listener.onTaskClick(
					this, 
					(String)this.getFieldFromTag(theView, TASK_LIST_NAME), 
					(String)this.getFieldFromTag(theView, TASK_NAME));
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton theButton, boolean theCheckedState)
	{
		if(null != _listener)
		{
			switch(theButton.getId())
			{
				case R.id.toggleDone:
				{
					_listener.onTaskDoneCheckedChanged(
							this, 
							(String)this.getFieldFromTag(theButton, TASK_LIST_NAME), 
							(String)this.getFieldFromTag(theButton, TASK_NAME),
							theCheckedState);
					return;
				}
				case R.id.toggleToday:
				{
					_listener.onTaskTodayCheckedChanged(
							this, 
							(String)this.getFieldFromTag(theButton, TASK_LIST_NAME), 
							(String)this.getFieldFromTag(theButton, TASK_NAME),
							theCheckedState);
					return;
				}
			}
		}
	}
	
}
