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
package org.eclipse.osee.ote.core.environment.status;

import java.rmi.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.command.CommandDescription;
import org.eclipse.osee.ote.core.environment.command.TestEnvironmentCommand;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentListener;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;


/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class StatusBoard implements ITestEnvironmentListener, OTEStatusBoard{
	private static final long TP_UPDATE_THROTTLE = 5000;
   private CommandDescription currentCommand;
	private ArrayList<IServiceStatusListener> listeners;

	private long lastTpUpdateTime = 0;
	
//	private long tpSentCount;
//	private long tpCanceledCount;
    /**
    * @return the listeners
    */
   ArrayList<IServiceStatusListener> getListeners() {
      return listeners;
   }

   private ThreadPoolExecutor executor ;
   private ScheduledExecutorService  scheduledExecutor;
   private Object testPointLock = new Object();
   private TestPointStatusBoardRunnable latestTestPointUpdate;
   private AtomicBoolean executeLatestTestPointUpdate = new AtomicBoolean();
//   private Future<?> lastTestPointUpdate;

	/**
	 * StatusBoard Constructor. This class handles passing status information from the test
	 * enviornment to the UI's (StatusHandler Class).
	 */
	public StatusBoard() {
		super();
		GCHelper.getGCHelper().addRefWatch(this);
		this.listeners = new ArrayList<IServiceStatusListener>(6);
		executeLatestTestPointUpdate.set(false);
		executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(1);
	   scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
	   scheduledExecutor.scheduleAtFixedRate(new Runnable(){

         public void run() {
            synchronized (testPointLock) {
                if(executeLatestTestPointUpdate.get()){
                   executeLatestTestPointUpdate.set(false);
                   lastTpUpdateTime = System.currentTimeMillis();
                   executor.submit(latestTestPointUpdate);
                }
            }
         }
	      
	   },  TP_UPDATE_THROTTLE,TP_UPDATE_THROTTLE, TimeUnit.MILLISECONDS);
	}

	/**
	 * Add UI listner to list of listners.
	 * 
	 * @param listener Refernece to the UI listener.
	 */
	public void addStatusListener(IServiceStatusListener listener) {
		listeners.add(listener);
	}

	public void onCommandAdded(TestEnvironment env, TestEnvironmentCommand cmd) {
		CommandAdded cmdAdded 	= new CommandAdded();
		cmdAdded.set(cmd.getDescription());
		notifyListeners(cmdAdded);
	}   

	public void onCommandRemoved(TestEnvironment env, CommandDescription cmdDesc, CommandEndedStatusEnum status) {
		CommandRemoved cmdRemoved = new CommandRemoved();
		cmdRemoved.setDescription(cmdDesc);
		cmdRemoved.setReason(status);
		notifyListeners(cmdRemoved);
	}

	public void onException(String message, Throwable t) {
		EnvironmentError envError= new EnvironmentError();
		envError.set(t);
		notifyListeners(envError);
	}

	/**
	 * Remove UI listener from list of listners
	 * 
	 * @param listener Reference to the UI listener.
	 */
	public void removeStatusListener(IServiceStatusListener listener) {
		listeners.remove(listener);
	}


	public void onCommandBegan(TestEnvironment env, CommandDescription cmdDesc) {
		this.currentCommand = cmdDesc;
		
		SequentialCommandBegan seqCmdBegan= new SequentialCommandBegan();
		seqCmdBegan.set(cmdDesc);
		notifyListeners(seqCmdBegan);
	}


	public void onCommandFinished(TestEnvironment env, CommandDescription cmdDesc, CommandEndedStatusEnum status) {
		OseeLog.log(TestEnvironment.class,		
				Level.INFO,
				"To End: " + cmdDesc.getGuid());
		
		SequentialCommandEnded seqCmdEnded= new SequentialCommandEnded();
		seqCmdEnded.set(cmdDesc, status);
		notifyListeners(seqCmdEnded);
	}


	public void onTestPointUpdate(int pass, int fail, String testClassName) {
	   TestPointStatusBoardRunnable runnable =  new TestPointStatusBoardRunnable(new TestPointUpdate(pass, fail, testClassName), this);
	   if(System.currentTimeMillis() - lastTpUpdateTime > TP_UPDATE_THROTTLE){
   	   lastTpUpdateTime = System.currentTimeMillis();
  		   executor.submit(runnable);
	   } else {
	      synchronized (testPointLock) {
	            latestTestPointUpdate = runnable;
	            executeLatestTestPointUpdate.set(true);
         }
	   }
	}

	
	
	void notifyListeners(final IServiceStatusData data) {
		IServiceStatusData classData = data;
		executor.execute(new StatusBoardRunnable(classData) {
			public void run() {

				int size = listeners.size();
				for (int i = 0; i < size; i++) {
					try {
						listeners.get(i).statusBoardUpdated(getData());
					} catch (ConnectException e) {
					   OseeLog.log(TestEnvironment.class,Level.SEVERE,
								e.getMessage(), e);
						listeners.remove(i);
						notifyListeners(getData());
						return;
					} catch (Throwable e) {
						e.printStackTrace();
						OseeLog.log(TestEnvironment.class, Level.SEVERE,
								e.getMessage(), e);
					}
				}
			}
		});
	}
	
	
	

	public void onEnvironmentKilled(TestEnvironment env) {

	}

	public void dispose(){
		currentCommand = null;
		listeners.clear();
		listeners = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentListener#onTestServerCommandFinished(org.eclipse.osee.ote.core.environment.TestEnvironment, org.eclipse.osee.ote.core.framework.command.ICommandHandle)
	 */
	public void onTestServerCommandFinished(TestEnvironment env,
			ICommandHandle handle) {
		notifyListeners(new TestServerCommandComplete(handle));
	}
	
	public void onTestComplete(String className, String serverOutfilePath, String clientOutfilePath, CommandEndedStatusEnum status, List<IHealthStatus> healthStatus){
		notifyListeners(new TestComplete(className, serverOutfilePath, clientOutfilePath, status, healthStatus));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentListener#onTestStart(java.lang.String)
	 */
	public void onTestStart(String className) {
		notifyListeners(new TestStart(className));
	}

}