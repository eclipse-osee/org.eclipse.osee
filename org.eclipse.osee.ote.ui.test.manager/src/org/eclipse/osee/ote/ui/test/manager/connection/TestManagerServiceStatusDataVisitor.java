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
package org.eclipse.osee.ote.ui.test.manager.connection;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.status.CommandAdded;
import org.eclipse.osee.ote.core.environment.status.CommandEndedStatusEnum;
import org.eclipse.osee.ote.core.environment.status.CommandRemoved;
import org.eclipse.osee.ote.core.environment.status.EnvironmentError;
import org.eclipse.osee.ote.core.environment.status.IServiceStatusData;
import org.eclipse.osee.ote.core.environment.status.IServiceStatusDataCommand;
import org.eclipse.osee.ote.core.environment.status.IServiceStatusDataVisitor;
import org.eclipse.osee.ote.core.environment.status.SequentialCommandBegan;
import org.eclipse.osee.ote.core.environment.status.SequentialCommandEnded;
import org.eclipse.osee.ote.core.environment.status.TestComplete;
import org.eclipse.osee.ote.core.environment.status.TestPointUpdate;
import org.eclipse.osee.ote.core.environment.status.TestServerCommandComplete;
import org.eclipse.osee.ote.core.environment.status.TestStart;
import org.eclipse.osee.ote.core.framework.command.ITestCommandResult;
import org.eclipse.osee.ote.core.framework.command.TestCommandStatus;
import org.eclipse.osee.ote.ui.TestCoreGuiPlugin;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask.ScriptStatusEnum;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
final class TestManagerServiceStatusDataVisitor implements IServiceStatusDataVisitor {

   private final ScriptManager scriptManager;
   private final TestManagerEditor testManagerEditor;
   private ExecutorService executor;

   protected TestManagerServiceStatusDataVisitor(ScriptManager scriptManager, TestManagerEditor testManagerEditor) {
      this.scriptManager = scriptManager;
      this.testManagerEditor = testManagerEditor;
      executor = Executors.newSingleThreadExecutor();
      
   }

   public void asCommandAdded(final CommandAdded commandAdded) {
      executor.submit(new StatusBoardRecieveEvent<CommandAdded>(commandAdded) {
         @Override
         public void run() {
            checkServiceStatusDataValid(commandAdded);
            logServiceStatusData(commandAdded);
            final ScriptTask task = getScriptTask(commandAdded);
            if (task != null) {
               task.setStatus(ScriptStatusEnum.IN_QUEUE);
               scriptManager.updateScriptTableViewer(task);
            }
         }
      });
      logExecutorSize();
   }

   public void asCommandRemoved(final CommandRemoved commandRemoved) {
      executor.submit(new StatusBoardRecieveEvent<CommandRemoved>(commandRemoved) {
         @Override
         public void run() {
            checkServiceStatusDataValid(commandRemoved);
            logServiceStatusData(commandRemoved);

            final ScriptTask task = getScriptTask(commandRemoved);
            if (task != null) {
               CommandEndedStatusEnum cmdStat = commandRemoved.getReason();
               if (cmdStat.equals(CommandEndedStatusEnum.ABORTED)) {
                  logOnConsole(Level.SEVERE, String.format("Test Aborted: [%s]", task.getName()));
                  task.setStatus(ScriptStatusEnum.CANCELLED);
               }
               notifyExecutionComplete(task);
               // userEnvironment.notifyScriptDequeued(task.getGuid());
               scriptManager.updateScriptTableViewer(task);
            }
         }
      });
      logExecutorSize();
   }

   public void asEnvironmentError(final EnvironmentError environmentError) {
      executor.submit(new StatusBoardRecieveEvent<EnvironmentError>(environmentError) {
         @Override
         public void run() {
            checkServiceStatusDataValid(environmentError);

            OseeLog.log(TestManagerPlugin.class, Level.SEVERE,
                  "errorOccured: " + environmentError.getErr().getMessage());
            environmentError.getErr().printStackTrace();
            final String msg = Lib.exceptionToString(environmentError.getErr());
            logOnConsole(Level.SEVERE, String.format("Test Environment Error: [%s]", msg));
            disconnectOnError(msg);
         }
      });
      logExecutorSize();
   }

   public void asSequentialCommandBegan(final SequentialCommandBegan sequentialCommandBegan) {
      executor.submit(new StatusBoardRecieveEvent<SequentialCommandBegan>(sequentialCommandBegan) {
         @Override
         public void run() {
            checkServiceStatusDataValid(sequentialCommandBegan);
            logServiceStatusData(sequentialCommandBegan);

            final ScriptTask task = getScriptTask(sequentialCommandBegan);
            if (task != null && task.getScriptModel() != null) {
               OseeLog.log(TestManagerPlugin.class, Level.INFO, String.format("Script Task: [%s]", task));
               logOnConsole(Level.INFO, String.format("Test Starting: [%s]", task.getName()));
               task.setStatus(ScriptStatusEnum.RUNNING);
               scriptManager.updateScriptTableViewer(task);
            }
         }
      });
      logExecutorSize();
   }

   public void asSequentialCommandEnded(final SequentialCommandEnded sequentialCommandEnded) {

      executor.submit(new StatusBoardRecieveEvent<SequentialCommandEnded>(sequentialCommandEnded) {
         @Override
         public void run() {
            checkServiceStatusDataValid(sequentialCommandEnded);
            logServiceStatusData(sequentialCommandEnded);
            final ScriptTask task = getScriptTask(sequentialCommandEnded);
            if (task != null) {
               OseeLog.log(TestManagerPlugin.class, Level.INFO, String.format("Script Task: [%s]", task));
               CommandEndedStatusEnum cmdStat = sequentialCommandEnded.getStatus();
               switch (cmdStat) {
                  case ABORTED:
                     logOnConsole(Level.SEVERE, String.format("Test Aborted: [%s]", task.getName()));
                     task.setStatus(ScriptStatusEnum.CANCELLED);
                     break;
                  case EXCEPTION:
                     task.setStatus(ScriptStatusEnum.CANCELLED);
                     logOnConsole(Level.SEVERE,
                           String.format("Test Aborted: [%s] - Exception Occurred", task.getName()));
                     break;
                  case HUNG:
                     task.setStatus(ScriptStatusEnum.CANCELLED);
                     logOnConsole(Level.SEVERE, String.format("Test Hung: [%s]", task.getName()));
                     break;
                  case RAN_TO_COMPLETION:
                     task.setStatus(ScriptStatusEnum.COMPLETE);
                     break;
                  default:
                     task.setStatus(ScriptStatusEnum.COMPLETE);
                     logOnConsole(Level.SEVERE, String.format("Test Ended Unexpectedly: [%s]", task.getName()));
                     break;
               }
               // onOutfileSave(task, sequentialCommandEnded.getDescription(),
               // isValidRun);
               logOnConsole(Level.INFO, String.format("Test Completed: [%s]", task.getName()));
               notifyExecutionComplete(task);
               // userEnvironment.notifyScriptDequeued(task.getGuid());
               scriptManager.updateScriptTableViewer(task);
            }
         }
      });
      logExecutorSize();
   }

   public void asTestPointUpdate(final TestPointUpdate testPointUpdate) {
      executor.submit(new StatusBoardRecieveEvent<TestPointUpdate>(testPointUpdate) {
         @Override
         public void run() {
            checkServiceStatusDataValid(testPointUpdate);
            final ScriptTask task = scriptManager.getScriptTask(testPointUpdate.getClassName());
            // final ScriptTask task = getScriptTask(testPointUpdate);
            if (task != null) {
               task.getScriptModel().getOutputModel().setPassedTestPoints(testPointUpdate.getPass());
               task.getScriptModel().getOutputModel().setFailedTestPoints(testPointUpdate.getFail());
               scriptManager.updateScriptTableViewerTimed(task);
            } else {
               OseeLog.log(TestManagerPlugin.class, Level.WARNING, "testPointsUpdated: task is null");
            }
         }
      });
      logExecutorSize();
   }

   private synchronized void logOnConsole(final Level level, final String msg) {
      if (level.equals(Level.SEVERE)) {
         TestCoreGuiPlugin.getDefault().getConsole().writeError(msg);
      } else {
         TestCoreGuiPlugin.getDefault().getConsole().write(msg);
      }
   }

   private void checkServiceStatusDataValid(IServiceStatusData statusData) {
      if (statusData == null) {
         throw new IllegalArgumentException(String.format("Error [%s] was null.", IServiceStatusData.class.getName()));
      }
   }

   private void disconnectOnError(final String cause) {
      Display.getDefault().asyncExec(new Runnable() {

         public void run() {
            TestCoreGuiPlugin.getDefault().getConsole().writeError(cause);
         }
      });
   }

   private void logServiceStatusData(IServiceStatusDataCommand statusData) {
      OseeLog.log(TestManagerPlugin.class, Level.FINE, String.format("%s: %s ", statusData.getClass().getName(),
            statusData.getDescription()));
   }

   private ScriptTask getScriptTask(IServiceStatusDataCommand statusData) {
      // statusData.getDescription().getDescription()
      return scriptManager.getScriptTask(statusData.getDescription().getDescription());
   }

   private void notifyExecutionComplete(ScriptTask scriptTask) {
      // if (userEnvironment.getLastGUIDToRun() != null &&
      // scriptTask.getGuid().equals(userEnvironment.getLastGUIDToRun())) {
      Display.getDefault().asyncExec(new Runnable() {

         public void run() {
            testManagerEditor.executionCompleted();
         }
      });
      logExecutorSize();
      // }
   }

   public void asTestServerCommandComplete(final TestServerCommandComplete end) {

      executor.submit(new StatusBoardRecieveEvent<TestServerCommandComplete>(end) {
         @Override
         public void run() {
            try {
               ITestCommandResult result = end.getHandle().get();
               TestCommandStatus status = result.getStatus();
               Throwable th = result.getThrowable();
               if (th != null) {
                  th.printStackTrace();
               }
               if (status != null) {
                  System.out.println(status.name());
               }
            } catch (RemoteException ex) {
               OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
            }

            notifyExecutionComplete(null);
         }
      });
      logExecutorSize();
   }

   public void asTestComplete(final TestComplete testComplete) {
      executor.submit(new StatusBoardRecieveEvent<TestComplete>(testComplete) {
         @Override
         public void run() {
            ScriptTask task = scriptManager.getScriptTask(testComplete.getClassName());
            if (task != null) {
               OseeLog.log(TestManagerPlugin.class, Level.INFO, String.format("Script Task: [%s]", task));
               boolean isValidRun = true;
               CommandEndedStatusEnum cmdStat = testComplete.getStatus();
               switch (cmdStat) {
                  case ABORTED:
                     logOnConsole(Level.SEVERE, String.format("Test Aborted: [%s]", task.getName()));
                     for (IHealthStatus status : testComplete.getHealthStatus()) {
                        String msg;
                        if (status.getException() != null) {
                           msg = Lib.exceptionToString(status.getException());
                        } else {
                           msg = status.getMessage();
                        }
                        logOnConsole(status.getLevel(), msg);
                     }
                     task.setStatus(ScriptStatusEnum.CANCELLED);
                     break;
                  case EXCEPTION:
                     task.setStatus(ScriptStatusEnum.CANCELLED);
                     logOnConsole(Level.SEVERE, String.format("Test Exception: [%s] - Exception Occurred",
                           task.getName()));
                     StringBuilder sb = new StringBuilder();
                     try {
                        for (IHealthStatus status : testComplete.getHealthStatus()) {
                           if (status.getException() != null) {
                              sb.append(Lib.exceptionToString(status.getException()));
                           } else if (status.getLevel().intValue() >= Level.SEVERE.intValue()) {
                              sb.append(status.getMessage());
                           }
                        }
                        logOnConsole(Level.SEVERE, sb.toString());
                     } catch (Throwable th) {
                        th.printStackTrace();
                     }
                     break;
                  case HUNG:
                     task.setStatus(ScriptStatusEnum.CANCELLED);
                     logOnConsole(Level.SEVERE, String.format("Test Hung: [%s]", task.getName()));
                     break;
                  case RAN_TO_COMPLETION:
                     task.setStatus(ScriptStatusEnum.COMPLETE);
                     break;
                  default:
                     task.setStatus(ScriptStatusEnum.COMPLETE);
                     logOnConsole(Level.SEVERE, String.format("Test Ended Unexpectedly: [%s]", task.getName()));
                     isValidRun = false;
                     break;
               }
               scriptManager.storeOutFile(task, testComplete, isValidRun);
               logOnConsole(Level.INFO, String.format("Test Completed: [%s]", task.getName()));

               scriptManager.notifyScriptDequeued(testComplete.getClassName());
               scriptManager.updateScriptTableViewer(task);

            }
         }
      });
      logExecutorSize();
   }

   public void asTestStart(final TestStart testStart) {
      executor.submit(new StatusBoardRecieveEvent<TestStart>(testStart) {
         @Override
         public void run() {
            ScriptTask task = scriptManager.getScriptTask(testStart.getTestClassName());
            if (task != null) {
               scriptManager.notifyScriptStart(task);
               logOnConsole(Level.INFO, String.format("Test Started: [%s]", task.getName()));
            }
         }
      });
      logExecutorSize();
   }
   
   private void logExecutorSize(){
      if(executor instanceof ThreadPoolExecutor){
         OseeLog.log(TestManagerServiceStatusDataVisitor.class, Level.FINE, String.format("Current StatusBoard Executor Size [%d]", ((ThreadPoolExecutor)executor).getQueue().size()));
      }
   }
}
