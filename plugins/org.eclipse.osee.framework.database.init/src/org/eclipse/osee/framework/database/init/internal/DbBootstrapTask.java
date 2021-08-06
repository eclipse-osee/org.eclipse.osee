/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.database.init.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.database.init.IDatabaseInitConfiguration;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;

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
      OseeProperties.setInDbInit(true);

      DatastoreEndpoint datastoreEndpoint = OsgiUtil.getService(getClass(), OseeClient.class).getDatastoreEndpoint();
      datastoreEndpoint.initialize();

      Conditions.checkNotNull(BranchManager.getBranchToken(SYSTEM_ROOT), "System root was not created - ");

      IOseeCachingService typeService = OsgiUtil.getService(getClass(), IOseeCachingService.class);
      typeService.clearAll();
   }
}