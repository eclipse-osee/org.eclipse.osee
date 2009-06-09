/*
 * Created on Jun 8, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.conflict.RelationConflict;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.skynet.core.status.EmptyMonitor;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class ConflictTest {
   private static final boolean DEBUG =
      "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core.test/debug/Junit"));
   private static final String COMMITTED_NEW_AND_DELETED_ARTIFACTS =
      "SELECT txs1.gamma_id, txs1.transaction_id, det1.branch_id, art1.art_id, 0 as attr_id, 0 as rel_link_id FROM osee_tx_details det1, osee_txs txs1, osee_artifact_version art1 WHERE txs1.tx_current = " + TxChange.DELETED.getValue() + " AND det1.transaction_id = txs1.transaction_id AND txs1.gamma_id = art1.gamma_id  AND  NOT EXISTS (SELECT ('x') FROM osee_tx_details det2, osee_txs txs2, osee_artifact_version art2 WHERE txs2.mod_type != " + ModificationType.DELETED.getValue() + " AND det1.branch_id = det2.branch_id AND det2.transaction_id = txs2.transaction_id AND txs2.gamma_id = art2.gamma_id AND art2.art_id = art1.art_id)";
   private static final String COMMITTED_NEW_AND_DELETED_ATTRIBUTES =
      "SELECT txs1.gamma_id, txs1.transaction_id, det1.branch_id, 0 as art_id, att1.attr_id, 0 as rel_link_id FROM osee_tx_details det1, osee_txs txs1, osee_attribute att1 WHERE txs1.tx_current = " + TxChange.DELETED.getValue() + " AND det1.transaction_id = txs1.transaction_id AND txs1.gamma_id = att1.gamma_id  AND  NOT EXISTS (SELECT ('x') FROM osee_tx_details det2, osee_txs txs2, osee_attribute att2 WHERE txs2.mod_type != " + ModificationType.DELETED.getValue() + " AND det1.branch_id = det2.branch_id AND det2.transaction_id = txs2.transaction_id AND txs2.gamma_id = att2.gamma_id AND att2.attr_id = att1.attr_id)";
   private static final String COMMITTED_NEW_AND_DELETED_RELATIONS =
      "SELECT txs1.gamma_id, txs1.transaction_id, det1.branch_id, 0 as art_id, 0 as attr_id, rel1.rel_link_id FROM osee_tx_details det1, osee_txs txs1, osee_relation_link rel1 WHERE txs1.tx_current = " + TxChange.DELETED.getValue() + " AND det1.transaction_id = txs1.transaction_id AND txs1.gamma_id = rel1.gamma_id  AND  NOT EXISTS (SELECT ('x') FROM osee_tx_details det2, osee_txs txs2, osee_relation_link rel2 WHERE txs2.mod_type != " + ModificationType.DELETED.getValue() + " AND det1.branch_id = det2.branch_id AND det2.transaction_id = txs2.transaction_id AND txs2.gamma_id = rel2.gamma_id AND rel2.rel_link_id = rel1.rel_link_id)";
   private static final String[] NO_TX_CURRENT_SET =
   {
      "SELECT distinct t1.",
      ", det.branch_id FROM osee_tx_details det, osee_txs txs, ",
      " t1 WHERE det.transaction_id = txs.transaction_id AND txs.gamma_id = t1.gamma_id AND txs.tx_current = 0 %s SELECT distinct t2.",
      ", det.branch_id FROM osee_tx_details det, osee_txs txs, ",
   " t2 WHERE det.transaction_id = txs.transaction_id AND txs.gamma_id = t2.gamma_id AND txs.tx_current != 0"};

   private static final String[] MULTIPLE_TX_CURRENT_SET =
   {
      "SELECT resulttable.branch_id, resulttable.",
      ", COUNT(resulttable.branch_id) AS numoccurrences FROM (SELECT txd1.branch_id, t1.",
      " FROM osee_tx_details txd1, osee_txs txs1, ",
      " t1 WHERE txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = t1.gamma_id AND txs1.tx_current != 0) resulttable GROUP BY resulttable.branch_id, resulttable.",
   " HAVING(COUNT(resulttable.branch_id) > 1) order by branch_id"};

   @BeforeClass
   public static void setUp() throws Exception {
      ConflictTestManager.initializeConflictTest();
   }

   @AfterClass
   public static void tearDown() throws Exception {
      ConflictTestManager.cleanUpConflictTest();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchManager#getMergeBranch(Branch, Branch)} .
    * @throws Exception 
    */
   @org.junit.Test
   public void testGetMergeBranchNotCreated() throws Exception {
      runMergeBranchNotCreated();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal#getConflictsPerBranch(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.transaction.TransactionId)}
    * .
    */
   @org.junit.Test
   public void testGetConflictsPerBranch() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      Collection<Conflict> conflicts = new HashSet<Conflict>();
      try {
         conflicts =
               ConflictManagerInternal.getConflictsPerBranch(ConflictTestManager.getSourceBranch(),
                     ConflictTestManager.getDestBranch(), TransactionIdManager.getStartEndPoint(
                           ConflictTestManager.getSourceBranch()).getKey(), new EmptyMonitor());
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
      assertEquals("Number of conflicts found is not equal to the number of conflicts expected",
            ConflictTestManager.numberOfConflicts(), conflicts.toArray().length);
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getAllLogs().size()),
            monitorLog.getAllLogs().size() == 0);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchManager#getMergeBranch(Branch, Branch)} .
    * @throws Exception 
    */
   @org.junit.Test
   public void testGetMergeBranchCreated() throws Exception {
      runMergeBranchCreated();
   }

   @org.junit.Test
   public void testResolveConflicts() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      try {
         Collection<Conflict> conflicts =
               ConflictManagerInternal.getConflictsPerBranch(ConflictTestManager.getSourceBranch(),
                     ConflictTestManager.getDestBranch(), TransactionIdManager.getStartEndPoint(
                           ConflictTestManager.getSourceBranch()).getKey(), new EmptyMonitor());
         int whichChange = 1;

         for (Conflict conflict : conflicts) {
            if (conflict instanceof ArtifactConflict && ((ArtifactConflict) conflict).statusNotResolvable()) {
               ((ArtifactConflict) conflict).revertSourceArtifact();
            } else if (conflict instanceof AttributeConflict) {
               ConflictTestManager.resolveAttributeConflict((AttributeConflict) conflict);
               conflict.setStatus(ConflictStatus.RESOLVED);
            } else if (conflict instanceof RelationConflict) {
               fail("Relation Conflicts are not supported yet");
            }
            whichChange++;
         }

         conflicts =
               ConflictManagerInternal.getConflictsPerBranch(ConflictTestManager.getSourceBranch(),
                     ConflictTestManager.getDestBranch(), TransactionIdManager.getStartEndPoint(
                           ConflictTestManager.getSourceBranch()).getKey(), new EmptyMonitor());

         for (Conflict conflict : conflicts) {
            assertTrue(
                  "This conflict was not found to be resolved ArtId = " + conflict.getArtId() + " " + conflict.getSourceDisplayData(),
                  conflict.statusResolved() || conflict.statusInformational());

         }
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getAllLogs().size()),
            monitorLog.getAllLogs().size() == 0);
   }

   public void CheckCommitWithoutResolutionErrors() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      try {
         ConflictManagerExternal conflictManager =
               new ConflictManagerExternal(ConflictTestManager.getDestBranch(), ConflictTestManager.getSourceBranch());
         BranchManager.commitBranch(conflictManager, false, false);
         assertTrue("Commit did not complete as expected", ConflictTestManager.validateCommit());

         assertEquals("Source Branch state incorrect", BranchState.COMMITTED,
               ConflictTestManager.getSourceBranch().getBranchState());

      } catch (Exception ex) {
         fail("No Exceptions should have been thrown. Not even the " + ex.getLocalizedMessage() + "Exception");
      }

      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getSevereLogs().size()),
            monitorLog.getSevereLogs().size() == 0);
   }
   
   @org.junit.Test
   public void testCommitFiltering() throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(COMMITTED_NEW_AND_DELETED_ARTIFACTS);
         if (chStmt.next()) {
            fail(String.format(
                  "Committed New and Deleted Artifact snuck through gamma_id = %d and transaction_id = %d",
                  chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
         }
      } finally {
         chStmt.close();
      }
      try {
         chStmt.runPreparedQuery(COMMITTED_NEW_AND_DELETED_ATTRIBUTES);
         if (chStmt.next()) {
            fail(String.format(
                  "Committed New and Deleted Attribute snuck through gamma_id = %d and transaction_id = %d",
                  chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
         }
      } finally {
         chStmt.close();
      }
      try {
         chStmt.runPreparedQuery(COMMITTED_NEW_AND_DELETED_RELATIONS);
         if (chStmt.next()) {
            fail(String.format(
                  "Committed New and Deleted Relation Links snuck through gamma_id = %d and transaction_id = %d",
                  chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
         }
      } finally {
         chStmt.close();
      }
      checkNoTxCurrent("art_id", "osee_artifact_version");
      checkNoTxCurrent("attr_id", "osee_attribute");
      checkNoTxCurrent("rel_link_id", "osee_relation_link");
      checkMultipleTxCurrent("art_id", "osee_artifact_version");
      checkMultipleTxCurrent("attr_id", "osee_attribute");
      checkMultipleTxCurrent("rel_link_id", "osee_relation_link");

   }

   private void checkNoTxCurrent(String dataId, String dataTable) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      StringBuilder builder = new StringBuilder();
      builder.append(NO_TX_CURRENT_SET[0]);
      builder.append(dataId);
      builder.append(NO_TX_CURRENT_SET[1]);
      builder.append(dataTable);
      builder.append(String.format(NO_TX_CURRENT_SET[2], SupportedDatabase.getComplementSql()));
      builder.append(dataId);
      builder.append(NO_TX_CURRENT_SET[3]);
      builder.append(dataTable);
      builder.append(NO_TX_CURRENT_SET[4]);

      try {
         chStmt.runPreparedQuery(builder.toString());
         if (chStmt.next()) {
            fail(String.format("No TX Current Set Failed for dataId = %s and dataTable = %s", dataId, dataTable));
         }
      } finally {
         chStmt.close();
      }
   }

   private void checkMultipleTxCurrent(String dataId, String dataTable) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      StringBuilder builder = new StringBuilder();
      builder.append(MULTIPLE_TX_CURRENT_SET[0]);
      builder.append(dataId);
      builder.append(MULTIPLE_TX_CURRENT_SET[1]);
      builder.append(dataId);
      builder.append(MULTIPLE_TX_CURRENT_SET[2]);
      builder.append(dataTable);
      builder.append(MULTIPLE_TX_CURRENT_SET[3]);
      builder.append(dataId);
      builder.append(MULTIPLE_TX_CURRENT_SET[4]);

      try {
         chStmt.runPreparedQuery(builder.toString());
         if (chStmt.next()) {
            fail(String.format("Multiple TX Current Set Failed for dataId = %s and dataTable = %s", dataId, dataTable));
         }
      } finally {
         chStmt.close();
      }
   }

   private void runMergeBranchNotCreated() throws Exception {
      TestUtil.sleep(5000);
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      try {
         Branch mergeBranch =
               BranchManager.getMergeBranch(ConflictTestManager.getSourceBranch(), ConflictTestManager.getDestBranch());

         assertTrue("The merge branch should be null as it hasn't been created yet", mergeBranch == null);
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getAllLogs().size()),
            monitorLog.getAllLogs().size() == 0);
   }

   private void runMergeBranchCreated() throws Exception {
      TestUtil.sleep(5000);
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      try {
         Branch mergeBranch =
               BranchManager.getMergeBranch(ConflictTestManager.getSourceBranch(), ConflictTestManager.getDestBranch());
         assertFalse(mergeBranch == null);
         Collection<Artifact> artifacts = ArtifactQuery.getArtifactsFromBranch(mergeBranch, true);
         if (DEBUG) {
            System.out.println("Found the following Artifacts on the branch ");
            System.out.print("     ");
            for (Artifact artifact : artifacts) {
               System.out.print(artifact.getArtId() + ", ");
            }
            System.out.println("\n");
         }
         assertEquals("The merge Branch does not contain the expected number of artifacts: ",
               ConflictTestManager.numberOfArtifactsOnMergeBranch(), artifacts.toArray().length);
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getAllLogs().size()),
            monitorLog.getAllLogs().size() == 0);
   }

}
