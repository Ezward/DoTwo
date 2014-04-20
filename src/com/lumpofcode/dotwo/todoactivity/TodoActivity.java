package com.lumpofcode.dotwo.todoactivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.TaskList;
import com.lumpofcode.dotwo.model.TaskLists;
import com.lumpofcode.dotwo.newlistdialog.NewListDialogFragment;
import com.lumpofcode.dotwo.newlistdialog.NewListDialogFragment.NewListDialogListener;
import com.lumpofcode.dotwo.todolists.OnAddTaskList;
import com.lumpofcode.dotwo.todolists.OnTaskListClickListener;
import com.lumpofcode.dotwo.todolists.TaskListsFragment;
import com.lumpofcode.dotwo.todopanel.TodoPanelFragment;

public class TodoActivity extends FragmentActivity implements NewListDialogListener, OnTaskListClickListener, OnAddTaskList
{
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter	mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager				mViewPager;
	
	private String _selectedTaskListName = null;
	private TaskListsFragment _taskListsFragment = null;
	private TodoPanelFragment _todoPanelFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_split);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageScrollStateChanged(int arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int position)
			{
				// null out the task list name
				// and tell the adapter 
				//_selectedTaskListName = null;
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo, menu);
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.action_new_list:
			{
				// bring up model dialog for adding list
				Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
				final FragmentManager fm = getSupportFragmentManager();
				final NewListDialogFragment theDialog = NewListDialogFragment.newInstance(true);
				theDialog.show(fm, null);
				return true;
			}
			case R.id.action_choose_list:
			{
				// bring up model dialog for adding list
				Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
				return true;
			}
			default:
			{
				return super.onOptionsItemSelected(item);				
			}
		}
	}



	@Override
	public void onTaskListClick(final String theTaskListName)
	{
		if((null != theTaskListName) && !theTaskListName.isEmpty())
		{
			_selectedTaskListName = theTaskListName;
			if(1 == mSectionsPagerAdapter.getCount())
			{
				// 
				// create a new todo list
				//
				_todoPanelFragment = newTodoFragment(_selectedTaskListName);
			}
			else
			{
				//
				// alter pre-existing todo list to show a different list
				//
				_todoPanelFragment.setTaskListByName(_selectedTaskListName);
			}
									
			// TaskList was clicked, show the list
			mSectionsPagerAdapter.notifyDataSetChanged();
			mViewPager.setCurrentItem(1);
		}
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
				return _taskListsFragment = newTaskListsFragment();
			}
			if(null == _todoPanelFragment)
			{
				_todoPanelFragment = newTodoFragment(_selectedTaskListName);				
			}
			return _todoPanelFragment;
		}

		@Override
		public int getCount()
		{
			return (null != _todoPanelFragment) ? 2 : 1;	// we have the TaskLists list plus each TaskList
		}
		

		@Override
		public CharSequence getPageTitle(int position)
		{
			//Locale l = Locale.getDefault();
			switch (position)
			{
				case 0:
					return getString(R.string.title_tasklists);		
				default:
					return _selectedTaskListName;
			}
		}
	}
	
	private TaskListsFragment newTaskListsFragment()
	{
		// getItem is called to instantiate the fragment for the given page.
		// Return a DummySectionFragment (defined as a static inner class
		// below) with the page number as its lone argument.
		TaskListsFragment fragment = new TaskListsFragment();
		Bundle args = new Bundle();
		// TODO: pass args to the todo list
		fragment.setArguments(args);
		return fragment;
	}
	private TodoPanelFragment newTodoFragment(final String theTaskListName)
	{
		// getItem is called to instantiate the fragment for the given page.
		// Return a DummySectionFragment (defined as a static inner class
		// below) with the page number as its lone argument.
		TodoPanelFragment fragment = new TodoPanelFragment();
		Bundle args = new Bundle();
		args.putString(TaskList.TASK_LIST_NAME, theTaskListName);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onFinishNewListDialog(String inputText)
	{
		// 
		// if inputText is not null, then tweet it
		//
		if((null != inputText) && !inputText.isEmpty())
		{
			if(null == TaskLists.getTaskListByName(inputText))
			{
				Toast.makeText(this, inputText, Toast.LENGTH_SHORT).show();
				TaskList theTaskList = TaskLists.newTaskList(inputText);
				if(null != _taskListsFragment)
				{
					_taskListsFragment.notifyDataSetChanged();
				}
								
				// tell the pager adapter that the data has changed
				mSectionsPagerAdapter.notifyDataSetChanged();
				
				// TODO : persist the list
			}
			else
			{
				Toast.makeText(this, R.string.msg_list_already_exists, Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onAddTaskList(String theTaskListName)
	{
		onFinishNewListDialog(theTaskListName);
	}

}
