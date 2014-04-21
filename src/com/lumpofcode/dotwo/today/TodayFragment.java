package com.lumpofcode.dotwo.today;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.TodayList;
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
			String theTaskName,
			boolean theSelectedState)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskTodayCheckedChanged(
			TaskListAdapter theAdapter,
			String theTaskName,
			boolean theSelectedState)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTaskClick(TaskListAdapter theAdapter, String theTaskName)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
