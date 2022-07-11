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

package org.eclipse.osee.ats.ide.config;

import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.UserManager;

/**
 * @author Donald G. Dunne
 */
public class AtsDbConfigBaseIde implements IDbInitializationTask {

   @Override
   public void run() {

      OseeProperties.setInDbInit(false);
      OseeProperties.setIsInTest(true);
      ClientSessionManager.releaseSession();
      // Re-authenticate so we can continue
      ClientSessionManager.getSession();
      UserManager.releaseUser();
      AtsUser currentUser = AtsApiService.get().getUserService().getCurrentUser();
      Conditions.assertNotEquals(currentUser.getIdIntValue(), SystemUser.OseeSystem.getIdIntValue(),
         "Should not be OSEE System user");
      AtsApiService.get().reloadServerAndClientCaches();
      AtsApiService.get().clearCaches();

      XResultData results = AtsApiService.get().getServerEndpoints().getConfigEndpoint().atsDbInit();
      if (results.isErrors()) {
         throw new OseeStateException(results.toString());
      }

   }

}