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
package org.eclipse.osee.framework.core.client.internal;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.database.core.IDatabaseInfoProvider;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class ClientDatabaseProvider implements IDatabaseInfoProvider {

   private static final String NAME = "Client Data Source";

   @Override
   public IDatabaseInfo getDatabaseInfo() throws OseeAuthenticationRequiredException {
      IDatabaseInfo databaseInfo = null;
      try {
         databaseInfo = InternalClientSessionManager.getInstance().getDatabaseInfo();
         OseeLog.reportStatus(new BaseStatus(NAME, Level.INFO, "%s [%s as %s]", databaseInfo.getDriver(),
               databaseInfo.getDatabaseName(), databaseInfo.getDatabaseLoginName()));
      } catch (OseeAuthenticationRequiredException ex) {
         OseeLog.reportStatus(new BaseStatus(NAME, Level.SEVERE, ex, "Error obtaining database connection."));
         throw ex;
      }
      return databaseInfo;
   }

   @Override
   public int getPriority() {
      return -1;
   }

}
