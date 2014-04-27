package com.lumpofcode.dotwo.todoactivity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
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
import com.lumpofcode.dotwo.model.TodayList;
import com.lumpofcode.dotwo.newlistdialog.NewListDialogFragment;
import com.lumpofcode.dotwo.newlistdialog.NewListDialogFragment.NewListDialogListener;
import com.lumpofcode.dotwo.today.TodayFragment;
import com.lumpofcode.dotwo.todolists.TaskListAddedListener;
import com.lumpofcode.dotwo.todopanel.TodoPanelFragment;
import com.lumpofcode.view.Pageable;

public class TodoActivity extends FragmentActivity implements NewListDialogListener, TaskListAddedListener, Pageable
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

	private List<TodoPanelFragment> _todoPanelFragments	= null;
	
	private boolean _dataLoaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		_dataLoaded = false;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		_todoPanelFragments = new ArrayList<TodoPanelFragment>(TaskLists.taskListCount());

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
				// _selectedTaskListName = null;
			}

		});
		
		//
		// load the data
		//
		final LoadModel theLoader = new LoadModel();
		theLoader.execute();
	}
	
	

	@Override
	protected void onDestroy()
	{
		mSectionsPagerAdapter = null;
		mViewPager.setAdapter(null);
		mViewPager = null;

		_todoPanelFragments.clear();
		_todoPanelFragments = null;
		
		super.onDestroy();
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
		switch (item.getItemId())
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
		public int getCount()
		{
			//
			// we show nothing until the data is loaded
			//
			// Once data is loaded we show the today list,
			// each task list and one empty task list
			// so the user can create a new list.
			//
			return _dataLoaded ? (TaskLists.taskListCount() + 2) : 0;	// we have the TaskLists list plus each TaskList
		}

		@Override
		public Fragment getItem(final int position)
		{
			//
			// the first position is the today list,
			// subsequent positions are individual lists in the
			// order that they are in the TaskLists collection
			//
			switch (position)
			{
				case 0:
				{
					return newTodayFragment(); 
				}
				default:
				{
					TodoPanelFragment theFragment;
					final int theTodoListIndex = position - 1;
					if(theTodoListIndex < TaskLists.taskListCount())
					{
						// fragment with list, so user can create and edit tasks
						theFragment = newTodoFragment(TaskLists.getTaskListByIndex(theTodoListIndex).name());
					}
					else
					{
						// empty fragment so user can create new list
						theFragment = newTodoFragment(null);
					}
					_todoPanelFragments.add(theFragment);
					return theFragment;
				}
			}
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			// Locale l = Locale.getDefault();
			switch (position)
			{
				case 0:
				{
					return getString(R.string.title_today_list);
				}
				default:
				{
					final int theTodoListIndex = position - 1;
					if(theTodoListIndex < TaskLists.taskListCount())
					{
						// fragment with list, so user can create and edit tasks
						return TaskLists.getTaskListByIndex(theTodoListIndex).name();
					}
					else
					{
						// empty fragment so user can create new list
						return mViewPager.getContext().getString(R.string.title_missing_list);
					}
				}
			}
		}
	}

	private TodayFragment newTodayFragment()
	{
		
		TodayFragment fragment = new TodayFragment();
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
		if ((null != inputText) && !inputText.isEmpty())
		{
			if (null == TaskLists.getTaskListByName(inputText))
			{
				Toast.makeText(this, inputText, Toast.LENGTH_SHORT).show();
				TaskList theTaskList = TaskLists.newTaskList(inputText);
				theTaskList.save();

				// tell the pager adapter that the data has changed
				mSectionsPagerAdapter.notifyDataSetChanged();
			}
			else
			{
				Toast.makeText(this, R.string.msg_list_already_exists, Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onTaskListAdded(String theTaskListName)
	{
		//
		// a task list was added, update the pages
		//
		mSectionsPagerAdapter.notifyDataSetChanged();
	}

	/**
	 * AsyncTask to load the model while showing a progress dialog
	 */
	private final class LoadModel extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog	progressDialog;

		@Override
		protected void onPreExecute()
		{
			progressDialog = ProgressDialog.show(
					TodoActivity.this, 
					TodoActivity.this.getString(R.string.load_progress_title),
					TodoActivity.this.getString(R.string.load_progress_description),
					true);

			// do initialization of required objects objects here
		};

		@Override
		protected Void doInBackground(Void... params)
		{
			//
			// load all the lists, then load the tasks for each list
			//
			TaskLists.load();
			TodayList.load();	// must be done after other TaskLists are loaded
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			
			//
			// close the dialog
			//
			progressDialog.dismiss();
			
			//
			// notify adapters that data has changed.
			//
			TaskLists.notifyDataSetChanged();
			for(int i = 0; i < TaskLists.taskListCount(); i += 1)
			{
				TaskLists.getTaskListByIndex(i).notifyDataSetChanged();
			}
			TodayList.notifyDataChanged();
			
			_dataLoaded = true;	// now task lists can be shown in pager
			mSectionsPagerAdapter.notifyDataSetChanged();
		};
	}

	//
	// Pageable interface
	//
	@Override
	public int getPageCount()
	{
		return mSectionsPagerAdapter.getCount();
	}

	@Override
	public void showPageByIndex(int position)
	{
		if((position >= 0) && (position < getPageCount()))
		{
			mViewPager.setCurrentItem(position);
		}
	}
}
