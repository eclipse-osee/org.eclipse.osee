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
package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Collection;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.conflict.RelationConflict;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
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
    * 
    * @throws Exception
    */
   @org.junit.Test
   public void testGetMergeBranchNotCreated() throws Exception {
      runMergeBranchNotCreated();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal#getConflictsPerBranch(org.eclipse.osee.framework.core.model.Branch, org.eclipse.osee.framework.core.model.Branch, org.eclipse.osee.framework.skynet.core.transaction.TransactionId)}
    * .
    */
   @org.junit.Test
   public void testGetConflictsPerBranch() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      Collection<Conflict> conflicts = null;
      try {
         conflicts =
               ConflictManagerInternal.getConflictsPerBranch(ConflictTestManager.getSourceBranch(),
                     ConflictTestManager.getDestBranch(), TransactionManager.getStartEndPoint(
                           ConflictTestManager.getSourceBranch()).getFirst(), new NullProgressMonitor());
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
      assertEquals("Number of conflicts found is not equal to the number of conflicts expected",
            ConflictTestManager.numberOfConflicts(), conflicts.toArray().length);
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getSevereLogs().size()),
            monitorLog.getSevereLogs().size() == 0);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchManager#getMergeBranch(Branch, Branch)} .
    * 
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
                     ConflictTestManager.getDestBranch(), TransactionManager.getStartEndPoint(
                           ConflictTestManager.getSourceBranch()).getFirst(), new NullProgressMonitor());
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
                     ConflictTestManager.getDestBranch(), TransactionManager.getStartEndPoint(
                           ConflictTestManager.getSourceBranch()).getFirst(), new NullProgressMonitor());

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

   public void checkCommitWithoutResolutionErrors() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      try {
         ConflictManagerExternal conflictManager =
               new ConflictManagerExternal(ConflictTestManager.getDestBranch(), ConflictTestManager.getSourceBranch());
         BranchManager.commitBranch(null, conflictManager, false, false);
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
      checkNoTxCurrent("art_id", "osee_artifact_version");
      checkNoTxCurrent("attr_id", "osee_attribute");
      checkNoTxCurrent("rel_link_id", "osee_relation_link");
      checkMultipleTxCurrent("art_id", "osee_artifact_version");
      checkMultipleTxCurrent("attr_id", "osee_attribute");
      checkMultipleTxCurrent("rel_link_id", "osee_relation_link");

   }

   private void checkNoTxCurrent(String dataId, String dataTable) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      StringBuilder builder = new StringBuilder();
      builder.append(NO_TX_CURRENT_SET[0]);
      builder.append(dataId);
      builder.append(NO_TX_CURRENT_SET[1]);
      builder.append(dataTable);
      builder.append(String.format(NO_TX_CURRENT_SET[2], chStmt.getComplementSql()));
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
      IOseeStatement chStmt = ConnectionHandler.getStatement();
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
         Collection<Artifact> artifacts = ArtifactQuery.getArtifactListFromBranch(mergeBranch, true);
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
