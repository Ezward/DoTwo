package com.lumpofcode.view;
public interface Pageable
{
	public abstract int getPageCount();
	public abstract void showPageByIndex(final int position);
}

