/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.internal.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.event.ArtifactChangeType;
import org.eclipse.osee.framework.core.event.OriginContext;
import org.eclipse.osee.framework.core.event.TransactionCommitTopic;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.search.ds.ArtifactData;
import org.eclipse.osee.orcs.search.ds.Attribute;
import org.eclipse.osee.orcs.search.ds.AttributeData;
import org.eclipse.osee.orcs.search.ds.RelationData;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Roberto E. Escobar
 */
public class TxCallableFactory {

   private static final ObjectMapper MAPPER = new ObjectMapper();

   private final Log logger;
   private final TxDataStore txDataStore;
   private final TxDataManager txManager;
   private volatile EventAdmin eventAdmin;

   public TxCallableFactory(Log logger, TxDataStore txDataStore, TxDataManager txManager) {
      super();
      this.logger = logger;
      this.txDataStore = txDataStore;
      this.txManager = txManager;
   }

   /**
    * Sets the EventAdmin for post-commit event dispatch.
    * Called by the bundle activator or DS when the service becomes available.
    */
   public void setEventAdmin(EventAdmin eventAdmin) {
      this.eventAdmin = eventAdmin;
   }

   public CancellableCallable<Integer> purgeTransactions(OrcsSession session,
      final Collection<? extends TransactionId> transactions) {
      return new AbstractTxCallable<Integer>("PurgeTransactions", session) {
         @Override
         protected Integer innerCall() throws Exception {
            return txDataStore.purgeTransactions(getSession(), transactions).call();
         }
      };
   }

   public Callable<Void> setTransactionComment(OrcsSession session, final TransactionId transaction,
      final String comment) {
      return new AbstractTxCallable<Void>("SetTxComment", session) {
         @Override
         protected Void innerCall() throws Exception {
            return txDataStore.setTransactionComment(getSession(), transaction, comment).call();
         }
      };
   }

   public CancellableCallable<TransactionToken> createTx(final TxData txData) {
      return new AbstractTxCallable<TransactionToken>("CommitTransaction", txData.getSession()) {

         @Override
         protected TransactionToken innerCall() throws Exception {
            TransactionToken transaction = TransactionToken.SENTINEL;
            try {
               txManager.startTx(txData);
               TransactionResult result = doCommit();
               // Single-pass extraction of all event data from the change set.
               // Must run before txCommitSuccess() which mutates mod types.
               CommitEventData eventData = extractCommitEventData(result, txData);
               // Collect attribute change data from dirty writeables (also before txCommitSuccess)
               AttributeChangeCollection attrChanges = collectAttributeChanges(txData);
               txManager.txCommitSuccess(txData);
               if (result != null) {
                  transaction = result.getTransaction();
               }
               if (transaction.isValid()) {
                  postCommitEvent(txData, transaction, attrChanges, eventData);
               }
            } catch (Exception ex) {
               try {
                  txManager.rollbackTx(txData);
               } catch (Exception ex2) {
                  // Preserve the rollback failure as a suppressed cause of the original commit
                  // exception so neither is lost when diagnosing a failed commit+rollback.
                  ex.addSuppressed(ex2);
               } finally {
                  OseeCoreException.wrapAndThrow(ex);
               }
            } finally {
               txManager.endTx(txData);
            }
            return transaction;
         }

         private TransactionResult doCommit() throws Exception {
            TransactionData changes = txManager.createChangeData(txData);
            Callable<TransactionResult> callable = txDataStore.commitTransaction(getSession(), changes);
            return callable.call();
         }
      };
   }

   /**
    * Holds the two JSON payloads collected from dirty attributes in a single pass.
    */
   private static class AttributeChangeCollection {
      String attrChangesJson = "{}";
      String associatedUsersJson = "[]";
      String changedAttributeTypeIdsJson = "[]";
   }

   /**
    * Collects attribute change data (and user references) from dirty writeables BEFORE
    * txCommitSuccess clears dirty flags. Single pass over the dirty attributes.
    * <p>
    * "Associated users" are gathered generically: any attribute whose type carries a
    * user-reference {@code DisplayHint} contributes its value(s) grouped by attribute type.
    * No domain (ATS/MIM) knowledge is needed -- the marker lives on the type.
    */
   private AttributeChangeCollection collectAttributeChanges(TxData txData) {
      AttributeChangeCollection collection = new AttributeChangeCollection();
      Map<String, List<Map<String, Object>>> attrChangesByArtifact = new HashMap<>();
      // typeId -> { encoding, userIds set }
      Map<String, String> typeEncoding = new HashMap<>();
      Map<String, LinkedHashSet<String>> usersByType = new HashMap<>();
      // Distinct attribute type ids changed anywhere in the tx, for client targeted refreshes.
      LinkedHashSet<String> changedAttributeTypeIds = new LinkedHashSet<>();
      try {
         for (Artifact artifact : txData.getAllWriteables()) {
            for (Attribute<?> attr : artifact.getAttributes(DeletionFlag.INCLUDE_DELETED)) {
               if (attr.isDirty()) {
                  String artId = String.valueOf(artifact.getId());
                  List<Map<String, Object>> attrList =
                     attrChangesByArtifact.computeIfAbsent(artId, k -> new ArrayList<>());

                  AttributeTypeToken attrType = attr.getOrcsData().getType();
                  String storageString = attr.getOrcsData().getDataProxy().getStorageString();
                  String uri = attr.getOrcsData().getDataProxy().getUri();

                  Map<String, Object> attrMap = new HashMap<>();
                  attrMap.put("attrId", attr.getId());
                  attrMap.put("attrTypeId", attrType.getId());
                  attrMap.put("gammaId", attr.getOrcsData().getVersion().getGammaId().getId());
                  attrMap.put("modType", attr.getOrcsData().getModType().getName());
                  List<String> data = new ArrayList<>();
                  data.add(storageString != null ? storageString : "");
                  data.add(uri != null ? uri : "");
                  attrMap.put("data", data);
                  attrList.add(attrMap);

                  changedAttributeTypeIds.add(String.valueOf(attrType.getId()));

                  // Generic user-reference collection via type-level DisplayHint marker
                  if (attrType.isUserReference() && storageString != null && !storageString.isEmpty()) {
                     String typeId = String.valueOf(attrType.getId());
                     typeEncoding.putIfAbsent(typeId, attrType.isUserArtId() ? "artId" : "userId");
                     usersByType.computeIfAbsent(typeId, k -> new LinkedHashSet<>()).add(storageString);
                  }
               }
            }
         }
      } catch (Exception ex) {
         // Attribute collection is best-effort; a failure must not break the commit. Log so the
         // notification gap is diagnosable rather than silent.
         logger.warn(ex, "Failed to collect attribute changes for commit notification");
      }
      try {
         collection.attrChangesJson = MAPPER.writeValueAsString(attrChangesByArtifact);
      } catch (Exception ex) {
         logger.warn(ex, "Failed to serialize attribute changes for commit notification");
         collection.attrChangesJson = "{}";
      }
      try {
         List<Map<String, Object>> associated = new ArrayList<>();
         for (Map.Entry<String, LinkedHashSet<String>> entry : usersByType.entrySet()) {
            Map<String, Object> group = new HashMap<>();
            group.put("typeId", entry.getKey());
            group.put("encoding", typeEncoding.get(entry.getKey()));
            group.put("userIds", new ArrayList<>(entry.getValue()));
            associated.add(group);
         }
         collection.associatedUsersJson = MAPPER.writeValueAsString(associated);
      } catch (Exception ex) {
         logger.warn(ex, "Failed to serialize associated users for commit notification");
         collection.associatedUsersJson = "[]";
      }
      try {
         collection.changedAttributeTypeIdsJson = MAPPER.writeValueAsString(new ArrayList<>(changedAttributeTypeIds));
      } catch (Exception ex) {
         logger.warn(ex, "Failed to serialize changed attribute type ids for commit notification");
         collection.changedAttributeTypeIdsJson = "[]";
      }
      return collection;
   }

   /**
    * Holds all data extracted from the committed change set in a single pass.
    */
   private static class CommitEventData {
      final LinkedHashSet<String> artifactIds = new LinkedHashSet<>();
      final Map<String, String> artifactTypeIds = new HashMap<>();
      final Map<String, String> artifactModTypes = new HashMap<>();
      final Set<String> changeTypes = new LinkedHashSet<>();
      final List<Map<String, Object>> relationChanges = new ArrayList<>();
   }

   /**
    * Single-pass extraction of all event metadata from the committed change set.
    * Must be called before txCommitSuccess() which may mutate mod types on shared objects.
    */
   private CommitEventData extractCommitEventData(TransactionResult result, TxData txData) {
      CommitEventData data = new CommitEventData();
      if (result == null || result.getChangeSet() == null) {
         data.changeTypes.add(ArtifactChangeType.ATTRIBUTE_MODIFIED);
         return data;
      }
      OrcsChangeSet changeSet = result.getChangeSet();

      // Build a type lookup from TxData writeables (already loaded in memory)
      Map<String, String> writeableTypes = new HashMap<>();
      for (Artifact artifact : txData.getAllWriteables()) {
         writeableTypes.put(String.valueOf(artifact.getId()), String.valueOf(artifact.getOrcsData().getType().getId()));
      }

      // Track new/deleted artifact IDs to avoid redundant attribute_modified
      Set<Long> newArtifactIds = new HashSet<>();
      Set<Long> deletedArtifactIds = new HashSet<>();

      for (ArtifactData artData : changeSet.getArtifactData()) {
         String id = String.valueOf(artData.getId());
         data.artifactIds.add(id);
         data.artifactTypeIds.put(id, String.valueOf(artData.getType().getId()));

         ModificationType modType = artData.getModType();
         // An artifact is in the change set whenever it is dirty, including when only its
         // attributes changed. In that case getModType() is the stale creation mod type
         // (usually NEW), not this transaction's change, so classify by structural change.
         boolean structuralChange = isStructuralArtifactChange(artData);

         if (structuralChange && modType.equals(ModificationType.NEW)) {
            data.artifactModTypes.put(id, ModificationType.NEW.getName());
            data.changeTypes.add(ArtifactChangeType.ARTIFACT_CREATED);
            newArtifactIds.add(artData.getId());
         } else if (structuralChange && (modType.equals(ModificationType.DELETED) || modType.equals(
            ModificationType.ARTIFACT_DELETED))) {
            data.artifactModTypes.put(id, modType.getName());
            data.changeTypes.add(ArtifactChangeType.ARTIFACT_DELETED);
            deletedArtifactIds.add(artData.getId());
         } else {
            data.artifactModTypes.put(id, ModificationType.MODIFIED.getName());
            data.changeTypes.add(ArtifactChangeType.ATTRIBUTE_MODIFIED);
         }
      }

      // Attributes (only contributes artifact IDs and attribute_modified for existing artifacts)
      for (AttributeData<?> attrData : changeSet.getAttributeData()) {
         String id = attrData.getArtifactId().getIdString();
         data.artifactIds.add(id);
         long artIdLong = attrData.getArtifactId().getId();
         if (!newArtifactIds.contains(artIdLong) && !deletedArtifactIds.contains(artIdLong)) {
            data.changeTypes.add(ArtifactChangeType.ATTRIBUTE_MODIFIED);
         }
      }

      // Relations (contributes both artifact IDs, change types, and serialized data)
      for (RelationData relData : changeSet.getRelationData()) {
         String idA = relData.getArtifactIdA().getIdString();
         String idB = relData.getArtifactIdB().getIdString();
         data.artifactIds.add(idA);
         data.artifactIds.add(idB);
         data.artifactModTypes.putIfAbsent(idA, "Modified");
         data.artifactModTypes.putIfAbsent(idB, "Modified");
         data.artifactTypeIds.putIfAbsent(idA, writeableTypes.getOrDefault(idA, "0"));
         data.artifactTypeIds.putIfAbsent(idB, writeableTypes.getOrDefault(idB, "0"));

         ModificationType modType = relData.getModType();
         if (modType.equals(ModificationType.NEW)) {
            data.changeTypes.add(ArtifactChangeType.RELATION_ADDED);
         } else if (modType.equals(ModificationType.DELETED)) {
            data.changeTypes.add(ArtifactChangeType.RELATION_DELETED);
         } else {
            data.changeTypes.add(ArtifactChangeType.RELATION_MODIFIED);
         }

         // Build relation change map for JSON serialization
         Map<String, Object> relMap = new HashMap<>();
         relMap.put("relTypeId", relData.getType().getId());
         relMap.put("relId", relData.getId());
         relMap.put("artIdA", relData.getArtifactIdA().getId());
         relMap.put("artIdB", relData.getArtifactIdB().getId());
         relMap.put("gammaId", relData.getVersion().getGammaId().getId());
         relMap.put("modType", modType.getName());
         relMap.put("rationale", relData.getRationale() != null ? relData.getRationale() : "");
         relMap.put("relOrder", relData.getRelOrder());
         data.relationChanges.add(relMap);
      }

      if (data.changeTypes.isEmpty()) {
         data.changeTypes.add(ArtifactChangeType.ATTRIBUTE_MODIFIED);
      }
      return data;
   }

   /**
    * True when the artifact row itself changed (vs. being in the change set only as a
    * container for dirty attributes). Mirrors {@code TxSqlBuilderImpl.visit(ArtifactData)}
    * so event classification matches what the persistence layer actually writes.
    */
   private static boolean isStructuralArtifactChange(ArtifactData artData) {
      return !artData.getVersion().isInStorage() || artData.hasTypeUuidChange() || artData.hasModTypeChange() || artData.isExistingVersionUsed();
   }

   /**
    * Posts an async OSGi event after successful commit.
    */
   private void postCommitEvent(TxData txData, TransactionToken transaction, AttributeChangeCollection attrChanges,
      CommitEventData eventData) {
      EventAdmin admin = this.eventAdmin;
      if (admin == null) {
         return;
      }
      try {
         // Build parallel arrays from the collected data
         List<String> artifactIds = new ArrayList<>(eventData.artifactIds);
         List<String> artifactTypeIds = new ArrayList<>();
         List<String> artifactModTypes = new ArrayList<>();
         for (String id : artifactIds) {
            artifactTypeIds.add(eventData.artifactTypeIds.getOrDefault(id, "0"));
            artifactModTypes.add(eventData.artifactModTypes.getOrDefault(id, "Modified"));
         }

         Map<String, Object> properties = new HashMap<>();
         properties.put(TransactionCommitTopic.BRANCH_ID, txData.getBranch().getIdString());
         properties.put(TransactionCommitTopic.TRANSACTION_ID, transaction.getIdString());
         properties.put(TransactionCommitTopic.AUTHOR_USER_ID,
            txData.getAuthor() != null ? txData.getAuthor().getIdString() : "");
         properties.put(TransactionCommitTopic.ARTIFACT_IDS, artifactIds.toArray(new String[0]));
         properties.put(TransactionCommitTopic.ARTIFACT_TYPE_IDS, artifactTypeIds.toArray(new String[0]));
         properties.put(TransactionCommitTopic.ARTIFACT_MOD_TYPES, artifactModTypes.toArray(new String[0]));
         properties.put(TransactionCommitTopic.ATTRIBUTE_CHANGES, attrChanges.attrChangesJson);
         properties.put(TransactionCommitTopic.ASSOCIATED_USERS, attrChanges.associatedUsersJson);
         properties.put(TransactionCommitTopic.CHANGED_ATTRIBUTE_TYPE_IDS,
            attrChanges.changedAttributeTypeIdsJson);
         properties.put(TransactionCommitTopic.CHANGE_TYPES, eventData.changeTypes.toArray(new String[0]));
         properties.put(TransactionCommitTopic.RELATION_CHANGES,
            MAPPER.writeValueAsString(eventData.relationChanges));

         // Capture the request-scoped origin id here, on the request thread (commit runs
         // synchronously). EventAdmin dispatch below is async, so the value must travel in the
         // payload -- the SSE handler must not read OriginContext off the EventAdmin thread.
         String originId = OriginContext.get();
         if (originId != null) {
            properties.put(TransactionCommitTopic.ORIGIN_ID, originId);
         }

         admin.postEvent(new Event(TransactionCommitTopic.TOPIC, properties));
      } catch (Exception ex) {
         if (logger.isWarnEnabled()) {
            logger.warn("Failed to post transaction commit event: %s", ex.getMessage());
         }
      }
   }


   private abstract class AbstractTxCallable<T> extends CancellableCallable<T> {

      private final String opName;
      private final OrcsSession session;

      public AbstractTxCallable(String opName, OrcsSession session) {
         super();
         this.opName = opName;
         this.session = session;
      }

      protected OrcsSession getSession() {
         return session;
      }

      @Override
      public final T call() throws Exception {
         long startTime = System.currentTimeMillis();
         long endTime = startTime;
         T result = null;
         try {
            if (logger.isTraceEnabled()) {
               logger.trace("%s [start] ", opName);
            }
            result = innerCall();
         } finally {
            endTime = System.currentTimeMillis() - startTime;
         }
         if (logger.isTraceEnabled()) {
            logger.trace("%s [%s] - completed", opName, Lib.asTimeString(endTime));
         }
         return result;
      }

      protected abstract T innerCall() throws Exception;

   }

   public void setTransactionCommitArtifact(OrcsSession session, TransactionId trans, ArtifactId commitArt) {
      txDataStore.setTransactionCommitArtifact(session, trans, commitArt);
   }
}
