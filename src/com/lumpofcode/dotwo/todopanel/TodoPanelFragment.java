package com.lumpofcode.dotwo.todopanel;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.Task;
import com.lumpofcode.dotwo.model.TaskList;
import com.lumpofcode.dotwo.model.TaskLists;
import com.lumpofcode.dotwo.model.TodayList;
import com.lumpofcode.dotwo.newtodo.TaskDetailsDialog;
import com.lumpofcode.dotwo.todolist.TaskListAdapter;
import com.lumpofcode.dotwo.todolist.TaskListAdapter.TaskListListener;

/**
 * @author Ed
 *
 */
public class TodoPanelFragment extends Fragment implements OnItemLongClickListener, TaskListListener
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
				_taskList.newTaskListAdapter(theView.getContext(), R.layout.todo_item, this));
		_listView = (ListView)theView.findViewById(R.id.listTodo);
		_listView.setAdapter(_listAdapters.get(theTaskListName));
		_listView.setOnItemLongClickListener(this);

		
		_newButton = (Button)theView.findViewById(R.id.buttonNewTodo);
		_newButton.setOnClickListener(new NewTodoClickListener());
		
		//
		// TextWatcher that enables the add button if the text in not empty
		//
		_editNewTodo = (EditText)theView.findViewById(R.id.editNewTodo);
		_editNewTodo.addTextChangedListener(new EditTodoWatcher());
		
		return theView;
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
		final TaskList theTaskList = TaskLists.getTaskListByName(theTaskListName);
		if(null != theTaskList)
		{
			// see if we already have an adapter for this list
			TaskListAdapter theAdapter = _listAdapters.get(theTaskListName);
			if(null == theAdapter)
			{
				// create a new adapter for the list and add it to the map
				theAdapter = theTaskList.newTaskListAdapter(getActivity(), R.layout.todo_item, this);
				_listAdapters.put(theTaskListName, theAdapter);
			}
			_listView.setAdapter(theAdapter);
			_taskList = theTaskList;
		}
	}
	
	//
	// handle when a checkbox on a task item is toggled
	//
	@Override
	public void onTaskDoneCheckedChanged(
			TaskListAdapter theAdapter,
			String theTaskName,
			boolean theSelectedState)
	{
		// TODO : notify today panel to redraw itself
	}

	@Override
	public void onTaskTodayCheckedChanged(
			TaskListAdapter theAdapter,
			String theTaskName,
			boolean theSelectedState)
	{
		// TODO: notify the today panel to sort and redraw itself
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
				
				// TODO : persist the task.
				
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
	// handle a long-click on task item in the list
	//
	@Override
	public boolean onItemLongClick(AdapterView<?> theParent, View theView, int thePosition, long theId)
	{
		// TODO : confirm delete dialog
		return false;
	}
	
	@Override
	public void onTaskClick(final TaskListAdapter theParent, final String theTaskName)
	{
		// we stashed the list name in the tag
		final Task theTask = _taskList.getTaskByName(theTaskName);
		final TaskDetailsDialog theDialog = TaskDetailsDialog.newTaskDetailsDialog(theTask, this);
		theDialog.show(getFragmentManager(), null);
		// NOTE: the dlalog will call onActivityResult() when it finishes with Ok
		//       We will not get called at all if it is cancelled or if no changes are made
	}

	//
	// called when a dialog finishes
	//
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// 
		// handle the result from dialogs
		//
		if(requestCode == TaskDetailsDialog.TASK_DETAILS_DIALOG)
		{
			//
			// a task has been edited
			//
			
			// notify the adapter so it redraws the item
			_listAdapters.get(_taskList.name()).notifyDataSetChanged();
			TodayList.notifyDataChanged();	// tell the today list to redraw itself.
			
			// TODO: save the task
		}
	}


}
