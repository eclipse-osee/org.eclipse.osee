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
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;

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
         StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
         try {
            String[] columnHeaders =
                  new String[] {"Art Id", "Attr id 1", "Attr id 2", "Name", "Value 1", "Value 2", "URI 1", "URI 2",
                        "Gamma ID 1", "Gamma Id 2", "ID to Delete"};
            sbFull.append(AHTML.beginMultiColumnTable(100, 1));
            sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
            sbFull.append(AHTML.addRowSpanMultiColumnTable("Attributes with the same values", columnHeaders.length));
            int count = showAttributeCleanUpDecisions(sameValues, fixErrors, sbFull, showDetails);
            sbFull.append(AHTML.addRowSpanMultiColumnTable("Attributes with different values", columnHeaders.length));
            count += showAttributeCleanUpDecisions(diffValues, false, sbFull, showDetails);
            builder.append(String.format("Failed: Found %d duplicate attributes", count));
         } finally {
            if (showDetails) {
               sbFull.append(AHTML.endMultiColumnTable());
               XResultData rd = new XResultData(SkynetActivator.getLogger());
               rd.addRaw(sbFull.toString());
               rd.report("Artifact TX_Current Fix", Manipulations.RAW_HTML);
            }

         }
      }

   }

   protected void showText(DuplicateAttribute duplicate, int x, boolean removeAttribute, StringBuffer builder) {
      String str =
            AHTML.addRowMultiColumnTable(new String[] {String.valueOf(duplicate.artId),
                  String.valueOf(duplicate.attrId1), String.valueOf(duplicate.attrId2), duplicate.name,
                  duplicate.value1, duplicate.value2, duplicate.uri1, duplicate.uri2, String.valueOf(duplicate.gamma1),
                  String.valueOf(duplicate.gamma2),
                  removeAttribute ? String.valueOf(duplicate.attrIDToDelete) : "Requires Hand Analysis"});
      builder.append(str);
   }

   private int showAttributeCleanUpDecisions(LinkedList<DuplicateAttribute> values, boolean removeAttribute, StringBuffer builder, boolean showDetails) throws SQLException {
      int x = 0;
      if (showDetails) {

      }

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
      return x;
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
