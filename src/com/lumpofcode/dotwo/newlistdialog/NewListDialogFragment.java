package com.lumpofcode.dotwo.newlistdialog;

import com.lumpofcode.dotwo.R;
import com.lumpofcode.dotwo.model.TaskLists;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class NewListDialogFragment extends DialogFragment implements OnClickListener, OnEditorActionListener
{
	private EditText			_editText;

	/**
	 * Dialog Fragment listener pattern. Activities that call this dialog must implement this interface.
	 */
	public interface NewListDialogListener
	{
		/**
		 * Called with the TweetDetailDailog finishes.
		 * 
		 * @param inputText
		 *            the text that was entered into the dialog
		 */
		void onFinishNewListDialog(String inputText);
	}

	public NewListDialogFragment()
	{
		// Empty constructor required for DialogFragment
	}

	/**
	 * Factory method to create a dialog instance.
	 * 
	 * @param isNewTweet
	 * @return the dialog instance.
	 */
	public static NewListDialogFragment newInstance(boolean isNewTweet)
	{
		NewListDialogFragment frag = new NewListDialogFragment();
//		Bundle args = new Bundle();
//		args.putBoolean(CREATE_TWEET_KEY, isNewTweet);
//		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		final Dialog theDialog = getDialog();
		final View theView = inflater.inflate(R.layout.fragment_new_list_dialog, container);
		_editText = (EditText) theView.findViewById(R.id.editListName);
		theDialog.setTitle(this.getString(R.string.title_new_list_dialog));

		final Button theOkButton = (Button) theView.findViewById(R.id.okListName);
		theOkButton.setOnClickListener(this);

		// Show soft keyboard automatically
		_editText.requestFocus();
		theDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return theView;
	}

	public void onNewListOk()
	{

		final String theListName = _editText.getText().toString().trim();
		if ((null != theListName) && !theListName.isEmpty())
		{
			if(null == TaskLists.getTaskListByName(theListName))
			{
				// Return input text to activity
				if(getActivity() instanceof NewListDialogListener)
				{
					NewListDialogListener listener = (NewListDialogListener) getActivity();
					
					// TODO: should we create list here or in activity?
					listener.onFinishNewListDialog(theListName);
				}
				dismiss();
			}
			else
			{
				Toast.makeText(getActivity(), R.string.msg_list_already_exists, Toast.LENGTH_SHORT).show();
			}
		}
		else // no name, just quit
		{
			dismiss();
		}
	}

	@Override
	public void onClick(View arg0)
	{
		onNewListOk();
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
			onNewListOk();
			return true;
		}
		return false;
	}
}
