package com.perf.lib;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DummyCallable implements Callable<Boolean>{

	private Long exectime; 
	private static int counter=0;
	private static ReentrantLock relock=new ReentrantLock();
	private static ReentrantReadWriteLock rwlock=new ReentrantReadWriteLock();
	
	public DummyCallable(Long time){
		exectime=time;
	}
	
	
	
	public Boolean call() throws Exception {
			Thread.currentThread().sleep(exectime);
			return true;
	}
	
}
