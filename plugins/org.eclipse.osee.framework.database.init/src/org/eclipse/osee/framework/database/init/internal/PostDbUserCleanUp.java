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

import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;

/**
 * @author Roberto E. Escobar
 */
public class PostDbUserCleanUp implements IDbInitializationTask {

   @Override
   public void run() {

      // Release bootstrap session
      ClientSessionManager.releaseSession();

      DatastoreEndpoint datastoreEndpoint = OsgiUtil.getService(getClass(), OseeClient.class).getDatastoreEndpoint();
      datastoreEndpoint.updateBootstrapUser();

      IOseeCachingService typeService = OsgiUtil.getService(getClass(), IOseeCachingService.class);
      typeService.clearAll();
      typeService.reloadTypes();
   }
}