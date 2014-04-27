package com.lumpofcode.dotwo.model;

import java.util.Comparator;

public enum TaskSortOrder
{
	BY_NAME(new CompareTaskName()),
	BY_PRIORITY(new CompareTaskPriority()),
	BY_IMPORTANCE(new CompareTaskImportance()),
	BY_DUE_DATE(new CompareTaskDueDate());
	
	private final Comparator<Task> _innerComparator;
	private final Comparator<Task> _doneComparator;
	
	private TaskSortOrder(final Comparator<Task> theComparator)
	{
		_innerComparator = theComparator;
		_doneComparator = new CompareDone();
	}
	
	public final Comparator<Task> comparator()
	{
		return _innerComparator;	// use this if we are not sorting on done
		//return _doneComparator; use this is if we are taking 'done' into account in sort order
	}
	
	/**
	 * Compare done, then the inner comparator.
	 */
	private final class CompareDone implements Comparator<Task>
	{
		@Override
		public final int compare(Task theTask, Task theOtherTask)
		{
			if(theTask.isDone())
			{
				if(theOtherTask.isDone())
				{
					if(null != _innerComparator)
					{
						return _innerComparator.compare(theTask, theOtherTask);
					}
					return 0;
				}
				return 1;
			}
			return -1;
		}
	}
	
	private static final int compareDueDate(Task theTask, Task theOtherTask)
	{
		// sort in descending order (largest {most recent} date first}
		if(theTask.dueDateUTC() < theOtherTask.dueDateUTC()) return 1;
		if(theTask.dueDateUTC() > theOtherTask.dueDateUTC()) return -1;
		return 0;
	}
	
	private static final class CompareTaskPriority implements Comparator<Task>
	{
		@Override
		public final int compare(Task theTask, Task theOtherTask)
		{
			// sort in descending order (highest priority to lowest priority)
			final long today = TodayList.getToday();
			if(theTask.priority(today) < theOtherTask.priority(today)) return 1;
			if(theTask.priority(today) > theOtherTask.priority(today)) return -1;
			
			// same priority, then sort by due date
			return TaskSortOrder.compareDueDate(theTask, theOtherTask);
		}
	}
	
	private static final class CompareTaskDueDate implements Comparator<Task>
	{
		@Override
		public final int compare(Task theTask, Task theOtherTask)
		{
			// sort in descending order (largest {most recent} date first}
			return TaskSortOrder.compareDueDate(theTask, theOtherTask);
		}
	};
	
	private static final class CompareTaskImportance implements Comparator<Task>
	{
		@Override
		public final int compare(Task theTask, Task theOtherTask)
		{
			// sort in descending order (hightest importance to lowest importance)
			if(theTask.importance1to5() < theOtherTask.importance1to5()) return 1;
			if(theTask.importance1to5() > theOtherTask.importance1to5()) return 1;
			return 0;
		}
	};
	
	private static final class CompareTaskName implements Comparator<Task>
	{
		@Override
		public final int compare(Task theTask, Task theOtherTask)
		{
			// sort names in natural, ascending order
			return theTask.name().compareTo(theOtherTask.name());
		}
	};

}

