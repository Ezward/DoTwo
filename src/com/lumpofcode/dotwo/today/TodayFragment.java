package com.lumpofcode.dotwo.today;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.TodayList;
import com.lumpofcode.dotwo.todolist.AbstractTaskListFragment;
import com.lumpofcode.dotwo.todolist.TaskListAdapter;

public class TodayFragment extends AbstractTaskListFragment
{
	//
	// attach an adapter to the TodayList
	//
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		final View theView = inflater.inflate(R.layout.fragment_today, container, false);
		
		final TaskListAdapter theAdapter = TodayList.attachAdapter(theView.getContext(), R.layout.todo_item, this);
		final ListView theListView = (ListView)theView.findViewById(R.id.listTodoToday);
		theListView.setAdapter(theAdapter);
		
		return theView;
	}
	
	

	//
	// detach the adapter from the TodayList
	//
	@Override
	public void onDestroyView()
	{
		//
		// detach from our adapter
		//
		TodayList.detachAdapter();
		super.onDestroyView();
	}
	
}
