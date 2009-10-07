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
      oseeTypes.addAll(oseeTypeIds);
      return oseeTypes;
   }
}
