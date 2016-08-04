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
package org.eclipse.osee.ats.api.util;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsChangeSet {

   void add(Object obj) throws OseeCoreException;

   /**
    * Store changed items.
    *
    * @throws OseeCoreException if no items exist to store. Use executeIfNeeded to execute quietly.
    */
   TransactionId execute() throws OseeCoreException;

   void clear();

   void addExecuteListener(IExecuteListener listener);

   void addToDelete(Object obj) throws OseeCoreException;

   void addAll(Object... objects) throws OseeCoreException;

   boolean isEmpty();

   void deleteSoleAttribute(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException;

   void setSoleAttributeValue(IAtsWorkItem workItem, IAttributeType attributeType, String value) throws OseeCoreException;

   void setSoleAttributeValue(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException;

   void setSoleAttributeValue(ArtifactId artifact, IAttributeType attributeType, String value);

   void addAttribute(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException;

   <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, IAttributeType attributeType, T value) throws OseeCoreException;

   <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) throws OseeCoreException;

   void deleteAttribute(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException;

   boolean isAttributeTypeValid(IAtsWorkItem workItem, IAttributeType attributeType);

   ArtifactId createArtifact(IArtifactType artifactType, String name);

   void deleteAttributes(IAtsObject atsObject, IAttributeType attributeType);

   ArtifactId createArtifact(IArtifactType artifactType, String name, String guid);

   ArtifactId createArtifact(IArtifactType artifactType, String name, String guid, Long uuid);

   void relate(Object object1, IRelationTypeSide relationSide, Object object2);

   AtsNotificationCollector getNotifications();

   void unrelateAll(Object object, IRelationTypeSide relationType);

   void setRelation(Object object1, IRelationTypeSide relationType, Object object2);

   public void setRelations(Object object, IRelationTypeSide relationSide, Collection<? extends Object> objects);

   <T> void setAttribute(IAtsWorkItem workItem, int attributeId, T value);

   ArtifactId createArtifact(IArtifactToken token);

   void deleteArtifact(ArtifactId artifact);

   void deleteAttribute(ArtifactId artifact, IAttribute<?> attr);

   void addWorkflowCreated(IAtsTeamWorkflow teamWf);

   void deleteArtifact(IAtsWorkItem workItem);

   void setValues(IAtsObject atsObject, IAttributeType attrType, List<String> values);

   String getComment();

   <T> void setAttribute(ArtifactId artifact, int attrId, T value);

   /**
    * Will check if anything is to be stored, else return quietly.
    */
   void executeIfNeeded();

   /**
    * User making these changes
    */
   IAtsUser getAsUser();

   void unrelate(ArtifactId artifact, IRelationTypeSide relationSide, ArtifactId artifact2);

   void unrelate(IAtsObject atsObject, IRelationTypeSide relationSide, IAtsObject atsObjec2);

   void unrelate(ArtifactId artifact, IRelationTypeSide relationSide, IAtsObject atsObject);

   void unrelate(IAtsObject atsObject, IRelationTypeSide relationSide, ArtifactId artifact);

   void addAttribute(ArtifactId artifactId, IAttributeType attrType, Object value);

   void setSoleAttributeFromString(ArtifactId artifact, IAttributeType attrType, String value);

   void setSoleAttributeFromString(IAtsObject atsObject, IAttributeType attributeType, String value);

}
