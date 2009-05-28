/*
 * Created on May 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

/**
 * @author Donald G. Dunne
 */
public class DbUtil {

   public static void getTableRowCounts(Map<String, Integer> tableCount, Collection<String> tableNames) throws OseeDataStoreException {
      for (String tableName : tableNames) {
         tableCount.put(tableName, getTableRowCount(tableName));
      }
   }

   public static int getTableRowCount(String tableName) throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchInt(0, "SELECT count(1) FROM " + tableName);
   }

}
