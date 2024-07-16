/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.EventTopicTransferType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventChangeTypeBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.IdJoinQuery;
import org.eclipse.osee.framework.skynet.core.utility.JoinUtility;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * Changes the descriptor type of an artifact to the provided descriptor.
 *
 * @author Jeff C. Phillips
 */
public class ChangeArtifactType {
   private final HashSet<AttributeTypeId> attributeTypes = new HashSet<>();
   private final HashSet<RelationTypeToken> relationTypes = new HashSet<>();
   private final HashMap<BranchId, SkynetTransaction> txMap = new HashMap<>();
   private final Set<EventBasicGuidArtifact> artifactChanges = new HashSet<>();
   private final Set<EventTopicArtifactTransfer> artifactTopicChanges = new HashSet<>();
   private final List<Artifact> modifiedArtifacts = new ArrayList<>();
   private static final IStatus promptStatus = new Status(IStatus.WARNING, Activator.PLUGIN_ID, 257, "", null);
   private final Map<GammaId, ArtifactId> gammaToArtId = new HashMap<>();
   private static final boolean useNewEvents = FrameworkEventUtil.USE_NEW_EVENTS;

   public static void changeArtifactType(Collection<? extends Artifact> inputArtifacts, ArtifactTypeToken newArtifactType, boolean prompt) {
      ChangeArtifactType app = new ChangeArtifactType();
      if (inputArtifacts.isEmpty()) {
         throw new OseeArgumentException("The artifact list can not be empty");
      }

      try {
         app.internalChangeArtifactType(inputArtifacts, newArtifactType, prompt);
      } catch (Exception ex) {
         ArtifactQuery.reloadArtifacts(app.modifiedArtifacts);
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   /**
    * the order of operations for this BLAM is important. Since the representations for the Attribute Type and the
    * associated attributes and relations are both in memory and in the database, it is important complete both
    * memory/database changes in such a manner that they stay in sync therefore, if any part of this blam fails, then
    * the type should not be changed
    */
   private void internalChangeArtifactType(Collection<? extends Artifact> inputArtifacts, ArtifactTypeToken newArtifactType, boolean prompt) {

      createAttributeRelationTransactions(inputArtifacts, newArtifactType);
      boolean changeOk = !prompt;
      if (prompt) {
         changeOk = doesUserAcceptArtifactChange(newArtifactType);
      }

      if (!changeOk) {
         ArtifactQuery.reloadArtifacts(modifiedArtifacts);
         return;
      }

      for (SkynetTransaction transaction : txMap.values()) {
         transaction.execute();
      }

      changeArtifactTypeOutsideofHistory(inputArtifacts, newArtifactType);

      sendLocalAndRemoteEvents(modifiedArtifacts);
   }

   private void sendLocalAndRemoteEvents(Collection<? extends Artifact> artifacts) {
      if (useNewEvents) {
         // make new artifactTopic event, with new topic and event class
         ArtifactTopicEvent artifactTopicEvent = new ArtifactTopicEvent(artifacts.iterator().next().getBranch());
         artifactTopicEvent.transferType = EventTopicTransferType.CHANGE;
         for (EventTopicArtifactTransfer art : artifactTopicChanges) {
            artifactTopicEvent.addArtifact(art);
         }
         OseeEventManager.kickArtifactTopicEvent(ChangeArtifactType.class, artifactTopicEvent);
      } else {
         ArtifactEvent artifactEvent = new ArtifactEvent(artifacts.iterator().next().getBranch());
         for (EventBasicGuidArtifact guidArt : artifactChanges) {
            artifactEvent.addArtifact(guidArt);
         }
         OseeEventManager.kickPersistEvent(ChangeArtifactType.class, artifactEvent);
      }
   }

   private void createAttributeRelationTransactions(Collection<? extends Artifact> inputArtifacts, ArtifactTypeToken newArtifactType) {
      IdJoinQuery artifactJoin = populateArtIdsInJoinIdTable(inputArtifacts);
      IdJoinQuery branchJoin = populateBranchIdsJoinIdTable();
      IdJoinQuery gammaJoin = populateGammaIdsJoinIdTable(artifactJoin);
      try (JdbcStatement chStmt = ConnectionHandler.getStatement()) {
         chStmt.runPreparedQuery(
            "select branch_id, gamma_id from osee_join_id jid1, osee_join_id jid2, osee_txs txs where jid1.query_id = ? and jid2.query_id = ? and jid1.id = txs.gamma_id and jid2.id = txs.branch_id and txs.tx_current = ?",
            gammaJoin.getQueryId(), branchJoin.getQueryId(), TxCurrent.CURRENT);

         while (chStmt.next()) {
            GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
            BranchId branch = BranchId.valueOf(chStmt.getLong("branch_id"));
            ArtifactId artId = gammaToArtId.get(gammaId);
            Artifact artifact = ArtifactQuery.checkArtifactFromId(artId, branch, DeletionFlag.EXCLUDE_DELETED);
            if (artifact != null) {
               deleteInvalidAttributes(artifact, newArtifactType);
               deleteInvalidRelations(artifact, newArtifactType);
               addTransaction(artifact, txMap);
               if (useNewEvents) {
                  artifactTopicChanges.add(
                     FrameworkEventUtil.artifactTransferFactory(artifact.getBranch(), artifact, newArtifactType,
                        EventModType.ChangeType, artifact.getArtifactType(), null, EventTopicTransferType.CHANGE));
               } else {
                  artifactChanges.add(new EventChangeTypeBasicGuidArtifact(artifact.getBranch(),
                     artifact.getArtifactType(), newArtifactType, artifact.getGuid()));
               }
            }
         }
      } finally {
         artifactJoin.close();
         branchJoin.close();
         gammaJoin.close();
      }
   }

   private void addTransaction(Artifact artifact, HashMap<BranchId, SkynetTransaction> txMap) {
      BranchId branch = artifact.getBranch();
      SkynetTransaction transaction = txMap.get(branch);
      if (transaction == null) {
         transaction = TransactionManager.createTransaction(branch, "Change Artifact Type");
         txMap.put(branch, transaction);
      }
      transaction.addArtifact(artifact);
      modifiedArtifacts.add(artifact);
   }

   public static void handleRemoteChangeType(EventChangeTypeBasicGuidArtifact guidArt) {
      handleRemoteChangeByArtAndType(ArtifactCache.getActive(guidArt), guidArt.getArtifactType());
   }

   public static void handleRemoteChangeByArtAndType(Artifact artifact, ArtifactTypeToken type) {
      try {
         if (artifact == null) {
            return;
         }
         ArtifactCache.deCache(artifact);
         RelationManager.deCache(artifact);
         artifact.setArtifactType(type);
         artifact.clearEditState();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error handling remote change type", ex);
      }
   }

   private void getConflictString(StringBuilder message, ArtifactTypeToken artifactType) {
      message.append("The following types are not supported on the artifact type " + artifactType.getName() + ":\n");
      message.append("\n");
      message.append("Attribute Types:\n" + attributeTypes + "\n");
      message.append("\n");
      message.append("Relation Types:\n" + relationTypes + "\n");
   }

   private void deleteInvalidAttributes(Artifact artifact, ArtifactTypeToken artifactType) {
      for (AttributeTypeId attributeType : artifact.getAttributeTypes()) {
         if (!artifactType.isValidAttributeType(attributeType)) {
            artifact.deleteAttributes(attributeType);
            attributeTypes.add(attributeType);
         }
      }
   }

   private void deleteInvalidRelations(Artifact artifact, ArtifactTypeToken artifactType) {
      for (RelationLink link : artifact.getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
         if (link.getRelationType().getRelationSideMax(artifactType, link.getSide(artifact)) == 0) {
            link.delete(false);
            relationTypes.add(link.getRelationType());
         }
      }
   }

   /**
    * @return true if the user accepts the deletion of the attributes and relations that are not compatible for the new
    * artifact type else false.
    */
   private boolean doesUserAcceptArtifactChange(ArtifactTypeToken artifactType) {
      if (!relationTypes.isEmpty() || !attributeTypes.isEmpty()) {

         StringBuilder sb = new StringBuilder(1024);
         getConflictString(sb, artifactType);

         Object result;
         try {
            result = DebugPlugin.getDefault().getStatusHandler(promptStatus).handleStatus(promptStatus, sb.toString());
         } catch (CoreException ex) {
            throw OseeCoreException.wrap(ex);
         }

         return (Boolean) result;

      } else {
         return true;
      }
   }

   private void changeArtifactTypeOutsideofHistory(Collection<? extends Artifact> inputArtifacts, ArtifactTypeToken newArtifactType) {
      List<Object[]> insertData = new ArrayList<>();

      String UPDATE = "UPDATE osee_artifact SET art_type_id = ? WHERE art_id = ?";

      for (Artifact artifact : inputArtifacts) {
         insertData.add(new Object[] {newArtifactType, artifact});
      }

      ConnectionHandler.getJdbcClient().runBatchUpdate(UPDATE, insertData);

      for (Artifact artifact : modifiedArtifacts) {
         artifact.setArtifactType(newArtifactType);
         artifact.clearEditState();
      }
   }

   private IdJoinQuery populateArtIdsInJoinIdTable(Collection<? extends Artifact> inputArtifacts) {
      IdJoinQuery artifactJoin = JoinUtility.createIdJoinQuery();
      artifactJoin.addAndStore(inputArtifacts);
      return artifactJoin;
   }

   private IdJoinQuery populateBranchIdsJoinIdTable() {
      IdJoinQuery branchJoin = JoinUtility.createIdJoinQuery();
      BranchFilter branchFilter = new BranchFilter(BranchArchivedState.UNARCHIVED);
      branchFilter.setNegatedBranchStates(BranchState.PURGED, BranchState.DELETED);
      branchJoin.addAndStore(BranchManager.getBranches(branchFilter));
      return branchJoin;
   }

   private IdJoinQuery populateGammaIdsJoinIdTable(IdJoinQuery artIds) {
      IdJoinQuery gammaJoin = JoinUtility.createIdJoinQuery();

      Consumer<JdbcStatement> consumer = stmt -> {
         ArtifactId artId = ArtifactId.valueOf(stmt.getLong("art_id"));
         GammaId gammaId = GammaId.valueOf(stmt.getLong("gamma_id"));
         gammaToArtId.put(gammaId, artId);
         gammaJoin.add(gammaId);
      };
      ConnectionHandler.getJdbcClient().runQuery(consumer,
         "select art_id, gamma_id from osee_artifact art, osee_join_id jid where jid.query_id = ? and jid.id = art.art_id",
         artIds.getQueryId());

      gammaJoin.store();
      return gammaJoin;
   }
}