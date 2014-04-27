package com.lumpofcode.random;

import java.util.Random;

/**
 * @author Ed
 * 
 * Threadsafe singleton for random numbers
 * 
 * @Threadsafe
 */
public class RandomNumber
{
	private static final ThreadLocal<Random> _random_ = new ThreadLocal<Random>();
	
	private RandomNumber() {};	// private constructor to enforce singleton.
	
	/**
	 * @return a pseudo-random uniformly distributed int. 
	 * 
	 * @Threadsafe
	 */
	public static final int nextInt()
	{
		return _get().nextInt();
	}
	
	/**
	 * @param n
	 * @return a pseudo-random uniformly distributed int in the half-open range [0, n). 
	 * 
	 * @Threadsafe
	 */
	public static final int nextInt(final int n)
	{
		return _get().nextInt(n);
	}
	
	/**
	 * @return instance of Random for the current thread
	 */
	private static final Random _get()
	{
		if(null == _random_.get())
		{
			_random_.set(new Random());
		}
		return _random_.get();
	}
	
}
