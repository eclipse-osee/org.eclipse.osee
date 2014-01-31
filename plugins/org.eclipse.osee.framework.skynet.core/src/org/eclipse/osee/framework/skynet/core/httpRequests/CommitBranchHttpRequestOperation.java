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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.message.BranchCommitRequest;
import org.eclipse.osee.framework.core.message.BranchCommitResponse;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidRelation;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;
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

/**
 * @author Megumi Telles
 * @author Ryan D. Brooks
 */
public final class CommitBranchHttpRequestOperation extends AbstractOperation {
   private final User user;
   private final Branch sourceBranch;
   private final Branch destinationBranch;
   private final boolean isArchiveAllowed;
   private final boolean skipChecksAndEvents;

   public CommitBranchHttpRequestOperation(User user, Branch sourceBranch, Branch destinationBranch, boolean isArchiveAllowed, boolean skipChecksAndEvents) {
      super("Commit " + sourceBranch, Activator.PLUGIN_ID);
      this.user = user;
      this.sourceBranch = sourceBranch;
      this.destinationBranch = destinationBranch;
      this.isArchiveAllowed = isArchiveAllowed;
      this.skipChecksAndEvents = skipChecksAndEvents;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.BRANCH_COMMIT.name());

      BranchState currentState = sourceBranch.getBranchState();
      sourceBranch.setBranchState(BranchState.COMMIT_IN_PROGRESS);

      BranchEvent branchEvent =
         new BranchEvent(BranchEventType.Committing, sourceBranch.getGuid(), destinationBranch.getGuid());
      OseeEventManager.kickBranchEvent(getClass(), branchEvent);

      BranchCommitRequest requestData =
         new BranchCommitRequest(user.getArtId(), sourceBranch.getId(), destinationBranch.getId(), isArchiveAllowed);

      BranchCommitResponse response = null;
      try {
         response =
            HttpClientMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters,
               CoreTranslatorId.BRANCH_COMMIT_REQUEST, requestData, CoreTranslatorId.BRANCH_COMMIT_RESPONSE);
      } catch (OseeCoreException ex) {
         sourceBranch.setBranchState(currentState);
         OseeEventManager.kickBranchEvent(getClass(),
            new BranchEvent(BranchEventType.CommitFailed, sourceBranch.getGuid()));
         throw ex;
      }

      if (response != null) {
         handleResponse(response, monitor, sourceBranch, destinationBranch);
      }
   }

   private void handleResponse(BranchCommitResponse response, IProgressMonitor monitor, Branch sourceBranch, Branch destinationBranch) throws OseeCoreException {
      TransactionRecord newTransaction = response.getTransaction();
      AccessPolicy accessPolicy = ServiceUtil.getAccessPolicy();
      accessPolicy.removePermissions(sourceBranch);

      // Update commit artifact cache with new information
      if (sourceBranch.getAssociatedArtifactId() > 0) {
         TransactionManager.cacheCommittedArtifactTransaction(BranchManager.getAssociatedArtifact(sourceBranch),
            newTransaction);
      }

      BranchManager.reloadBranch(sourceBranch);

      if (!skipChecksAndEvents) {
         Collection<Change> changes = new ArrayList<Change>();
         IOperation operation = ChangeManager.comparedToPreviousTx(newTransaction, changes);
         doSubWork(operation, monitor, 1.0);
         handleArtifactEvents(newTransaction, changes);
      }

      OseeEventManager.kickBranchEvent(getClass(), new BranchEvent(BranchEventType.Committed, sourceBranch.getGuid(),
         destinationBranch.getGuid()));
   }

   private void handleArtifactEvents(TransactionRecord newTransaction, Collection<Change> changes) throws OseeCoreException {
      ArtifactEvent artifactEvent = new ArtifactEvent(newTransaction.getBranch());
      artifactEvent.setTransactionId(newTransaction.getId());

      Map<Integer, EventModifiedBasicGuidArtifact> artEventMap = new HashMap<Integer, EventModifiedBasicGuidArtifact>();
      Set<Artifact> artifacts = new HashSet<Artifact>();

      for (Change change : changes) {
         LoadChangeType changeType = change.getChangeType();
         switch (changeType) {
            case artifact:
               // Don't do anything.  When kicking Persist event to all clients we need only to create the artifact changed based on the Changed Attributes
               break;
            case relation:
               RelationChange relChange = ((RelationChange) change);
               RelationEventType relationEventType =
                  change.getModificationType().isDeleted() ? RelationEventType.Deleted : change.getModificationType().isUnDeleted() ? RelationEventType.Undeleted : RelationEventType.Added;

               DefaultBasicGuidRelation defaultBasicGuidRelation =
                  new DefaultBasicGuidRelation(relChange.getBranch().getGuid(), relChange.getRelationType().getGuid(),
                     relChange.getItemId(), (int) relChange.getGamma(),
                     relChange.getChangeArtifact().getBasicGuidArtifact(),
                     relChange.getEndTxBArtifact().getBasicGuidArtifact());
               EventBasicGuidRelation event =
                  new EventBasicGuidRelation(relationEventType, relChange.getArtId(), relChange.getBArtId(),
                     defaultBasicGuidRelation);
               event.setRationale(relChange.getRationale());
               artifactEvent.getRelations().add(event);
               break;
            case attribute:
               // Only reload items that were already in the active cache
               int artifactId = change.getArtId();
               Artifact artifact = ArtifactCache.getActive(change.getArtId(), newTransaction.getBranch());
               if (artifact != null) {
                  artifacts.add(artifact);
               }

               Artifact changedArtifact = change.getChangeArtifact();
               if (changedArtifact != null) {

                  EventModifiedBasicGuidArtifact artEvent = artEventMap.get(artifactId);
                  if (artEvent == null) {
                     artEvent =
                        new EventModifiedBasicGuidArtifact(newTransaction.getBranch().getGuid(),
                           change.getArtifactType().getGuid(), changedArtifact.getGuid(),
                           new ArrayList<org.eclipse.osee.framework.skynet.core.event.model.AttributeChange>());
                     artifactEvent.getArtifacts().add(artEvent);
                  }

                  AttributeChange attributeChange = (AttributeChange) change;
                  org.eclipse.osee.framework.skynet.core.event.model.AttributeChange attrChangeEvent =
                     new org.eclipse.osee.framework.skynet.core.event.model.AttributeChange();
                  attrChangeEvent.setAttrTypeGuid(attributeChange.getAttributeType().getGuid());
                  attrChangeEvent.setGammaId((int) attributeChange.getGamma());
                  attrChangeEvent.setAttributeId(attributeChange.getAttrId());
                  attrChangeEvent.setModTypeGuid(AttributeEventModificationType.getType(
                     attributeChange.getModificationType()).getGuid());

                  Attribute<?> attribute = changedArtifact.getAttributeById(attributeChange.getAttrId(), true);
                  if (attribute != null) {
                     for (Object obj : attribute.getAttributeDataProvider().getData()) {
                        if (obj == null) {
                           attrChangeEvent.getData().add("");
                        } else if (obj instanceof String) {
                           attrChangeEvent.getData().add((String) obj);
                        } else {
                           OseeLog.log(Activator.class, Level.SEVERE,
                              "Unhandled data type " + obj.getClass().getSimpleName());
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