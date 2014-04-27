package com.lumpofcode.array;

import java.util.Random;

import com.lumpofcode.random.RandomNumber;

/**
 * @author Ed
 * 
 * Utilities to operate on arrays.
 *
 */
public final class ArrayUtils
{
	private ArrayUtils() {};	// private constructor to enforce singleton
	
	/**
	 * Shuffle and array of int.
	 * This uses the Fisher–Yates algorithm.
	 * 
	 * @param theArray of int.
	 */
	public static final void intShuffle(final int[] theArray)
	{
	    int index;
	    for (int i = theArray.length - 1; i > 0; i--)
	    {
	        index = RandomNumber.nextInt(i + 1);
	        if (index != i)
	        {
	            theArray[index] ^= theArray[i];
	            theArray[i] ^= theArray[index];
	            theArray[index] ^= theArray[i];
	        }
	    }
	}
	
	/**
	 * Shuffle and array of String.
	 * This uses the Fisher–Yates algorithm.
	 * 
	 * @param theArray of String
	 */
	public static final void stringShuffle(final String[] theArray)
	{
	    int index;
	    String temp;
	    for (int i = theArray.length - 1; i > 0; i--)
	    {
	        index = RandomNumber.nextInt(i + 1);
	        temp = theArray[index];
	        theArray[index] = theArray[i];
	        theArray[i] = temp;
	    }
	}
}
