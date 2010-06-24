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
package org.eclipse.osee.framework.core.datastore;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.IOseeBranchServiceProvider;
import org.eclipse.osee.framework.core.datastore.internal.Activator;
import org.eclipse.osee.framework.core.datastore.schema.operations.OperationFactory;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class DatastoreInitOperation extends AbstractOperation {

   private static final String ADD_PERMISSION =
         "INSERT INTO OSEE_PERMISSION (PERMISSION_ID, PERMISSION_NAME) VALUES (?,?)";

   private final IOseeBranchServiceProvider branchServiceProvider;
   private final IOseeDatabaseServiceProvider dbService;
   private final IOseeSchemaProvider schemaProvider;
   private final SchemaCreationOptions options;
   private final IApplicationServerManager appServerManager;

   public DatastoreInitOperation(IApplicationServerManager appServerManager, IOseeDatabaseServiceProvider dbService, IOseeBranchServiceProvider branchServiceProvider, IOseeSchemaProvider schemaProvider, SchemaCreationOptions options) {
      super("Datastore Initialization", Activator.PLUGIN_ID);
      this.appServerManager= appServerManager;
      this.dbService = dbService;
      this.branchServiceProvider = branchServiceProvider;
      this.schemaProvider = schemaProvider;
      this.options = options;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Conditions.checkExpressionFailOnTrue(dbService.getOseeDatabaseService().isProduction(),
            "Error - attempting to initialize a production datastore.");

      IOperation subOp = OperationFactory.createDbSchema(dbService, schemaProvider, options);
      doSubWork(subOp, monitor, 0.30);

      dbService.getOseeDatabaseService().getSequence().clear();

      appServerManager.executeLookupRegistration();

      deleteBinaryBackingData();
      String binaryDataPath = OseeServerProperties.getOseeApplicationServerData();
      Lib.deleteDir(new File(binaryDataPath + File.separator + "attr"));

      OseeInfo.putValue(OseeInfo.DB_ID_KEY, GUID.create());
      addDefaultPermissions();

      subOp = branchServiceProvider.getBranchService().createSystemRootBranch(monitor);
      doSubWork(subOp, monitor, 0.30);
   }

   @SuppressWarnings("unchecked")
   private void addDefaultPermissions() throws OseeDataStoreException {
      IOseeDatabaseService service = dbService.getOseeDatabaseService();
      for (PermissionEnum permission : PermissionEnum.values()) {
         service.runPreparedUpdate(ADD_PERMISSION, permission.getPermId(), permission.getName());
      }
   }

   private static void deleteBinaryBackingData() {
      String binaryDataPath = OseeServerProperties.getOseeApplicationServerData();
      OseeLog.log(Activator.class, Level.INFO, String.format("Deleting application server binary data [%s]...",
            binaryDataPath));
      Lib.deleteDir(new File(binaryDataPath + File.separator + "attr"));
   }
}
