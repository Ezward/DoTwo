package com.lumpofcode.dotwo.todolist;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.Task;

public class TaskListAdapter extends ArrayAdapter<Task>
{
	final int itemLayoutId;
	
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
		final Task theTask = getItem(position);
		final TextView theNameView = (TextView)theItemView.findViewById(R.id.textTask);
		theNameView.setText(theTask.name());
		
		final ToggleButton theToggle = (ToggleButton)theItemView.findViewById(R.id.toggleDone);
		theToggle.setSelected(theTask.isDone());
		
		return theItemView;
	}
}
