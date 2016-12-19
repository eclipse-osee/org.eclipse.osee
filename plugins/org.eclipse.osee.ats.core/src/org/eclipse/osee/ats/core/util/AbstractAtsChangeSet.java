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
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsChangeSet implements IAtsChangeSet {

   protected String comment;
   protected final Set<Object> objects = new CopyOnWriteArraySet<>();
   protected final Set<Object> deleteObjects = new CopyOnWriteArraySet<>();
   protected final Set<IExecuteListener> listeners = new CopyOnWriteArraySet<>();
   protected final IAtsUser asUser;
   protected final AtsNotificationCollector notifications = new AtsNotificationCollector();
   protected final List<IAtsWorkItem> workItemsCreated = new ArrayList<>();
   protected boolean execptionIfEmpty = true;

   public AbstractAtsChangeSet(String comment, IAtsUser asUser) {
      this.comment = comment;
      this.asUser = asUser;
      Conditions.checkNotNullOrEmpty(comment, "comment");
      Conditions.checkNotNull(asUser, "user");
   }

   @Override
   public void add(Object obj) throws OseeCoreException {
      Conditions.checkNotNull(obj, "object");
      if (obj instanceof Collection) {
         objects.addAll((Collection<?>) obj);
      } else {
         objects.add(obj);
      }
   }

   @Override
   public void addAll(Object... objects) throws OseeCoreException {
      Conditions.checkNotNull(objects, "objects");
      for (Object obj : objects) {
         add(obj);
      }
   }

   @Override
   public Set<Object> getObjects() {
      return objects;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   @Override
   public void clear() {
      objects.clear();
      deleteObjects.clear();
      listeners.clear();
   }

   public void reset(String comment) {
      clear();
      this.comment = comment;
   }

   @Override
   public boolean isEmpty() {
      return objects.isEmpty() && deleteObjects.isEmpty();
   }

   @Override
   public void addExecuteListener(IExecuteListener listener) {
      Conditions.checkNotNull(listener, "listener");
      listeners.add(listener);
   }

   @Override
   public void addToDelete(Object obj) throws OseeCoreException {
      Conditions.checkNotNull(obj, "object");
      if (obj instanceof Collection) {
         deleteObjects.addAll((Collection<?>) obj);
      } else {
         deleteObjects.add(obj);
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
   public ArtifactId createArtifact(IArtifactToken token) {
      return createArtifact(token.getArtifactType(), token.getName(), token.getGuid(), token.getUuid());
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
   public void executeIfNeeded() {
      execptionIfEmpty = false;
      execute();
   }

   @Override
   public IAtsUser getAsUser() {
      return asUser;
   }

   @Override
   public void unrelate(IAtsObject atsObject, IRelationTypeSide relationSide, IAtsObject atsObjec2) {
      unrelate(atsObject.getStoreObject(), relationSide, atsObjec2.getStoreObject());
   }

   @Override
   public void unrelate(ArtifactId artifact, IRelationTypeSide relationSide, IAtsObject atsObject) {
      unrelate(artifact, relationSide, atsObject.getStoreObject());
   }

   @Override
   public void unrelate(IAtsObject atsObject, IRelationTypeSide relationSide, ArtifactId artifact) {
      unrelate(atsObject.getStoreObject(), relationSide, artifact);
   }

   @Override
   public void setSoleAttributeFromString(IAtsObject atsObject, IAttributeType attributeType, String value) {
      setSoleAttributeFromString(atsObject.getStoreObject(), attributeType, value);
   }

   @Override
   public void setRelation(Object object1, IRelationTypeSide relationSide, Object object2) {
      setRelations(object1, relationSide, Collections.singleton(object2));
   }

}
