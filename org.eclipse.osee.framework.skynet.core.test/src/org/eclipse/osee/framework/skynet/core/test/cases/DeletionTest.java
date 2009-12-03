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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeTransactionJob;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.junit.Before;

/**
 * @author Theron Virgin
 */
public class DeletionTest {

   private static final String CHECK_FOR_ZERO_TX_CURRENT =
         "SELECT tx_current, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_artifact_version art WHERE det.branch_id = ? AND det.transaction_id < ? AND det.transaction_id = txs.transaction_id AND txs.tx_current != 0 AND txs.gamma_id = art.gamma_id and art.art_id = ?";
   private static final String CHECK_FOR_DELETED_TX_CURRENT =
         "SELECT tx_current, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_artifact_version art WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.tx_current = 2 AND txs.gamma_id = art.gamma_id and art.art_id = ?";

   private static final String CHECK_FOR_ZERO_TX_CURRENT_ATTRIBUTE =
         "SELECT tx_current, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_attribute att WHERE det.branch_id = ? AND det.transaction_id < ? AND det.transaction_id = txs.transaction_id AND txs.tx_current != 0 AND txs.gamma_id = att.gamma_id and att.attr_id = ?";
   private static final String CHECK_FOR_DELETED_TX_CURRENT_ATTRIBUTE =
         "SELECT tx_current, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_attribute att WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.tx_current = ? AND txs.gamma_id = att.gamma_id and att.attr_id = ?";

   private static final String CHECK_FOR_ZERO_TX_CURRENT_RELATION =
         "SELECT tx_current, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_relation_link rel WHERE det.branch_id = ? AND det.transaction_id < ? AND det.transaction_id = txs.transaction_id AND txs.tx_current != 0 AND txs.gamma_id = rel.gamma_id and rel.rel_link_id = ?";
   private static final String CHECK_FOR_DELETED_TX_CURRENT_RELATION =
         "SELECT tx_current, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_relation_link rel WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.tx_current = ? AND txs.gamma_id = rel.gamma_id and rel.rel_link_id = ?";

   private static final String GET_DELETED_TRANSACTION = "SELECT * FROM osee_txs WHERE transaction_id = ?";

   private static final String GET_ARTIFACT_DEBUG =
         "Select det.branch_id, det.transaction_id, txs.tx_current, txs.mod_type, txs.gamma_id, art.art_id FROM osee_tx_details det, osee_txs txs, osee_artifact_version art WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id AND art.art_id = ?";

   private static final String GET_ATTRIBUTE_DEBUG =
         "Select det.branch_id, det.transaction_id, txs.tx_current, txs.mod_type, txs.gamma_id, att.art_id, att.attr_id FROM osee_tx_details det, osee_txs txs, osee_attribute att WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = att.gamma_id AND att.attr_id = ?";

   private static final String GET_RELATION_DEBUG =
         "Select det.branch_id, det.transaction_id, txs.tx_current, txs.mod_type, txs.gamma_id, rel.rel_link_id, rel.a_art_id, rel.b_art_id FROM osee_tx_details det, osee_txs txs, osee_relation_link rel WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id AND rel.rel_link_id = ?";

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core.test/debug/Junit"));

   private static final boolean DELETE_TRANSACTION_TEST = true;
   private static final boolean INDIVIDUAL_DELETE_TEST = true;

   /**
    * @param name
    */
   public DeletionTest(String name) {
   }

   @Before
   protected void setUp() throws Exception {
      assertFalse(ClientSessionManager.isProductionDataStore());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchManager#getMergeBranch(Branch, Branch)} .
    */
   public void deleteAndCheckTXCurrents() throws OseeCoreException, InterruptedException {
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
            for (Attribute<?> attribute : artifact.getAttributes()) {
               dumpAttribute(attribute);
            }
            for (RelationLink relation : artifact.getRelationsAll(true)) {
               dumpRelation(relation, artifact);
            }
         }
         System.err.println("Deleting the first set of artifacts");
      }
      for (Artifact artifact : artifacts) {
         artifact.deleteAndPersist();
         if (DEBUG) {
            System.err.println("Deleting Artifact " + artifact.getArtId());
         }
      }

      IOseeStatement chStmt = ConnectionHandler.getStatement();
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
               chStmt.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT, artifact.getBranch().getId(),
                     artifact.getTransactionNumber(), artifact.getArtId());
               if (chStmt.next()) {
                  fail("Artifact " + artifact.getArtId() + " old Transaction < " + artifact.getTransactionNumber() + "  is set to " + chStmt.getInt("tx_current") + " , should be 0 on branch " + artifact.getBranch().getId());
               }
            } finally {
               chStmt.close();
            }
            try {
               chStmt.runPreparedQuery(CHECK_FOR_DELETED_TX_CURRENT, artifact.getBranch().getId(), artifact.getArtId());
               if (chStmt.next()) {
                  assertTrue(
                        "Artifact " + artifact.getArtId() + " Transaction: " + artifact.getTransactionNumber() + " should be 3 on branch " + artifact.getBranch().getId(),
                        chStmt.getInt("transaction_id") == artifact.getTransactionNumber());
               } else {
                  fail("Artifact " + artifact.getArtId() + " was not given a tx_current value of 2 when it was deleted on branch " + artifact.getBranch().getId() + " on transaction " + artifact.getTransactionNumber());
               }
            } finally {
               chStmt.close();
            }
         }

         //Check that attributes are Artifact Deleted
         for (Attribute<?> attribute : artifact.getAllAttributesIncludingHardDeleted()) {
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

      if (DELETE_TRANSACTION_TEST) {
         Job job = new PurgeTransactionJob(true, deletionTransaction);
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
                  chStmt.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT, artifact.getBranch().getId(), deletionTransaction,
                        artifact.getArtId());
                  if (chStmt.next()) {
                     if (deletionTransaction == chStmt.getInt("transaction_id")) {
                        fail("Artifact " + artifact.getArtId() + " tx_current set on  " + chStmt.getInt("transaction_id") + " when it should be < " + deletionTransaction + " on branch " + artifact.getBranch().getId());
                     }
                     if (chStmt.next()) {
                        fail("Artifact " + artifact.getArtId() + " has multiple tx_current set on " + artifact.getBranch().getId());
                     }
                  } else {
                     fail("Artifact " + artifact.getArtId() + " has no tx_current set on " + artifact.getBranch().getId());
                  }
               } finally {
                  chStmt.close();
               }
            }

            //Check that attributes are Artifact Deleted
            for (Attribute<?> attribute : artifact.getAllAttributesIncludingHardDeleted()) {
               if (DEBUG) {
                  dumpAttribute(attribute);
               } else {
                  try {
                     chStmt.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT_ATTRIBUTE, artifact.getBranch().getId(),
                           deletionTransaction, attribute.getAttrId());
                     if (chStmt.next()) {
                        if (deletionTransaction == chStmt.getInt("transaction_id")) {
                           fail("Attribute " + attribute.getAttrId() + " tx_current set on  " + chStmt.getInt("transaction_id") + " when it should be < " + deletionTransaction + " on branch " + artifact.getBranch().getId());
                        }
                        if (chStmt.next()) {
                           fail("Attribute " + attribute.getAttrId() + " has multiple tx_current set on " + artifact.getBranch().getId());
                        }
                     } else {
                        fail("Attribute " + attribute.getAttrId() + " has no tx_current set on " + artifact.getBranch().getId());
                     }
                  } finally {
                     chStmt.close();
                  }
               }
            }
            for (RelationLink relation : artifact.getRelationsAll(true)) {
               if (DEBUG) {
                  dumpRelation(relation, artifact);
               } else {
                  try {
                     chStmt.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT_RELATION, artifact.getBranch().getId(),
                           deletionTransaction, relation.getRelationId());
                     if (chStmt.next()) {
                        if (deletionTransaction == chStmt.getInt("transaction_id")) {
                           fail("Relation " + relation.getRelationId() + " tx_current set on  " + chStmt.getInt("transaction_id") + " when it should be < " + deletionTransaction + " on branch " + artifact.getBranch().getId());
                        }
                        if (chStmt.next()) {
                           fail("Relation " + relation.getRelationId() + " has multiple tx_current set on " + artifact.getBranch().getId());
                        }
                     } else {
                        fail("Relation " + relation.getRelationId() + " has no tx_current set on " + artifact.getBranch().getId());
                     }
                  } finally {
                     chStmt.close();
                  }
               }
            }

         }

         try {
            chStmt.runPreparedQuery(GET_DELETED_TRANSACTION, deletionTransaction);
            assertTrue(
                  "Trancsaction " + deletionTransaction + " should be deleted and should not be found in the database",
                  !chStmt.next());
         } finally {
            chStmt.close();
         }
      }
      if (INDIVIDUAL_DELETE_TEST) {

         //Check deleting an attribute and deleting a relation directly create the desired effect.

         if (ConflictTestManager.getArtifacts(true, ConflictTestManager.DELETION_ATTRIBUTE_TEST_QUERY).size() > 0) {

            Artifact artifactForDeletionCheck =
                  ConflictTestManager.getArtifacts(true, ConflictTestManager.DELETION_ATTRIBUTE_TEST_QUERY).get(0);

            if (artifactForDeletionCheck != null) {
               Attribute<?> attribute = artifactForDeletionCheck.getAttributes().get(0);
               RelationLink relation =
                     artifactForDeletionCheck.getRelations(RelationTypeManager.getType("Default Hierarchical")).get(0);
               attribute.delete();
               relation.delete(true);
               artifactForDeletionCheck.persist();
               //check for internal deletions and then check the database

               assertTrue("Attribute " + attribute.getAttrId() + " should be deleted but isn't", attribute.isDeleted());
               assertTrue("Relation " + relation.getRelationId() + " should be deleted but isn't", relation.isDeleted());

               checkAttribute(artifactForDeletionCheck, attribute, TxChange.DELETED.getValue());
               checkRelation(artifactForDeletionCheck, relation, TxChange.DELETED.getValue());
            }

         }
      }


      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getAllLogs().size()),
            monitorLog.getAllLogs().size() == 0);
      if (DEBUG) {
         fail("Deletion Test was run with tracing enabled to prevent stopping at a failure so no conditions were checked.");
      }
      if (!DELETE_TRANSACTION_TEST) {
         fail("The Delete Transaction Test was not run. Check the flag");
      }
      if (!INDIVIDUAL_DELETE_TEST) {
         fail("The Individual Deletion Test was not run. Check the flag");
      }
   }

   private void checkAttribute(Artifact artifact, Attribute<?> attribute, int value) throws OseeDataStoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT_ATTRIBUTE, artifact.getBranch().getId(),
               artifact.getTransactionNumber(), attribute.getAttrId());
         if (chStmt.next()) {
            fail("Attribute " + attribute.getAttrId() + " old Transaction < : " + artifact.getTransactionNumber() + "  is set to " + chStmt.getInt("tx_current") + " , should be 0 on branch " + artifact.getBranch().getId());
         }
      } finally {
         chStmt.close();
      }
      try {
         chStmt.runPreparedQuery(CHECK_FOR_DELETED_TX_CURRENT_ATTRIBUTE, artifact.getBranch().getId(), value,
               attribute.getAttrId());
         if (chStmt.next()) {
            assertTrue(
                  "Attribute " + attribute.getAttrId() + " Transaction: " + artifact.getTransactionNumber() + " should be 3 on branch " + artifact.getBranch().getId(),
                  chStmt.getInt("transaction_id") == artifact.getTransactionNumber());
         } else {
            fail("Attribute " + attribute.getAttrId() + " was not given a tx_current value of 3 when it was deleted on branch " + artifact.getBranch().getId());
         }
      } finally {
         chStmt.close();
      }
   }

   public void checkRelation(Artifact artifact, RelationLink relation, int value) throws OseeDataStoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      assertTrue(
            "Relation should be deleted between Parent: " + relation.getAArtifactId() + " and child " + relation.getBArtifactId(),
            relation.isDeleted());
      try {
         chStmt.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT_RELATION, artifact.getBranch().getId(),
               artifact.getTransactionNumber(), relation.getRelationId());
         if (chStmt.next()) {
            fail("Relation " + relation.getRelationId() + " old Transaction < : " + artifact.getTransactionNumber() + "  is set to " + chStmt.getInt("tx_current") + " , should be 0 on branch " + artifact.getBranch().getId());
         }
      } finally {
         chStmt.close();
      }
      try {
         chStmt.runPreparedQuery(CHECK_FOR_DELETED_TX_CURRENT_RELATION, artifact.getBranch().getId(), value,
               relation.getRelationId());
         if (chStmt.next()) {
            assertTrue(
                  "Relation " + relation.getRelationId() + " Transaction: " + artifact.getTransactionNumber() + " should be " + value + " on branch " + artifact.getBranch().getId(),
                  chStmt.getInt("transaction_id") >= artifact.getTransactionNumber());
            if (chStmt.next()) {
               fail("Relation " + relation.getRelationId() + " has multiple tx_current values of " + value + " when it was deleted on branch " + artifact.getBranch().getId() + " on transaction " + artifact.getTransactionNumber());
            }
         } else {
            fail("Relation " + relation.getRelationId() + " was not given a tx_current value of " + value + " when it was deleted on branch " + artifact.getBranch().getId() + " on transaction " + artifact.getTransactionNumber());
         }
      } finally {
         chStmt.close();
      }
   }

   public static void dumpArtifact(Artifact artifact) throws OseeDataStoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         System.out.println("  Artifact Dump : " + artifact.getName());
         chStmt.runPreparedQuery(GET_ARTIFACT_DEBUG, artifact.getBranch().getId(), artifact.getArtId());
         while (chStmt.next()) {
            System.out.println(String.format(
                  "      Art Id = %d  Branch Id = %d TX_Current = %d mod_type = %d Transaction_id = %d Gamma_id = %d",
                  chStmt.getInt("art_id"), chStmt.getInt("branch_id"), chStmt.getInt("tx_current"),
                  chStmt.getInt("mod_type"), chStmt.getInt("transaction_id"), chStmt.getInt("gamma_id")));
         }

      } finally {
         chStmt.close();
      }
   }

   public static void dumpAttribute(Attribute<?> attribute) throws OseeDataStoreException, OseeStateException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         System.out.println("  Attribute Dump");
         chStmt.runPreparedQuery(GET_ATTRIBUTE_DEBUG, attribute.getArtifact().getBranch().getId(),
               attribute.getAttrId());
         while (chStmt.next()) {
            System.out.println(String.format(
                  "        Attribute Id = %d  Art_id = %d Branch Id = %d TX_Current = %d mod_type = %d Transaction_id = %d Gamma_id = %d",
                  chStmt.getInt("attr_id"), chStmt.getInt("art_id"), chStmt.getInt("branch_id"),
                  chStmt.getInt("tx_current"), chStmt.getInt("mod_type"), chStmt.getInt("transaction_id"),
                  chStmt.getInt("gamma_id")));
         }

      } finally {
         chStmt.close();
      }
   }

   public static void dumpRelation(RelationLink relation, Artifact artifact) throws OseeDataStoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         System.out.println("  Relation Dump");
         chStmt.runPreparedQuery(GET_RELATION_DEBUG, artifact.getBranch().getId(), relation.getRelationId());
         while (chStmt.next()) {
            System.out.println(String.format(
                  "        Relation Id = %d  a_art_id = %d b_art_id = %d Branch Id = %d TX_Current = %d mod_type = %d Transaction_id = %d Gamma_id = %d",
                  chStmt.getInt("rel_link_id"), chStmt.getInt("a_art_id"), chStmt.getInt("b_art_id"),
                  chStmt.getInt("branch_id"), chStmt.getInt("tx_current"), chStmt.getInt("mod_type"),
                  chStmt.getInt("transaction_id"), chStmt.getInt("gamma_id")));
         }

      } finally {
         chStmt.close();
      }
   }
}
