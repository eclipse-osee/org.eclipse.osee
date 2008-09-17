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
package org.eclipse.osee.framework.skynet.core.test.nonproduction.components;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import junit.framework.TestCase;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.DeleteTransactionJob;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Theron Virgin
 */
public class DeletionTest extends TestCase {

   private static final String CHECK_FOR_ZERO_TX_CURRENT =
         "SELECT tx_current, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_artifact_version art WHERE det.branch_id = ? AND det.transaction_id < ? AND det.transaction_id = txs.transaction_id AND txs.tx_current != 0 AND txs.gamma_id = art.gamma_id and art.art_id = ?";
   private static final String CHECK_FOR_DELETED_TX_CURRENT =
         "SELECT tx_current, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_artifact_version art WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.tx_current = 2 AND txs.gamma_id = art.gamma_id and art.art_id = ?";

   private static final String CHECK_FOR_ZERO_TX_CURRENT_ATTRIBUTE =
         "SELECT tx_current, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_attribute att WHERE det.branch_id = ? AND det.transaction_id < ? AND det.transaction_id = txs.transaction_id AND txs.tx_current != 0 AND txs.gamma_id = att.gamma_id and att.attr_id = ?";
   private static final String CHECK_FOR_DELETED_TX_CURRENT_ATTRIBUTE =
         "SELECT tx_current, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_attribute att WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.tx_current = ? AND txs.gamma_id = att.gamma_id and att.attr_id = ?";

   private static final String CHECK_FOR_ZERO_TX_CURRENT_RELATION =
         "SELECT tx_current, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_rel_link rel WHERE det.branch_id = ? AND det.transaction_id < ? AND det.transaction_id = txs.transaction_id AND txs.tx_current != 0 AND txs.gamma_id = rel.gamma_id and rel.rel_link_id = ?";
   private static final String CHECK_FOR_DELETED_TX_CURRENT_RELATION =
         "SELECT tx_current, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_rel_link rel WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.tx_current = ? AND txs.gamma_id = rel.gamma_id and rel.rel_link_id = ?";

   private static final String GET_DELETED_TRANSACTION = "SELECT * FROM osee_define_txs WHERE transaction_id = ?";

   private static final String GET_ARTIFACT_DEBUG =
         "Select det.branch_id, det.transaction_id, txs.tx_current, txs.mod_type, txs.gamma_id, art.art_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_artifact_version art WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id AND art.art_id = ?";

   private static final String GET_ATTRIBUTE_DEBUG =
         "Select det.branch_id, det.transaction_id, txs.tx_current, txs.mod_type, txs.gamma_id, att.art_id, att.attr_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_attribute att WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = att.gamma_id AND att.attr_id = ?";

   private static final String GET_RELATION_DEBUG =
         "Select det.branch_id, det.transaction_id, txs.tx_current, txs.mod_type, txs.gamma_id, rel.rel_link_id, rel.a_art_id, rel.b_art_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_rel_link rel WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id AND rel.rel_link_id = ?";

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core.test/debug/Junit"));

   /**
    * @param name
    */
   public DeletionTest(String name) {
      super(name);
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      assertFalse(DatabaseActivator.getInstance().isProductionDb());
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager#getMergeBranch(java.lang.Integer, java.lang.Integer)}
    * .
    */
   public void deleteAndCheckTXCurrents() throws SQLException, OseeCoreException, InterruptedException {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      Collection<Artifact> artifacts = ConflictTestManager.getArtifacts(true, ConflictTestManager.DELETION_TEST_QUERY);
      Collection<Artifact> artifactsToCheck = new LinkedList<Artifact>();
      int deletionTransaction = 0;
      for (Artifact artifact : artifacts) {
         artifactsToCheck.add(artifact);
         artifactsToCheck.addAll(artifact.getDescendants());
      }
      if (DEBUG) {
         System.err.println("Initial Status artifacts");
         for (Artifact artifact : artifactsToCheck) {
            dumpArtifact(artifact);
            for (Attribute<?> attribute : artifact.getAttributes(true)) {
               dumpAttribute(attribute);
            }
            for (RelationLink relation : artifact.getRelationsAll(true)) {
               dumpRelation(relation, artifact);
            }
         }
         System.err.println("Deleting the first set of artifacts");
      }
      for (Artifact artifact : artifacts) {
         artifact.delete();
         if (DEBUG) {
            System.err.println("Deleting Artifact " + artifact.getArtId());
         }
      }

      ConnectionHandlerStatement chstmt = null;
      ResultSet rSet = null;
      //Let's check both through the API and the SQL to make sure the Artifact is internally deleted
      //and deleted in the Database, That we don't get some bad data case.

      //Check that artifacts are deleted
      for (Artifact artifact : artifactsToCheck) {
         deletionTransaction = artifact.getTransactionNumber();
         assertTrue("Artifact " + artifact.getArtId() + " should be deleted, but isn't", artifact.isDeleted());
         //Now Check Artifact in the DB tx_currents etc

         if (DEBUG) {
            dumpArtifact(artifact);
         } else {
            try {
               chstmt =
                     ConnectionHandler.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT, artifact.getBranch().getBranchId(),
                           artifact.getTransactionNumber(), artifact.getArtId());
               rSet = chstmt.getRset();
               if (rSet.next()) {
                  fail("Artifact " + artifact.getArtId() + " old Transaction < " + artifact.getTransactionNumber() + "  is set to " + rSet.getInt("tx_current") + " , should be 0 on branch " + artifact.getBranch().getBranchId());
               }
            } finally {
               DbUtil.close(chstmt);
            }
            try {
               chstmt =
                     ConnectionHandler.runPreparedQuery(CHECK_FOR_DELETED_TX_CURRENT,
                           artifact.getBranch().getBranchId(), artifact.getArtId());
               rSet = chstmt.getRset();
               if (rSet.next()) {
                  assertTrue(
                        "Artifact " + artifact.getArtId() + " Transaction: " + artifact.getTransactionNumber() + " should be 3 on branch " + artifact.getBranch().getBranchId(),
                        rSet.getInt("transaction_id") == artifact.getTransactionNumber());
               } else {
                  fail("Artifact " + artifact.getArtId() + " was not given a tx_current value of 2 when it was deleted on branch " + artifact.getBranch().getBranchId() + " on transaction " + artifact.getTransactionNumber());
               }
            } finally {
               DbUtil.close(chstmt);
            }
         }

         //Check that attributes are Artifact Deleted
         for (Attribute<?> attribute : artifact.getAttributes(true)) {
            if (DEBUG) {
               dumpAttribute(attribute);
            } else {
               checkAttribute(artifact, attribute, TxChange.ARTIFACT_DELETED.getValue());
            }
         }
         //Check that relations are deleted.
         for (RelationLink relation : artifact.getRelationsAll(true)) {
            if (DEBUG) {
               dumpRelation(relation, artifact);
            } else {
               checkRelation(artifact, relation, TxChange.ARTIFACT_DELETED.getValue());
            }
         }
      }

      //OK now lets delete the transaction and check for the same thing

      Job job = new DeleteTransactionJob(true, deletionTransaction);
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
      job.join();

      if (DEBUG) {
         System.err.println("Deleting the Transaction");
      }
      //This is only a DB deletion so it won't be reflected in the 
      for (Artifact artifact : artifactsToCheck) {
         if (DEBUG) {
            dumpArtifact(artifact);
         } else {
            try {
               chstmt =
                     ConnectionHandler.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT, artifact.getBranch().getBranchId(),
                           deletionTransaction, artifact.getArtId());
               rSet = chstmt.getRset();
               if (rSet.next()) {
                  if (deletionTransaction == rSet.getInt("transaction_id")) {
                     fail("Artifact " + artifact.getArtId() + " tx_current set on  " + rSet.getInt("transaction_id") + " when it should be < " + deletionTransaction + " on branch " + artifact.getBranch().getBranchId());
                  }
                  if (rSet.next()) {
                     fail("Artifact " + artifact.getArtId() + " has multiple tx_current set on " + artifact.getBranch().getBranchId());
                  }
               } else {
                  fail("Artifact " + artifact.getArtId() + " has no tx_current set on " + artifact.getBranch().getBranchId());
               }
            } finally {
               DbUtil.close(chstmt);
            }
         }

         //Check that attributes are Artifact Deleted
         for (Attribute<?> attribute : artifact.getAttributes(true)) {
            if (DEBUG) {
               dumpAttribute(attribute);
            } else {
               try {
                  chstmt =
                        ConnectionHandler.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT_ATTRIBUTE,
                              artifact.getBranch().getBranchId(), deletionTransaction, attribute.getAttrId());
                  rSet = chstmt.getRset();
                  if (rSet.next()) {
                     if (deletionTransaction == rSet.getInt("transaction_id")) {
                        fail("Attribute " + attribute.getAttrId() + " tx_current set on  " + rSet.getInt("transaction_id") + " when it should be < " + deletionTransaction + " on branch " + artifact.getBranch().getBranchId());
                     }
                     if (rSet.next()) {
                        fail("Attribute " + attribute.getAttrId() + " has multiple tx_current set on " + artifact.getBranch().getBranchId());
                     }
                  } else {
                     fail("Attribute " + attribute.getAttrId() + " has no tx_current set on " + artifact.getBranch().getBranchId());
                  }
               } finally {
                  DbUtil.close(chstmt);
               }
            }
         }
         for (RelationLink relation : artifact.getRelationsAll(true)) {
            if (DEBUG) {
               dumpRelation(relation, artifact);
            } else {
               try {
                  chstmt =
                        ConnectionHandler.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT_RELATION,
                              artifact.getBranch().getBranchId(), deletionTransaction, relation.getRelationId());
                  rSet = chstmt.getRset();
                  if (rSet.next()) {
                     if (deletionTransaction == rSet.getInt("transaction_id")) {
                        fail("Relation " + relation.getRelationId() + " tx_current set on  " + rSet.getInt("transaction_id") + " when it should be < " + deletionTransaction + " on branch " + artifact.getBranch().getBranchId());
                     }
                     if (rSet.next()) {
                        fail("Relation " + relation.getRelationId() + " has multiple tx_current set on " + artifact.getBranch().getBranchId());
                     }
                  } else {
                     fail("Relation " + relation.getRelationId() + " has no tx_current set on " + artifact.getBranch().getBranchId());
                  }
               } finally {
                  DbUtil.close(chstmt);
               }
            }
         }

      }

      try {
         chstmt = ConnectionHandler.runPreparedQuery(GET_DELETED_TRANSACTION, deletionTransaction);
         rSet = chstmt.getRset();
         assertTrue(
               "Trancsaction " + deletionTransaction + " should be deleted and should not be found in the database",
               !rSet.next());
      } finally {
         DbUtil.close(chstmt);
      }

      //Check deleting an attribute and deleting a relation directly create the desired effect.

      if (ConflictTestManager.getArtifacts(true, ConflictTestManager.DELETION_ATTRIBUTE_TEST_QUERY).size() > 0) {

         Artifact artifactForDeletionCheck =
               ConflictTestManager.getArtifacts(true, ConflictTestManager.DELETION_ATTRIBUTE_TEST_QUERY).get(0);

         if (artifactForDeletionCheck != null) {
            Attribute<?> attribute = artifactForDeletionCheck.getAttributes(false).get(0);
            RelationLink relation =
                  artifactForDeletionCheck.getRelations(RelationTypeManager.getType("Default Hierarchical")).get(0);
            attribute.delete();
            relation.delete(true);
            artifactForDeletionCheck.persistAttributesAndRelations();
            //check for internal deletions and then check the database

            assertTrue("Attribute " + attribute.getAttrId() + " should be deleted but isn't", attribute.isDeleted());
            assertTrue("Relation " + relation.getRelationId() + " should be deleted but isn't", relation.isDeleted());

            checkAttribute(artifactForDeletionCheck, attribute, TxChange.DELETED.getValue());
            checkRelation(artifactForDeletionCheck, relation, TxChange.DELETED.getValue());
         }

      }

      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getSevereLogs().size()),
            monitorLog.getSevereLogs().size() == 0);
      if (DEBUG) {
         fail("Deletion Test was run with tracing enabled to prevent stopping at a failure so no conditions were checked.");
      }
   }

   private void checkAttribute(Artifact artifact, Attribute<?> attribute, int value) throws SQLException {

      ConnectionHandlerStatement chstmt = null;
      ResultSet rSet = null;
      try {
         chstmt =
               ConnectionHandler.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT_ATTRIBUTE,
                     artifact.getBranch().getBranchId(), artifact.getTransactionNumber(), attribute.getAttrId());
         rSet = chstmt.getRset();
         if (rSet.next()) {
            fail("Attribute " + attribute.getAttrId() + " old Transaction < : " + artifact.getTransactionNumber() + "  is set to " + rSet.getInt("tx_current") + " , should be 0 on branch " + artifact.getBranch().getBranchId());
         }
      } finally {
         DbUtil.close(chstmt);
      }
      try {
         chstmt =
               ConnectionHandler.runPreparedQuery(CHECK_FOR_DELETED_TX_CURRENT_ATTRIBUTE,
                     artifact.getBranch().getBranchId(), value, attribute.getAttrId());
         rSet = chstmt.getRset();
         if (rSet.next()) {
            assertTrue(
                  "Attribute " + attribute.getAttrId() + " Transaction: " + artifact.getTransactionNumber() + " should be 3 on branch " + artifact.getBranch().getBranchId(),
                  rSet.getInt("transaction_id") == artifact.getTransactionNumber());
         } else {
            fail("Attribute " + attribute.getAttrId() + " was not given a tx_current value of 3 when it was deleted on branch " + artifact.getBranch().getBranchId());
         }
      } finally {
         DbUtil.close(chstmt);
      }
   }

   public void checkRelation(Artifact artifact, RelationLink relation, int value) throws SQLException {
      ConnectionHandlerStatement chstmt = null;
      ResultSet rSet = null;
      assertTrue(
            "Relation should be deleted between Parent: " + relation.getAArtifactId() + " and child " + relation.getBArtifactId(),
            relation.isDeleted());
      try {
         chstmt =
               ConnectionHandler.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT_RELATION,
                     artifact.getBranch().getBranchId(), artifact.getTransactionNumber(), relation.getRelationId());
         rSet = chstmt.getRset();
         if (rSet.next()) {
            fail("Relation " + relation.getRelationId() + " old Transaction < : " + artifact.getTransactionNumber() + "  is set to " + rSet.getInt("tx_current") + " , should be 0 on branch " + artifact.getBranch().getBranchId());
         }
      } finally {
         DbUtil.close(chstmt);
      }
      try {
         chstmt =
               ConnectionHandler.runPreparedQuery(CHECK_FOR_DELETED_TX_CURRENT_RELATION,
                     artifact.getBranch().getBranchId(), value, relation.getRelationId());
         rSet = chstmt.getRset();
         if (rSet.next()) {
            assertTrue(
                  "Relation " + relation.getRelationId() + " Transaction: " + artifact.getTransactionNumber() + " should be " + value + " on branch " + artifact.getBranch().getBranchId(),
                  rSet.getInt("transaction_id") >= artifact.getTransactionNumber());
            if (rSet.next()) {
               fail("Relation " + relation.getRelationId() + " has multiple tx_current values of " + value + " when it was deleted on branch " + artifact.getBranch().getBranchId() + " on transaction " + artifact.getTransactionNumber());
            }
         } else {
            fail("Relation " + relation.getRelationId() + " was not given a tx_current value of " + value + " when it was deleted on branch " + artifact.getBranch().getBranchId() + " on transaction " + artifact.getTransactionNumber());
         }
      } finally {
         DbUtil.close(chstmt);
      }
   }

   private void dumpArtifact(Artifact artifact) throws SQLException {
      ConnectionHandlerStatement chstmt = null;
      ResultSet rSet = null;
      try {
         System.out.println("Artifact Dump : " + artifact.getInternalDescriptiveName());
         chstmt =
               ConnectionHandler.runPreparedQuery(GET_ARTIFACT_DEBUG, artifact.getBranch().getBranchId(),
                     artifact.getArtId());
         rSet = chstmt.getRset();
         while (rSet.next()) {
            System.out.println(String.format(
                  "      Art Id = %d  Branch Id = %d TX_Current = %d mod_type = %d Transaction_id = %d Gamma_id = %d",
                  rSet.getInt("art_id"), rSet.getInt("branch_id"), rSet.getInt("tx_current"), rSet.getInt("mod_type"),
                  rSet.getInt("transaction_id"), rSet.getInt("gamma_id")));
         }

      } finally {
         DbUtil.close(chstmt);
      }
   }

   private void dumpAttribute(Attribute<?> attribute) throws SQLException {
      ConnectionHandlerStatement chstmt = null;
      ResultSet rSet = null;
      try {
         System.out.println("  Atttribute Dump");
         chstmt =
               ConnectionHandler.runPreparedQuery(GET_ATTRIBUTE_DEBUG,
                     attribute.getArtifact().getBranch().getBranchId(), attribute.getAttrId());
         rSet = chstmt.getRset();
         while (rSet.next()) {
            System.out.println(String.format(
                  "        Attribute Id = %d  Art_id = %d Branch Id = %d TX_Current = %d mod_type = %d Transaction_id = %d Gamma_id = %d",
                  rSet.getInt("attr_id"), rSet.getInt("art_id"), rSet.getInt("branch_id"), rSet.getInt("tx_current"),
                  rSet.getInt("mod_type"), rSet.getInt("transaction_id"), rSet.getInt("gamma_id")));
         }

      } finally {
         DbUtil.close(chstmt);
      }
   }

   private void dumpRelation(RelationLink relation, Artifact artifact) throws SQLException {
      ConnectionHandlerStatement chstmt = null;
      ResultSet rSet = null;
      try {
         System.out.println("  Relation Dump");
         chstmt =
               ConnectionHandler.runPreparedQuery(GET_RELATION_DEBUG, artifact.getBranch().getBranchId(),
                     relation.getRelationId());
         rSet = chstmt.getRset();
         while (rSet.next()) {
            System.out.println(String.format(
                  "        Relation Id = %d  a_art_id = %d b_art_id = %d Branch Id = %d TX_Current = %d mod_type = %d Transaction_id = %d Gamma_id = %d",
                  rSet.getInt("rel_link_id"), rSet.getInt("a_art_id"), rSet.getInt("b_art_id"),
                  rSet.getInt("branch_id"), rSet.getInt("tx_current"), rSet.getInt("mod_type"),
                  rSet.getInt("transaction_id"), rSet.getInt("gamma_id")));
         }

      } finally {
         DbUtil.close(chstmt);
      }
   }
}
