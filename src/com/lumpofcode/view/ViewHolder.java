package com.lumpofcode.view;

import android.util.SparseArray;
import android.view.View;

/**
 * Class to implement View holder pattern
 * in a generic way via setTag().
 * 
 * NOTE: this uses the View's setTag() field, 
 *       so this field cannot be used for anything
 *       else.  If you have been using teh setTag()
 *       field, simply move your object into
 *       the ViewHolder instead.
 */
public class ViewHolder
{
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id)
	{
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null)
		{
			// 
			// this View does not yet have a holder.
			// create a new sparse array to use
			// as our holder and save it in
			// the View's setTag().
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		
		//
		// try to retrieve the child view from the holder.
		// if it is not there, get it from the parent View
		// and stash it in the holder so we don't 
		// have to find it the next time.
		//
		View childView = viewHolder.get(id);
		if (childView == null)
		{
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}
