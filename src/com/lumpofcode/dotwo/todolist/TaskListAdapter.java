package com.lumpofcode.dotwo.todolist;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.Task;

public class TaskListAdapter extends ArrayAdapter<Task>
{
	private final int itemLayoutId;
	private ToggleButton _toggleDone;
	private ToggleButton _toggleToday;
	
	public TaskListAdapter(final Context context, final int theItemLayoutId, final ArrayList<Task> theTaskArray)
	{
		super(context, 0, theTaskArray);
		itemLayoutId = theItemLayoutId;
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
		
		// fill in the view
		final Task theTask = this.getItem(position);
		final TextView theNameView = (TextView)theItemView.findViewById(R.id.textTask);
		theNameView.setText(theTask.name());
		
		_toggleDone = (ToggleButton)theItemView.findViewById(R.id.toggleDone);
		_toggleDone.setSelected(theTask.isDone());
		_toggleDone.setOnCheckedChangeListener(toggleListener());
		
		_toggleToday = (ToggleButton)theItemView.findViewById(R.id.toggleToday);
		_toggleToday.setSelected(theTask.isToday());
		_toggleToday.setOnCheckedChangeListener(toggleListener());
		return theItemView;
	}
	
	private OnCheckedChangeListener __toggleListener = null;
	private OnCheckedChangeListener toggleListener()
	{
		if(null == __toggleListener)
		{
			__toggleListener = new ToggleListener();
		}
		return __toggleListener;
	}
	
	private class ToggleListener implements OnCheckedChangeListener
	{

		@Override
		public void onCheckedChanged(CompoundButton theView, boolean isSelected)
		{
			if(isSelected)
			{
				
			}
		}
		
	}
}
