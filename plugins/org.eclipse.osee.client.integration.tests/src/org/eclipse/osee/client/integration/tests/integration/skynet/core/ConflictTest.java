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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.ConflictTestManager;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.conflict.RelationConflict;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runners.MethodSorters;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConflictTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public MethodRule oseeHousekeepingRule = new OseeHousekeepingRule();

   @BeforeClass
   public static void setUp() throws Exception {
      ConflictTestManager.initializeConflictTest();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchManager#getMergeBranch(BranchId, BranchId)} .
    */
   @org.junit.Test
   public void test01GetMergeBranchNotCreated() throws Exception {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      try {
         IOseeBranch mergeBranch =
            BranchManager.getMergeBranch(ConflictTestManager.getSourceBranch(), ConflictTestManager.getDestBranch());

         assertTrue("The merge branch should be null as it hasn't been created yet", mergeBranch == null);
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getSevereLogs().size()),
         monitorLog.getSevereLogs().isEmpty());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal#getConflictsPerBranch(BranchId, BranchId, TransactionId)}
    * .
    */
   @org.junit.Test
   public void test02GetConflictsPerBranch() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      Collection<Conflict> conflicts = null;
      try {
         conflicts = ConflictManagerInternal.getConflictsPerBranch(ConflictTestManager.getSourceBranch(),
            ConflictTestManager.getDestBranch(), ConflictTestManager.getSourceBaseTransaction(),
            new NullProgressMonitor());
         int expectedNumber = ConflictTestManager.numberOfConflicts();
         Assert.assertNotNull(conflicts);
         int actualNumber = conflicts.size();
         assertTrue(
            "(Intermittent failures - needs re-write) - Number of conflicts found is not equal to the number of conflicts expected",
            expectedNumber <= actualNumber && actualNumber <= expectedNumber + 1);
      } catch (Exception ex) {
         fail(Lib.exceptionToString(ex));
      }
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getSevereLogs().size()),
         monitorLog.getSevereLogs().isEmpty());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchManager#getMergeBranch(BranchId, BranchId)} .
    */
   @org.junit.Test
   public void test03GetMergeBranchCreated() throws Exception {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      try {
         IOseeBranch mergeBranch =
            BranchManager.getMergeBranch(ConflictTestManager.getSourceBranch(), ConflictTestManager.getDestBranch());
         assertFalse(mergeBranch == null);
         Collection<Artifact> artifacts = ArtifactQuery.getArtifactListFromBranch(mergeBranch, INCLUDE_DELETED);

         int expectedNumber = ConflictTestManager.numberOfArtifactsOnMergeBranch();
         int actualNumber = artifacts.size();
         assertTrue(
            "(Intermittent failures - needs re-write) - The merge Branch does not contain the expected number of artifacts: ",
            expectedNumber <= actualNumber && actualNumber <= expectedNumber + 1);
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getAllLogs().size()),
         monitorLog.getAllLogs().isEmpty());
   }

   @org.junit.Test
   public void test04ResolveConflicts() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      try {
         TransactionRecord baseTx = ConflictTestManager.getSourceBaseTransaction();
         Collection<Conflict> conflicts =
            ConflictManagerInternal.getConflictsPerBranch(ConflictTestManager.getSourceBranch(),
               ConflictTestManager.getDestBranch(), baseTx, new NullProgressMonitor());

         for (Conflict conflict : conflicts) {
            if (conflict instanceof AttributeConflict) {
               ConflictTestManager.resolveAttributeConflict((AttributeConflict) conflict);
               conflict.setStatus(ConflictStatus.RESOLVED);
            } else if (conflict instanceof RelationConflict) {
               fail("Relation Conflicts are not supported yet");
            }
         }

         conflicts = ConflictManagerInternal.getConflictsPerBranch(ConflictTestManager.getSourceBranch(),
            ConflictTestManager.getDestBranch(), baseTx, new NullProgressMonitor());

         for (Conflict conflict : conflicts) {
            ConflictStatus status = conflict.getStatus();
            assertTrue(
               "This conflict was not found to be resolved ArtId = " + conflict.getArtId() + " " + conflict.getSourceDisplayData(),
               status.isResolved() || status.isInformational());

         }
      } catch (Exception ex) {
         fail(Lib.exceptionToString(ex));
      }
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getAllLogs().size()),
         monitorLog.getAllLogs().isEmpty());
   }

   @Ignore
   public void test05CommitWithoutResolutionErrors() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      try {
         ConflictManagerExternal conflictManager =
            new ConflictManagerExternal(ConflictTestManager.getDestBranch(), ConflictTestManager.getSourceBranch());
         BranchManager.commitBranch(null, conflictManager, false, false);
         assertTrue("Commit did not complete as expected", ConflictTestManager.validateCommit());

         assertTrue("Source Branch state incorrect",
            BranchManager.getState(ConflictTestManager.getSourceBranch()).isCommitted());

      } catch (Exception ex) {
         fail("No Exceptions should have been thrown. Not even the " + ex.getLocalizedMessage() + "Exception");
      }

      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getSevereLogs().size()),
         monitorLog.getSevereLogs().isEmpty());
   }

   @Test
   public void testMultiplicityCommit() {
      BranchId parent = SAW_Bld_1;
      Artifact testArt =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, parent, "Multiplicity Test");
      testArt.persist("Save testArt on parent");
      Artifact rootArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(parent);
      rootArtifact.addChild(testArt);
      rootArtifact.persist(getClass().getSimpleName());

      BranchId child1 = BranchManager.createWorkingBranch(parent, "Child1");
      IOseeBranch child2 = BranchManager.createWorkingBranch(parent, "Child2");

      Artifact onChild1 = ArtifactQuery.getArtifactFromId(testArt, child1);
      onChild1.setSoleAttributeFromString(CoreAttributeTypes.ParagraphNumber, "1");
      onChild1.persist("Save paragraph number on child1");

      List<Attribute<Object>> attributes = onChild1.getAttributes(CoreAttributeTypes.ParagraphNumber);
      Assert.assertTrue(attributes.size() == 1);
      AttributeId child1AttrId = attributes.iterator().next();

      ConflictManagerExternal mgr = new ConflictManagerExternal(parent, child1);
      BranchManager.commitBranch(new NullProgressMonitor(), mgr, true, false);
      Assert.assertFalse(mgr.originalConflictsExist());

      Artifact onChild2 = ArtifactQuery.getArtifactFromId(testArt, child2);
      onChild2.setSoleAttributeFromString(CoreAttributeTypes.ParagraphNumber, "2");
      onChild2.persist("Save paragraph number on child2");

      attributes = onChild2.getAttributes(CoreAttributeTypes.ParagraphNumber);
      Assert.assertTrue(attributes.size() == 1);

      Assert.assertNotEquals(child1AttrId, attributes.iterator().next());

      // enable multiplicity conflict checking
      OseeInfo.setValue("osee.disable.multiplicity.conflicts", "false");

      mgr = new ConflictManagerExternal(parent, child2);
      Assert.assertTrue(mgr.originalConflictsExist());
      List<Conflict> conflicts = mgr.getOriginalConflicts();
      Assert.assertTrue(conflicts.size() == 1);
      Conflict conflict = conflicts.iterator().next();
      Id conflictObjId = conflict.getObjectId();
      Assert.assertEquals(conflictObjId, child1AttrId);

      BranchManager.purgeBranch(BranchManager.getMergeBranch(child2, parent));
      BranchManager.purgeBranch(child2);
      testArt.delete();
      testArt.persist("delete artifact");
   }

   @Ignore
   @org.junit.Test
   public void test06CommitFiltering() throws OseeCoreException {
      checkNoTxCurrent("art_id", "osee_artifact");
      checkNoTxCurrent("attr_id", "osee_attribute");
      checkNoTxCurrent("rel_link_id", "osee_relation_link");

      checkMultipleTxCurrent("art_id", "osee_artifact");
      checkMultipleTxCurrent("rel_link_id", "osee_relation_link");

      //TODO: Causes intermittent failures
      //      checkMultipleTxCurrent("attr_id", "osee_attribute");
   }

   //@formatter:off
   private static final String NO_TX_CURRENT_SET =
      "SELECT DISTINCT t1.%s, txs1.branch_id FROM osee_txs txs1, %s t1 " +
      "WHERE txs1.gamma_id = t1.gamma_id AND txs1.tx_current = 0 %s " +
      "SELECT DISTINCT t2.%s, txs2.branch_id FROM osee_txs txs2, %s t2 " +
      "WHERE txs2.gamma_id = t2.gamma_id AND txs2.tx_current != 0";

   private static final String MULTIPLE_TX_CURRENT_SET =
         "SELECT resulttable.branch_id, resulttable.%s, COUNT(resulttable.branch_id) AS numoccurrences FROM " +
         "(SELECT txs1.branch_id, t1.%s FROM osee_txs txs1, %s t1 WHERE txs1.gamma_id = t1.gamma_id AND txs1.tx_current != 0) resulttable " +
         "GROUP BY resulttable.branch_id, resulttable.%s HAVING(COUNT(resulttable.branch_id) > 1) order by branch_id";
   //@formatter:on

   private static void checkNoTxCurrent(String dataId, String dataTable) throws OseeCoreException {
      JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();
      String complementSql = jdbcClient.getDbType().getComplementSql();

      String query = String.format(NO_TX_CURRENT_SET, dataId, dataTable, complementSql, dataId, dataTable);
      if (jdbcClient.fetch(null, query) != null) {
         fail(String.format("No TX Current Set Failed for dataId = %s and dataTable = %s", dataId, dataTable));
      }
   }

   private static void checkMultipleTxCurrent(String dataId, String dataTable) throws OseeCoreException {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         String query = String.format(MULTIPLE_TX_CURRENT_SET, dataId, dataId, dataTable, dataId);
         chStmt.runPreparedQuery(query);
         if (chStmt.next()) {
            fail(String.format("Multiple TX Current Set Failed for dataId = %s and dataTable = %s", dataId, dataTable));
         }
      } finally {
         chStmt.close();
      }
   }

}
