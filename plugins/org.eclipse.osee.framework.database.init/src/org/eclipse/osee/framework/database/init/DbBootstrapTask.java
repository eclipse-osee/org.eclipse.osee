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

package org.eclipse.osee.framework.database.init;

import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.core.client.BaseCredentialProvider;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.message.DatastoreInitRequest;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.core.util.HttpMessage;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.init.internal.DatabaseInitActivator;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * @author Andrew M. Finkbeiner
 */
public class DbBootstrapTask implements IDbInitializationTask {
   private DbInitConfiguration configuration;

   public void setConfiguration(DbInitConfiguration configuration) {
      this.configuration = configuration;
   }

   @Override
   public void run() throws OseeCoreException {
      Conditions.checkNotNull(configuration, "DbInitConfiguration Info");

      DbUtil.setDbInit(true);

      createOseeDatastore();

      IOseeCachingService service = DatabaseInitActivator.getInstance().getCachingService();
      service.clearAll();

      IOseeDatabaseService databaseService = DatabaseInitActivator.getInstance().getDatabaseService();
      databaseService.getSequence().clear();

      Branch systemRoot = BranchManager.getSystemRootBranch();
      Conditions.checkNotNull(systemRoot, "System root was not created - ");

      ClientSessionManager.authenticate(new BaseCredentialProvider() {

         @Override
         public OseeCredential getCredential() {
            OseeCredential credential = new OseeCredential();
            credential.setUserName(SystemUser.BootStrap.getName());
            return credential;
         }
      });

      List<String> oseeTypes = configuration.getOseeTypeExtensionIds();
      Conditions.checkExpressionFailOnTrue(oseeTypes.isEmpty(), "osee types cannot be empty");

      OseeTypesSetup oseeTypesSetup = new OseeTypesSetup();
      oseeTypesSetup.execute(oseeTypes);
   }

   private void createOseeDatastore() throws OseeCoreException {
      //    OseeClientProperties.isOseeImportAllowed();

      String tableDataSpace = OseeClientProperties.getOseeTableDataSpaceForDbInit();
      String indexDataSpace = OseeClientProperties.getOseeIndexDataSpaceForDbInit();
      boolean useSchemasSpecified = OseeClientProperties.useSchemasSpecifiedInDbConfigFiles();

      DatastoreInitRequest requestData = new DatastoreInitRequest(tableDataSpace, indexDataSpace, useSchemasSpecified);

      String datastoreInitContext = OseeServerContext.OSEE_CONFIGURE_CONTEXT + "/datastore/initialize";
      String urlString =
         HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(datastoreInitContext,
            new HashMap<String, String>());

      IDataTranslationService service = DatabaseInitActivator.getInstance().getTranslationService();
      AcquireResult updateResponse =
         HttpMessage.send(urlString, service, CoreTranslatorId.OSEE_DATASTORE_INIT_REQUEST, requestData, null);
      if (!updateResponse.wasSuccessful()) {
         throw new OseeStateException("Error during datastore init");
      }
   }
}
