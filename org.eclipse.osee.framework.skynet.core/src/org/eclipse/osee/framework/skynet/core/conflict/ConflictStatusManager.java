/*
 * Created on Mar 24, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.conflict;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict.Status;

/**
 * @author Theron Virgin
 */
public class ConflictStatusManager {

   private static final String MERGE_UPDATE_STATUS =
         "UPDATE osee_define_conflict SET status = ? WHERE source_gamma_id = ? AND dest_gamma_id = ?";
   private static final String MERGE_ATTRIBUTE_STATUS =
         "SELECT source_gamma_id, dest_gamma_id, status  From " + "osee_define_conflict WHERE branch_id = ? AND conflict_id = ? AND " + "conflict_type = ?";
   private static final String MERGE_UPDATE_GAMMAS =
         "UPDATE osee_define_conflict SET source_gamma_id = ?, " + "dest_gamma_id = ?, status = ? WHERE branch_id = ? AND conflict_id = ? AND conflict_type = ?";
   private static final String MERGE_INSERT_STATUS =
         "INSERT INTO osee_define_conflict ( conflict_id, branch_id, source_gamma_id, " + "dest_gamma_id, status, conflict_type) VALUES ( ?, ?, ?, ?, ?, ?)";

   public static void setStatus(Status status, int sourceGamma, int destGamma) throws SQLException {
      ConnectionHandlerStatement HandlerStatement = null;
      //Gammas should be up to date so you can use them to get entry
      //just update the status field.
      try {
         HandlerStatement =
               ConnectionHandler.runPreparedQuery(MERGE_UPDATE_STATUS, SQL3DataType.INTEGER, status.Value(),
                     SQL3DataType.INTEGER, sourceGamma, SQL3DataType.INTEGER, destGamma);
         ResultSet updateSet = HandlerStatement.getRset();
         updateSet.next();
      } finally {
         DbUtil.close(HandlerStatement);
      }
   }

   public static Status computeStatus(int sourceGamma, int destGamma, int branchID, int objectID, int conflictType, Conflict.Status passedStatus) throws SQLException {
      ConnectionHandlerStatement checkHandlerStatement = null;
      ConnectionHandlerStatement updateHandlerStatement = null;
      ConnectionHandlerStatement insertHandlerStatement = null;
      Conflict.Status status;
      //Check for a value in the table, if there is not one in there then
      //add it with an unedited setting and return unedited
      //If gammas are out of date, update the gammas and down grade markedMerged to Edited

      try {
         checkHandlerStatement =
               ConnectionHandler.runPreparedQuery(MERGE_ATTRIBUTE_STATUS, SQL3DataType.INTEGER, branchID,
                     SQL3DataType.INTEGER, objectID, SQL3DataType.INTEGER, conflictType);

         ResultSet statusSet = checkHandlerStatement.getRset();
         if (statusSet.next()) {
            //There was an entry so lets check it and update it.
            int intStatus = statusSet.getInt("status");
            if ((statusSet.getInt("source_gamma_id") != sourceGamma) || (statusSet.getInt("dest_gamma_id") != destGamma)) {
               if (intStatus == Status.RESOLVED.Value()) intStatus = Status.OUT_OF_DATE.Value();
               try {
                  updateHandlerStatement =
                        ConnectionHandler.runPreparedQuery(MERGE_UPDATE_GAMMAS, SQL3DataType.INTEGER, sourceGamma,
                              SQL3DataType.INTEGER, destGamma, SQL3DataType.INTEGER, intStatus, SQL3DataType.INTEGER,
                              branchID, SQL3DataType.INTEGER, objectID, SQL3DataType.INTEGER, conflictType);
                  ResultSet updateSet = updateHandlerStatement.getRset();
                  updateSet.next();
               } finally {
                  DbUtil.close(updateHandlerStatement);
               }
            }
            return Status.getStatus(intStatus);
         }
         // add the entry to the table and set as UNTOUCHED

      } finally {
         DbUtil.close(checkHandlerStatement);
      }
      status = passedStatus;
      try {

         insertHandlerStatement =
               ConnectionHandler.runPreparedQuery(MERGE_INSERT_STATUS, SQL3DataType.INTEGER, objectID,
                     SQL3DataType.INTEGER, branchID, SQL3DataType.INTEGER, sourceGamma, SQL3DataType.INTEGER,
                     destGamma, SQL3DataType.INTEGER, status.Value(), SQL3DataType.INTEGER, conflictType);

         ResultSet insertSet = insertHandlerStatement.getRset();
         insertSet.next();
      } finally {
         DbUtil.close(insertHandlerStatement);
      }

      return status;
   }

}
