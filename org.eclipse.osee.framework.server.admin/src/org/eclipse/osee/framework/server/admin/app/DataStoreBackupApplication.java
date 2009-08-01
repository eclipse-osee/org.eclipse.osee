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
package org.eclipse.osee.framework.server.admin.app;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osee.framework.branch.management.ExportOptions;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.server.admin.Activator;

/**
 * @author Roberto E. Escobar
 */
public class DataStoreBackupApplication implements IApplication {
   private static final String ALL_BRANCHES_QUERY = "select branch_id from osee_branch";

   public DataStoreBackupApplication() {
   }

   private List<Integer> getAllBranches() throws OseeDataStoreException {
      List<Integer> toReturn = new ArrayList<Integer>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(100, ALL_BRANCHES_QUERY);
         while (chStmt.next()) {
            toReturn.add(chStmt.getInt("branch_id"));
         }
      } finally {
         chStmt.close();
      }
      return toReturn;
   }

   private void transferToBackupLocation(IResourceLocator locator, File backupFolder) throws Exception {
      InputStream inputStream = null;
      OutputStream outputStream = null;
      try {
         IResource resource = Activator.getInstance().getResourceManager().acquire(locator, new Options());
         inputStream = resource.getContent();

         outputStream = new BufferedOutputStream(new FileOutputStream(new File(backupFolder, resource.getName())));

         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } finally {
         try {
            if (inputStream != null) {
               inputStream.close();
            }
         } finally {
            if (outputStream != null) {
               outputStream.flush();
               outputStream.close();
            }
         }
      }
   }

   @Override
   public Object start(IApplicationContext context) throws Exception {
      try {
         String backupName = "osee_" + Lib.getDateTimeString();

         Options options = new Options();
         options.put(ExportOptions.COMPRESS.name(), true);

         List<Integer> branchIds = getAllBranches();

         int totalBranches = branchIds.size();
         OseeLog.log(Activator.class, Level.INFO, String.format("Exporting [%s] branch%s", totalBranches,
               totalBranches == 1 ? "" : "es"));

         IResourceLocator exportLocator =
               Activator.getInstance().getBranchExchange().exportBranch(backupName, options, branchIds);

         OseeLog.log(Activator.class, Level.INFO, String.format("Verifying export file integrity [%s]",
               exportLocator.getLocation()));

         IResourceLocator exportCheckLocator =
               Activator.getInstance().getBranchExchange().checkIntegrity(exportLocator);
         OseeLog.log(Activator.class, Level.INFO, String.format("Verified [%s]", exportCheckLocator.getLocation()));

         OseeLog.log(Activator.class, Level.INFO, String.format("Completed export - [%s] branch%s", totalBranches,
               totalBranches == 1 ? "" : "es"));

         String backupPath = (String) context.getArguments().get("osee.backup.path");
         if (Strings.isValid(backupPath)) {
            if (!backupPath.endsWith(File.separator)) {
               backupPath = backupPath + File.separator;
            }
            File backupFolder = new File(backupPath);
            backupFolder.mkdirs();
            transferToBackupLocation(exportLocator, backupFolder);
            transferToBackupLocation(exportCheckLocator, backupFolder);

            Activator.getInstance().getResourceManager().delete(exportLocator);
            Activator.getInstance().getResourceManager().delete(exportCheckLocator);
         }
      } catch (Throwable ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return IApplication.EXIT_OK;
   }

   @Override
   public void stop() {
   }

}
