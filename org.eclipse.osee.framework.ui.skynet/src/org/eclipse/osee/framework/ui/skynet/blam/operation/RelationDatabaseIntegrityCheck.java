/*
 * Created on Jun 24, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;

/**
 * @author Jeff C. Phillips
 */
public class RelationDatabaseIntegrityCheck extends AbstractBlam {

   private static final String NO_ADDRESSING_ARTIFACTS =
         "select * from osee_Define_rel_link t1 where (a_art_id not in (select art_id from osee_Define_artifact_version t2, osee_Define_txs t3 where t2.gamma_id = t3.gamma_id) OR b_art_id not in (select art_id from osee_Define_artifact_version t4, osee_Define_txs t5 where t4.gamma_id = t5.gamma_id))";

   private static final String DELETED_A_ARTIFACTS =
         "select rl1.* from osee_Define_txs tx1, osee_Define_txs tx2, osee_Define_tx_details td1, osee_Define_tx_details td2, osee_Define_rel_link rl1, osee_define_artifact_version av1 where tx1.transaction_id = td1.transaction_id and tx1.gamma_id = rl1.gamma_id and tx1.tx_current = 1 and td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = av1.gamma_id and tx2.tx_current = 2 and av1.art_id = rl1.a_art_id";

   private static final String DELETED_B_ARTIFACTS =
         "select rl1.* from osee_Define_txs tx1, osee_Define_txs tx2, osee_Define_tx_details td1, osee_Define_tx_details td2, osee_Define_rel_link rl1, osee_define_artifact_version av1 where tx1.transaction_id = td1.transaction_id and tx1.gamma_id = rl1.gamma_id and tx1.tx_current = 1 and td1.branch_id = td2.branch_id and td2.transaction_id = tx2.transaction_id and tx2.gamma_id = av1.gamma_id and tx2.tx_current = 2 and av1.art_id = rl1.b_art_id";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      try {
         final XResultData rd = new XResultData(SkynetActivator.getLogger());
         runIt(monitor, rd);
      } catch (Exception ex) {

         OSEELog.logException(SkynetActivator.class, ex, false);
      }
      monitor.done();
   }

   /**
    * @param monitor
    * @param rd
    * @throws SQLException
    */
   private void runIt(IProgressMonitor monitor, XResultData rd) throws SQLException {
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      String[] columnHeaders = new String[] {"Rel id", "Rel gamma id", "A art id", "B art id"};
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
      ConnectionHandlerStatement chStmt = null;

      try {
         sbFull.append(AHTML.addRowSpanMultiColumnTable("Relation links with artifacts that have no addressing",
               columnHeaders.length));
         chStmt = ConnectionHandler.runPreparedQuery(NO_ADDRESSING_ARTIFACTS);
         ResultSet rSet = chStmt.getRset();

         while (rSet.next()) {
            String str =
                  AHTML.addRowMultiColumnTable(new String[] {rSet.getString("rel_link_id"), rSet.getString("gamma_id"),
                        rSet.getString("a_art_id"), rSet.getString("b_art_id")});
            sbFull.append(str);
         }

         sbFull.append(AHTML.addRowSpanMultiColumnTable("Relation links that have deleted artifacts",
               columnHeaders.length));
         chStmt = ConnectionHandler.runPreparedQuery(DELETED_A_ARTIFACTS);
         rSet = chStmt.getRset();

         while (rSet.next()) {
            String str =
                  AHTML.addRowMultiColumnTable(new String[] {rSet.getString("rel_link_id"), rSet.getString("gamma_id"),
                        rSet.getString("a_art_id"), rSet.getString("b_art_id")});
            sbFull.append(str);
         }

         chStmt = ConnectionHandler.runPreparedQuery(DELETED_B_ARTIFACTS);
         rSet = chStmt.getRset();

         while (rSet.next()) {
            String str =
                  AHTML.addRowMultiColumnTable(new String[] {rSet.getString("rel_link_id"), rSet.getString("gamma_id"),
                        rSet.getString("a_art_id"), rSet.getString("b_art_id")});
            sbFull.append(str);
         }
      } finally {
         DbUtil.close(chStmt);
         sbFull.append(AHTML.endMultiColumnTable());
         rd.addRaw(sbFull.toString());
         rd.report("Relation Database Integrity Check", Manipulations.RAW_HTML);
      }
   }

   public String getXWidgetsXml() {
      return "<xWidgets></xWidgets>";
   }
}
