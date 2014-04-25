package com.lumpofcode.dotwo.todopanel;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.Task;
import com.lumpofcode.dotwo.model.TaskList;
import com.lumpofcode.dotwo.model.TaskLists;
import com.lumpofcode.dotwo.model.TodayList;
import com.lumpofcode.dotwo.newtodo.TaskDetailsDialog;
import com.lumpofcode.dotwo.todolist.AbstractTaskListFragment;
import com.lumpofcode.dotwo.todolist.TaskListAdapter;

/**
 * @author Ed
 *
 */
public class TodoPanelFragment extends AbstractTaskListFragment
{
	// we hold a collection of list adapters mapped to list name
	// so we can show any list (one at a time) with the same fragment.
	private Map<String,TaskListAdapter> _listAdapters = new HashMap<String, TaskListAdapter>();
	private TaskList _taskList;		// current task list
	
	private Button _newButton;		// (Button)theView.findViewById(R.id.buttonNewTodo);
	private EditText _editNewTodo;	// (EditText)theView.findViewById(R.id.editNewTodo);
	private ListView _listView;		// (ListView)theView.findViewById(R.id.listTodo);
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//
		// space to track lists
		//
		_listAdapters = new HashMap<String, TaskListAdapter>();
	}

	@Override
	public void onDestroy()
	{
		// 
		// detach lists from list adapters
		//
		if(null != _listAdapters)
		{
			for(String theTaskListName : _listAdapters.keySet())
			{
				final TaskList theList = TaskLists.getTaskListByName(theTaskListName);
				theList.detachAdapter();
			}
			_listAdapters.clear();
			_listAdapters = null;
		}
		
		super.onDestroy();
	}



	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		
		final View theView = inflater.inflate(R.layout.fragment_todo_panel, container, false);
		
		// get the name of the initial list of tasks from the arguments
		// that will be the first list we show.
		final String theTaskListName = getArguments().getString(TaskList.TASK_LIST_NAME);
		_taskList = TaskLists.getTaskListByName(theTaskListName);
		_listAdapters.put(
				theTaskListName, 
				_taskList.attachAdapter(theView.getContext(), R.layout.todo_item, this));
		_listView = (ListView)theView.findViewById(R.id.listTodo);
		_listView.setAdapter(_listAdapters.get(theTaskListName));

		
		_newButton = (Button)theView.findViewById(R.id.buttonNewTodo);
		_newButton.setOnClickListener(new NewTodoClickListener());
		
		//
		// TextWatcher that enables the add button if the text in not empty
		//
		_editNewTodo = (EditText)theView.findViewById(R.id.editNewTodo);
		_editNewTodo.addTextChangedListener(new EditTodoWatcher());
		
		return theView;
	}
	
	
	
	@Override
	public void onDestroyView()
	{
		//
		// free-up anything we create in onCreateView()
		//
		if(null != _listAdapters)
		{
			_listAdapters.remove(_taskList.name());
			_taskList.detachAdapter();	// detach the list adapter from the list
			_taskList = null;
			_listView = null;
			_newButton = null;
			_editNewTodo = null;
		}

		super.onDestroyView();
	}

	/**
	 * Make the list show a new set of tasks.
	 * NOTE: this will cause a visual glitch, so it
	 *       is best to do this while the list is not showing.
	 * 
	 * @param theTaskListName
	 */
	public void setTaskListByName(final String theTaskListName)
	{
		if(this.isResumed() && (null != this.getView()))
		{
			final TaskList theTaskList = TaskLists.getTaskListByName(theTaskListName);
			if(null != theTaskList)
			{
				// see if we already have an adapter for this list
				TaskListAdapter theAdapter = _listAdapters.get(theTaskListName);
				if(null == theAdapter)
				{
					// create a new adapter for the list and add it to the map
					// TODO: THIS LEADS TO A NULL POINTER EXCEPTION IN inner init() functino
					//       of the ArrayAdapter().  I'm guessing getActivity() returns null
					//       because this method is called on the fragment either after it
					//       is destroyed or before it is created.  The TodoActivity() is
					//       holding onto the fragment rather than creating a new one
					//       when it is asked to, so I think that this causes this method
					//       to get called on a fragment that Android think's it has destroyed.
					//
					theAdapter = theTaskList.attachAdapter(getActivity(), R.layout.todo_item, this);
					_listAdapters.put(theTaskListName, theAdapter);
				}
				_listView.setAdapter(theAdapter);
				_taskList = theTaskList;
			}
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
			final String theTodoName = _editNewTodo.getEditableText().toString();
			if(!theTodoName.isEmpty())
			{
				// for now, just create a new todo directly
				Task theTask = _taskList.newTask(theTodoName);
				_listAdapters.get(_taskList.name()).notifyDataSetChanged();	// tell our task list that it's changed
				
				TodayList.addTask(theTask);	// add it to the today list in sort order
											// this will notify the task list directly.
				
				// persist the task.
				theTask.save();
				
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
			// notify the adapter so it redraws the item
			_listAdapters.get(_taskList.name()).notifyDataSetChanged();
		}
	}


}
