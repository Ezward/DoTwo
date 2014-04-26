package com.lumpofcode.dotwo.today;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.TodayList;
import com.lumpofcode.dotwo.todolist.AbstractTaskListFragment;
import com.lumpofcode.dotwo.todolist.TaskListAdapter;
import com.lumpofcode.view.Pageable;

public class TodayFragment extends AbstractTaskListFragment
{
	//
	// attach an adapter to the TodayList
	//
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		final View theView = inflater.inflate(R.layout.fragment_today, container, false);
		
		final View theEmptyView = theView.findViewById(R.id.emptyTodayPanel);
		final Button theEmptyButton = (Button)theEmptyView.findViewById(R.id.buttonTodayEmpty);
		theEmptyButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View theButton)
			{
				//
				// the activity is the controller for this fragment,
				// call back out to it do do the work of paging
				//
				final FragmentActivity theActivity = getActivity();
				if(theActivity instanceof Pageable)
				{
					final Pageable thePager = (Pageable)theActivity;
					thePager.showPageByIndex(1);	// select the first activity.
				}
			}
		});

		final TaskListAdapter theAdapter = TodayList.attachAdapter(theView.getContext(), R.layout.todo_item, this);
		final ListView theListView = (ListView)theView.findViewById(R.id.listTodoToday);
		theListView.setAdapter(theAdapter);
		theListView.setEmptyView(theEmptyView);	// the list will manage showing/hiding the empty view.
		
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
	
	public void onDestroy()
	{
		//
		// detach from our adapter
		// NOTE: this may be called without onDestroyView being called
		//       first because of our loading and the progress dialog.
		//
		TodayList.detachAdapter();
		super.onDestroy();
	}
	
}
