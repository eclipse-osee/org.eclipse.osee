/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.environment;

import java.util.LinkedList;
import java.util.WeakHashMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;

public class AsynchRemoteJobs implements Runnable {

	private LinkedList<Runnable> jobs;
	   private static WeakHashMap<Object, AsynchRemoteJobs> map = new WeakHashMap<Object, AsynchRemoteJobs>();
	   
	   public static AsynchRemoteJobs getInstance(Object obj){
		   AsynchRemoteJobs asynchRemoteJobs = null;
		   asynchRemoteJobs = map.get(obj);
		   if(asynchRemoteJobs == null){
			   asynchRemoteJobs = new AsynchRemoteJobs();
			   map.put(obj, asynchRemoteJobs);
		   }
		   return asynchRemoteJobs;
	   }
	   
	   
	   
	   
	public AsynchRemoteJobs(){
		jobs = new LinkedList<Runnable>();
		Thread th = new Thread(this);
		th.setName("AsynchRemoteJobs[Test Environment]");
		th.start();
	}	
	
	public void addJob(Runnable job){
		synchronized(jobs){
			jobs.addLast(job);
		}
		synchronized(this){
			this.notify();
		}
	}
	
	public void run() {
		while(true){
			while(jobs.size() > 0){
				Runnable run = null;
				synchronized(jobs){
					 run = jobs.removeFirst();
				}
				run.run();				
			}
			try {
				synchronized(this){
					this.wait();
				}
			} catch (InterruptedException e) {
				OseeLog.log(TestEnvironment.class, Level.SEVERE, e);
			}
		}
	}
}
