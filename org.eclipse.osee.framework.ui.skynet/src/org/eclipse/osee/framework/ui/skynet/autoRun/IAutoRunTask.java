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

package org.eclipse.osee.framework.ui.skynet.autoRun;

import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IAutoRunTask {

   public enum RunDb {
      Production_Db, Test_Db
   };

   public enum TaskType {
      Regression, Db_Health, Data_Exchange, Backup
   }

   public void startTasks(XResultData resultData) throws Exception;

   /**
    * @return xx:xx start time
    */
   public String get24HourStartTime();

   public String[] getNotificationEmailAddresses();

   public void setAutoRunUniqueId(String autoRunUniqueId);

   public String getAutoRunUniqueId();

   public TaskType getTaskType();

   public String getDescription();

   public RunDb getRunDb();

   public String getCategory();
}
