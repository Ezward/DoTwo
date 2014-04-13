package com.lumpofcode.dotwo.todoactivity;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.TaskLists;
import com.lumpofcode.dotwo.today.TodayFragment;
import com.lumpofcode.dotwo.todolist.TodoListFragment;
import com.lumpofcode.dotwo.todolist.TodoListFragment.TodoListType;
import com.lumpofcode.dotwo.todopanel.TodoPanelFragment;

public class TodoActivity extends FragmentActivity
{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter	mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager				mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			if(0 == position)
			{
				return newTodayTodoList();
			}
			if(1 == position)
			{
				return newAllTodoList();
			}
			return newSharedTodoList();
		}

		@Override
		public int getCount()
		{
			// Show 3 total pages.
			return 3;
		}
		
		private Fragment newTodayTodoList()
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new TodayFragment();
			Bundle args = new Bundle();
			args.putInt(TodoListFragment.ARG_TODO_LIST_TYPE, TodoListType.TODAY.ordinal());
			args.putString(TodoListFragment.ARG_TODO_LIST_NAME, TaskLists.getTaskListByIndex(0).name());
			fragment.setArguments(args);
			return fragment;
		}
		private Fragment newSharedTodoList()
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new TodoPanelFragment();
			Bundle args = new Bundle();
			args.putInt(TodoListFragment.ARG_TODO_LIST_TYPE, TodoListType.SHARED.ordinal());
			fragment.setArguments(args);
			return fragment;
		}
		private Fragment newAllTodoList()
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new TodoPanelFragment();
			Bundle args = new Bundle();
			args.putInt(TodoListFragment.ARG_TODO_LIST_TYPE, TodoListType.ALL.ordinal());
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			Locale l = Locale.getDefault();
			switch (position)
			{
				case 0:
					return "Today";		//getString(R.string.title_section1).toUpperCase(l);
				case 1:
					return "Groceries";	//getString(R.string.title_section2).toUpperCase(l);
				case 2:
					return "Work";		// getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}
}
