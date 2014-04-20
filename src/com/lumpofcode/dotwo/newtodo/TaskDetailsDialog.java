package com.lumpofcode.dotwo.newtodo;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.lumpofcode.datepicker.SimpleDatePickerDialog;
import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.Task;
import com.lumpofcode.dotwo.model.TaskList;
import com.lumpofcode.dotwo.model.TaskLists;

public class TaskDetailsDialog extends DialogFragment implements OnClickListener, OnEditorActionListener
{
	/**
	 * the response code that is passed to the fragment when the dialog is finished.
	 */
	public static final int TASK_DETAILS_DIALOG = TaskDetailsDialog.class.hashCode();
	public static final int RESULT_OK = 1;
	
	public static final String ARG_TASK_LIST_NAME = "ARG_TASK_LIST_NAME";
	public static final String ARG_TASK_NAME = "ARG_TASK_NAME";
	
	
	private TaskList _taskList;
	private Task _task;
	
	private EditText _editName;
	private EditText _editDescription;
	private RatingBar _ratingImportance1to5;
	private TextView _textDueDate;
	private Button _buttonOk;
	
	public interface OnTaskModifiedListener
	{
		/**
		 * Called when the dialog finishes with an edited result.
		 * 
		 * Note: if the dialog is cancelled, no change is made to 
		 *       the task and this method is not called.
		 *       
		 * @param theTaskListName
		 * @param theTaskName
		 */
		void onTaskModified(final String theTaskListName,final String theTaskName);
	}
	
	/**
	 * Factory to create a TaskDetails dialog that is called by a fragment.
	 * 
	 * @param theTask
	 * @param theCaller
	 * @return
	 */
	public static final TaskDetailsDialog newTaskDetailsDialog(final Task theTask, Fragment theCaller)
	{
		TaskDetailsDialog theDialog = newTaskDetailsDialog(theTask);
		
		// tack on response code for calling fragment
		theDialog.setTargetFragment(theCaller, TASK_DETAILS_DIALOG);

		return theDialog;
	}
	
	
	/**
	 * Create a new TaskDetails dialog is called by an activity.
	 * The activity must implement OnTaskModifiedListener to get the result.
	 * 
	 * @param theTask
	 * @return
	 */
	public static final TaskDetailsDialog newTaskDetailsDialog(final Task theTask)
	{
		TaskDetailsDialog theDialog = new TaskDetailsDialog();
		
		// pass arguments to dialog instance
		Bundle theArguments = new Bundle();
		theArguments.putString(ARG_TASK_LIST_NAME, theTask.list());
		theArguments.putString(ARG_TASK_NAME, theTask.name());
		theDialog.setArguments(theArguments);

		return theDialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		final View theView = inflater.inflate(R.layout.layout_task_details, container, false);

		// get the task from the arguments
		final String theTaskListName = getArguments().getString(ARG_TASK_LIST_NAME);
		final String theTaskName = getArguments().getString(ARG_TASK_NAME);
		
		final TaskList theTaskList = TaskLists.getTaskListByName(theTaskListName);
		_task = theTaskList.getTaskByName(theTaskName);
		
		//
		// prefill the fields from the task
		//
		_editName = (EditText)theView.findViewById(R.id.editTaskNameDetail);
		_editName.setText(_task.name());
		
		_editDescription = (EditText)theView.findViewById(R.id.editTaskDescriptionDetail);
		_editDescription.setText(_task.description());
		
		_ratingImportance1to5 = (RatingBar)theView.findViewById(R.id.ratingTaskImportanceDetail);
		_ratingImportance1to5.setRating(_task.importance1to5());
		
		final CharSequence theRelativeTime = DateUtils.getRelativeDateTimeString(
		        theView.getContext(),		// Context 
		        _task.dueDateUTC(),			// The time to display
		        DateUtils.DAY_IN_MILLIS,	// The resolution 
		        DateUtils.WEEK_IN_MILLIS,	// The maximum time at which the time will switch to default date instead of spans. 
		        0); 						// Eventual flags
		_textDueDate = (TextView)theView.findViewById(R.id.textTaskDueDateDetail);
		_textDueDate.setText(theRelativeTime);
		_textDueDate.setTag(Long.valueOf(_task.dueDateUTC()));	
		_textDueDate.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View theView)
			{
				// open a DatePicker dialog
			    DatePickerDialog theDialog = SimpleDatePickerDialog.newSimpleDatePickerDialog(
			    		theView.getContext(), 
			            new DatePickerDialog.OnDateSetListener() 
			    		{
				            public void onDateSet(DatePicker view, int year,
				                                                int monthOfYear, int dayOfMonth) 
				            {
				            	
			                       Time chosenDate = new Time();        
			                       chosenDate.set(dayOfMonth, monthOfYear, year);
			                       long theDateMs = chosenDate.toMillis(true);
			                       
			                       _textDueDate.setTag(Long.valueOf(theDateMs));
			                       
			                       CharSequence strDate = DateFormat.format("MMMM dd, yyyy", theDateMs);
			                       Toast.makeText(view.getContext(), 
			                            "Date picked: " + strDate, Toast.LENGTH_SHORT).show();
				            }
				        }, 
				        (Long)_textDueDate.getTag(),
				        theView.getContext().getString(R.string.title_due_date_dialog));
			    theDialog.show();
			}
		});
		
		_buttonOk = (Button)theView.findViewById(R.id.buttonOkTaskDetails);
		_buttonOk.setOnClickListener(this);
		
		return theView;
	}

	@SuppressWarnings("deprecation")
	private final void onOK()
	{
		// validate view contents
		final String theName = _editName.getText().toString().trim();
		if ((null != theName) && !theName.isEmpty())
		{
			//
			// it's ok if it does not change, or it is not a duplicate
			//
			if(theName.equals(_task.name()) || (null == _taskList.getTaskByName(theName)))
			{
				// TODO: Test fields; don't tell caller if they did not change.
				//
				// update the task, then tell the caller we did it.
				// NOTE: we don't save here, we leave that up to the outer controller
				//
				_task.description(_editDescription.getText().toString());
				_task.importance1to5((int)_ratingImportance1to5.getRating());
				_task.dueDateUTC((Long)_textDueDate.getTag());
				
				// 
				// tell the caller we modified the task.
				//
				if(null != this.getTargetFragment())
				{
					final Intent theData = new Intent();
					theData.putExtra(ARG_TASK_LIST_NAME, _task.list());
					theData.putExtra(ARG_TASK_NAME, _task.name());
					this.getTargetFragment().onActivityResult(TASK_DETAILS_DIALOG, RESULT_OK, theData);
				}
				else if(getActivity() instanceof OnTaskModifiedListener)
				{
					OnTaskModifiedListener listener = (OnTaskModifiedListener) getActivity();
					
					// tell the listener that we changed this task
					listener.onTaskModified(_task.list(), _task.name());
				}
				dismiss();
			}
			else
			{
				Toast.makeText(getActivity(), R.string.msg_task_name_already_exists, Toast.LENGTH_SHORT).show();
			}
		}
		else // no name, just quit, don't bother telling caller
		{
			dismiss();
		}
	}

	@Override
	public void onClick(View theButton)
	{
		onOK();
	}

	//
	// Fires whenever the textfield has an action performed
	// In this case, when the "Done" button is pressed
	//
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		if (EditorInfo.IME_ACTION_DONE == actionId)
		{
			onOK();
			return true;
		}
		return false;
	}
}
