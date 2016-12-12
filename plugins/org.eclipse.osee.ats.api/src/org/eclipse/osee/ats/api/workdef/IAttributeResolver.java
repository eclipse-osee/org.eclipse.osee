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
package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAttributeResolver {

   boolean isAttributeNamed(String attributeName) throws OseeCoreException;

   String getUnqualifiedName(String attributeName);

   void setXWidgetNameBasedOnAttributeName(String attributeName, IAtsWidgetDefinition widgetDef);

   String getDescription(String attributeName);

   <T> T getSoleAttributeValue(IAtsObject atsObject, IAttributeType attributeType, T defaultReturnValue) throws OseeCoreException;

   Collection<String> getAttributesToStringList(IAtsObject atsObject, AttributeTypeId attributeType) throws OseeCoreException;

   boolean isAttributeTypeValid(IAtsWorkItem workItem, AttributeTypeId attributeType) throws OseeCoreException;

   String getSoleAttributeValueAsString(IAtsObject atsObject, IAttributeType attributeType, String defaultReturnValue) throws OseeCoreException;

   int getAttributeCount(IAtsObject atsObject, IAttributeType attributeType) throws OseeCoreException;

   int getAttributeCount(ArtifactId artifact, IAttributeType attributeType) throws OseeCoreException;

   void addAttribute(IAtsWorkItem workItem, IAttributeType attributeType, Object value) throws OseeCoreException;

   <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact);

   <T> Collection<IAttribute<T>> getAttributes(IAtsWorkItem workItem) throws OseeCoreException;

   <T> Collection<IAttribute<T>> getAttributes(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException;

   <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact, IAttributeType attributeType) throws OseeCoreException;

   void deleteSoleAttribute(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException;

   <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) throws OseeCoreException;

   <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, IAttributeType attributeType, T value) throws OseeCoreException;

   void deleteSoleAttribute(IAtsWorkItem workItem, IAttributeType attributeType, IAtsChangeSet changes) throws OseeCoreException;

   void setSoleAttributeValue(IAtsObject atsObject, IAttributeType attributeType, Object value, IAtsChangeSet changes) throws OseeCoreException;

   void addAttribute(IAtsWorkItem workItem, IAttributeType attributeType, Object value, IAtsChangeSet changes) throws OseeCoreException;

   void deleteSoleAttribute(IAtsWorkItem workItem, IAttributeType attributeType, Object value, IAtsChangeSet changes) throws OseeCoreException;

   <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, IAttributeType attributeType, T value, IAtsChangeSet changes) throws OseeCoreException;

   <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr, IAtsChangeSet changes) throws OseeCoreException;

   IAttributeType getAttributeType(String atrributeName);

   void setSoleAttributeValue(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException;

   <T> T getSoleAttributeValue(ArtifactId artifact, IAttributeType attributeType, T defaultValue);

   <T> Collection<T> getAttributeValues(ArtifactId artifact, IAttributeType attributeType);

   Collection<Object> getAttributeValues(IAtsObject atsObject, IAttributeType attributeType);

   String getSoleAttributeValueAsString(ArtifactId artifact, IAttributeType worktype, String defaultReturnValue);

   int getAttributeCount(IAtsWorkItem workItem, IAttributeType attributeType);

   default public String getAttributesToStringUniqueList(IAtsObject atsObject, AttributeTypeId attributeType, String separator) {
      Set<String> strs = new HashSet<>();
      strs.addAll(getAttributesToStringList(atsObject, attributeType));
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString(separator, strs);
   }

   Collection<String> getAttributesToStringList(ArtifactId customizeStoreArt, IAttributeType xviewercustomization);

}
