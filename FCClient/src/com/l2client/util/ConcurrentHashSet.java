package com.l2client.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


public class ConcurrentHashSet<E> extends AbstractSet<E> {

	protected ConcurrentHashMap<E,Object> hashMap = new ConcurrentHashMap<E, Object>();
	
	private final static Object ob = new Object();

	public ConcurrentHashSet(){
		
	}

	@Override
    public boolean add(E e) {
		return hashMap.put(e, ob)!= null;
    }
	
	@Override
	public boolean remove(Object o){
		return (hashMap.remove(o)!=null);
	}
	
	@Override
	public boolean isEmpty(){
		return hashMap.isEmpty();
	}
	
	@Override
	public boolean contains(Object o){
		return hashMap.containsKey(o);
	}
	
	@Override
	public Iterator<E> iterator() {
		return hashMap.keySet().iterator();
	}
	
	@Override
	public <E> E[] toArray(E[] arr){
		return hashMap.keySet().toArray(arr);
	}
	
	@Override
	public Object[] toArray(){
		return hashMap.keySet().toArray();
	}
	
	@Override
	public boolean addAll(Collection <? extends E> col){
		boolean ret = true;
		for(E o : col)
			ret = ret && (hashMap.put(o, ob) != null);
		
		return ret;
	}
	@Override
	public int size() {
		return hashMap.size();
	}
}
