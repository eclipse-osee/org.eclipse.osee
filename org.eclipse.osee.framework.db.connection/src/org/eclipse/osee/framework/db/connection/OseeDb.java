/*
 * Created on Apr 24, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection;

import org.eclipse.osee.framework.db.connection.info.DbInformation;

/**
 * @author b1528444
 */
public class OseeDb {

   public static DbInformation getDefaultDatabaseService() {
      return Activator.getInstance().getDbConnectionInformation().getDefaultDatabaseInfo();
   }

   public static DbInformation getDatabaseService(String id) {
      return Activator.getInstance().getDbConnectionInformation().getDatabaseInfo(id);
   }

}
