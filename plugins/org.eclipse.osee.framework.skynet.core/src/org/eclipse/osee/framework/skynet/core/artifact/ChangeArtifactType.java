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
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventChangeTypeBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
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
   private final HashSet<IRelationType> relationTypes = new HashSet<>();
   private final HashMap<BranchId, SkynetTransaction> txMap = new HashMap<>();
   private final Set<EventBasicGuidArtifact> artifactChanges = new HashSet<>();
   private final List<Artifact> modifiedArtifacts = new ArrayList<>();
   private static final IStatus promptStatus = new Status(IStatus.WARNING, Activator.PLUGIN_ID, 257, "", null);
   private final Map<Integer, Integer> gammaToArtId = new HashMap<>();

   public static void changeArtifactType(Collection<? extends Artifact> inputArtifacts, ArtifactTypeId newArtifactTypeToken, boolean prompt) throws OseeCoreException {

      ChangeArtifactType app = new ChangeArtifactType();
      if (inputArtifacts.isEmpty()) {
         throw new OseeArgumentException("The artifact list can not be empty");
      }

      if (newArtifactTypeToken == null) {
         throw new OseeArgumentException("The new artifact type can not be empty");
      }

      ArtifactType newArtifactType = ArtifactTypeManager.getType(newArtifactTypeToken);

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
   private void internalChangeArtifactType(Collection<? extends Artifact> inputArtifacts, ArtifactType newArtifactType, boolean prompt) throws OseeDataStoreException, OseeCoreException {

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

   private void sendLocalAndRemoteEvents(Collection<? extends Artifact> artifacts) throws OseeCoreException {
      ArtifactEvent artifactEvent = new ArtifactEvent(artifacts.iterator().next().getBranch());
      for (EventBasicGuidArtifact guidArt : artifactChanges) {
         artifactEvent.getArtifacts().add(guidArt);
      }

      OseeEventManager.kickPersistEvent(ChangeArtifactType.class, artifactEvent);
   }

   private void createAttributeRelationTransactions(Collection<? extends Artifact> inputArtifacts, ArtifactType newArtifactType) throws OseeDataStoreException, OseeCoreException {
      IdJoinQuery artifactJoin = populateArtIdsInJoinIdTable(inputArtifacts);
      IdJoinQuery branchJoin = populateBranchIdsJoinIdTable();
      IdJoinQuery gammaJoin = populateGammaIdsJoinIdTable(artifactJoin);
      try (JdbcStatement chStmt = ConnectionHandler.getStatement()) {
         chStmt.runPreparedQuery(
            "select branch_id, gamma_id from osee_join_id jid1, osee_join_id jid2, osee_txs txs where jid1.query_id = ? and jid2.query_id = ? and jid1.id = txs.gamma_id and jid2.id = txs.branch_id and txs.tx_current = ?",
            gammaJoin.getQueryId(), branchJoin.getQueryId(), TxChange.CURRENT.getValue());

         while (chStmt.next()) {
            int gammaId = chStmt.getInt("gamma_id");
            BranchId branch = BranchId.valueOf(chStmt.getLong("branch_id"));
            int artId = gammaToArtId.get(gammaId);
            Artifact artifact = ArtifactQuery.checkArtifactFromId(artId, branch, DeletionFlag.EXCLUDE_DELETED);
            if (artifact != null) {
               deleteInvalidAttributes(artifact, newArtifactType);
               deleteInvalidRelations(artifact, newArtifactType);
               addTransaction(artifact, txMap);
               artifactChanges.add(new EventChangeTypeBasicGuidArtifact(artifact.getBranch(),
                  artifact.getArtifactTypeId(), newArtifactType, artifact.getGuid()));
            }
         }
      } finally {
         artifactJoin.close();
         branchJoin.close();
         gammaJoin.close();
      }
   }

   private void addTransaction(Artifact artifact, HashMap<BranchId, SkynetTransaction> txMap) throws OseeCoreException {
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
      try {
         Artifact artifact = ArtifactCache.getActive(guidArt);
         if (artifact == null) {
            return;
         }
         ArtifactCache.deCache(artifact);
         RelationManager.deCache(artifact);
         artifact.setArtifactType(guidArt.getArtifactType());
         artifact.clearEditState();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error handling remote change type", ex);
      }
   }

   private void getConflictString(StringBuilder message, IArtifactType artifactType) {
      message.append("The following types are not supported on the artifact type " + artifactType.getName() + ":\n");
      message.append("\n");
      message.append("Attribute Types:\n" + attributeTypes + "\n");
      message.append("\n");
      message.append("Relation Types:\n" + relationTypes + "\n");
   }

   private void deleteInvalidAttributes(Artifact artifact, ArtifactTypeId artifactType) throws OseeCoreException {

      for (AttributeTypeId attributeType : artifact.getAttributeTypes()) {
         ArtifactType aType = ArtifactTypeManager.getType(artifactType);
         if (!aType.isValidAttributeType(attributeType, BranchManager.getBranch(artifact.getBranch()))) {
            artifact.deleteAttributes(attributeType);
            attributeTypes.add(attributeType);
         }
      }
   }

   private void deleteInvalidRelations(Artifact artifact, ArtifactTypeId artifactType) throws OseeCoreException {

      for (RelationLink link : artifact.getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
         if (RelationTypeManager.getRelationSideMax(link.getRelationType(), artifactType,
            link.getSide(artifact)) == 0) {
            link.delete(false);
            relationTypes.add(link.getRelationType());
         }
      }
   }

   /**
    * @return true if the user accepts the deletion of the attributes and relations that are not compatible for the new
    * artifact type else false.
    */
   private boolean doesUserAcceptArtifactChange(IArtifactType artifactType) throws OseeCoreException {
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

   private void changeArtifactTypeOutsideofHistory(Collection<? extends Artifact> inputArtifacts, ArtifactType newArtifactType) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<>();

      String UPDATE = "UPDATE osee_artifact SET art_type_id = ? WHERE art_id = ?";

      for (Artifact artifact : inputArtifacts) {
         insertData.add(toUpdate(newArtifactType.getId(), artifact.getArtId()));
      }

      ConnectionHandler.getJdbcClient().runBatchUpdate(UPDATE, insertData);

      for (Artifact artifact : modifiedArtifacts) {
         artifact.setArtifactType(newArtifactType);
         artifact.clearEditState();
      }
   }

   private Object[] toUpdate(long art_type_id, int art_id) {
      return new Object[] {art_type_id, art_id};
   }

   private IdJoinQuery populateArtIdsInJoinIdTable(Collection<? extends Artifact> inputArtifacts) throws OseeDataStoreException, OseeCoreException {
      IdJoinQuery artifactJoin = JoinUtility.createIdJoinQuery();
      artifactJoin.addAndStore(inputArtifacts);
      return artifactJoin;
   }

   private IdJoinQuery populateBranchIdsJoinIdTable() throws OseeDataStoreException, OseeCoreException {
      IdJoinQuery branchJoin = JoinUtility.createIdJoinQuery();
      BranchFilter branchFilter = new BranchFilter(BranchArchivedState.UNARCHIVED);
      branchFilter.setNegatedBranchStates(BranchState.PURGED, BranchState.DELETED);
      branchJoin.addAndStore(BranchManager.getBranches(branchFilter));
      return branchJoin;
   }

   private IdJoinQuery populateGammaIdsJoinIdTable(IdJoinQuery artIds) throws OseeDataStoreException, OseeCoreException {
      IdJoinQuery gammaJoin = JoinUtility.createIdJoinQuery();

      Consumer<JdbcStatement> consumer = stmt -> {
         Integer artId = stmt.getInt("art_id");
         Long gammaId = stmt.getLong("gamma_id");
         gammaToArtId.put(gammaId.intValue(), artId);
         gammaJoin.add(gammaId);
      };
      ConnectionHandler.getJdbcClient().runQuery(consumer,
         "select art_id, gamma_id from osee_artifact art, osee_join_id jid where jid.query_id = ? and jid.id = art.art_id",
         artIds.getQueryId());

      gammaJoin.store();
      return gammaJoin;
   }
}