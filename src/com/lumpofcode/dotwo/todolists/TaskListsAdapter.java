package com.lumpofcode.dotwo.todolists;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.TaskList;
import com.lumpofcode.dotwo.model.TaskLists;

public class TaskListsAdapter extends ArrayAdapter<TaskList>
{
	private final OnTaskListClickListener _clickListener;
	

	public TaskListsAdapter(
			final Context context, 
			final ArrayList<TaskList> theTaskListArray,
			final OnTaskListClickListener theClickListener)
	{
		super(context, 0, theTaskListArray);
		_clickListener = theClickListener;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent)
	{
		View theItemView = convertView;
		if(null == theItemView)
		{
			final LayoutInflater theInflator = LayoutInflater.from(getContext());
			theItemView = theInflator.inflate(R.layout.todo_lists_item, null);
		}
		
		// fill in the view
		final TaskList theTaskList = this.getItem(position);
		final TextView theNameView = (TextView)theItemView.findViewById(R.id.textList);
		theNameView.setText(theTaskList.name());
		
		final TextView theStatusView = (TextView)theItemView.findViewById(R.id.textListDetail);
		theStatusView.setText(theItemView.getContext().getString(R.string.template_list_status).replace("{LIST_SIZE}", Integer.toString(TaskLists.taskListCount())));
		
		//
		// set the tag to be the name of the list
		//
		theItemView.setTag(theTaskList.name());
		
		//
		// add click listener on view that notifies ViewPager to switch page
		//
		theItemView.setOnClickListener(taskListClickListener());
		
		return theItemView;
	}
	
	private View.OnClickListener _taskListClickListener = null;
	private View.OnClickListener taskListClickListener()
	{
		if(null == _taskListClickListener)
		{
			_taskListClickListener = new View.OnClickListener()
			{
				@Override
				public void onClick(View theView)
				{
					final String theTaskListName = (String)theView.getTag();
					_clickListener.onTaskListClick(theTaskListName);
				}
				
			};
		}
		return _taskListClickListener;
	}

}
