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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.Asserts;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.ConflictTestManager;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.PurgeTransactionOperationWithListener;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Theron Virgin
 */
@Ignore("This test does not work")
public class ConflictDeletionTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   private static final String CHECK_FOR_ZERO_TX_CURRENT =
      "SELECT txs.tx_current, txs.transaction_id FROM osee_txs txs, osee_artifact art WHERE txs.branch_id = ? AND txs.transaction_id < ? AND txs.tx_current != 0 AND txs.gamma_id = art.gamma_id and art.art_id = ?";
   private static final String CHECK_FOR_DELETED_TX_CURRENT =
      "SELECT txs.tx_current, txs.transaction_id FROM osee_txs txs, osee_artifact art WHERE txs.branch_id = ? AND txs.tx_current = 2 AND txs.gamma_id = art.gamma_id and art.art_id = ?";

   private static final String CHECK_FOR_ZERO_TX_CURRENT_ATTRIBUTE =
      "SELECT txs.tx_current, txs.transaction_id FROM osee_txs txs, osee_attribute att WHERE txs.branch_id = ? AND txs.transaction_id < ? AND txs.tx_current != 0 AND txs.gamma_id = att.gamma_id and att.attr_id = ?";
   private static final String CHECK_FOR_DELETED_TX_CURRENT_ATTRIBUTE =
      "SELECT txs.tx_current, txs.transaction_id FROM osee_txs txs, osee_attribute att WHERE txs.branch_id = ? AND txs.tx_current = ? AND txs.gamma_id = att.gamma_id and att.attr_id = ?";

   private static final String CHECK_FOR_ZERO_TX_CURRENT_RELATION =
      "SELECT txs.tx_current, txs.transaction_id FROM osee_txs txs, osee_relation_link rel WHERE txs.branch_id = ? AND txs.transaction_id < ? AND txs.tx_current != 0 AND txs.gamma_id = rel.gamma_id and rel.rel_link_id = ?";
   private static final String CHECK_FOR_DELETED_TX_CURRENT_RELATION =
      "SELECT txs.tx_current, txs.transaction_id FROM osee_txs txs, osee_relation_link rel WHERE txs.branch_id = ? AND txs.tx_current = ? AND txs.gamma_id = rel.gamma_id and rel.rel_link_id = ?";

   private static final String GET_DELETED_TRANSACTION = "SELECT * FROM osee_txs WHERE transaction_id = ?";

   private static final String GET_ARTIFACT_DEBUG =
      "select txs.branch_id, txs.transaction_id, txs.tx_current, txs.mod_type, txs.gamma_id, art.art_id FROM osee_txs txs, osee_artifact art WHERE txs.branch_id = ? AND txs.gamma_id = art.gamma_id AND art.art_id = ?";

   private static final String GET_ATTRIBUTE_DEBUG =
      "select txs.branch_id, txs.transaction_id, txs.tx_current, txs.mod_type, txs.gamma_id, att.art_id, att.attr_id FROM osee_txs txs, osee_attribute att WHERE txs.branch_id = ? AND txs.gamma_id = att.gamma_id AND att.attr_id = ?";

   private static final String GET_RELATION_DEBUG =
      "select txs.branch_id, txs.transaction_id, txs.tx_current, txs.mod_type, txs.gamma_id, rel.rel_link_id, rel.a_art_id, rel.b_art_id FROM osee_txs txs, osee_relation_link rel WHERE txs.branch_id = ? AND txs.gamma_id = rel.gamma_id AND rel.rel_link_id = ?";

   private static final boolean DEBUG = false;
   private static final boolean DELETE_TRANSACTION_TEST = true;
   private static final boolean INDIVIDUAL_DELETE_TEST = true;

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchManager#getMergeBranch(BranchId, BranchId)} .
    */
   @Test
   public void deleteAndCheckTXCurrents() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      Collection<Artifact> artifacts = ConflictTestManager.getArtifacts(true, ConflictTestManager.DELETION_TEST_QUERY);
      Collection<Artifact> artifactsToCheck = new LinkedList<>();

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
            for (RelationLink relation : artifact.getRelationsAll(DeletionFlag.INCLUDE_DELETED)) {
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

      JdbcStatement chStmt = ConnectionHandler.getStatement();
      //Let's check both through the API and the SQL to make sure the Artifact is internally deleted
      //and deleted in the Database, That we don't get some bad data case.

      //Check that artifacts are deleted
      TransactionId deletionTransaction = TransactionId.SENTINEL;
      for (Artifact artifact : artifactsToCheck) {
         deletionTransaction = artifact.getTransaction();
         assertTrue("Artifact " + artifact + " should be deleted, but isn't", artifact.isDeleted());
         //Now Check Artifact in the DB tx_currents etc

         if (DEBUG) {
            dumpArtifact(artifact);
         } else {
            try {
               chStmt.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT, artifact.getBranch(), artifact.getTransaction(),
                  artifact);
               if (chStmt.next()) {
                  fail(
                     "Artifact " + artifact + " old Transaction < " + artifact.getTransaction() + "  is set to " + chStmt.getInt(
                        "tx_current") + " , should be 0 on branch " + artifact.getBranch());
               }
            } finally {
               chStmt.close();
            }
            try {
               chStmt.runPreparedQuery(CHECK_FOR_DELETED_TX_CURRENT, artifact.getBranch(), artifact);
               if (chStmt.next()) {
                  assertTrue(
                     "Artifact " + artifact + " Transaction: " + artifact.getTransaction() + " should be 3 on branch " + artifact.getBranch(),
                     artifact.getTransaction().equals(chStmt.getLong("transaction_id")));
               } else {
                  fail(
                     "Artifact " + artifact + " was not given a tx_current value of 2 when it was deleted on branch " + artifact.getBranch() + " on transaction " + artifact.getTransaction());
               }
            } finally {
               chStmt.close();
            }
         }

         //Check that attributes are Artifact Deleted
         for (Attribute<?> attribute : artifact.getAttributes(true)) {
            if (DEBUG) {
               dumpAttribute(attribute);
            } else {
               checkAttribute(artifact, attribute, TxChange.ARTIFACT_DELETED);
            }
         }
         //Check that relations are deleted.
         for (RelationLink relation : artifact.getRelationsAll(DeletionFlag.INCLUDE_DELETED)) {
            if (DEBUG) {
               dumpRelation(relation, artifact);
            } else {
               checkRelation(artifact, relation, TxChange.ARTIFACT_DELETED);
            }
         }
      }

      //OK now lets delete the transaction and check for the same thing

      if (DELETE_TRANSACTION_TEST) {
         IOperation operation = PurgeTransactionOperationWithListener.getPurgeTransactionOperation(deletionTransaction);
         Asserts.assertOperation(operation, IStatus.OK);
         if (DEBUG) {
            System.err.println("Deleting the Transaction");
         }
         //This is only a DB deletion so it won't be reflected in the
         for (Artifact artifact : artifactsToCheck) {
            if (DEBUG) {
               dumpArtifact(artifact);
            } else {
               try {
                  chStmt.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT, artifact.getBranch(), deletionTransaction,
                     artifact);
                  if (chStmt.next()) {
                     if (deletionTransaction.equals(chStmt.getLong("transaction_id"))) {
                        fail("Artifact " + artifact + " tx_current set on  " + chStmt.getInt(
                           "transaction_id") + " when it should be < " + deletionTransaction + " on branch " + artifact.getBranch());
                     }
                     if (chStmt.next()) {
                        fail("Artifact " + artifact + " has multiple tx_current set on " + artifact.getBranch());
                     }
                  } else {
                     fail("Artifact " + artifact + " has no tx_current set on " + artifact.getBranch());
                  }
               } finally {
                  chStmt.close();
               }
            }

            //Check that attributes are Artifact Deleted
            for (Attribute<?> attribute : artifact.getAttributes(true)) {
               if (DEBUG) {
                  dumpAttribute(attribute);
               } else {
                  try {
                     chStmt.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT_ATTRIBUTE, artifact.getBranch(),
                        deletionTransaction, attribute.getId());
                     if (chStmt.next()) {
                        if (deletionTransaction.equals(chStmt.getLong("transaction_id"))) {
                           fail("Attribute " + attribute.getId() + " tx_current set on  " + chStmt.getInt(
                              "transaction_id") + " when it should be < " + deletionTransaction + " on branch " + artifact.getBranch());
                        }
                        if (chStmt.next()) {
                           fail(
                              "Attribute " + attribute.getId() + " has multiple tx_current set on " + artifact.getBranch());
                        }
                     } else {
                        fail("Attribute " + attribute.getId() + " has no tx_current set on " + artifact.getBranch());
                     }
                  } finally {
                     chStmt.close();
                  }
               }
            }
            for (RelationLink relation : artifact.getRelationsAll(DeletionFlag.INCLUDE_DELETED)) {
               if (DEBUG) {
                  dumpRelation(relation, artifact);
               } else {
                  try {
                     chStmt.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT_RELATION, artifact.getBranch(),
                        deletionTransaction, relation.getId());
                     if (chStmt.next()) {
                        if (deletionTransaction.equals(chStmt.getLong("transaction_id"))) {
                           fail("Relation " + relation.getId() + " tx_current set on  " + chStmt.getInt(
                              "transaction_id") + " when it should be < " + deletionTransaction + " on branch " + artifact.getBranch());
                        }
                        if (chStmt.next()) {
                           fail(
                              "Relation " + relation.getId() + " has multiple tx_current set on " + artifact.getBranch());
                        }
                     } else {
                        fail("Relation " + relation.getId() + " has no tx_current set on " + artifact.getBranch());
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
                  artifactForDeletionCheck.getRelations(CoreRelationTypes.Default_Hierarchical__Child).get(0);
               attribute.delete();
               relation.delete(true);
               artifactForDeletionCheck.persist(getClass().getSimpleName());
               //check for internal deletions and then check the database

               assertTrue("Attribute " + attribute.getId() + " should be deleted but isn't", attribute.isDeleted());
               assertTrue("Relation " + relation.getId() + " should be deleted but isn't", relation.isDeleted());

               checkAttribute(artifactForDeletionCheck, attribute, TxChange.DELETED);
               checkRelation(artifactForDeletionCheck, relation, TxChange.DELETED);
            }

         }
      }

      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getAllLogs().size()),
         monitorLog.getAllLogs().isEmpty());
      if (DEBUG) {
         fail(
            "Deletion Test was run with tracing enabled to prevent stopping at a failure so no conditions were checked.");
      }
      if (!DELETE_TRANSACTION_TEST) {
         fail("The Delete Transaction Test was not run. Check the flag");
      }
      if (!INDIVIDUAL_DELETE_TEST) {
         fail("The Individual Deletion Test was not run. Check the flag");
      }
   }

   private void checkAttribute(Artifact artifact, Attribute<?> attribute, TxChange value) {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT_ATTRIBUTE, artifact.getBranch(), artifact.getTransaction(),
            attribute.getId());
         if (chStmt.next()) {
            fail(
               "Attribute " + attribute.getId() + " old Transaction < : " + artifact.getTransaction() + "  is set to " + chStmt.getInt(
                  "tx_current") + " , should be 0 on branch " + artifact.getBranch());
         }
      } finally {
         chStmt.close();
      }
      try {
         chStmt.runPreparedQuery(CHECK_FOR_DELETED_TX_CURRENT_ATTRIBUTE, artifact.getBranch(), value,
            attribute.getId());
         if (chStmt.next()) {
            assertTrue(
               "Attribute " + attribute.getId() + " Transaction: " + artifact.getTransaction() + " should be 3 on branch " + artifact.getBranch(),
               artifact.getTransaction().equals(chStmt.getLong("transaction_id")));
         } else {
            fail(
               "Attribute " + attribute.getId() + " was not given a tx_current value of 3 when it was deleted on branch " + artifact.getBranch());
         }
      } finally {
         chStmt.close();
      }
   }

   public void checkRelation(Artifact artifact, RelationLink relation, TxChange value) {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      assertTrue(
         "Relation should be deleted between Parent: " + relation.getArtifactIdA() + " and child " + relation.getArtifactIdB(),
         relation.isDeleted());
      try {
         chStmt.runPreparedQuery(CHECK_FOR_ZERO_TX_CURRENT_RELATION, artifact.getBranch(), artifact.getTransaction(),
            relation.getId());
         if (chStmt.next()) {
            fail(
               "Relation " + relation.getId() + " old Transaction < : " + artifact.getTransaction() + "  is set to " + chStmt.getInt(
                  "tx_current") + " , should be 0 on branch " + artifact.getBranch());
         }
      } finally {
         chStmt.close();
      }
      try {
         chStmt.runPreparedQuery(CHECK_FOR_DELETED_TX_CURRENT_RELATION, artifact.getBranch(), value, relation.getId());
         if (chStmt.next()) {
            assertTrue(
               "Relation " + relation.getId() + " Transaction: " + artifact.getTransaction() + " should be " + value + " on branch " + artifact.getBranch(),
               artifact.getTransaction().isOlderThan(TransactionId.valueOf(chStmt.getLong("transaction_id"))));
            if (chStmt.next()) {
               fail(
                  "Relation " + relation.getId() + " has multiple tx_current values of " + value + " when it was deleted on branch " + artifact.getBranch() + " on transaction " + artifact.getTransaction());
            }
         } else {
            fail(
               "Relation " + relation.getId() + " was not given a tx_current value of " + value + " when it was deleted on branch " + artifact.getBranch() + " on transaction " + artifact.getTransaction());
         }
      } finally {
         chStmt.close();
      }
   }

   public static void dumpArtifact(Artifact artifact) {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         if (DEBUG) {
            System.out.println("  Artifact Dump : " + artifact.getName());
            chStmt.runPreparedQuery(GET_ARTIFACT_DEBUG, artifact.getBranch(), artifact);
            while (chStmt.next()) {
               System.out.println(String.format(
                  "      Art Id = %d  Branch Uuid = %d TX_Current = %d mod_type = %d Transaction_id = %d Gamma_id = %d",
                  chStmt.getInt("art_id"), chStmt.getLong("branch_id"), chStmt.getInt("tx_current"),
                  chStmt.getInt("mod_type"), chStmt.getLong("transaction_id"), chStmt.getLong("gamma_id")));
            }
         }
      } finally {
         chStmt.close();
      }
   }

   public static void dumpAttribute(Attribute<?> attribute) {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         if (DEBUG) {
            System.out.println("  Attribute Dump");
            chStmt.runPreparedQuery(GET_ATTRIBUTE_DEBUG, attribute.getArtifact().getBranch(), attribute.getId());
            while (chStmt.next()) {
               System.out.println(String.format(
                  "        Attribute Id = %d  Art_id = %d Branch Uuid = %d TX_Current = %d mod_type = %d Transaction_id = %d Gamma_id = %s",
                  chStmt.getInt("attr_id"), chStmt.getInt("art_id"), chStmt.getLong("branch_id"),
                  chStmt.getInt("tx_current"), chStmt.getInt("mod_type"), chStmt.getLong("transaction_id"),
                  chStmt.getLong("gamma_id")));
            }
         }
      } finally {
         chStmt.close();
      }
   }

   public static void dumpRelation(RelationLink relation, Artifact artifact) {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         if (DEBUG) {
            System.out.println("  Relation Dump");
            chStmt.runPreparedQuery(GET_RELATION_DEBUG, artifact.getBranch(), relation.getId());
            while (chStmt.next()) {
               System.out.println(String.format(
                  "        Relation Id = %d  a_art_id = %d b_art_id = %d Branch Uuid = %d TX_Current = %d mod_type = %d Transaction_id = %d Gamma_id = %d",
                  chStmt.getInt("rel_link_id"), chStmt.getInt("a_art_id"), chStmt.getInt("b_art_id"),
                  chStmt.getLong("branch_id"), chStmt.getInt("tx_current"), chStmt.getInt("mod_type"),
                  chStmt.getLong("transaction_id"), chStmt.getLong("gamma_id")));
            }
         }
      } finally {
         chStmt.close();
      }
   }
}
