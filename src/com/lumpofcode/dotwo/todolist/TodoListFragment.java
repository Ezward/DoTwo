package com.lumpofcode.dotwo.todolist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.TaskLists;

public class TodoListFragment extends Fragment
{
	public static final String ARG_TODO_LIST_NAME = "ARG_TODO_LIST_NAME";
	public static final String ARG_TODO_LIST_TYPE = "ARG_TODO_LIST_TYPE";
	public enum TodoListType
	{
		TODAY,
		SHARED,
		ALL;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		final View theView = inflater.inflate(R.layout.fragment_todo_list, container, false);
		
		// create and attach the task list adapter
		final TaskListAdapter theAdapter = TaskLists.getTaskListByIndex(0).newTaskListAdapter(theView.getContext(), R.layout.todo_item);
		final ListView theListView = (ListView)theView.findViewById(R.id.listTodos);
		theListView.setAdapter(theAdapter);
		
		return theView;
	}

}
