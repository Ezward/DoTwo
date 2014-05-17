package com.lumpofcode.dotwo.newtodo;

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
	
	public static final String ARG_TASK_LIST_ID = TaskDetailsDialog.class.getName() + "ARG_TASK_LIST_ID";
	public static final String ARG_TASK_ID = TaskDetailsDialog.class.getName() + "ARG_TASK_ID";
	
	
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
		 * @param theTaskListId
		 * @param theTaskId
		 */
		void onTaskModified(final String theTaskListId, final String theTaskId);
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
		theDialog.setStyle(STYLE_NO_TITLE, theDialog.getTheme());
		
		// pass arguments to dialog instance
		Bundle theArguments = new Bundle();
		theArguments.putString(ARG_TASK_LIST_ID, theTask.list().id());
		theArguments.putString(ARG_TASK_ID, theTask.id());
		theDialog.setArguments(theArguments);
		

		return theDialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// get the task from the arguments
		final String theTaskListId = getArguments().getString(ARG_TASK_LIST_ID);
		final String theTaskId = getArguments().getString(ARG_TASK_ID);
		
		final TaskList theTaskList = TaskLists.getTaskListById(theTaskListId);
		_task = theTaskList.getTaskById(theTaskId);

		// set the title
		getDialog().setTitle(theTaskList.name() + ":" + _task.name());
		
		//
		// prefill the fields from the task
		//
		final View theView = inflater.inflate(R.layout.layout_task_details, container, false);
		_editName = (EditText)theView.findViewById(R.id.editTaskNameDetail);
		_editName.setText(_task.name());
		
		_editDescription = (EditText)theView.findViewById(R.id.editTaskDescriptionDetail);
		_editDescription.setText(_task.description());
		
		_ratingImportance1to5 = (RatingBar)theView.findViewById(R.id.ratingTaskImportanceDetail);
		_ratingImportance1to5.setRating(_task.importance1to5());
		
		_textDueDate = (TextView)theView.findViewById(R.id.textTaskDueDateDetail);
		_textDueDate.setTag(Long.valueOf(_task.dueDateUTC()));	
		_textDueDate.setText(getRelativeDateTimeString(theView.getContext(), _task.dueDateUTC()));
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
				            public void onDateSet(
				            		DatePicker theView, 
				            		int year,
				                    int monthOfYear, 
				                    int dayOfMonth) 
				            {
				            	
							   Time chosenDate = new Time();        
							   chosenDate.set(dayOfMonth, monthOfYear, year);
							   long theDateMs = chosenDate.toMillis(true);
							   
							   // stash updated value in tag, so we can apply it when user hits ok
							   // update text to show them what they just did.
							   _textDueDate.setTag(Long.valueOf(theDateMs));
							   _textDueDate.setText(getRelativeDateTimeString(theView.getContext(), theDateMs));
							
							   CharSequence strDate = DateFormat.format("MMMM dd, yyyy", theDateMs);
							   Toast.makeText(theView.getContext(), 
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

	private final CharSequence getRelativeDateTimeString(Context theContext, long theDueDate)
	{
		return DateUtils.getRelativeDateTimeString(
			theContext,					// Context 
			theDueDate,					// The time to display
	        DateUtils.DAY_IN_MILLIS,	// The resolution 
	        DateUtils.WEEK_IN_MILLIS,	// The maximum time at which the time will switch to default date instead of spans. 
	        0); 						// Eventual flags
}

	private final void onOK()
	{
		// validate view contents
		final String theName = _editName.getText().toString().trim();
		if ((null != theName) && !theName.isEmpty())
		{
			//
			// it's ok if it does not change, or it is not a duplicate
			//
			if(theName.equals(_task.name()) || (null == _task.list().findTaskByName(theName, 0)))
			{
				// TODO: Test fields; don't tell caller if they did not change.
				//
				// update the task, then tell the caller we did it.
				// NOTE: we don't save here, we leave that up to the outer controller
				//
				_task.name(theName);
				_task.description(_editDescription.getText().toString());
				_task.importance1to5((int)_ratingImportance1to5.getRating());
				_task.dueDateUTC((Long)_textDueDate.getTag());
				_task.save();	// persist the change
				_task.list().notifyDataSetChanged();	// tell whomever is listening.
				
				// 
				// tell the caller we modified the task.
				//
				if(null != this.getTargetFragment())
				{
					final Intent theData = new Intent();
					theData.putExtra(ARG_TASK_LIST_ID, _task.list().id());
					theData.putExtra(ARG_TASK_ID, _task.id());
					this.getTargetFragment().onActivityResult(TASK_DETAILS_DIALOG, RESULT_OK, theData);
				}
				else if(getActivity() instanceof OnTaskModifiedListener)
				{
					OnTaskModifiedListener listener = (OnTaskModifiedListener) getActivity();
					
					// tell the listener that we changed this task
					listener.onTaskModified(_task.list().id(), _task.id());
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
		switch(actionId)
		{
			case EditorInfo.IME_ACTION_DONE:
			case EditorInfo.IME_ACTION_GO:
			{
				onOK();
				return true;
			}
			default:
				return false;
		}
	}
}
