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

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.notify.AtsWorkItemNotificationEvent;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author Donald G. Dunne
 */
public interface IAtsChangeSet {

   void add(Object obj);

   /**
    * Store changed items. if no items exist to store. Use executeIfNeeded to execute quietly.
    */
   TransactionId execute();

   void clear();

   void addExecuteListener(IExecuteListener listener);

   void addToDelete(Object obj);

   void addAll(Object... objects);

   boolean isEmpty();

   void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType);

   void setSoleAttributeValue(IAtsWorkItem workItem, AttributeTypeToken attributeType, String value);

   void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeToken attributeType, Object value);

   void setSoleAttributeValue(ArtifactId artifact, AttributeTypeToken attributeType, Object value);

   void addAttribute(IAtsObject atsObject, AttributeTypeToken attributeType, Object value);

   <T> void setValue(IAtsWorkItem workItem, IAttribute<T> attr, AttributeTypeId attributeType, T value);

   <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr);

   void deleteAttribute(IAtsObject atsObject, AttributeTypeId attributeType, Object value);

   boolean isAttributeTypeValid(IAtsWorkItem workItem, AttributeTypeId attributeType);

   ArtifactToken createArtifact(IArtifactType artifactType, String name);

   void deleteAttributes(IAtsObject atsObject, AttributeTypeId attributeType);

   ArtifactToken createArtifact(IArtifactType artifactType, String name, Long artifactId);

   void relate(Object object1, RelationTypeSide relationSide, Object object2);

   AtsNotificationCollector getNotifications();

   void addWorkItemNotificationEvent(AtsWorkItemNotificationEvent workItemNotificationEvent);

   void addNotificationEvent(AtsNotificationEvent notifyEvent);

   void unrelateAll(Object object, RelationTypeSide relationType);

   void setRelation(Object object1, RelationTypeSide relationType, Object object2);

   /**
    * Set objects as the current related. Objects related and not in obects parameter will be unrelated. Missing objects
    * will be related. No order will be set.
    */
   void setRelations(Object object, RelationTypeSide relationSide, Collection<? extends Object> objects);

   /**
    * Set objects as the current related and set the order based on the order in the given list. Any objects already
    * related that are not in the provided artifacts list will be un-related.
    */
   void setRelationsAndOrder(Object object, RelationTypeSide relationSide, Collection<? extends Object> objects);

   <T> void setAttribute(IAtsWorkItem workItem, AttributeId attributeId, T value);

   ArtifactId createArtifact(ArtifactToken token);

   void deleteArtifact(ArtifactId artifact);

   void deleteAttribute(ArtifactId artifact, IAttribute<?> attr);

   void addWorkflowCreated(IAtsTeamWorkflow teamWf);

   void deleteArtifact(IAtsWorkItem workItem);

   void setAttributeValues(IAtsObject atsObject, AttributeTypeToken attrType, List<Object> values);

   void setAttributeValues(ArtifactId artifact, AttributeTypeToken attrType, List<Object> values);

   String getComment();

   <T> void setAttribute(ArtifactId artifact, AttributeId attrId, T value);

   /**
    * Will check if anything is to be stored, else return quietly.
    */
   TransactionId executeIfNeeded();

   /**
    * User making these changes
    */
   IAtsUser getAsUser();

   void unrelate(ArtifactId artifact, RelationTypeSide relationSide, ArtifactId artifact2);

   void unrelate(IAtsObject atsObject, RelationTypeSide relationSide, IAtsObject atsObjec2);

   void unrelate(ArtifactId artifact, RelationTypeSide relationSide, IAtsObject atsObject);

   void unrelate(IAtsObject atsObject, RelationTypeSide relationSide, ArtifactId artifact);

   void addAttribute(ArtifactId artifactId, AttributeTypeToken attrType, Object value);

   void setSoleAttributeFromString(ArtifactId artifact, AttributeTypeToken attrType, String value);

   void setSoleAttributeFromString(IAtsObject atsObject, AttributeTypeToken attributeType, String value);

   void setSoleAttributeFromStream(ArtifactId artifact, AttributeTypeToken attributeType, InputStream inputStream);

   void reset(String string);

   void addChild(ArtifactId parent, ArtifactId child);

   void unrelateFromAll(RelationTypeSide relationSide, ArtifactId artifact);

   void setName(ArtifactToken artifact, String name);

   void setName(IAtsObject atsObject, String name);

   List<IAtsWorkItem> getWorkItemsCreated();

   void deleteAttributes(ArtifactId artifact, AttributeTypeToken attributeType);

   /**
    * This can be removed when both the client and server accept ArtifactId as a value. In 25.0, client accepts
    * ArtifactId while server expects String.
    */
   void addArtifactReferencedAttribute(ArtifactId artifact, AttributeTypeToken attributeType, ArtifactId artifactRef);

   void setAttributeValuesAsStrings(IAtsObject atsObject, AttributeTypeToken attrType, List<String> values);

   void addChild(IAtsObject parent, IAtsObject child);

}