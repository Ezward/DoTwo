package com.lumpofcode.dotwo.todopanel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.Task;
import com.lumpofcode.dotwo.model.TaskList;
import com.lumpofcode.dotwo.model.TaskLists;
import com.lumpofcode.dotwo.model.TodayList;
import com.lumpofcode.dotwo.newtodo.TaskDetailsDialog;
import com.lumpofcode.dotwo.todolist.AbstractTaskListFragment;
import com.lumpofcode.dotwo.todolist.TaskListAdapter;
import com.lumpofcode.dotwo.todolists.TaskListAddedListener;

/**
 * @author Ed
 *
 */
public class TodoPanelFragment extends AbstractTaskListFragment
{
	private static final int MISSING_LIST_FRAME = 0;
	private static final int EMPTY_LIST_FRAME = 1;
	private static final String TASK_LIST_NAME_DATA = TodoPanelFragment.class.getName() + "TASK_LIST_NAME_DATA";
	
	// we hold a collection of list adapters mapped to list name
	// so we can show any list (one at a time) with the same fragment.
	private TaskList _taskList = null;		// current task list
	private TaskListAdapter _adapter = null;
	private ListView _listView = null;		// (ListView)theView.findViewById(R.id.listTodo);
	
	private Button _newButton = null;		// (Button)theView.findViewById(R.id.buttonNewTodo);
	private EditText _editNewTodo = null;	// (EditText)theView.findViewById(R.id.editNewTodo);
	private ViewFlipper _viewFlipper = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d(TodoPanelFragment.class.getSimpleName(), "onCreate(" + ((null != savedInstanceState)? savedInstanceState.getString(TASK_LIST_NAME_DATA) : "null") + ")");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy()
	{
		Log.d(TodoPanelFragment.class.getSimpleName(), "onDestroy()");
		super.onDestroy();
	}
	
	



	@Override
	public void onAttach(Activity activity)
	{
		Log.d(TodoPanelFragment.class.getSimpleName(), "onAttach(Activity)");
		super.onAttach(activity);
	}

	@Override
	public void onDetach()
	{
		Log.d(TodoPanelFragment.class.getSimpleName(), "onDetach()");
		super.onDetach();
	}

	@Override
	public void onPause()
	{
		Log.d(TodoPanelFragment.class.getSimpleName(), "onPause()");
		super.onPause();
	}

	@Override
	public void onResume()
	{
		Log.d(TodoPanelFragment.class.getSimpleName(), "onResume()");
		super.onResume();
	}

	@Override
	public void onStart()
	{
		Log.d(TodoPanelFragment.class.getSimpleName(), "onStart()");
		super.onStart();
	}

	@Override
	public void onStop()
	{
		Log.d(TodoPanelFragment.class.getSimpleName(), "onStop()");
		super.onStop();
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState)
	{
		Log.d(TodoPanelFragment.class.getSimpleName(), "onViewStateRestored(" + ((null != savedInstanceState)? savedInstanceState.getString(TASK_LIST_NAME_DATA) : "null") + ")");
		super.onViewStateRestored(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(TodoPanelFragment.class.getSimpleName(), "onCreateView(" + ((null != savedInstanceState)? savedInstanceState.getString(TASK_LIST_NAME_DATA) : "null") + ")");
		
		final View theView = inflater.inflate(R.layout.fragment_todo_panel, container, false);
		
		_viewFlipper = (ViewFlipper)theView.findViewById(R.id.emptyOrMissingTaskListFrame);
		
		
		// get the name of the initial list of tasks from the arguments
		// that will be the first list we show.
		_listView = (ListView)theView.findViewById(R.id.listTodo);
		_listView.setEmptyView(_viewFlipper);	// empty view shows when list is empty
				
		_newButton = (Button)theView.findViewById(R.id.buttonNewTodo);
		_newButton.setOnClickListener(new NewTodoClickListener());
		
		//
		// TextWatcher that enables the add button if the text in not empty
		//
		_editNewTodo = (EditText)theView.findViewById(R.id.editNewTodo);
		_editNewTodo.addTextChangedListener(new EditTodoWatcher());
		
		restoreFromSavedInstanceState(savedInstanceState);
		return theView;
	}
	
	
	
	@Override
	public void onDestroyView()
	{
		Log.d(TodoPanelFragment.class.getSimpleName(), "onDestroyView()");
		//
		// free-up anything we create in onCreateView()
		//
		if(null!= _taskList)
		{
			_taskList.detachAdapter();
		}
		_listView = null;
		_newButton = null;
		_editNewTodo = null;

		super.onDestroyView();
	}
	
	

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		Log.d(TodoPanelFragment.class.getSimpleName(), "onSaveInstanceState(Bundle outState)");
		super.onSaveInstanceState(outState);
		
		if(null != _taskList)
		{
			outState.putString(TASK_LIST_NAME_DATA, _taskList.name());	
		}
	}
	
	private void restoreFromSavedInstanceState(Bundle savedInstanceState)
	{
		if(null != savedInstanceState)
		{
			//
			// restore the task list from the saved instance
			//
			setTaskListByName(savedInstanceState.getString(TASK_LIST_NAME_DATA));
		}
		else
		{
			//
			// set the task list from the fragment argument
			//
			setTaskListByName(getArguments().getString(TaskList.TASK_LIST_NAME));
		}

	}
	
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		Log.d(TodoPanelFragment.class.getSimpleName(), "onActivityCreated(" + ((null != savedInstanceState)? savedInstanceState.getString(TASK_LIST_NAME_DATA) : "null") + ")");
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * Change the task list for this panel.
	 * 
	 * @param theTaskListName
	 */
	public final void setTaskListByName(final String theTaskListName)
	{
		if(null != _listView)
		{
			if(null == theTaskListName)
			{
				// we are detaching the list
				if(null != _taskList)
				{
					// fully remove the adapter
					_taskList.detachAdapter();
					_adapter = null;
					_viewFlipper.setDisplayedChild(MISSING_LIST_FRAME);
				}
			}
			else if((null == _taskList) || !_taskList.name().equals(theTaskListName))
			{
				// we are changing lists
				if(null != _taskList)
				{
					// detach from current list
					_taskList.detachAdapter();
				}
				// attach to new list
				_taskList = TaskLists.getTaskListByName(theTaskListName);
				_adapter = _taskList.attachAdapter(_listView.getContext(), R.layout.todo_item, this);
				_viewFlipper.setDisplayedChild(EMPTY_LIST_FRAME);
			}
			_listView.setAdapter(_adapter);
		}
	}
	
		
	/**
	 * TextWatcher to enable the Add button if new todo name is not empty
	 *
	 */
	private final class EditTodoWatcher implements TextWatcher
	{
		@Override
		public void afterTextChanged(Editable theEditable) 
		{ 
			//
			// enable add button if text length > 0
			//
			_newButton.setEnabled(theEditable.length() > 0);
		}

		// no-ops for other methods
		@Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3){ /* no op */ }
		@Override public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3){ /* no op */ }
	}
	
	private final class NewTodoClickListener implements OnClickListener
	{
		@Override
		public void onClick(View theView)
		{
			final String theName = _editNewTodo.getEditableText().toString();
			if(!theName.isEmpty())
			{
				// 
				// if we don't have a list, then we are creating a list
				// if we have a list, we are creating a task.
				//
				if(null == _taskList)
				{
					if(null == TaskLists.getTaskListByName(theName))
					{
						//
						// no task list, so we are creating a new one
						//
						// for now, just create a new todo directly
						TaskList theTaskList = TaskLists.newTaskList(theName);
						theTaskList.save();
						
						// make this panel use the new list
						setTaskListByName(theName);
						
						final FragmentActivity theActivity = getActivity();
						if(theActivity instanceof TaskListAddedListener)
						{
							final TaskListAddedListener theCallback = (TaskListAddedListener)theActivity;
							theCallback.onTaskListAdded(theName);
						}
						
						// show the correct view in the 'empty' frame
						_viewFlipper.setInAnimation(
								AnimationUtils.loadAnimation(_viewFlipper.getContext(), R.anim.slide_in_from_bottom));
						_viewFlipper.setOutAnimation(
								AnimationUtils.loadAnimation(_viewFlipper.getContext(), R.anim.slide_out_from_bottom));
						_viewFlipper.setDisplayedChild(EMPTY_LIST_FRAME);
					}
					else
					{
						Toast.makeText(getActivity(), R.string.msg_list_already_exists, Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					//
					// we have a task list, so we creating a task
					//
					Task theTask = _taskList.newTask(theName);
					_adapter.notifyDataSetChanged();	// tell our task list that it's changed
					
					TodayList.addTask(theTask);	// add it to the today list in sort order
					TodayList.notifyDataChanged();
					
					// persist the task.
					theTask.save();
				}
				
				// drop through and close the text editor
				_editNewTodo.getEditableText().clear();
				_editNewTodo.clearFocus();
				
				//
				// hide the keyboard
				//
				final Context theContext = _editNewTodo.getContext();
				InputMethodManager theInputManager = 
						(InputMethodManager)theContext.getSystemService(Context.INPUT_METHOD_SERVICE); 

				theInputManager.hideSoftInputFromWindow(
						_editNewTodo.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	//
	// called when a dialog finishes
	//
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// AbstractTaskListFragment will handle the TodayList
		super.onActivityResult(requestCode, resultCode, data);
		
		// 
		// handle the result from dialogs
		//
		if(requestCode == TaskDetailsDialog.TASK_DETAILS_DIALOG)
		{
			//
			// we don't need to do anything, the dialog is
			// doing the saving and notifying.
			//
		}
	}


}
