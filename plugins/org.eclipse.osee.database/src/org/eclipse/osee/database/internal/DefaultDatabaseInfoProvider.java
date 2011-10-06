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
package org.eclipse.osee.database.internal;

import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.DatabaseInfoManager;
import org.eclipse.osee.framework.database.core.IDatabaseInfoProvider;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class DefaultDatabaseInfoProvider implements IDatabaseInfoProvider {

   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   private Log getLogger() {
      return logger;
   }

   @Override
   public IDatabaseInfo getDatabaseInfo() throws OseeDataStoreException {
      IDatabaseInfo databaseInfo = null;
      databaseInfo = DatabaseInfoManager.getDefault();
      getLogger().info("%s [%s as %s]", databaseInfo.getDriver(), databaseInfo.getDatabaseName(),
         databaseInfo.getDatabaseLoginName());
      return databaseInfo;
   }

   @Override
   public int getPriority() {
      return Integer.MIN_VALUE;
   }

   @Override
   public String toString() {
      return "Default DB Connection Info Provider";
   }

}
