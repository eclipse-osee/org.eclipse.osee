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
package org.eclipse.osee.framework.database;

import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.info.DbDetailData.ConfigField;

/**
 * @author Roberto E. Escobar
 */
public class NotOnProductionDbInitializationRule implements IDbInitializationRule {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.IDbInitializationRule#isAllowed()
    */
   @Override
   public boolean isAllowed() throws OseeCoreException {
      return !isProductionDb();
   }

   public static boolean isProductionDb() throws OseeCoreException {
      boolean isProductionDb = false;

      DbInformation dbInfo = OseeDbConnection.getDefaultDatabaseService();
      String dbServiceId = dbInfo.getDatabaseDetails().getFieldValue(ConfigField.DatabaseName);
      for (String productionDb : DatabaseActivator.getInstance().getProductionDbs()) {
         if (dbServiceId.equals(productionDb)) {
            isProductionDb = true;
            break;
         }
      }
      return isProductionDb;
   }

}
