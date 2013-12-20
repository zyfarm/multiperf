package com.perf.lib;

import java.util.concurrent.CountDownLatch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("unused")
public class MainTaskFrame {

	private int concurrent_type;
	private int concurrent_num;
	private int concurrent_worker;
	private ExecutorService service = null;
	private CountDownLatch startlatch = new CountDownLatch(1);
	private CountDownLatch endlatch = null;
	private ArrayList<DummyCallable> calllist;
	private Long RT = null;
	private Long startmemory;
	private Timer memorywatcher;
	private int contype;
	public static Long memory = 0L;
	public static int REAL_CON=0;
	public static int FAKE_CON=1;
	public MainTaskFrame(int type, int concurrent_num, int worker_num,
			int exectype, Long time, int contype) {
		concurrent_type = type;
		this.concurrent_num = concurrent_num;
		this.concurrent_worker = worker_num;
		this.endlatch = new CountDownLatch(concurrent_num);
		this.contype = contype;
		memorywatcher = new Timer();

		if (exectype == 1) {
			service = Executors.newCachedThreadPool();
		} else {
			service = Executors.newFixedThreadPool(worker_num);
		}

		calllist = new ArrayList<DummyCallable>();
		for (int i = 0; i < concurrent_num; i++) {
			calllist.add(new DummyCallable(time));
		}

	}

	public Long getRT() {
		return this.RT;
	}

	public Long getUsedMemory() {
		return memory - startmemory;
	}
	
	
	public void go() throws Exception {

		/**
		 * 启动线程内存使用监控
		 */
		memorywatcher.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Long totalm = Runtime.getRuntime().totalMemory();
				Long freem = Runtime.getRuntime().freeMemory();
				Long usedmemory = totalm - freem;
				if (usedmemory > memory) {
					memory = usedmemory;
					System.out.println("Memory: " + memory / 1000);
				}
			}
		}, 0, 50);

		Long totalm = Runtime.getRuntime().totalMemory();
		Long freem = Runtime.getRuntime().freeMemory();
		startmemory = totalm - freem;

		System.out.println("init memory:" + memory / 1000);
		Thread.sleep(1000);
		
		
		if (this.contype == REAL_CON) {
			for (int i = 0; i < concurrent_num; i++) {
				Task runner = new Task<Boolean>(calllist.get(i), startlatch,
						endlatch, service, contype);
				runner.start();
			}

			System.out.println("Go Test...");
			startlatch.countDown();
			Long start = System.currentTimeMillis();
			endlatch.await();
			Long end = System.currentTimeMillis();
			RT = end - start;
		} else {
			for (int i = 0; i < concurrent_num; i++) {
				Task runner = new Task<Boolean>(calllist.get(i), startlatch,
						endlatch, service, contype);
				runner.start();
			}
			

			Long start = System.currentTimeMillis();
			endlatch.await();
			Long end = System.currentTimeMillis();
			RT = end - start;
		}

	}

}
