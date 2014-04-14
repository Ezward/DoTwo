package com.lumpofcode.dotwo.todopanel;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.Task;
import com.lumpofcode.dotwo.model.TaskList;
import com.lumpofcode.dotwo.model.TaskLists;
import com.lumpofcode.dotwo.todolist.TaskListAdapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class TodoPanelFragment extends Fragment
{
	private String _taskListName;
	private TaskList _taskList;
	private TaskListAdapter _listAdapter;
	private Button _newButton;		//(Button)theView.findViewById(R.id.buttonNewTodo);
	private EditText _editNewTodo;	// (EditText)theView.findViewById(R.id.editNewTodo);
	
    @Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		_taskListName = getArguments().getString(TaskList.TASK_LIST_NAME);   
	}

	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		final View theView = inflater.inflate(R.layout.fragment_todo_panel, container, false);
		
		// create and attach the task list adapter
		_taskList = TaskLists.getTaskListByName(_taskListName);
		_listAdapter = _taskList.newTaskListAdapter(theView.getContext(), R.layout.todo_item);
		final ListView theListView = (ListView)theView.findViewById(R.id.listTodo);
		theListView.setAdapter(_listAdapter);
		
		final LinearLayout theNewTodoPanel = (LinearLayout)theView.findViewById(R.id.panelNewTodo);
		_newButton = (Button)theView.findViewById(R.id.buttonNewTodo);
		_editNewTodo = (EditText)theView.findViewById(R.id.editNewTodo);
		
		_newButton.setOnClickListener(new NewTodoClickListener());
		
		//
		// TextWatcher that enables the add button if the text in not empty
		//
		_editNewTodo.addTextChangedListener(new EditTodoWatcher());
		
		return theView;
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
			//
			// TODO: open the task details for a new task
			//
			final String theTodoName = _editNewTodo.getEditableText().toString();
			if(!theTodoName.isEmpty())
			{
				// for now, just create a new todo directly
				Task theTask = _taskList.newTask(theTodoName);
				// TODO : persist the task.
				
				_editNewTodo.getEditableText().clear();
				_editNewTodo.clearFocus();
				_listAdapter.notifyDataSetChanged();
				
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



}
