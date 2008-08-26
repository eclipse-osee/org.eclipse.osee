/*
 * Created on Aug 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Theron Virgin
 */
public class DuplicateAttributes extends DatabaseHealthTask {
   private class DuplicateAttribute {
      protected int artId;
      protected int attrId1;
      protected int attrId2;
      protected String name;
      protected String value1;
      protected String value2;
      protected String uri1;
      protected String uri2;
      protected int gamma1;
      protected int gamma2;
      protected int attrIDToDelete = 0;
      protected LinkedList<Integer> branches1 = new LinkedList<Integer>();
      protected LinkedList<Integer> branches2 = new LinkedList<Integer>();
   }

   private static final String GET_DUPLICATE_ATTRIBUTES =
         "SELECT attr1.art_id, aty1.NAME, attr1.attr_id as attr_id_1, attr2.attr_id as attr_id_2, attr1.value as value_1, attr2.value as value_2, attr1.uri as uri_1, attr2.uri as uri_2, attr1.gamma_id as gamma_id_1, attr2.gamma_id as gamma_id_2 FROM osee_define_attribute attr1, osee_define_attribute attr2, osee_define_attribute_type  aty1 WHERE attr1.art_id = attr2.art_id AND attr1.attr_id < attr2.attr_id AND attr1.attr_type_id = attr2.attr_type_id AND attr1.attr_type_id = aty1.attr_type_id AND aty1.max_occurence = 1  AND EXISTS (SELECT 'x' FROM osee_define_txs txs1 WHERE txs1.gamma_id = attr1.gamma_id) AND EXISTS (SELECT 'x' FROM osee_define_txs txs2 WHERE txs2.gamma_id = attr2.gamma_id) order by aty1.NAME, attr1.art_id";

   private static final String BRANCHES_WITH_ONLY_ATTR =
         "SELECT DISTINCT branch_id FROM osee_define_tx_details det WHERE EXISTS (SELECT 'x' FROM osee_define_txs txs, osee_define_attribute att WHERE det.transaction_id = txs.transaction_id AND txs.gamma_id = att.gamma_id AND att.attr_id = ?) MINUS (SELECT DISTINCT branch_id FROM osee_define_tx_details det WHERE EXISTS (SELECT 'x' FROM osee_define_txs txs, osee_define_attribute att WHERE det.transaction_id = txs.transaction_id AND txs.gamma_id = att.gamma_id AND att.attr_id = ?))";

   private static final String DELETE_ATTR = "DELETE FROM osee_define_attribute WHERE attr_id = ?";

   private static final String FILTER_DELTED =
         "SELECT * FROM osee_define_txs txs, osee_define_attribute atr WHERE txs.tx_current = 1 AND txs.gamma_id = atr.gamma_id AND atr.attr_id = ?";

   boolean fixErrors = false;
   boolean processTxCurrent = true;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask#getFixTaskName()
    */
   @Override
   public String getFixTaskName() {
      return "Fix Duplicate Attribute Errors";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask#getVerifyTaskName()
    */
   @Override
   public String getVerifyTaskName() {
      return "Check for Duplicate Attribute Errors";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask#run(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask.Operation, java.lang.StringBuilder)
    */
   @Override
   public void run(BlamVariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception {
      LinkedList<DuplicateAttribute> sameValues = new LinkedList<DuplicateAttribute>();
      LinkedList<DuplicateAttribute> diffValues = new LinkedList<DuplicateAttribute>();
      ConnectionHandlerStatement connectionHandlerStatement = null;
      ConnectionHandlerStatement connectionHandlerStatement2 = null;
      ResultSet resultSet = null;
      ResultSet resultSet2 = null;
      fixErrors = operation.equals(Operation.Fix);
      //--- Test's for two attributes that are on the same artifact but have different attr_ids, when ---//
      //--- the attribute type has a maximum of 1 allowable attributes. ---------------------------------//

      monitor.beginTask("Clean Up Duplicate Attributes", processTxCurrent ? 20 : 8);
      monitor.subTask("Querying for Duplicate Attributes");
      if (monitor.isCanceled()) return;
      try {
         connectionHandlerStatement = ConnectionHandler.runPreparedQuery(GET_DUPLICATE_ATTRIBUTES);
         resultSet = connectionHandlerStatement.getRset();
         monitor.worked(6);
         monitor.subTask("Processing Results");
         if (monitor.isCanceled()) return;
         while (resultSet.next()) {
            try {
               connectionHandlerStatement2 =
                     ConnectionHandler.runPreparedQuery(FILTER_DELTED, resultSet.getInt("attr_id_1"));
               resultSet2 = connectionHandlerStatement2.getRset();
               if (resultSet2.next()) {
                  DbUtil.close(connectionHandlerStatement2);
                  connectionHandlerStatement2 =
                        ConnectionHandler.runPreparedQuery(FILTER_DELTED, resultSet.getInt("attr_id_2"));
                  resultSet2 = connectionHandlerStatement2.getRset();
                  if (resultSet2.next()) {
                     DuplicateAttribute duplicateAttribute;
                     duplicateAttribute = new DuplicateAttribute();
                     duplicateAttribute.artId = resultSet.getInt("art_id");
                     duplicateAttribute.attrId1 = resultSet.getInt("attr_id_1");
                     duplicateAttribute.attrId2 = resultSet.getInt("attr_id_2");
                     duplicateAttribute.name = resultSet.getString("name");
                     duplicateAttribute.value1 = resultSet.getString("value_1");
                     duplicateAttribute.value2 = resultSet.getString("value_2");
                     duplicateAttribute.uri1 = resultSet.getString("uri_1");
                     duplicateAttribute.uri2 = resultSet.getString("uri_2");
                     duplicateAttribute.gamma1 = resultSet.getInt("gamma_id_1");
                     duplicateAttribute.gamma2 = resultSet.getInt("gamma_id_2");

                     if ((duplicateAttribute.value1 != null && duplicateAttribute.value2 != null && duplicateAttribute.value1.equals(duplicateAttribute.value2)) || (duplicateAttribute.uri1 != null && duplicateAttribute.uri2 != null && duplicateAttribute.uri1.equals(duplicateAttribute.uri2)) || (duplicateAttribute.value1 == null && duplicateAttribute.value2 == null && duplicateAttribute.uri1 == null && duplicateAttribute.uri2 == null)) {
                        sameValues.add(duplicateAttribute);
                     } else {
                        diffValues.add(duplicateAttribute);
                     }
                  }

               }
            } finally {
               DbUtil.close(connectionHandlerStatement2);
            }

         }

      } finally {
         DbUtil.close(connectionHandlerStatement);
         DbUtil.close(connectionHandlerStatement2);
      }
      monitor.worked(2);
      monitor.subTask("Cleaning Up Attrinbutes");
      if (monitor.isCanceled()) return;
      if (sameValues.isEmpty() && diffValues.isEmpty()) {
         builder.append("PASSED: No Duplicate Attributes Found\n");
      } else {
         if (showDetails) builder.append("FAILED: Found the following Duplicate Attributes\n");
         builder.append("ATTRIBUTES WITH THE SAME VALUES\n");
         if (sameValues.isEmpty()) {
            builder.append("  None Found\n");
         }
         showAttributeCleanUpDecisions(sameValues, true && fixErrors, builder, showDetails);
         builder.append("ATTRIBUTES WITH DIFFERENT VALUES\n");
         if (diffValues.isEmpty()) {
            builder.append("  None Found\n");
         }
         showAttributeCleanUpDecisions(diffValues, false, builder, showDetails);
      }

   }

   protected void showText(DuplicateAttribute duplicate, int x, boolean removeAttribute, StringBuilder builder) {
      if (!removeAttribute) {
         if (fixErrors) {
            builder.append("ATTRIBUTE REQUIRES HAND ANALYSIS TO DETERMINE HOW TO REMOVE <->");
         }
      }
      builder.append(String.format(
            "%-4d Art ID = %-8d Attr_id_1 = %-8d Attr_id_2 = %-8d Name = %s   Value_1 = %s   Value_2 = %s    URI_1 = %s   URI_2 = %s  GAMMA_1 = %-8d  GAMMA_2 = %-8d  Delete = %-8d\n",
            x, duplicate.artId, duplicate.attrId1, duplicate.attrId2, duplicate.name, duplicate.value1,
            duplicate.value2, duplicate.uri1, duplicate.uri2, duplicate.gamma1, duplicate.gamma2,
            duplicate.attrIDToDelete));
   }

   private void showAttributeCleanUpDecisions(LinkedList<DuplicateAttribute> values, boolean removeAttribute, StringBuilder builder, boolean showDetails) throws SQLException {
      int x = 0;
      for (DuplicateAttribute loopDuplicate : values) {
         findProminentAttribute(loopDuplicate.attrId1, loopDuplicate.attrId2, loopDuplicate.branches1);
         findProminentAttribute(loopDuplicate.attrId2, loopDuplicate.attrId1, loopDuplicate.branches2);

         if (loopDuplicate.branches1.size() == 0) {
            loopDuplicate.attrIDToDelete = loopDuplicate.attrId1;
         } else if (loopDuplicate.branches2.size() == 0) {
            loopDuplicate.attrIDToDelete = loopDuplicate.attrId2;
         }

         if (loopDuplicate.attrIDToDelete != 0 && removeAttribute) {
            ConnectionHandler.runPreparedUpdate(DELETE_ATTR, loopDuplicate.attrIDToDelete);
         }
         if (showDetails) {
            showText(loopDuplicate, x++, removeAttribute, builder);
         }
      }
      if (!showDetails) {
         builder.append("     ");
         builder.append(x);
         builder.append(" instances found");
      }
   }

   //--- Find out if there is an attribute that is on every branch that has either one of the attributes ---//
   private void findProminentAttribute(int attrId1, int attrId2, LinkedList<Integer> branches) throws SQLException {
      ConnectionHandlerStatement connectionHandlerStatement = null;
      try {
         connectionHandlerStatement = ConnectionHandler.runPreparedQuery(BRANCHES_WITH_ONLY_ATTR, attrId1, attrId2);
         ResultSet resultSet = connectionHandlerStatement.getRset();
         while (resultSet.next()) {
            branches.add(new Integer(resultSet.getInt("branch_id")));
         }

      } finally {
         DbUtil.close(connectionHandlerStatement);
      }
   }
}
