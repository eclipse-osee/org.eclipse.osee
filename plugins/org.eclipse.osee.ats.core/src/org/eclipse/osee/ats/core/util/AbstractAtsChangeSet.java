/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.notify.AtsWorkItemNotificationEvent;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsRelationChange.RelationOperation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
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
   protected final IAtsUser asUser;
   protected final AtsNotificationCollector notifications = new AtsNotificationCollector();
   protected final List<IAtsWorkItem> workItemsCreated = new ArrayList<>();
   protected boolean execptionIfEmpty = true;
   protected BranchId branch;

   public AbstractAtsChangeSet(String comment, BranchId branch, IAtsUser asUser) {
      this.comment = comment;
      this.branch = branch;
      this.asUser = asUser;
      Conditions.checkNotNullOrEmpty(comment, "comment");
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(asUser, "user");
      Conditions.assertTrue(branch.isValid(), "%s is not a valid branch", branch);
   }

   @Override
   public void add(Object obj) {
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
      } else if (obj instanceof ArtifactToken) {
         ArtifactToken artTok = (ArtifactToken) obj;
         if (!artTok.getBranch().equals(branch)) {
            throw new OseeArgumentException("Can't add %s from branch %s to conflicting branch %s in same transaction",
               artTok.toStringWithId(), artTok.getBranchIdString(), branch.getIdString());
         }
         artifacts.add((ArtifactToken) obj);
      } else if (obj instanceof ArtifactId) {
         artifacts.add((ArtifactId) obj);
      } else if (obj instanceof AtsRelationChange) {
         relations.add((AtsRelationChange) obj);
      } else {
         throw new OseeArgumentException("Object not supported: " + obj);
      }
   }

   @Override
   public void addAll(Object... objects) {
      Conditions.checkNotNull(objects, "objects");
      for (Object obj : objects) {
         add(obj);
      }
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   @Override
   public void clear() {
      relations.clear();
      atsObjects.clear();
      artifacts.clear();
      deleteArtifacts.clear();
      deleteAtsObjects.clear();
      listeners.clear();
   }

   @Override
   public void reset(String comment) {
      clear();
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
      return createArtifact(token.getArtifactType(), token.getName(), token.getId());
   }

   @Override
   public void deleteArtifact(IAtsWorkItem task) {
      deleteArtifact(task.getStoreObject());
   }

   @Override
   public void addWorkflowCreated(IAtsTeamWorkflow teamWf) {
      workItemsCreated.add(teamWf);
   }

   @Override
   public TransactionId executeIfNeeded() {
      execptionIfEmpty = false;
      return execute();
   }

   @Override
   public IAtsUser getAsUser() {
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
      relate(parent, CoreRelationTypes.DefaultHierarchical_Child, child);
   }

   @Override
   public void setName(ArtifactToken artifact, String name) {
      setSoleAttributeValue(artifact, CoreAttributeTypes.Name, name);
   }

   @Override
   public void setName(IAtsObject atsObject, String name) {
      setSoleAttributeValue(atsObject, CoreAttributeTypes.Name, name);
   }

   @Override
   public void addWorkItemNotificationEvent(AtsWorkItemNotificationEvent workItemNotificationEvent) {
      notifications.getWorkItemNotificationEvents().add(workItemNotificationEvent);
   }

   @Override
   public void addNotificationEvent(AtsNotificationEvent notifyEvent) {
      notifications.getNotificationEvents().add(notifyEvent);
   }

   @Override
   public List<IAtsWorkItem> getWorkItemsCreated() {
      return workItemsCreated;
   }

   @Override
   public void addChild(IAtsObject parent, IAtsObject child) {
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
      ArtifactToken art = createArtifact(artifact);
      if (parent != null) {
         addChild(parent, art);
      }
      return art;
   }

   public BranchId getBranch() {
      return branch;
   }

}
