package com.perf.lib;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Task<E> implements Runnable{
	private Callable<E> task;
	private CountDownLatch startlatch,endlatch;
	private ExecutorService service;
	private Thread t;
	private int contype;
	
	public Task(Callable<E> call,CountDownLatch start,CountDownLatch end,ExecutorService service, int contype){
		this.task=call;
		this.startlatch=start;
		this.endlatch=end;
		this.service=service;
		this.contype=contype;
		t=new Thread(this);
		
	}
	
	public void start(){
		t.start();
	}
	
	public void run() {
		try{
			if(contype==0){
				startlatch.await();
			}
			
			
			Future<E> fut=service.submit(task);
			Boolean ret=(Boolean)fut.get();
			System.out.println("Thread-"+Thread.currentThread().getId()+":"+ret);
			endlatch.countDown();
		}catch(InterruptedException e){
			
		}catch(Exception e){
			
		}
		
		
		
	}
	
}
