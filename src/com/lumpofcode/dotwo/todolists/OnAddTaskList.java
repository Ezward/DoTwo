package com.lumpofcode.dotwo.todolists;

public interface OnAddTaskList
{
	/**
	 * Called when the user has asked for a new list to be created.
	 * This is a request to create the list; it does not exist
	 * at this point.
	 * 
	 * @param theTaskListName the name the user wants for the list
	 */
	public void onAddTaskList(final String theTaskListName);
}
