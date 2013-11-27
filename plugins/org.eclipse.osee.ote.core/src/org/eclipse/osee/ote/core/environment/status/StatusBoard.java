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

import java.io.IOException;
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
import org.eclipse.osee.ote.core.environment.status.msg.CommandAddedMessage;
import org.eclipse.osee.ote.core.environment.status.msg.CommandRemovedMessage;
import org.eclipse.osee.ote.core.environment.status.msg.EnvErrorMessage;
import org.eclipse.osee.ote.core.environment.status.msg.SequentialCommandBeganMessage;
import org.eclipse.osee.ote.core.environment.status.msg.SequentialCommandEndedMessage;
import org.eclipse.osee.ote.core.environment.status.msg.TestCompleteMessage;
import org.eclipse.osee.ote.core.environment.status.msg.TestPointUpdateMessage;
import org.eclipse.osee.ote.core.environment.status.msg.TestServerCommandCompleteMessage;
import org.eclipse.osee.ote.core.environment.status.msg.TestStartMessage;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;
import org.eclipse.osee.ote.core.framework.command.ITestCommandResult;
import org.eclipse.osee.ote.core.framework.command.TestCommandStatus;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;
import org.osgi.service.event.EventAdmin;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class StatusBoard implements ITestEnvironmentListener, OTEStatusBoard {
   private static final long TP_UPDATE_THROTTLE = 5000;
   
   private long lastTpUpdateTime = 0;

   private final ThreadPoolExecutor executor;
   private final ScheduledExecutorService scheduledExecutor;
   private final Object testPointLock = new Object();
   private TestPointStatusBoardRunnable latestTestPointUpdate;
   private final AtomicBoolean executeLatestTestPointUpdate = new AtomicBoolean();
private EventAdmin eventAdmin;

   //   private Future<?> lastTestPointUpdate;

   /**
    * StatusBoard Constructor. This class handles passing status information from the test enviornment to the UI's
    * (StatusHandler Class).
    */
   public StatusBoard() {
      super();
      GCHelper.getGCHelper().addRefWatch(this);
      executeLatestTestPointUpdate.set(false);
      executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
      scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
      scheduledExecutor.scheduleAtFixedRate(new Runnable() {

         @Override
         public void run() {
            synchronized (testPointLock) {
               if (executeLatestTestPointUpdate.get()) {
                  executeLatestTestPointUpdate.set(false);
                  lastTpUpdateTime = System.currentTimeMillis();
                  executor.submit(latestTestPointUpdate);
               }
            }
         }

      }, TP_UPDATE_THROTTLE, TP_UPDATE_THROTTLE, TimeUnit.MILLISECONDS);
   }


   public void start(){
	   
   }
   
   public void stop(){
	  dispose();   
   }
   
   public void bindEventAdmin(EventAdmin eventAdmin){
	   this.eventAdmin = eventAdmin;
   }
   
   public void unbindEventAdmin(EventAdmin eventAdmin){
	   this.eventAdmin = null;
   }
   
   @Override
   public void onCommandAdded(TestEnvironment env, TestEnvironmentCommand cmd) {
      CommandAdded cmdAdded = new CommandAdded();
      cmdAdded.set(cmd.getDescription());
      try {
    	  CommandAddedMessage msg = new CommandAddedMessage(cmdAdded);
    	  notifyListeners(msg);
      } catch (IOException e) {
    	  OseeLog.log(StatusBoard.class, Level.SEVERE, e);
      }
   }

   @Override
   public void onCommandRemoved(TestEnvironment env, CommandDescription cmdDesc, CommandEndedStatusEnum status) {
      CommandRemoved cmdRemoved = new CommandRemoved();
      cmdRemoved.setDescription(cmdDesc);
      cmdRemoved.setReason(status);
      try {
    	  CommandRemovedMessage msg = new CommandRemovedMessage(cmdRemoved);
    	  notifyListeners(msg);
      } catch (IOException e) {
    	  OseeLog.log(StatusBoard.class, Level.SEVERE, e);
      }
   }

   @Override
   public void onException(String message, Throwable t) {
      EnvironmentError envError = new EnvironmentError();
      envError.set(t);
      try {
    	  EnvErrorMessage msg = new EnvErrorMessage(envError);
    	  notifyListeners(msg);
      } catch (IOException e) {
    	  OseeLog.log(StatusBoard.class, Level.SEVERE, e);
      }
   }

   @Override
   public void onCommandBegan(TestEnvironment env, CommandDescription cmdDesc) {
      SequentialCommandBegan seqCmdBegan = new SequentialCommandBegan();
      seqCmdBegan.set(cmdDesc);
      try {
    	  SequentialCommandBeganMessage msg = new SequentialCommandBeganMessage(seqCmdBegan);
    	  notifyListeners(msg);
      } catch (IOException e) {
    	  OseeLog.log(StatusBoard.class, Level.SEVERE, e);
      }
   }

   @Override
   public void onCommandFinished(TestEnvironment env, CommandDescription cmdDesc, CommandEndedStatusEnum status) {
      OseeLog.log(TestEnvironment.class, Level.INFO, "To End: " + cmdDesc.getGuid());

      SequentialCommandEnded seqCmdEnded = new SequentialCommandEnded();
      seqCmdEnded.set(cmdDesc, status);
      try {
    	  SequentialCommandEndedMessage msg = new SequentialCommandEndedMessage(seqCmdEnded);
    	  notifyListeners(msg);
      } catch (IOException e) {
    	  OseeLog.log(StatusBoard.class, Level.SEVERE, e);
      }
   }

   @Override
   public void onTestPointUpdate(int pass, int fail, String testClassName) {
	   try {
		   TestPointStatusBoardRunnable runnable =
				   new TestPointStatusBoardRunnable(new TestPointUpdateMessage(new TestPointUpdate(pass, fail, testClassName)), eventAdmin);
		   if (System.currentTimeMillis() - lastTpUpdateTime > TP_UPDATE_THROTTLE) {
			   lastTpUpdateTime = System.currentTimeMillis();
			   executor.submit(runnable);
		   } else {
			   synchronized (testPointLock) {
				   latestTestPointUpdate = runnable;
				   executeLatestTestPointUpdate.set(true);
			   }
		   }
	   } catch (IOException e) {
		   OseeLog.log(StatusBoard.class, Level.SEVERE, e);
	   }
   }

   @SuppressWarnings("rawtypes")
   void notifyListeners(final SerializedClassMessage msg) {
      executor.execute(new StatusBoardRunnable(msg) {
         @Override
         public void run() {
        	 OteEventMessageUtil.sendEvent(msg, eventAdmin);
         }
      });
   }

   @Override
   public void onEnvironmentKilled(TestEnvironment env) {

   }

   @Override
   public void dispose() {
   }

   @Override
   public void onTestServerCommandFinished(TestEnvironment env, ICommandHandle handle) {
	   try {
	      ITestCommandResult status = handle.get();
	      TestCommandStatus cmdStatus = null;
	      Throwable th = null;
	      if(status != null){
	         cmdStatus = status.getStatus();
	         th = status.getThrowable();
	      }
		   TestServerCommandCompleteMessage msg = new TestServerCommandCompleteMessage(new TestServerCommandComplete(cmdStatus, th));
		   notifyListeners(msg);
	   } catch (IOException e) {
		   OseeLog.log(StatusBoard.class, Level.SEVERE, e);
	   }
   }

   @Override
   public void onTestComplete(String className, String serverOutfilePath, String clientOutfilePath, CommandEndedStatusEnum status, List<IHealthStatus> healthStatus) {
	   try {
		   TestCompleteMessage msg = new TestCompleteMessage(new TestComplete(className, serverOutfilePath, clientOutfilePath, status, healthStatus));
		   notifyListeners(msg);
	   } catch (IOException e) {
		   OseeLog.log(StatusBoard.class, Level.SEVERE, e);
	   }
   }

   @Override
   public void onTestStart(String className, String serverOutfilePath, String clientOutfilePath ) {
	   try {
		   TestStartMessage msg = new TestStartMessage(new TestStart(className, serverOutfilePath, clientOutfilePath));
		   notifyListeners(msg);
	   } catch (IOException e) {
		   OseeLog.log(StatusBoard.class, Level.SEVERE, e);
	   }
   }

}