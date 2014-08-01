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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsChangeSet {

   void add(Object obj) throws OseeCoreException;

   Collection<Object> getObjects();

   void execute() throws OseeCoreException;

   void clear();

   void addExecuteListener(IExecuteListener listener);

   void addToDelete(Object obj) throws OseeCoreException;

   void addAll(Object... objects) throws OseeCoreException;

   boolean isEmpty();

   void deleteSoleAttribute(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException;

   void setSoleAttributeValue(IAtsWorkItem workItem, IAttributeType attributeType, String value) throws OseeCoreException;

   void setSoleAttributeValue(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException;

   void addAttribute(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException;

   <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, IAttributeType attributeType, T value) throws OseeCoreException;

   <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) throws OseeCoreException;

   void deleteAttribute(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException;

   boolean isAttributeTypeValid(IAtsWorkItem workItem, IAttributeType attributeType);

   Object createArtifact(IArtifactType artifactType, String name);

   void deleteAttributes(IAtsObject atsObject, IAttributeType attributeType);

   Object createArtifact(IArtifactType artifactType, String name, String guid);

   void relate(Object object1, IRelationTypeSide relationSide, Object object2);

   AtsNotificationCollector getNotifications();

}
