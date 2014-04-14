package com.lumpofcode.dotwo.todoactivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.TaskList;
import com.lumpofcode.dotwo.model.TaskLists;
import com.lumpofcode.dotwo.newlistdialog.NewListDialogFragment;
import com.lumpofcode.dotwo.newlistdialog.NewListDialogFragment.NewListDialogListener;
import com.lumpofcode.dotwo.today.TodayFragment;
import com.lumpofcode.dotwo.todopanel.TodoPanelFragment;

public class TodoActivity extends FragmentActivity implements NewListDialogListener
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
				return newTodayFragment();
			}
			return newTodoFragment(TaskLists.getTaskListByIndex(position - 1).name(), position);
		}

		@Override
		public int getCount()
		{
			// Show 3 total pages.
			return TaskLists.taskListCount() + 1;	// we have the Today list as well as others
		}
		
		private Fragment newTodayFragment()
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new TodayFragment();
			Bundle args = new Bundle();
			// TODO: pass args to the todo list
			fragment.setArguments(args);
			return fragment;
		}
		private Fragment newTodoFragment(final String theTaskListName, final int position)
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new TodoPanelFragment();
			Bundle args = new Bundle();
			args.putString(TaskList.TASK_LIST_NAME, theTaskListName);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			//Locale l = Locale.getDefault();
			switch (position)
			{
				case 0:
					return getString(R.string.Today);		//getString(R.string.title_section1).toUpperCase(l);
				default:
					return TaskLists.getTaskListByIndex(position - 1).name();
			}
		}
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

}
