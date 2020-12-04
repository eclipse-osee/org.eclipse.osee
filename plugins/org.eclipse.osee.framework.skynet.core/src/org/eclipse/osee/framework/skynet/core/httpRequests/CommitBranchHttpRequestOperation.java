/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.skynet.core.httpRequests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.core.model.event.DefaultBasicIdRelation;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventModifiedBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.BranchCommitOptions;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;

/**
 * @author Megumi Telles
 * @author Ryan D. Brooks
 */
public final class CommitBranchHttpRequestOperation extends AbstractOperation {
   private final ArtifactId committer;
   private final BranchId sourceBranch;
   private final BranchId destinationBranch;
   private final boolean isArchiveAllowed;
   private final boolean skipChecksAndEvents;
   private TransactionResult transactionResult;

   public CommitBranchHttpRequestOperation(ArtifactId committer, BranchId sourceBranch, BranchId destinationBranch, boolean isArchiveAllowed, boolean skipChecksAndEvents) {
      super("Commit " + sourceBranch, Activator.PLUGIN_ID);
      this.committer = committer;
      this.sourceBranch = sourceBranch;
      this.destinationBranch = destinationBranch;
      this.isArchiveAllowed = isArchiveAllowed;
      this.skipChecksAndEvents = skipChecksAndEvents;

   }

   @Override
   protected void doWork(IProgressMonitor monitor) {
      // Set to new result in case exception happens before commitBranch completes
      transactionResult = new TransactionResult();
      try {
         BranchEvent branchEvent = new BranchEvent(BranchEventType.Committing, sourceBranch, destinationBranch);
         OseeEventManager.kickBranchEvent(getClass(), branchEvent);

         OseeClient client = ServiceUtil.getOseeClient();
         BranchEndpoint branchEp = client.getBranchEndpoint();

         BranchCommitOptions options = new BranchCommitOptions();
         options.setArchive(isArchiveAllowed);
         options.setCommitter(committer);
         transactionResult = branchEp.commitBranch(sourceBranch, destinationBranch, options);

         if (transactionResult.getResults().isErrors() || transactionResult.getTx().isInvalid()) {
            OseeEventManager.kickBranchEvent(getClass(), new BranchEvent(BranchEventType.CommitFailed, sourceBranch));
         } else {
            handleResponse(transactionResult.getTx(), monitor, sourceBranch, destinationBranch);
         }
      } catch (Exception ex) {
         // Set back to what it was in local Branch object
         OseeEventManager.kickBranchEvent(getClass(), new BranchEvent(BranchEventType.CommitFailed, sourceBranch));
         transactionResult.getResults().errorf("Exception CommitBranchHttpRequestOperation [%s]",
            Lib.exceptionToString(ex));
      }
   }

   private void handleResponse(TransactionToken newTransaction, IProgressMonitor monitor, BranchId sourceBranch, BranchId destinationBranch) {
      AccessPolicy accessPolicy = ServiceUtil.getAccessPolicy();
      accessPolicy.removePermissions(sourceBranch);

      // Update commit artifact cache with new information
      Artifact associatedArtifact = BranchManager.getAssociatedArtifact(sourceBranch);
      if (associatedArtifact.isValid()) {
         TransactionManager.cacheCommittedArtifactTransaction(associatedArtifact, newTransaction);
      }

      BranchManager.reloadBranch(sourceBranch);

      if (!skipChecksAndEvents) {
         Collection<Change> changes = new ArrayList<>();
         IOperation operation = ChangeManager.comparedToPreviousTx(newTransaction, changes);
         doSubWork(operation, monitor, 1.0);
         handleArtifactEvents(newTransaction, changes);
      }

      OseeEventManager.kickBranchEvent(getClass(),
         new BranchEvent(BranchEventType.Committed, sourceBranch, destinationBranch));
   }

   private void handleArtifactEvents(TransactionToken newTransaction, Collection<Change> changes) {
      ArtifactEvent artifactEvent = new ArtifactEvent(newTransaction);
      Map<Integer, EventModifiedBasicGuidArtifact> artEventMap = new HashMap<>();
      Set<Artifact> artifacts = new HashSet<>();

      for (Change change : changes) {
         ChangeType changeType = change.getChangeType();
         if (changeType.isArtifactChange()) {
            // Don't do anything.  When kicking Persist event to all clients we need only to create the artifact changed based on the Changed Attributes
         } else if (changeType.isRelationChange()) {
            RelationChange relChange = (RelationChange) change;
            RelationEventType relationEventType =
               change.getModificationType().isDeleted() ? RelationEventType.Deleted : change.getModificationType().isUnDeleted() ? RelationEventType.Undeleted : RelationEventType.Added;

            DefaultBasicIdRelation defaultBasicGuidRelation = new DefaultBasicIdRelation(relChange.getBranch(),
               relChange.getRelationType().getId(), relChange.getItemId().getId(), relChange.getGamma(),
               relChange.getChangeArtifact().getBasicGuidArtifact(),
               relChange.getEndTxBArtifact().getBasicGuidArtifact());
            EventBasicGuidRelation event = new EventBasicGuidRelation(relationEventType, relChange.getArtId(),
               relChange.getBArtId(), defaultBasicGuidRelation);
            event.setRationale(relChange.getRationale());
            artifactEvent.getRelations().add(event);
            Artifact artA = ArtifactCache.getActive(relChange.getArtId(), newTransaction.getBranch());
            if (artA != null) {
               artifacts.add(artA);
            }
            Artifact artB = ArtifactCache.getActive(relChange.getArtId(), newTransaction.getBranch());
            if (artB != null) {
               artifacts.add(artB);
            }
         } else if (changeType.isAttributeChange()) {
            // Only reload items that were already in the active cache
            ArtifactId artifactId = change.getArtId();
            Artifact artifact = ArtifactCache.getActive(artifactId, newTransaction.getBranch());
            if (artifact != null) {
               artifacts.add(artifact);
            }

            Artifact changedArtifact = change.getChangeArtifact();
            if (changedArtifact.isValid()) {

               EventModifiedBasicGuidArtifact artEvent = artEventMap.get(artifactId.getId().intValue());
               if (artEvent == null) {
                  artEvent = new EventModifiedBasicGuidArtifact(newTransaction.getBranch(), change.getArtifactType(),
                     changedArtifact.getGuid(),
                     new ArrayList<org.eclipse.osee.framework.skynet.core.event.model.AttributeChange>());
                  artifactEvent.addArtifact(artEvent);
               }

               AttributeChange attributeChange = (AttributeChange) change;
               org.eclipse.osee.framework.skynet.core.event.model.AttributeChange attrChangeEvent =
                  new org.eclipse.osee.framework.skynet.core.event.model.AttributeChange();
               attrChangeEvent.setAttrTypeGuid(attributeChange.getAttributeType().getId());
               attrChangeEvent.setGammaId(attributeChange.getGamma());
               attrChangeEvent.setAttributeId(attributeChange.getAttrId().getId().intValue());
               attrChangeEvent.setModTypeGuid(
                  AttributeEventModificationType.getType(attributeChange.getModificationType()).getGuid());

               Attribute<?> attribute = changedArtifact.getAttributeById(attributeChange.getAttrId().getId(), true);
               if (attribute != null) {
                  for (Object obj : attribute.getAttributeDataProvider().getData()) {
                     if (obj == null) {
                        attrChangeEvent.getData().add("");
                     } else {
                        attrChangeEvent.getData().add(obj);
                     }
                  }
               }
               artEvent.getAttributeChanges().add(attrChangeEvent);
            }
         }
      }

      // Kicks event to other clients; This is ignored by this client which is why below is required
      OseeEventManager.kickPersistEvent(getClass(), artifactEvent);

      // Create a new copy of same event because you can't send same event twice
      ArtifactEvent artifactEvent2 = FrameworkEventUtil.getPersistEvent(
         FrameworkEventUtil.getRemotePersistEvent(artifactEvent), ServiceUtil.getOrcsTokenService());

      // Kicks event to this client to update Artifact model with commit changes since commit was on server
      OseeEventManager.kickCommitEvent(getClass(), artifactEvent2);
   }

   public TransactionResult getTransactionResult() {
      return transactionResult;
   }

   public void setTransactionResult(TransactionResult transactionResult) {
      this.transactionResult = transactionResult;
   }

}
