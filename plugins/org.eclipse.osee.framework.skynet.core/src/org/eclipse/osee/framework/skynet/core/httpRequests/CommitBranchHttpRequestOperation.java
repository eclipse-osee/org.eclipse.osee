/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.httpRequests;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.model.event.DefaultBasicIdRelation;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
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
import org.eclipse.osee.framework.skynet.core.revision.LoadChangeType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.BranchCommitOptions;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.Transaction;

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
      BranchState currentState = BranchManager.getState(sourceBranch);
      BranchManager.getBranch(sourceBranch).setBranchState(BranchState.COMMIT_IN_PROGRESS); // the server changes the state in the database to COMMIT_IN_PROGRESS

      BranchEvent branchEvent = new BranchEvent(BranchEventType.Committing, sourceBranch, destinationBranch);
      OseeEventManager.kickBranchEvent(getClass(), branchEvent);

      OseeClient client = ServiceUtil.getOseeClient();
      BranchEndpoint proxy = client.getBranchEndpoint();

      BranchCommitOptions options = new BranchCommitOptions();
      options.setArchive(isArchiveAllowed);
      options.setCommitter(committer);
      try {
         Response response = proxy.commitBranch(sourceBranch, destinationBranch, options);
         if (Status.CREATED.getStatusCode() == response.getStatus()) {
            BranchManager.setState(sourceBranch, BranchState.COMMITTED);
            Long txId = getTransactionId(response);
            handleResponse(txId, monitor, sourceBranch, destinationBranch);
         }
      } catch (Exception ex) {
         BranchManager.setState(sourceBranch, currentState);
         OseeEventManager.kickBranchEvent(getClass(), new BranchEvent(BranchEventType.CommitFailed, sourceBranch));
         throw ex;
      }
   }

   private long getTransactionId(Response response) {
      long toReturn = -1;
      if (response.hasEntity()) {
         Transaction tx = response.readEntity(Transaction.class);
         toReturn = tx.getTxId().getId();
      } else {
         URI location = response.getLocation();
         if (location != null) {
            String path = location.toASCIIString();
            int index = path.lastIndexOf("txs/");
            if (index > 0 && index < path.length()) {
               String value = path.substring(index);
               if (Strings.isNumeric(value)) {
                  toReturn = Integer.parseInt(value);
               }
            }
         }
      }
      return toReturn;
   }

   private void handleResponse(Long newTxId, IProgressMonitor monitor, BranchId sourceBranch, BranchId destinationBranch) {
      TransactionToken newTransaction = TransactionToken.valueOf(newTxId, destinationBranch);
      AccessPolicy accessPolicy = ServiceUtil.getAccessPolicy();
      accessPolicy.removePermissions(sourceBranch);

      // Update commit artifact cache with new information
      Artifact associatedArtifact = BranchManager.getAssociatedArtifact(sourceBranch);
      if (associatedArtifact.notEqual(SystemUser.OseeSystem)) {
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
         LoadChangeType changeType = change.getChangeType();
         switch (changeType) {
            case artifact:
               // Don't do anything.  When kicking Persist event to all clients we need only to create the artifact changed based on the Changed Attributes
               break;
            case relation:
               RelationChange relChange = (RelationChange) change;
               RelationEventType relationEventType =
                  change.getModificationType().isDeleted() ? RelationEventType.Deleted : change.getModificationType().isUnDeleted() ? RelationEventType.Undeleted : RelationEventType.Added;

               DefaultBasicIdRelation defaultBasicGuidRelation = new DefaultBasicIdRelation(relChange.getBranch(),
                  relChange.getRelationType().getGuid(), relChange.getItemId().getId().intValue(),
                  relChange.getGamma().getId().intValue(), relChange.getChangeArtifact().getBasicGuidArtifact(),
                  relChange.getEndTxBArtifact().getBasicGuidArtifact());
               EventBasicGuidRelation event = new EventBasicGuidRelation(relationEventType, relChange.getArtId(),
                  relChange.getBArtId(), defaultBasicGuidRelation);
               event.setRationale(relChange.getRationale());
               artifactEvent.getRelations().add(event);
               break;
            case attribute:
               // Only reload items that were already in the active cache
               ArtifactId artifactId = change.getArtId();
               Artifact artifact = ArtifactCache.getActive(artifactId, newTransaction.getBranch());
               if (artifact != null) {
                  artifacts.add(artifact);
               }

               Artifact changedArtifact = change.getChangeArtifact();
               if (changedArtifact != null) {

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
                  attrChangeEvent.setGammaId(attributeChange.getGamma().getId().intValue());
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
               break;
            default:
               break;
         }
      }

      ArtifactQuery.reloadArtifacts(artifacts);
      OseeEventManager.kickPersistEvent(getClass(), artifactEvent);
   }

}