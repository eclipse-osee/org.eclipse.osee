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
import java.util.List;

public class DbInitConfiguration {
   private final boolean isBareBones;
   private final List<String> dbInitTasks;
   private final List<String> oseeTypeIds;

   public DbInitConfiguration(boolean isBareBones) {
      this.isBareBones = isBareBones;
      this.dbInitTasks = new ArrayList<String>();
      this.oseeTypeIds = new ArrayList<String>();
   }

   public void addTask(String taskId) {
      dbInitTasks.add(taskId);
   }

   public void addOseeType(String oseeTypesExtensionIds) {
      oseeTypeIds.add(oseeTypesExtensionIds);
   }

   public boolean isBareBones() {
      return isBareBones;
   }

   public List<String> getTaskExtensionIds() {
      List<String> initTasks = new ArrayList<String>();
      initTasks.add("org.eclipse.osee.framework.database.init.DbBootstrapTask");
      initTasks.addAll(dbInitTasks);
      initTasks.add("org.eclipse.osee.framework.database.init.PostDbUserCleanUp");
      initTasks.add("org.eclipse.osee.framework.database.init.SkynetDbBranchDataImport");
      initTasks.add("org.eclipse.osee.framework.database.init.PostDatabaseInitialization");
      return initTasks;
   }

   public List<String> getOseeTypeExtensionIds() {
      List<String> oseeTypes = new ArrayList<String>();
      oseeTypes.add("org.eclipse.osee.framework.skynet.core.OseeTypes_Framework");
      oseeTypes.add("org.eclipse.osee.ats.OseeTypes_ATS");
      oseeTypes.add("org.eclipse.osee.coverage.OseeTypes_Coverage");
      oseeTypes.add("org.eclipse.osee.ote.define.OseeTypesOTE");
      oseeTypes.addAll(oseeTypeIds);
      return oseeTypes;
   }
}
