package com.lumpofcode.dotwo.todolists;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.TaskList;
import com.lumpofcode.dotwo.model.TaskLists;

public class TaskListsFragment extends Fragment implements OnTaskListClickListener
{	
	private TaskListsAdapter _listAdapter = null;
	private Button _buttonNewList;		//(Button)theView.findViewById(R.id.buttonNewTodo);
	private EditText _editNewListName;	// (EditText)theView.findViewById(R.id.editNewTodo);
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		final View theView = inflater.inflate(R.layout.fragment_todo_lists, container, false);
		
		final ListView theList = (ListView)theView.findViewById(R.id.listTaskList);
		_listAdapter = TaskLists.newTaskListsAdapter(theView.getContext(), this);
		theList.setAdapter(_listAdapter);
		
		//
		// handle creating a new tasklist
		//
		_buttonNewList = (Button)theView.findViewById(R.id.buttonNewTaskList);		
		_buttonNewList.setOnClickListener(new NewListClickListener());
		
		//
		// TextWatcher that enables the add button if the text in not empty
		//
		_editNewListName = (EditText)theView.findViewById(R.id.editNewTaskList);
		_editNewListName.addTextChangedListener(new EditListWatcher());

		return theView;
	}
	

	public void notifyDataSetChanged()
	{
		if(null != _listAdapter)
		{
			_listAdapter.notifyDataSetChanged();
		}
	}

	//
	// called when an item in the list is clicked.
	// we in turn call the activity to tell it which tasklist was clicked.
	//
	@Override
	public void onTaskListClick(String theTaskListName)
	{
		// tell the activity about the click
		final Activity theActivity = getActivity();
		if(theActivity instanceof OnTaskListClickListener)
		{
			final OnTaskListClickListener theListener = (OnTaskListClickListener)theActivity;
			theListener.onTaskListClick(theTaskListName);
		} 
		
	}

	/**
	 * TextWatcher to enable the Add button if new todo name is not empty
	 *
	 */
	private final class EditListWatcher implements TextWatcher
	{
		@Override
		public void afterTextChanged(Editable theEditable) 
		{ 
			//
			// enable add button if text length > 0
			//
			_buttonNewList.setEnabled(theEditable.length() > 0);
		}

		// no-ops for other methods
		@Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3){ /* no op */ }
		@Override public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3){ /* no op */ }
	}
	
	private final class NewListClickListener implements OnClickListener
	{
		@Override
		public void onClick(View theView)
		{
			//
			// TODO: open the task details for a new task
			//
			final String theListName = _editNewListName.getEditableText().toString();
			if(!theListName.isEmpty())
			{
				// for now, just create a new todo directly
				TaskList theTaskList = TaskLists.newTaskList(theListName);
				theTaskList.save();
				
				_editNewListName.getEditableText().clear();
				_editNewListName.clearFocus();
				_listAdapter.notifyDataSetChanged();
				
				//
				// hide the keyboard
				//
				final Context theContext = _editNewListName.getContext();
				InputMethodManager theInputManager = 
						(InputMethodManager)theContext.getSystemService(Context.INPUT_METHOD_SERVICE); 

				theInputManager.hideSoftInputFromWindow(
						_editNewListName.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

}
