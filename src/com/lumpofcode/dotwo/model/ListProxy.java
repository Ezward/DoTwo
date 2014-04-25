/**
 * 
 */
package com.lumpofcode.dotwo.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Ed
 *
 */
public class ListProxy<K> implements List<K>
{
	private List<K> _list;
	
	public ListProxy(List<K> theProxiedList)
	{
		if(null == theProxiedList) throw new IllegalArgumentException();
	}
	
	public List<K> list()
	{
		return _list;
	}
	
	public void list(List<K> theProxiedList)
	{
		if(_list != theProxiedList)
		{
			// TODO : might notify listeners of list change
			_list = theProxiedList;
		}
	}
	
	@Override
	public boolean add(K arg0)
	{
		return _list.add(arg0);
	}

	@Override
	public void add(int arg0, K arg1)
	{
		_list.add(arg0, arg1);
		
	}

	@Override
	public boolean addAll(Collection<? extends K> arg0)
	{
		return _list.addAll(arg0);
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends K> arg1)
	{
		return _list.addAll(arg0, arg1);
	}

	@Override
	public void clear()
	{
		_list.clear();
	}

	@Override
	public boolean contains(Object arg0)
	{
		return _list.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0)
	{
		return _list.containsAll(arg0);
	}

	@Override
	public K get(int arg0)
	{
		return _list.get(arg0);
	}

	@Override
	public int indexOf(Object arg0)
	{
		return _list.indexOf(arg0);
	}

	@Override
	public boolean isEmpty()
	{
		return _list.isEmpty();
	}

	@Override
	public Iterator<K> iterator()
	{
		return _list.iterator();
	}

	@Override
	public int lastIndexOf(Object arg0)
	{
		return _list.lastIndexOf(arg0);
	}

	@Override
	public ListIterator<K> listIterator()
	{
		return _list.listIterator();
	}

	@Override
	public ListIterator<K> listIterator(int arg0)
	{
		return _list.listIterator(arg0);
	}

	@Override
	public K remove(int arg0)
	{
		return _list.remove(arg0);
	}

	@Override
	public boolean remove(Object object)
	{
		return _list.remove(object);
	}

	@Override
	public boolean removeAll(Collection<?> arg0)
	{
		return _list.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0)
	{
		return _list.retainAll(arg0);
	}

	@Override
	public K set(int location, K object)
	{
		return _list.set(location, object);
	}

	@Override
	public int size()
	{
		return _list.size();
	}

	@Override
	public List<K> subList(int start, int end)
	{
		return _list.subList(start, end);
	}

	@Override
	public Object[] toArray()
	{
		return _list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] array)
	{
		return _list.toArray(array);
	}

}
