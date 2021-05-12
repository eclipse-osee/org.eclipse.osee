/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.database.init;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseInitConfiguration implements IDatabaseInitConfiguration {
   private final List<String> dbInitTasks;

   public DatabaseInitConfiguration() {
      this.dbInitTasks = new ArrayList<>();
   }

   public void addTask(String taskId) {
      dbInitTasks.add(taskId);
   }

   @Override
   public List<String> getTaskExtensionIds() {
      List<String> initTasks = new ArrayList<>();
      addDefaultTask(initTasks, DefaultDbInitTasks.BOOTSTRAP_TASK);
      initTasks.addAll(dbInitTasks);
      addDefaultTask(initTasks, DefaultDbInitTasks.DB_USER_CLEANUP);
      addDefaultTask(initTasks, DefaultDbInitTasks.BRANCH_DATA_IMPORT);
      return initTasks;
   }

   private void addDefaultTask(Collection<String> initTasks, DefaultDbInitTasks task) {
      initTasks.add(task.getExtensionId());
   }
}