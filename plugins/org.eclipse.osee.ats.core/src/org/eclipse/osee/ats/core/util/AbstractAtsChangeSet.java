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

package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.notify.AtsWorkItemNotificationEvent;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.ats.core.util.AtsRelationChange.RelationOperation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsChangeSet implements IAtsChangeSet {

   protected String comment;
   protected final Set<AtsRelationChange> relations = new CopyOnWriteArraySet<>();
   protected final Set<IAtsObject> atsObjects = new CopyOnWriteArraySet<>();
   protected final Set<ArtifactId> artifacts = new CopyOnWriteArraySet<>();
   protected final Set<IAtsObject> deleteAtsObjects = new CopyOnWriteArraySet<>();
   protected final Set<ArtifactId> deleteArtifacts = new CopyOnWriteArraySet<>();
   protected final Set<IExecuteListener> listeners = new CopyOnWriteArraySet<>();
   protected final AtsUser asUser;
   protected final AtsNotificationCollector notifications = new AtsNotificationCollector();
   protected final List<IAtsWorkItem> workItemsCreated = new ArrayList<>();
   protected boolean execptionIfEmpty = true;
   protected BranchToken branch;
   protected Set<ArtifactId> ids = new HashSet<>();
   protected Map<String, String> seqNameToStartNum = new HashMap<>();
   protected boolean executed = false;
   protected HashCollection<IAtsWorkItem, AtsUser> initialAssignees = new HashCollection<>();
   protected AtsApi atsApi;
   protected TransactionToken transactionTok = TransactionToken.SENTINEL;

   public AbstractAtsChangeSet(String comment, BranchToken branch, AtsUser asUser) {
      this.comment = comment;
      this.branch = branch;
      this.asUser = asUser;
      this.atsApi = AtsApiService.get();
      Conditions.checkNotNullOrEmpty(comment, "comment");
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(asUser, "user");
      Conditions.assertTrue(branch.isValid(), "%s is not a valid branch", branch);
   }

   @Override
   public Set<ArtifactId> getIds() {
      return ids;
   }

   protected void checkExecuted() {
      if (executed) {
         throw new OseeStateException("Change Set already used.");
      }
   }

   @Override
   public void add(Object obj) {
      checkExecuted();
      Conditions.checkNotNull(obj, "object");
      if (obj instanceof Collection) {
         for (Object object : (Collection<?>) obj) {
            add(object);
         }
      } else if (obj instanceof IAtsObject) {
         IAtsObject atsObj = (IAtsObject) obj;
         if (atsObj.getStoreObject() != null && !atsObj.getStoreObject().getBranch().equals(branch)) {
            throw new OseeArgumentException("Can't add %s from branch %s to conflicting branch %s in same transaction",
               atsObj.toStringWithId(), atsObj.getStoreObject().getBranchIdString(), branch.getIdString());
         }
         atsObjects.add((IAtsObject) obj);
         ids.add(((IAtsObject) obj).getArtifactId());
      } else if (obj instanceof ArtifactToken) {
         ArtifactToken artTok = (ArtifactToken) obj;
         if (!artTok.getBranch().equals(branch)) {
            throw new OseeArgumentException("Can't add %s from branch %s to conflicting branch %s in same transaction",
               artTok.toStringWithId(), artTok.getBranchIdString(), branch.getIdString());
         }
         artifacts.add((ArtifactToken) obj);
         ids.add((ArtifactToken) obj);
      } else if (obj instanceof ArtifactId) {
         artifacts.add((ArtifactId) obj);
         ids.add((ArtifactId) obj);
      } else if (obj instanceof AtsRelationChange) {
         AtsRelationChange relation = (AtsRelationChange) obj;
         relations.add(relation);
         for (Object ob : relation.getObjects()) {
            add(ob);
         }
      } else {
         throw new OseeArgumentException("Object not supported: " + obj);
      }
   }

   @Override
   public void addAll(Object... objects) {
      checkExecuted();
      Conditions.checkNotNull(objects, "objects");
      for (Object obj : objects) {
         add(obj);
      }
   }

   public void setComment(String comment) {
      checkExecuted();
      this.comment = comment;
   }

   @Override
   public boolean isEmpty() {
      return artifacts.isEmpty() && deleteArtifacts.isEmpty() && atsObjects.isEmpty() && deleteAtsObjects.isEmpty() && relations.isEmpty();
   }

   @Override
   public void addExecuteListener(IExecuteListener listener) {
      Conditions.checkNotNull(listener, "listener");
      listeners.add(listener);
   }

   @Override
   public void addToDelete(Object obj) {
      checkExecuted();
      Conditions.checkNotNull(obj, "object");
      if (obj instanceof Collection) {
         for (Object object : (Collection<?>) obj) {
            add(object);
         }
      } else if (obj instanceof IAtsObject) {
         deleteAtsObjects.add((IAtsObject) obj);
      } else if (obj instanceof ArtifactId) {
         deleteArtifacts.add((ArtifactId) obj);
      } else {
         throw new OseeArgumentException("Object not supported: " + obj);
      }
   }

   @Override
   public String getComment() {
      return comment;
   }

   @Override
   public AtsNotificationCollector getNotifications() {
      return notifications;
   }

   @Override
   public ArtifactToken createArtifact(ArtifactToken token) {
      checkExecuted();
      ArtifactTypeToken typeToken = token.getArtifactType();
      if (typeToken.isInvalid()) {
         throw new OseeArgumentException("Artifact Type Token %s is invalid for artifact creation",
            typeToken.toStringWithId());
      }
      return createArtifact(token.getArtifactType(), token.getName(), token.getId());
   }

   @Override
   public void deleteArtifact(IAtsWorkItem task) {
      checkExecuted();
      deleteArtifact(task.getStoreObject());
   }

   @Override
   public void addWorkflowCreated(IAtsTeamWorkflow teamWf) {
      checkExecuted();
      workItemsCreated.add(teamWf);
   }

   @Override
   public AtsUser getAsUser() {
      return asUser;
   }

   @Override
   public void unrelate(IAtsObject atsObject, RelationTypeSide relationSide, IAtsObject atsObjec2) {
      unrelate(atsObject.getStoreObject(), relationSide, atsObjec2.getStoreObject());
   }

   @Override
   public void unrelate(ArtifactId artifact, RelationTypeSide relationSide, IAtsObject atsObject) {
      unrelate(artifact, relationSide, atsObject.getStoreObject());
   }

   @Override
   public void unrelate(IAtsObject atsObject, RelationTypeSide relationSide, ArtifactId artifact) {
      unrelate(atsObject.getStoreObject(), relationSide, artifact);
   }

   @Override
   public void setSoleAttributeFromString(IAtsObject atsObject, AttributeTypeGeneric<?> attributeType, String value) {
      setSoleAttributeFromString(atsObject.getStoreObject(), attributeType, value);
   }

   @Override
   public void setRelation(Object object1, RelationTypeSide relationSide, Object object2) {
      setRelations(object1, relationSide, Collections.singleton(object2));
   }

   @Override
   public void addChild(ArtifactId parent, ArtifactId child) {
      checkExecuted();
      relate(parent, CoreRelationTypes.DefaultHierarchical_Child, child);
   }

   @Override
   public void setName(ArtifactToken artifact, String name) {
      checkExecuted();
      setSoleAttributeValue(artifact, CoreAttributeTypes.Name, name);
   }

   @Override
   public void setName(IAtsObject atsObject, String name) {
      checkExecuted();
      setSoleAttributeValue(atsObject, CoreAttributeTypes.Name, name);
   }

   @Override
   public void addWorkItemNotificationEvent(AtsWorkItemNotificationEvent workItemNotificationEvent) {
      checkExecuted();
      notifications.getWorkItemNotificationEvents().add(workItemNotificationEvent);
   }

   @Override
   public void addNotificationEvent(AtsNotificationEvent notifyEvent) {
      checkExecuted();
      notifications.getNotificationEvents().add(notifyEvent);
   }

   @Override
   public List<IAtsWorkItem> getWorkItemsCreated() {
      return workItemsCreated;
   }

   @Override
   public void addChild(IAtsObject parent, IAtsObject child) {
      checkExecuted();
      add(new AtsRelationChange(parent, CoreRelationTypes.DefaultHierarchical_Child, Collections.singleton(child),
         RelationOperation.Add));
   }

   /**
    * @return ArtifactId that is part of this change set
    */
   public ArtifactId getStoredArtifact(Id id) {
      for (ArtifactId art : artifacts) {
         if (art.getId().equals(id.getId())) {
            return art;
         }
      }
      return null;
   }

   /**
    * @return ATS Object that is part of this change set
    */
   public IAtsObject getStoredAtsObject(Id id) {
      for (IAtsObject obj : atsObjects) {
         if (obj.equals(id)) {
            return obj;
         }
      }
      return null;
   }

   @Override
   public ArtifactToken createArtifact(ArtifactToken parent, ArtifactToken artifact) {
      checkExecuted();
      ArtifactToken art = createArtifact(artifact);
      if (parent != null) {
         addChild(parent, art);
      }
      return art;
   }

   public BranchId getBranch() {
      return branch;
   }

   @Override
   public void addTag(IAtsObject atsObject, String tag) {
      checkExecuted();
      if (atsObject.getTags() != null) {
         if (!atsObject.getTags().contains(tag)) {
            addAttribute(atsObject, CoreAttributeTypes.StaticId, tag);
         }
      }
   }

   @Override
   public void addAtsIdSequence(String seqName, String seqStart) {
      checkExecuted();
      seqNameToStartNum.put(seqName, seqStart);
   }

   @Override
   public void addAssignee(IAtsWorkItem workItem, AtsUser assignee) {
      assigneesChanging(workItem);
      List<AtsUser> assignees = workItem.getAssignees();
      assignees.add(assignee);
      setAssignees(workItem, assignees);
   }

   @Override
   public void setAssignees(IAtsWorkItem workItem, Collection<AtsUser> newAssignees) {
      assigneesChanging(workItem);
      if (newAssignees == null || newAssignees.isEmpty()) {
         newAssignees = new HashSet<>();
         newAssignees.add(AtsCoreUsers.UNASSIGNED_USER);
      }

      for (AtsUser assignee : newAssignees) {
         if (AtsCoreUsers.isSystemUser(assignee)) {
            throw new OseeArgumentException("Can not assign workflow to System User");
         }
      }

      if (newAssignees.size() > 1 && newAssignees.contains(AtsCoreUsers.UNASSIGNED_USER)) {
         newAssignees.remove(AtsCoreUsers.UNASSIGNED_USER);
      }

      List<Object> newAssigneeIds = new ArrayList<>();
      for (AtsUser user : newAssignees) {
         newAssigneeIds.add(user.getIdString());
      }

      setAttributeValues(workItem, AtsAttributeTypes.CurrentStateAssignee, newAssigneeIds);

      atsApi.getWorkItemService().getStateMgr(workItem).createOrUpdateState(workItem.getCurrentStateName(),
         newAssignees);
   }

   @Override
   public void setAssignee(IAtsWorkItem workItem, IStateToken state, AtsUser assignee) {
      assigneesChanging(workItem);

      setAttributeValues(workItem, AtsAttributeTypes.CurrentStateAssignee, Arrays.asList(assignee.getIdString()));

      atsApi.getWorkItemService().getStateMgr(workItem).createOrUpdateState(workItem.getCurrentStateName(),
         Collections.singleton(assignee));
   }

   @Override
   public void removeAssignee(IAtsWorkItem workItem, AtsUser assignee) {
      assigneesChanging(workItem);
      List<AtsUser> assignees = workItem.getAssignees();
      assignees.remove(assignee);
      setAssignees(workItem, assignees);
   }

   @Override
   public void clearAssignees(IAtsWorkItem workItem) {
      assigneesChanging(workItem);

      deleteAttributes(workItem, AtsAttributeTypes.CurrentStateAssignee);

      atsApi.getWorkItemService().getStateMgr(workItem).createOrUpdateState(workItem.getCurrentStateName(),
         Collections.emptyList());
   }

   @Override
   public void addAssignees(IAtsWorkItem workItem, Collection<AtsUser> assignees) {
      assigneesChanging(workItem);
      Set<AtsUser> newAssignees = new HashSet<>();
      newAssignees.addAll(workItem.getAssignees());
      newAssignees.addAll(assignees);
      setAssignees(workItem, newAssignees);
   }

   @Override
   public void setAssignee(IAtsWorkItem workItem, AtsUser assignee) {
      assigneesChanging(workItem);
      setAssignees(workItem, Arrays.asList(assignee));
   }

   private void assigneesChanging(IAtsWorkItem workItem) {
      if (initialAssignees.getValues(workItem) == null) {
         initialAssignees.put(workItem, workItem.getAssignees());
      }
   }

   protected void addAssigneeNotificationEvents() {
      for (Entry<IAtsWorkItem, List<AtsUser>> entry : initialAssignees.entrySet()) {
         IAtsWorkItem workItem = entry.getKey();
         List<AtsUser> initialAssignees = entry.getValue();
         List<AtsUser> assigneesAdded = org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(
            workItem.getAssignees(), initialAssignees);
         if (!assigneesAdded.isEmpty()) {
            AtsWorkItemNotificationEvent notificationEvent = AtsNotificationEventFactory.getWorkItemNotificationEvent(
               asUser, workItem, assigneesAdded, AtsNotifyType.Assigned);
            addWorkItemNotificationEvent(notificationEvent);
         }
      }
   }

   public boolean isExecuted() {
      return executed;
   }

   public void setExecuted(boolean executed) {
      this.executed = executed;
   }

   @Override
   public void setCreatedBy(IAtsWorkItem workItem, AtsUser user, boolean logChange, Date date) {
      if (logChange) {
         logCreatedByChange(workItem, user);
      }
      atsApi.getAttributeResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CreatedBy, user.getUserId());
      if (date != null) {
         atsApi.getAttributeResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CreatedDate, date);
      }
   }

   private void logCreatedByChange(IAtsWorkItem workItem, AtsUser user) {
      if (atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.CreatedBy, null) == null) {
         workItem.getLog().addLog(LogType.Originated, "", "", new Date(), user.getUserId());
      } else {
         workItem.getLog().addLog(LogType.Originated, "",
            "Changed by " + atsApi.getUserService().getCurrentUser().getName(), new Date(), user.getUserId());
      }
   }

   @Override
   public void initalizeWorkflow(IAtsWorkItem workItem, IStateToken startState, Collection<AtsUser> assignees) {
      setSoleAttributeValue(workItem, AtsAttributeTypes.CurrentStateType, startState.getStateType().name());
      setSoleAttributeValue(workItem, AtsAttributeTypes.CurrentStateName, startState.getName());
      setAssignees(workItem, assignees);

      // Update StateManager for backwards compatibility
      atsApi.getWorkItemService().getStateMgr(workItem).createOrUpdateState(startState.getName(), assignees);
      atsApi.getWorkItemService().getStateMgr(workItem).setCurrentState(startState.getName());
   }

   @Override
   public void updateForTransition(IAtsWorkItem workItem, IStateToken toState, Collection<AtsUser> toStateAssigees) {
      setSoleAttributeValue(workItem, AtsAttributeTypes.CurrentStateName, toState.getName());
      if (toState.isCompletedOrCancelled()) {
         clearAssignees(workItem);
         toStateAssigees.clear();
      } else {
         setAssignees(workItem, toStateAssigees);
      }
      setSoleAttributeValue(workItem, AtsAttributeTypes.CurrentStateType, toState.getStateType().name());

      // Update StateManager for backwards compatibility
      atsApi.getWorkItemService().getStateMgr(workItem).createOrUpdateState(toState.getName(), toStateAssigees);
      atsApi.getWorkItemService().getStateMgr(workItem).setCurrentState(toState.getName());
   }

   /////////////////////////////////////////////
   ////////////// EXECUTE //////////////////////
   /////////////////////////////////////////////

   @Override
   public TransactionToken execute() {
      try {
         executePreCheck();
         executeAtsObjects();

         internalExecuteTransaction();

         if (transactionTok.isValid()) {
            executeNotifyListeners();
            executeSendNotifications();
            executeClearCaches();
            executeUpdateAnySequences();
         }

         executed = true;
      } catch (Exception ex) {
         executeHandleException(ex);
      }
      return transactionTok;
   }

   protected void executeHandleException(Exception ex) {
      throw OseeCoreException.wrap(ex);
   }

   /**
    * Execute transaction and set transactionTok
    */
   protected abstract void internalExecuteTransaction();

   // First
   protected void executePreCheck() {
      checkExecuted();
      Conditions.checkNotNull(comment, "comment");
      if (isEmpty() && execptionIfEmpty) {
         throw new OseeArgumentException("objects/deleteObjects cannot be empty");
      }
   }

   // Second
   protected void executeAtsObjects() {
      // First, create or update any artifacts that changed
      for (IAtsObject atsObject : new ArrayList<>(atsObjects)) {
         if (atsObject instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) atsObject;

            // Update StateManager for backwards compatibility
            IAtsStateManager stateMgr = atsApi.getWorkItemService().getStateMgr(workItem);
            Conditions.assertNotNull(stateMgr, "StateManager");
            stateMgr.writeToStore(this);

            if (workItem.getLog().isDirty()) {
               atsApi.getLogFactory().writeToStore(workItem, atsApi.getAttributeResolver(), this);
            }
         }
      }
   }

   protected void executeNotifyListeners() {
      for (IExecuteListener listener : listeners) {
         listener.changesStored(this);
      }
   }

   protected void executeSendNotifications() {
      addAssigneeNotificationEvents();
      atsApi.getNotificationService().sendNotifications(notifications);
   }

   protected void executeClearCaches() {
      for (IAtsObject atsObject : new ArrayList<>(atsObjects)) {
         if (atsObject instanceof IAtsWorkItem) {
            atsApi.getWorkDefinitionService().internalClearWorkDefinition((IAtsWorkItem) atsObject);
            atsApi.getWorkItemService().internalClearStateManager((IAtsWorkItem) atsObject);
            atsApi.getStoreService().clearCaches((IAtsWorkItem) atsObject);
         }
      }
   }

   @Override
   public TransactionToken executeIfNeeded() {
      execptionIfEmpty = false;
      TransactionToken tx = execute();
      executed = true;
      return tx;
   }

   public void executeUpdateAnySequences() {
      for (Entry<String, String> entry : seqNameToStartNum.entrySet()) {
         String query = String.format("INSERT INTO osee_sequence (last_sequence, sequence_name) VALUES (%s, '%s')",
            entry.getValue(), entry.getKey());
         atsApi.getQueryService().runUpdate(query);
      }
   }

}
