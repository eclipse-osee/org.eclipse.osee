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

package org.eclipse.osee.framework.database.init.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.BaseCredentialProvider;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.database.init.IDatabaseInitConfiguration;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.httpRequests.DatastoreInitializationOperation;
import org.eclipse.osee.jdbc.JdbcClient;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * @author Andrew M. Finkbeiner
 */
public class DbBootstrapTask implements IDbInitializationTask {
   private IDatabaseInitConfiguration configuration;

   public void setConfiguration(IDatabaseInitConfiguration configuration) {
      this.configuration = configuration;
   }

   @Override
   public void run() {
      Conditions.checkNotNull(configuration, "DbInitConfiguration Info");

      OseeClientProperties.setInDbInit(true);

      createOseeDatastore();

      Bundle bundle = Platform.getBundle("org.eclipse.osee.framework.skynet.core");
      int state = bundle.getState();
      if (state != Bundle.ACTIVE) {
         try {
            bundle.start();
         } catch (BundleException ex) {
            throw new OseeCoreException(ex);
         }
      }
      IOseeCachingService service = Activator.getInstance().getCachingService();
      service.clearAll();

      JdbcClient jdbcClient = Activator.getInstance().getJdbcClient();
      jdbcClient.invalidateSequences();

      Conditions.checkNotNull(BranchManager.getBranchToken(SYSTEM_ROOT), "System root was not created - ");

      ClientSessionManager.releaseSession();
      ClientSessionManager.authenticate(new BaseCredentialProvider() {

         @Override
         public OseeCredential getCredential() {
            OseeCredential credential = super.getCredential();
            credential.setUserName(SystemUser.BootStrap.getName());
            return credential;
         }
      });

      List<String> oseeTypes = configuration.getOseeTypeExtensionIds();
      Conditions.checkExpressionFailOnTrue(oseeTypes.isEmpty(), "osee types cannot be empty");

      OseeTypesSetup oseeTypesSetup = new OseeTypesSetup();
      oseeTypesSetup.execute(oseeTypes);

      service.clearAll();
      service.reloadTypes();
   }

   private void createOseeDatastore() {
      //    OseeClientProperties.isOseeImportAllowed();
      String tableDataSpace = OseeClientProperties.getOseeTableDataSpaceForDbInit();
      String indexDataSpace = OseeClientProperties.getOseeIndexDataSpaceForDbInit();
      boolean useSchemasSpecified = OseeClientProperties.useSchemasSpecifiedInDbConfigFiles();

      DatastoreInitializationOperation op =
         new DatastoreInitializationOperation(tableDataSpace, indexDataSpace, useSchemasSpecified);
      Operations.executeWorkAndCheckStatus(op);
   }
}
