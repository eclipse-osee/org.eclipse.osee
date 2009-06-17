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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.reportdata.ReportData;
import org.eclipse.osee.framework.jdk.core.reportdata.ReportDataListener;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.command.TestEnvironmentCommand;
import org.eclipse.osee.ote.core.environment.interfaces.IReportData;

public class ReportDataControl implements IReportData {
   private ReportData queueData;
   // private ArrayList<ReportDataListener> queueListeners;
   private ArrayList<ReportDataListener> queueListeners;

   public ReportDataControl() {
      this.queueListeners = new ArrayList<ReportDataListener>();
      // ArrayList<String> queueHeaders = new ArrayList<String>();
      ArrayList<String> queueHeaders = new ArrayList<String>();
      queueHeaders.add("User");
      queueHeaders.add("Script");
      this.queueData = new ReportData(queueHeaders);
   }

   @SuppressWarnings("unchecked")
   public void addQueueListener(ReportDataListener listener, List cmds) {
      queueListeners.add(listener);
      updateQueueData(cmds);
      try {
         listener.updateData(queueData);
      }
      catch (RemoteException e) {
    	  OseeLog.log(TestEnvironment.class, Level.SEVERE, e);
      }
   }

   public void removeQueueListener(ReportDataListener listener) {
      queueListeners.remove(listener);
   }

   @SuppressWarnings("unchecked")
   public void updateQueueListeners(List cmds) {
      updateQueueData(cmds);
      for (int i = 0; i < queueListeners.size(); i++) {
         try {
            queueListeners.get(i).updateData(queueData);
         }
         catch (RemoteException e) {
        	 OseeLog.log(TestEnvironment.class, Level.SEVERE, e);
         }
      }
   }

   private void updateQueueData(List<TestEnvironmentCommand> cmds) {
      queueData.clearItems();
      ArrayList<String> values;
      for (int i = 0; i < cmds.size(); i++) {
         TestEnvironmentCommand cmd = (TestEnvironmentCommand) cmds.get(i);
         values = new ArrayList<String>();
         values.add((cmd.getUser() == null) ? "unknown" : cmd.getUser().getName());
         values.add(cmd.getDescription().getDescription());
         queueData.addItem(cmd.getDescription().getGuid(), values);
      }
   }
}
