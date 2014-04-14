package com.lumpofcode.dotwo.today;

import java.util.ArrayList;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.Task;
import com.lumpofcode.dotwo.todolist.TaskListAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TodayFragment extends Fragment
{
	private ArrayList<Task> _tasks = new ArrayList<Task>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		final View theView = inflater.inflate(R.layout.fragment_today, container, false);
		
		final TaskListAdapter theAdapter = new TaskListAdapter(theView.getContext(), R.layout.todo_item, _tasks);
		final ListView theListView = (ListView)theView.findViewById(R.id.listTodoToday);
		theListView.setAdapter(theAdapter);
		
		return theView;
	}

}
