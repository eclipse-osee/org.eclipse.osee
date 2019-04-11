/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.init;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseInitConfiguration implements IDatabaseInitConfiguration {
   private final List<String> dbInitTasks;
   private final List<String> oseeTypeIds;
   private final List<UserToken> dbInitUsers = new ArrayList<>();

   public DatabaseInitConfiguration() {
      this.dbInitTasks = new ArrayList<>();
      this.oseeTypeIds = new ArrayList<>();
   }

   public void addTask(String taskId) {
      dbInitTasks.add(taskId);
   }

   public void addOseeType(String oseeTypesExtensionIds) {
      oseeTypeIds.add(oseeTypesExtensionIds);
   }

   public void addTask(DefaultDbInitTasks task) {
      addDefaultTask(dbInitTasks, task);
   }

   public void addOseeType(DefaultOseeTypeDefinitions typeDef) {
      addDefaultType(oseeTypeIds, typeDef);
   }

   public void addUserTokens(List<UserToken> userTokens) {
      dbInitUsers.clear();
      dbInitUsers.addAll(userTokens);
   }

   @Override
   public List<String> getTaskExtensionIds() {
      List<String> initTasks = new ArrayList<>();
      addDefaultTask(initTasks, DefaultDbInitTasks.BOOTSTRAP_TASK);
      initTasks.addAll(dbInitTasks);
      addDefaultTask(initTasks, DefaultDbInitTasks.DB_USER_CLEANUP);
      addDefaultTask(initTasks, DefaultDbInitTasks.BRANCH_DATA_IMPORT);
      addDefaultTask(initTasks, DefaultDbInitTasks.DB_STATS);
      return initTasks;
   }

   @Override
   public List<String> getOseeTypeExtensionIds() {
      Set<String> oseeTypes = new LinkedHashSet<>();
      oseeTypes.addAll(oseeTypeIds);
      return new ArrayList<>(oseeTypes);
   }

   @Override
   public List<UserToken> getUserTokens() {
      return dbInitUsers;
   }

   private void addDefaultTask(Collection<String> initTasks, DefaultDbInitTasks task) {
      initTasks.add(task.getExtensionId());
   }

   private void addDefaultType(Collection<String> initTasks, DefaultOseeTypeDefinitions type) {
      initTasks.add(type.getExtensionId());
   }
}
