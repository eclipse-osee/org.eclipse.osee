/*
 * Created on Aug 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test.nonproduction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.test.nonproduction.components.ConflictTestManager;
import org.eclipse.osee.framework.skynet.core.test.nonproduction.components.DeletionTest;

/**
 * @author Theron Virgin
 */
public class RevertTest extends TestCase {
   private static final String GET_BASELINED_TRANSACTIONS =
         "SELECT txs.gamma_id, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_artifact_version art WHERE det.branch_id = ? AND det.tx_type = 1 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id and art.art_id = ? UNION SELECT txs.gamma_id, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_attribute attr WHERE det.branch_id = ? AND det.tx_type = 1 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = attr.gamma_id and attr.art_id = ? UNION SELECT txs.gamma_id, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_relation_link rel WHERE det.branch_id = ? AND det.tx_type = 1 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id and (rel.a_art_id = ? OR rel.b_art_id = ?)";
   private static final String GET_NON_BASELINED_TRANSACTIONS =
         "SELECT txs.gamma_id, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_artifact_version art WHERE det.branch_id = ? AND det.tx_type = 0 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id and art.art_id = ? UNION SELECT txs.gamma_id, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_attribute attr WHERE det.branch_id = ? AND det.tx_type = 0 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = attr.gamma_id and attr.art_id = ? UNION SELECT txs.gamma_id, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_relation_link rel WHERE det.branch_id = ? AND det.tx_type = 0 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id and (rel.a_art_id = ? OR rel.b_art_id = ?)";

   private static final String GAMMA_UNIQUE =
         "SELECT gamma_id FROM osee_txs txs WHERE txs.gamma_id = ? AND NOT EXISTS (SELECT 'x' FROM osee_txs txs1, osee_tx_details det1 WHERE det1.branch_id != ? AND det1.transaction_id = txs1.transaction_id and txs.gamma_id = txs1.gamma_id) AND NOT EXISTS (SELECT 'x' FROM osee_txs txs1, osee_tx_details det1 WHERE tx_type = 1 AND det1.transaction_id = txs1.transaction_id and txs.gamma_id = txs1.gamma_id)";
   private static final String GAMMAS_KEEP =
         "SELECT gamma_id FROM osee_txs txs WHERE txs.gamma_id = ? AND (EXISTS (SELECT 'x' FROM osee_txs txs1, osee_tx_details det1 WHERE det1.branch_id != ? AND det1.transaction_id = txs1.transaction_id and txs.gamma_id = txs1.gamma_id) OR EXISTS (SELECT 'x' FROM osee_txs txs1, osee_tx_details det1 WHERE tx_type = 1 AND det1.transaction_id = txs1.transaction_id and txs.gamma_id = txs1.gamma_id))";

   private static final String GET_TXS_ENTRIES = "SELECT * FROM osee_txs WHERE gamma_id = ? AND transaction_id = ?";

   private static final String GET_GAMMAS_IN_DATA =
         "SELECT gamma_id FROM osee_artifact_version WHERE gamma_id = ? UNION SELECT gamma_id FROM osee_attribute WHERE gamma_id = ? UNION SELECT gamma_id FROM osee_relation_link WHERE gamma_id = ?";

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core.test/debug/Junit"));

   protected void tearDown() throws Exception {
      super.tearDown();
      ConflictTestManager.cleanUpConflictTest();
   }

   protected void setUp() throws Exception {
      super.setUp();
      ConflictTestManager.initializeConflictTest();
      assertFalse(ClientSessionManager.isProductionDataStore());
   }

   public void testRevertArtifacts() throws OseeCoreException {

      Set<Pair<Integer, Integer>> baselines = new HashSet<Pair<Integer, Integer>>();
      Set<Pair<Integer, Integer>> nonBaselines = new HashSet<Pair<Integer, Integer>>();
      Set<Integer> uniqueGammas = new HashSet<Integer>();
      Set<Integer> keepGammas = new HashSet<Integer>();
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      Collection<Artifact> artifacts =
            ConflictTestManager.getArtifacts(true, ConflictTestManager.REVERT_ARTIFACT_QUERY);
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      for (Artifact artifact : artifacts) {
         try {
            if (DEBUG) {
               System.out.println("     Baselined Transactions");
            }
            chStmt.runPreparedQuery(GET_BASELINED_TRANSACTIONS, artifact.getBranch().getBranchId(),
                  artifact.getArtId(), artifact.getBranch().getBranchId(), artifact.getArtId(),
                  artifact.getBranch().getBranchId(), artifact.getArtId(), artifact.getArtId());
            while (chStmt.next()) {
               baselines.add(new Pair<Integer, Integer>(chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               if (DEBUG) {
                  System.out.println(String.format("          Gamma ID = %d Transaction Id = %d",
                        chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               }
            }
         } finally {
            chStmt.close();
         }
         if (DEBUG) {
            System.out.println("     Nonbaselined Transactions");
         }
         try {
            chStmt.runPreparedQuery(GET_NON_BASELINED_TRANSACTIONS, artifact.getBranch().getBranchId(),
                  artifact.getArtId(), artifact.getBranch().getBranchId(), artifact.getArtId(),
                  artifact.getBranch().getBranchId(), artifact.getArtId(), artifact.getArtId());
            while (chStmt.next()) {
               nonBaselines.add(new Pair<Integer, Integer>(chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               if (DEBUG) {
                  System.out.println(String.format("          Gamma ID = %d Transaction Id = %d",
                        chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               }
            }
         } finally {
            chStmt.close();
         }
         for (Pair<Integer, Integer> pairs : baselines) {
            try {
               chStmt.runPreparedQuery(GAMMA_UNIQUE, pairs.getKey(), artifact.getBranch().getBranchId());
               while (chStmt.next()) {
                  uniqueGammas.add(chStmt.getInt("gamma_id"));
               }
            } finally {
               chStmt.close();
            }
            try {
               chStmt.runPreparedQuery(GAMMAS_KEEP, pairs.getKey(), artifact.getBranch().getBranchId());
               while (chStmt.next()) {
                  keepGammas.add(chStmt.getInt("gamma_id"));
               }
            } finally {
               chStmt.close();
            }
         }
         for (Pair<Integer, Integer> pairs : nonBaselines) {
            try {
               chStmt.runPreparedQuery(GAMMA_UNIQUE, pairs.getKey(), artifact.getBranch().getBranchId());
               while (chStmt.next()) {
                  uniqueGammas.add(chStmt.getInt("gamma_id"));
               }
            } finally {

               try {
                  chStmt.runPreparedQuery(GAMMAS_KEEP, pairs.getKey(), artifact.getBranch().getBranchId());
                  while (chStmt.next()) {
                     keepGammas.add(chStmt.getInt("gamma_id"));
                  }
               } finally {
                  chStmt.close();
               }
            }
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

      //Ok now lets revert the artifacts
      for (Artifact artifact : artifacts) {
         if (DEBUG) {
            System.out.println("Before Revert");
            DeletionTest.dumpArtifact(artifact);
         }
         artifact.revert();
         if (DEBUG) {
            System.out.println("After Revert");
            DeletionTest.dumpArtifact(artifact);
         }
      }

      //Now lets check that everything is as should be

      for (Pair<Integer, Integer> pairs : baselines) {
         chStmt.runPreparedQuery(GET_TXS_ENTRIES, pairs.getKey().intValue(), pairs.getValue().intValue());
         assertTrue(String.format("Deleted A TXS Entry that should not have been Deleted: Gamma %d Trans %d",
               pairs.getKey(), pairs.getValue()), chStmt.next());
      }

      for (Pair<Integer, Integer> pairs : nonBaselines) {
         chStmt.runPreparedQuery(GET_TXS_ENTRIES, pairs.getKey().intValue(), pairs.getValue().intValue());
         assertTrue(String.format("Did not Delete A TXS Entry that should have been Deleted: Gamma %d Trans %d",
               pairs.getKey(), pairs.getValue()), !chStmt.next());
      }

      for (Integer gammas : uniqueGammas) {
         chStmt.runPreparedQuery(GET_GAMMAS_IN_DATA, gammas.intValue(), gammas.intValue(), gammas.intValue());
         assertTrue(String.format("Did not Delete A TXS Entry that should have been Deleted: Gamma %d", gammas),
               !chStmt.next());
      }

      for (Integer gammas : keepGammas) {
         chStmt.runPreparedQuery(GET_GAMMAS_IN_DATA, gammas.intValue(), gammas.intValue(), gammas.intValue());
         assertTrue(String.format("Deleted A TXS Entry that should not have been Deleted: Gamma %d", gammas),
               chStmt.next());
      }

   }
   private static final String GET_BASELINE_RELATION_LINKS =
         "Select txs.transaction_id, txs.gamma_id FROM osee_relation_link rel, osee_txs txs, osee_tx_details det WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id AND rel.rel_link_id = ? AND det.tx_type = 1";
   private static final String GET_CHANGES_RELATION_LINKS =
         "Select txs.transaction_id, txs.gamma_id FROM osee_relation_link rel, osee_txs txs, osee_tx_details det WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id AND atr.rel_link_id = ? AND det.tx_type = 0";

   //Revert relations needs to handle fixing the 
   public void testRevertRelationLinks() throws OseeCoreException {
      /*
            Set<Pair<Integer, Integer>> baselines = new HashSet<Pair<Integer, Integer>>();
            Set<Pair<Integer, Integer>> nonBaselines = new HashSet<Pair<Integer, Integer>>();
            Set<Integer> uniqueGammas = new HashSet<Integer>();
            Set<Integer> keepGammas = new HashSet<Integer>();
            Set<Pair<Integer, Integer>> artifactBaselines = new HashSet<Pair<Integer, Integer>>();
            Set<Pair<Integer, Integer>> artifactNonBaselines = new HashSet<Pair<Integer, Integer>>();
            Set<Integer> artifactUniqueGammas = new HashSet<Integer>();
            Set<Integer> artifactKeepGammas = new HashSet<Integer>();
            Set<RelationLink> relations = new HashSet<RelationLink>();
            SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
            OseeLog.registerLoggerListener(monitorLog);
            Collection<Artifact> artifacts =
                  ConflictTestManager.getArtifacts(true, ConflictTestManager.REVERT_REL_LINK_QUERY);
            ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

            for (Artifact artifact : artifacts) {
               for (RelationLink link : artifact.getRelationsAll(true)) {
                  relations.add(link);
                  link.delete(true);
                  }
               }

               attribute.delete();
               artifact.persistAttributes();

               assertTrue(String.format("Attribute Should be deleted but isn't Attribute Id = %d Art Id = %d",
                     attribute.getAttrId(), attribute.getArtifact().getArtId()), attribute.isDeleted());

               if (DEBUG) {
                  System.out.println("   Attribute");
                  System.out.println("     Baselined Transactions");
               }
               try {
                  chStmt =
                        ConnectionHandler.runPreparedQuery(GET_BASELINE_ATTRIBUTE, artifact.getBranch().getBranchId(),
                              attribute.getAttrId());
                  while (chStmt.next()) {
                     baselines.add(new Pair<Integer, Integer>(chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
                     if (DEBUG) {
                        System.out.println(String.format("          Gamma ID = %d Transaction Id = %d",
                              chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
                     }
                  }
               } finally {
                  chStmt.close();
               }

               if (DEBUG) {
                  System.out.println("     Nonbaselined Transactions");
               }
               try {
                  chStmt =
                        ConnectionHandler.runPreparedQuery(GET_CHANGES_ATTRIBUTE, artifact.getBranch().getBranchId(),
                              attribute.getAttrId());
                  while (chStmt.next()) {
                     nonBaselines.add(new Pair<Integer, Integer>(chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
                     if (DEBUG) {
                        System.out.println(String.format("          Gamma ID = %d Transaction Id = %d",
                              chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
                     }
                  }
               } finally {
                  chStmt.close();
               }
               for (Pair<Integer, Integer> pairs : baselines) {
                  keepGammas.add(pairs.getKey());
               }
               for (Pair<Integer, Integer> pairs : nonBaselines) {
                  try {
                     chStmt =
                           ConnectionHandler.runPreparedQuery(GAMMA_UNIQUE, pairs.getKey(),
                                 artifact.getBranch().getBranchId());
                     if (chStmt.next()) {
                        uniqueGammas.add(chStmt.getInt("gamma_id"));
                     } else {
                        keepGammas.add(pairs.getKey());
                     }
                  } finally {
                     chStmt.close();
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

               if (DEBUG) {
                  System.out.println("   Artifact Version");
                  System.out.println("     Neccessary Transactions");
               }
               try {
                  chStmt =
                        ConnectionHandler.runPreparedQuery(GET_BASELINE_ARTIFACT_VERSION + getGammaString(keepGammas,
                              uniqueGammas) + ")", artifact.getBranch().getBranchId(), artifact.getArtId());
                  while (chStmt.next()) {
                     artifactBaselines.add(new Pair<Integer, Integer>(chStmt.getInt("gamma_id"),
                           chStmt.getInt("transaction_id")));
                     if (DEBUG) {
                        System.out.println(String.format("          Gamma ID = %d Transaction Id = %d",
                              chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
                     }
                  }
               } finally {
                  chStmt.close();
               }
               if (DEBUG) {
                  System.out.println("     Removable Transactions");
               }
               try {
                  chStmt =
                        ConnectionHandler.runPreparedQuery(GET_CHANGES_ARTIFACT_VERSION + getGammaString(keepGammas,
                              uniqueGammas) + ")", artifact.getBranch().getBranchId(), artifact.getArtId());
                  while (chStmt.next()) {
                     artifactNonBaselines.add(new Pair<Integer, Integer>(chStmt.getInt("gamma_id"),
                           chStmt.getInt("transaction_id")));
                     if (DEBUG) {
                        System.out.println(String.format("          Gamma ID = %d Transaction Id = %d",
                              chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
                     }
                  }
               } finally {
                  chStmt.close();
               }

               for (Pair<Integer, Integer> pairs : artifactBaselines) {
                  artifactKeepGammas.add(pairs.getKey());
               }
               for (Pair<Integer, Integer> pairs : artifactNonBaselines) {
                  try {
                     chStmt =
                           ConnectionHandler.runPreparedQuery(GAMMA_UNIQUE, pairs.getKey(),
                                 artifact.getBranch().getBranchId());
                     if (chStmt.next()) {
                        artifactUniqueGammas.add(chStmt.getInt("gamma_id"));
                     } else {
                        artifactKeepGammas.add(pairs.getKey());
                     }
                  } finally {
                     chStmt.close();
                  }
               }
               if (DEBUG) {
                  System.out.println("     Gammas to Remove");
                  for (Integer integer : artifactUniqueGammas) {
                     System.out.println(String.format("          Gamma ID = %d ", integer.intValue()));
                  }
                  System.out.println("     Gammas to Keep");
                  for (Integer integer : artifactKeepGammas) {
                     System.out.println(String.format("          Gamma ID = %d ", integer.intValue()));
                  }
               }
            }

            //Now lets check that everything is as should be
            for (Attribute<?> attribute : relations) {
               if (DEBUG) {
                  System.out.println("Before Revert");
                  DeletionTest.dumpArtifact(attribute.getArtifact());
                  DeletionTest.dumpAttribute(attribute);
               }
               ArtifactPersistenceManager.getInstance().revertAttribute(attribute);
               if (DEBUG) {
                  System.out.println("After Revert");
                  DeletionTest.dumpArtifact(attribute.getArtifact());
                  DeletionTest.dumpAttribute(attribute);
               }
            }

            checkBaselines(baselines);
            checkNonBaselines(nonBaselines);
            checkUniqueGammas(uniqueGammas);
            checkKeepGammas(keepGammas);
            checkBaselines(artifactBaselines);
            checkNonBaselines(artifactNonBaselines);
            checkUniqueGammas(artifactUniqueGammas);
            checkKeepGammas(artifactKeepGammas);*/

   }

   private static final String GET_BASELINE_ATTRIBUTE =
         "Select txs.transaction_id, txs.gamma_id FROM osee_attribute atr, osee_txs txs, osee_tx_details det WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = atr.gamma_id AND atr.attr_id = ? AND det.tx_type = 1";
   private static final String GET_CHANGES_ATTRIBUTE =
         "Select txs.transaction_id, txs.gamma_id FROM osee_attribute atr, osee_txs txs, osee_tx_details det WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = atr.gamma_id AND atr.attr_id = ? AND det.tx_type = 0";
   private static final String GET_CHANGES_ARTIFACT_VERSION =
         "Select txs.transaction_id, txs.gamma_id FROM osee_artifact_version art, osee_txs txs, osee_tx_details det WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id AND art.art_id = ? AND NOT EXISTS (SELECT 'x' FROM osee_txs txs2 WHERE txs2.transaction_id = txs.transaction_id AND txs2.gamma_id != txs.gamma_id AND txs2.gamma_id NOT IN ";
   private static final String GET_BASELINE_ARTIFACT_VERSION =
         "Select txs.transaction_id, txs.gamma_id FROM osee_artifact_version art, osee_txs txs, osee_tx_details det WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id AND art.art_id = ? AND EXISTS (SELECT 'x' FROM osee_txs txs2 WHERE txs2.transaction_id = txs.transaction_id AND txs2.gamma_id != txs.gamma_id AND txs2.gamma_id NOT IN ";

   public void testRevertAttributes() throws OseeCoreException {

      Set<Pair<Integer, Integer>> baselines = new HashSet<Pair<Integer, Integer>>();
      Set<Pair<Integer, Integer>> nonBaselines = new HashSet<Pair<Integer, Integer>>();
      Set<Integer> uniqueGammas = new HashSet<Integer>();
      Set<Integer> keepGammas = new HashSet<Integer>();
      Set<Pair<Integer, Integer>> artifactBaselines = new HashSet<Pair<Integer, Integer>>();
      Set<Pair<Integer, Integer>> artifactNonBaselines = new HashSet<Pair<Integer, Integer>>();
      Set<Integer> artifactUniqueGammas = new HashSet<Integer>();
      Set<Integer> artifactKeepGammas = new HashSet<Integer>();
      Set<Attribute<?>> attributes = new HashSet<Attribute<?>>();
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      Collection<Artifact> artifacts =
            ConflictTestManager.getArtifacts(true, ConflictTestManager.REVERT_ATTRIBUTE_QUERY);
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      for (Artifact artifact : artifacts) {
         Collection<Attribute<?>> attributs = artifact.getAttributes(true);
         Attribute<?> attribute = null;
         for (Attribute<?> attr : attributs) {
            if (attr.getAttributeType().getName().equals("Name")) {
               attribute = attr;
            }
         }

         attributes.add(attribute);
         attribute.delete();
         artifact.persistAttributes();

         assertTrue(String.format("Attribute Should be deleted but isn't Attribute Id = %d Art Id = %d",
               attribute.getAttrId(), attribute.getArtifact().getArtId()), attribute.isDeleted());

         if (DEBUG) {
            System.out.println("   Attribute");
            System.out.println("     Baselined Transactions");
         }
         try {
            chStmt.runPreparedQuery(GET_BASELINE_ATTRIBUTE, artifact.getBranch().getBranchId(), attribute.getAttrId());
            while (chStmt.next()) {
               baselines.add(new Pair<Integer, Integer>(chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               if (DEBUG) {
                  System.out.println(String.format("          Gamma ID = %d Transaction Id = %d",
                        chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               }
            }
         } finally {
            chStmt.close();
         }

         if (DEBUG) {
            System.out.println("     Nonbaselined Transactions");
         }
         try {
            chStmt.runPreparedQuery(GET_CHANGES_ATTRIBUTE, artifact.getBranch().getBranchId(), attribute.getAttrId());
            while (chStmt.next()) {
               nonBaselines.add(new Pair<Integer, Integer>(chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               if (DEBUG) {
                  System.out.println(String.format("          Gamma ID = %d Transaction Id = %d",
                        chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               }
            }
         } finally {
            chStmt.close();
         }
         for (Pair<Integer, Integer> pairs : baselines) {
            keepGammas.add(pairs.getKey());
         }
         for (Pair<Integer, Integer> pairs : nonBaselines) {
            try {
               chStmt.runPreparedQuery(GAMMA_UNIQUE, pairs.getKey(), artifact.getBranch().getBranchId());
               if (chStmt.next()) {
                  uniqueGammas.add(chStmt.getInt("gamma_id"));
               } else {
                  keepGammas.add(pairs.getKey());
               }
            } finally {
               chStmt.close();
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

         if (DEBUG) {
            System.out.println("   Artifact Version");
            System.out.println("     Neccessary Transactions");
         }
         try {
            chStmt.runPreparedQuery(GET_BASELINE_ARTIFACT_VERSION + getGammaString(keepGammas, uniqueGammas) + ")",
                  artifact.getBranch().getBranchId(), artifact.getArtId());
            while (chStmt.next()) {
               artifactBaselines.add(new Pair<Integer, Integer>(chStmt.getInt("gamma_id"),
                     chStmt.getInt("transaction_id")));
               if (DEBUG) {
                  System.out.println(String.format("          Gamma ID = %d Transaction Id = %d",
                        chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               }
            }
         } finally {
            chStmt.close();
         }
         if (DEBUG) {
            System.out.println("     Removable Transactions");
         }
         try {
            chStmt.runPreparedQuery(GET_CHANGES_ARTIFACT_VERSION + getGammaString(keepGammas, uniqueGammas) + ")",
                  artifact.getBranch().getBranchId(), artifact.getArtId());
            while (chStmt.next()) {
               artifactNonBaselines.add(new Pair<Integer, Integer>(chStmt.getInt("gamma_id"),
                     chStmt.getInt("transaction_id")));
               if (DEBUG) {
                  System.out.println(String.format("          Gamma ID = %d Transaction Id = %d",
                        chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               }
            }
         } finally {
            chStmt.close();
         }

         for (Pair<Integer, Integer> pairs : artifactBaselines) {
            artifactKeepGammas.add(pairs.getKey());
         }
         for (Pair<Integer, Integer> pairs : artifactNonBaselines) {
            try {
               chStmt.runPreparedQuery(GAMMA_UNIQUE, pairs.getKey(), artifact.getBranch().getBranchId());
               if (chStmt.next()) {
                  artifactUniqueGammas.add(chStmt.getInt("gamma_id"));
               } else {
                  artifactKeepGammas.add(pairs.getKey());
               }
            } finally {
               chStmt.close();
            }
         }
         if (DEBUG) {
            System.out.println("     Gammas to Remove");
            for (Integer integer : artifactUniqueGammas) {
               System.out.println(String.format("          Gamma ID = %d ", integer.intValue()));
            }
            System.out.println("     Gammas to Keep");
            for (Integer integer : artifactKeepGammas) {
               System.out.println(String.format("          Gamma ID = %d ", integer.intValue()));
            }
         }
      }

      //Now lets check that everything is as should be
      for (Attribute<?> attribute : attributes) {
         if (DEBUG) {
            System.out.println("Before Revert");
            DeletionTest.dumpArtifact(attribute.getArtifact());
            DeletionTest.dumpAttribute(attribute);
         }
         attribute.revert();
         if (DEBUG) {
            System.out.println("After Revert");
            DeletionTest.dumpArtifact(attribute.getArtifact());
            DeletionTest.dumpAttribute(attribute);
         }
      }

      checkBaselines(baselines);
      checkNonBaselines(nonBaselines);
      checkUniqueGammas(uniqueGammas);
      checkKeepGammas(keepGammas);
      checkBaselines(artifactBaselines);
      checkNonBaselines(artifactNonBaselines);
      checkUniqueGammas(artifactUniqueGammas);
      checkKeepGammas(artifactKeepGammas);

   }

   /**
    * @param keepGammas
    * @param uniqueGammas
    * @return
    */
   private String getGammaString(Set<Integer> keepGammas, Set<Integer> uniqueGammas) {
      StringBuilder builder = new StringBuilder();
      builder.append("(");
      for (Integer integer : keepGammas) {
         builder.append(integer);
         builder.append(", ");
      }
      for (Integer integer : uniqueGammas) {
         builder.append(integer);
         builder.append(", ");
      }
      builder.append(" 0)");
      return builder.toString();
   }

   private void checkBaselines(Set<Pair<Integer, Integer>> baselines) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         for (Pair<Integer, Integer> pairs : baselines) {
            chStmt.runPreparedQuery(GET_TXS_ENTRIES, pairs.getKey().intValue(), pairs.getValue().intValue());
            assertTrue(String.format("Deleted A TXS Entry that should not have been Deleted: Gamma %d Trans %d",
                  pairs.getKey(), pairs.getValue()), chStmt.next());
         }

      } finally {
         chStmt.close();
      }
   }

   private void checkNonBaselines(Set<Pair<Integer, Integer>> nonBaselines) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         for (Pair<Integer, Integer> pairs : nonBaselines) {
            chStmt.runPreparedQuery(GET_TXS_ENTRIES, pairs.getKey().intValue(), pairs.getValue().intValue());
            assertTrue(String.format("Did not Delete A TXS Entry that should have been Deleted: Gamma %d Trans %d",
                  pairs.getKey(), pairs.getValue()), !chStmt.next());
         }

      } finally {
         chStmt.close();
      }

   }

   private void checkUniqueGammas(Set<Integer> uniqueGammas) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         for (Integer gammas : uniqueGammas) {
            chStmt.runPreparedQuery(GET_GAMMAS_IN_DATA, gammas.intValue(), gammas.intValue(), gammas.intValue());
            assertTrue(String.format("Did not Delete A TXS Entry that should have been Deleted: Gamma %d", gammas),
                  !chStmt.next());
         }

      } finally {
         chStmt.close();
      }
   }

   private void checkKeepGammas(Set<Integer> keepGammas) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         for (Integer gammas : keepGammas) {
            chStmt.runPreparedQuery(GET_GAMMAS_IN_DATA, gammas.intValue(), gammas.intValue(), gammas.intValue());
            assertTrue(String.format("Deleted A TXS Entry that should not have been Deleted: Gamma %d", gammas),
                  chStmt.next());
         }

      } finally {
         chStmt.close();
      }
   }
}
