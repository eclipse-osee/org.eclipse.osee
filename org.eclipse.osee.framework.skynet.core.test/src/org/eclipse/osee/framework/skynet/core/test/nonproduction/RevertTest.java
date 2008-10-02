/*
 * Created on Aug 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test.nonproduction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.test.nonproduction.components.ConflictTestManager;

/**
 * @author Theron Virgin
 */
public class RevertTest extends TestCase {
   private static final String GET_BASELINED_TRANSACTIONS =
         "SELECT txs.gamma_id, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_artifact_version art WHERE det.branch_id = ? AND det.tx_type = 1 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id and art.art_id = ? UNION SELECT txs.gamma_id, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_attribute attr WHERE det.branch_id = ? AND det.tx_type = 1 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = attr.gamma_id and attr.art_id = ? UNION SELECT txs.gamma_id, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_rel_link rel WHERE det.branch_id = ? AND det.tx_type = 1 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id and (rel.a_art_id = ? OR rel.b_art_id = ?)";
   private static final String GET_NON_BASELINED_TRANSACTIONS =
         "SELECT txs.gamma_id, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_artifact_version art WHERE det.branch_id = ? AND det.tx_type = 0 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id and art.art_id = ? UNION SELECT txs.gamma_id, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_attribute attr WHERE det.branch_id = ? AND det.tx_type = 0 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = attr.gamma_id and attr.art_id = ? UNION SELECT txs.gamma_id, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_rel_link rel WHERE det.branch_id = ? AND det.tx_type = 0 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id and (rel.a_art_id = ? OR rel.b_art_id = ?)";

   private static final String GAMMA_UNIQUE =
         "SELECT gamma_id FROM osee_define_txs txs WHERE txs.gamma_id = ? AND NOT EXISTS (SELECT 'x' FROM osee_define_txs txs1, osee_define_tx_details det1 WHERE det1.branch_id != ? AND det1.transaction_id = txs1.transaction_id and txs.gamma_id = txs1.gamma_id) AND NOT EXISTS (SELECT 'x' FROM osee_define_txs txs1, osee_define_tx_details det1 WHERE tx_type = 1 AND det1.transaction_id = txs1.transaction_id and txs.gamma_id = txs1.gamma_id)";
   private static final String GAMMAS_KEEP =
         "SELECT gamma_id FROM osee_define_txs txs WHERE txs.gamma_id = ? AND (EXISTS (SELECT 'x' FROM osee_define_txs txs1, osee_define_tx_details det1 WHERE det1.branch_id != ? AND det1.transaction_id = txs1.transaction_id and txs.gamma_id = txs1.gamma_id) OR EXISTS (SELECT 'x' FROM osee_define_txs txs1, osee_define_tx_details det1 WHERE tx_type = 1 AND det1.transaction_id = txs1.transaction_id and txs.gamma_id = txs1.gamma_id))";

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core.test/debug/Junit"));

   protected void tearDown() throws Exception {
      super.tearDown();
      ConflictTestManager.cleanUpConflictTest();
   }

   protected void setUp() throws Exception {
      super.setUp();
      ConflictTestManager.initializeConflictTest();
      assertFalse(DatabaseActivator.getInstance().isProductionDb());
   }

   public void testRevertArtifacts() throws SQLException {

      Set<Pair<Integer, Integer>> baselines = new HashSet<Pair<Integer, Integer>>();
      Set<Pair<Integer, Integer>> nonBaselines = new HashSet<Pair<Integer, Integer>>();
      Set<Integer> uniqueGammas = new HashSet<Integer>();
      Set<Integer> keepGammas = new HashSet<Integer>();
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      Collection<Artifact> artifacts = ConflictTestManager.getArtifacts(true, ConflictTestManager.REVERT_QUERY);
      ConnectionHandlerStatement chstmt = null;
      ResultSet rSet = null;
      //Let's check both through the API and the SQL to make sure the Artifact is internally deleted
      //and deleted in the Database, That we don't get some bad data case.

      for (Artifact artifact : artifacts) {
         try {
            if (DEBUG) {
               System.out.println("     Baselined Transactions");
            }
            chstmt =
                  ConnectionHandler.runPreparedQuery(GET_BASELINED_TRANSACTIONS, artifact.getBranch().getBranchId(),
                        artifact.getArtId(), artifact.getBranch().getBranchId(), artifact.getArtId(),
                        artifact.getBranch().getBranchId(), artifact.getArtId(), artifact.getArtId());
            rSet = chstmt.getRset();
            while (rSet.next()) {
               baselines.add(new Pair<Integer, Integer>(rSet.getInt("gamma_id"), rSet.getInt("transaction_id")));
               if (DEBUG) {
                  System.out.println(String.format("          Gamma ID = %d Transaction Id = %d",
                        rSet.getInt("gamma_id"), rSet.getInt("transaction_id")));
               }
            }

         } finally {
            DbUtil.close(chstmt);
         }
         if (DEBUG) {
            System.out.println("     Nonbaselined Transactions");
         }
         try {
            chstmt =
                  ConnectionHandler.runPreparedQuery(GET_NON_BASELINED_TRANSACTIONS,
                        artifact.getBranch().getBranchId(), artifact.getArtId(), artifact.getBranch().getBranchId(),
                        artifact.getArtId(), artifact.getBranch().getBranchId(), artifact.getArtId(),
                        artifact.getArtId());
            rSet = chstmt.getRset();
            while (rSet.next()) {
               nonBaselines.add(new Pair<Integer, Integer>(rSet.getInt("gamma_id"), rSet.getInt("transaction_id")));
               if (DEBUG) {
                  System.out.println(String.format("          Gamma ID = %d Transaction Id = %d",
                        rSet.getInt("gamma_id"), rSet.getInt("transaction_id")));
               }
            }

         } finally {
            DbUtil.close(chstmt);
         }
         for (Pair<Integer, Integer> pairs : nonBaselines) {
            try {
               chstmt =
                     ConnectionHandler.runPreparedQuery(GAMMA_UNIQUE, pairs.getKey(),
                           artifact.getBranch().getBranchId());
               rSet = chstmt.getRset();
               while (rSet.next()) {
                  uniqueGammas.add(rSet.getInt("gamma_id"));
               }

            } finally {
               DbUtil.close(chstmt);
            }
         }
         for (Pair<Integer, Integer> pairs : nonBaselines) {
            try {
               chstmt =
                     ConnectionHandler.runPreparedQuery(GAMMAS_KEEP, pairs.getKey(), artifact.getBranch().getBranchId());
               rSet = chstmt.getRset();
               while (rSet.next()) {
                  keepGammas.add(rSet.getInt("gamma_id"));
               }

            } finally {
               DbUtil.close(chstmt);
            }
         }
         if (DEBUG) {
            System.out.println("     Gammas to Remove");
            for (Integer integer : uniqueGammas) {
               System.out.println(String.format("          Gamma ID = %d ", integer.intValue()));
            }
            System.out.println("     Gammas to Keep");
            for (Integer integer : keepGammas) {
               System.out.println(String.format("          Gamma ID = %d ", integer.intValue()));
            }
         }
      }

   }
}
