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
package org.eclipse.osee.orcs.db.internal.console.inwork;

/**
 * @author Roberto E. Escobar
 */
public class DataStoreBackupApplication {
   //   implements IApplication {
   //   private static final String ALL_BRANCHES_QUERY = "select branch_id from osee_branch";
   //
   //   private List<Integer> getAllBranches() throws OseeCoreException {
   //      List<Integer> toReturn = new ArrayList<Integer>();
   //      IOseeStatement chStmt = ConnectionHandler.getStatement();
   //      try {
   //         chStmt.runPreparedQuery(100, ALL_BRANCHES_QUERY);
   //         while (chStmt.next()) {
   //            toReturn.add(chStmt.getInt("branch_id"));
   //         }
   //      } finally {
   //         chStmt.close();
   //      }
   //      return toReturn;
   //   }
   //
   //   private void transferToBackupLocation(IResourceLocator locator, File backupFolder) throws Exception {
   //      InputStream inputStream = null;
   //      OutputStream outputStream = null;
   //      try {
   //         IResource resource = Activator.getResourceManager().acquire(locator, new PropertyStore());
   //         inputStream = resource.getContent();
   //
   //         outputStream = new BufferedOutputStream(new FileOutputStream(new File(backupFolder, resource.getName())));
   //
   //         Lib.inputStreamToOutputStream(inputStream, outputStream);
   //      } finally {
   //         try {
   //            if (inputStream != null) {
   //               inputStream.close();
   //            }
   //         } finally {
   //            if (outputStream != null) {
   //               outputStream.flush();
   //               outputStream.close();
   //            }
   //         }
   //      }
   //   }
   //
   //   @Override
   //   public Object start(IApplicationContext context) throws Exception {
   //      try {
   //         String backupName = "osee_" + Lib.getDateTimeString();
   //
   //         PropertyStore options = new PropertyStore();
   //         options.put(ExportOptions.COMPRESS.name(), true);
   //
   //         List<Integer> branchIds = getAllBranches();
   //
   //         int totalBranches = branchIds.size();
   //         OseeLog.logf(Activator.class, Level.INFO, "Exporting [%s] branch%s", totalBranches,
   //            totalBranches == 1 ? "" : "es");
   //
   //         IResourceLocator exportLocator = Activator.getBranchExchange().exportBranch(backupName, options, branchIds);
   //
   //         OseeLog.logf(Activator.class, Level.INFO, "Verifying export file integrity [%s]", exportLocator.getLocation());
   //
   //         IResourceLocator exportCheckLocator = Activator.getBranchExchange().checkIntegrity(exportLocator);
   //         OseeLog.logf(Activator.class, Level.INFO, "Verified [%s]", exportCheckLocator.getLocation());
   //
   //         OseeLog.logf(Activator.class, Level.INFO, "Completed export - [%s] branch%s", totalBranches,
   //            totalBranches == 1 ? "" : "es");
   //
   //         String backupPath = (String) context.getArguments().get("osee.backup.path");
   //         if (Strings.isValid(backupPath)) {
   //            if (!backupPath.endsWith(File.separator)) {
   //               backupPath = backupPath + File.separator;
   //            }
   //            File backupFolder = new File(backupPath);
   //            backupFolder.mkdirs();
   //            transferToBackupLocation(exportLocator, backupFolder);
   //            transferToBackupLocation(exportCheckLocator, backupFolder);
   //
   //            Activator.getResourceManager().delete(exportLocator);
   //            Activator.getResourceManager().delete(exportCheckLocator);
   //         }
   //      } catch (Throwable ex) {
   //         OseeLog.log(Activator.class, Level.SEVERE, ex);
   //      }
   //      return IApplication.EXIT_OK;
   //   }
   //
   //   @Override
   //   public void stop() {
   //      //
   //   }

}
