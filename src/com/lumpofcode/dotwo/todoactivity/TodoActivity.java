package com.lumpofcode.dotwo.todoactivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.TaskList;
import com.lumpofcode.dotwo.model.TaskLists;
import com.lumpofcode.dotwo.model.TaskSortOrder;
import com.lumpofcode.dotwo.model.TodayList;
import com.lumpofcode.dotwo.newlistdialog.NewListDialogFragment.NewListDialogListener;
import com.lumpofcode.dotwo.newtodo.TaskDetailsDialog.OnTaskModifiedListener;
import com.lumpofcode.dotwo.today.TodayFragment;
import com.lumpofcode.dotwo.todolists.TaskListAddedListener;
import com.lumpofcode.dotwo.todopanel.TodoPanelFragment;
import com.lumpofcode.view.Pageable;

/**
 * @author Ed
 *
 */
/**
 * @author Ed
 *
 */
public class TodoActivity extends ActionBarActivity implements NewListDialogListener, TaskListAddedListener, Pageable, OnClickListener, OnTaskModifiedListener
{
	private static final String DATA_LOADED_DATA = TodoActivity.class.getName() + "DATA_LOADED_DATA";
	private static final String SORT_BUTTON_DATA = TodoActivity.class.getName() + "SORT_BUTTON_DATA";
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager				mViewPager;	
	private ViewFlipper _sortFlipper = null;
	private Button _toggleSortByDate = null;
	private Button _toggleSortByPriority = null;
	private Button _toggleSortByImportance = null;
	private Button _toggleSortByName = null;
	
	private boolean _dataLoaded = false;
	private int _sortButtonIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(TodoActivity.class.getSimpleName(), "onCreate(" + ((null != savedInstanceState) ? savedInstanceState.getBoolean(DATA_LOADED_DATA): "null") + ")");

		//
		// load the data
		//
		if(null != savedInstanceState)
		{
			_dataLoaded = savedInstanceState.getBoolean(DATA_LOADED_DATA);
			_sortButtonIndex = savedInstanceState.getInt(SORT_BUTTON_DATA);
		}
		
		if(!_dataLoaded)
		{
			//
			// load the data the first time
			//
			final LoadModel theLoader = new LoadModel();
			theLoader.execute();
		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);
		
		// Create the adapter that will return a fragment for each of the vies
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
				// TODO 
			}

		});
		
		//
		// setup the view flipper to animate the flip
		//
		_sortFlipper = (ViewFlipper)findViewById(R.id.sortFlipper);
		_sortFlipper.setInAnimation(
				AnimationUtils.loadAnimation(_sortFlipper.getContext(), R.anim.slide_in_from_bottom_fast));
		_sortFlipper.setOutAnimation(
				AnimationUtils.loadAnimation(_sortFlipper.getContext(), R.anim.slide_out_from_bottom_fast));

		_toggleSortByDate = (Button)findViewById(R.id.toggleSortByDueDate);
		_toggleSortByDate.setOnClickListener(this);
		_toggleSortByPriority = (Button)findViewById(R.id.toggleSortByPriority);
		_toggleSortByPriority.setOnClickListener(this);
		_toggleSortByImportance = (Button)findViewById(R.id.toggleSortByImportance);
		_toggleSortByImportance.setOnClickListener(this);
		_toggleSortByName = (Button)findViewById(R.id.toggleSortByName);
		_toggleSortByName.setOnClickListener(this);
		_setSortButtonByIndex(_sortButtonIndex);
	}
	

	@Override
	protected void onDestroy()
	{
		Log.d(TodoActivity.class.getSimpleName(), "onDestroy()");
		mSectionsPagerAdapter = null;
		mViewPager.setAdapter(null);
		mViewPager = null;

		super.onDestroy();
	}



	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		Log.d(TodoActivity.class.getSimpleName(), "onSaveInstanceState(Bundle outState)");
		super.onSaveInstanceState(outState);
		
		outState.putBoolean(DATA_LOADED_DATA, _dataLoaded);
		outState.putInt(SORT_BUTTON_DATA, _getSortButtonIndex());
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
			case R.id.action_goto_today:
			{
				// bring up model dialog for adding list
				Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
				showPageByIndex(0);	// first page is the today page
				return true;
			}
			case R.id.action_new_list:
			{
				// bring up model dialog for adding list
				Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
				showPageByIndex(getPageCount() - 1);	// last page is for new list.
//				Old code opened dialog to get list name
//				final FragmentManager fm = getSupportFragmentManager();
//				final NewListDialogFragment theDialog = NewListDialogFragment.newInstance(true);
//				theDialog.show(fm, null);
				return true;
			}
			default:
			{
				return super.onOptionsItemSelected(item);
			}
		}
	}


	//
	// handle sort buttons
	//
	@Override
	public void onClick(View theButton)
	{
		//
		// the current button was pressed.
		// find it's index in the flipper, then calculate the next
		// button's index and get it's id.
		//
		final int theButtonIndex = _sortFlipper.indexOfChild(theButton);
		final int theNextIndex = (theButtonIndex + 1) % _sortFlipper.getChildCount();
		_setSortButtonByIndex(theNextIndex);
	}
	
	private int _getSortButtonIndex()
	{
		return _sortFlipper.indexOfChild(_sortFlipper.getCurrentView());
	}
	private void _setSortButtonByIndex(final int theButtonIndex)
	{
		final int theNextId = _sortFlipper.getChildAt(theButtonIndex).getId();
		switch(theNextId)
		{
			case R.id.toggleSortByDueDate:
			{
				_sortFlipper.setDisplayedChild(theButtonIndex);
				TodayList.sortOrder(TaskSortOrder.BY_DUE_DATE);
				TaskLists.sort(TaskSortOrder.BY_DUE_DATE);
				return;
			}
			case R.id.toggleSortByPriority:		
			{
				_sortFlipper.setDisplayedChild(theButtonIndex);
				TodayList.sortOrder(TaskSortOrder.BY_PRIORITY);
				TaskLists.sort(TaskSortOrder.BY_PRIORITY);
				return;
			}
			case R.id.toggleSortByImportance:	
			{
				_sortFlipper.setDisplayedChild(theButtonIndex);
				TodayList.sortOrder(TaskSortOrder.BY_IMPORTANCE);
				TaskLists.sort(TaskSortOrder.BY_IMPORTANCE);
				return;
			}
			case R.id.toggleSortByName:	
			{
				_sortFlipper.setDisplayedChild(theButtonIndex);
				TodayList.sortOrder(TaskSortOrder.BY_NAME);
				TaskLists.sort(TaskSortOrder.BY_NAME);
				return;
			}
		}

	}
			
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
	 */
	private class SectionsPagerAdapter extends FragmentStatePagerAdapter
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
					Log.d(TodoActivity.class.getSimpleName(), "newTodayFragment()");
					return newTodayFragment(); 
				}
				default:
				{
					TodoPanelFragment theFragment;
					final int theTodoListIndex = position - 1;
					if(theTodoListIndex < TaskLists.taskListCount())
					{
						Log.d(TodoActivity.class.getSimpleName(), "newTodayFragment(" + TaskLists.getTaskListByIndex(theTodoListIndex).name() + ")");
						// fragment with list, so user can create and edit tasks
						theFragment = newTodoFragment(TaskLists.getTaskListByIndex(theTodoListIndex).name());
					}
					else
					{
						Log.d(TodoActivity.class.getSimpleName(), "newTodayFragment(null)");
						// empty fragment so user can create new list
						theFragment = newTodoFragment(null);
					}
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



	@Override
	public void onTaskModified(String theTaskListId, String theTaskId)
	{
		//
		// we don't need to do anything, the dialog is
		// doing the saving and notifying.
		//
	}


}
