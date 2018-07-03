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
package org.eclipse.osee.ats.rest.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.core.workflow.AbstractAtsAttributeResolverService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeResolverServiceImpl extends AbstractAtsAttributeResolverService {

   private OrcsApi orcsApi;
   private Log logger;
   private AtsApi atsApi;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   private ArtifactReadable getArtifact(IAtsObject atsObject) {
      return (ArtifactReadable) atsApi.getQueryService().getArtifact(atsObject);
   }

   private ArtifactReadable getArtifact(ArtifactId artifact) {
      return (ArtifactReadable) atsApi.getQueryService().getArtifact(artifact);
   }

   public void start() {
      Conditions.checkNotNull(orcsApi, "OrcsApi");
      logger.info("AtsAttributeResolverServiceImpl started");
   }

   public void stop() {
      //
   }

   @Override
   public boolean isAttributeNamed(String attributeName) {
      return getAttributeType(attributeName) != null;
   }

   @Override
   public String getUnqualifiedName(String attributeName) {
      return getAttributeType(attributeName).getUnqualifiedName();
   }

   @Override
   public void setXWidgetNameBasedOnAttributeName(String attributeName, IAtsWidgetDefinition widgetDef) {
      try {
         if (!Strings.isValid(widgetDef.getXWidgetName())) {
            widgetDef.setXWidgetName(
               AttributeTypeToXWidgetName.getXWidgetName(orcsApi, getAttributeType(attributeName)));
         }
      } catch (OseeCoreException ex) {
         logger.error(ex, "Error setXWidgetNameBasedOnAttributeName - attributeName [%s] widgetDef[%s]", attributeName,
            widgetDef);
      }
   }

   @Override
   public String getDescription(String attributeName) {
      return getAttributeType(attributeName).getDescription();
   }

   @Override
   public AttributeTypeToken getAttributeType(String attributeName) {
      AttributeTypeToken attrType = null;
      try {
         for (AttributeTypeToken type : orcsApi.getOrcsTypes().getAttributeTypes().getAll()) {
            if (type.getName().equals(attributeName)) {
               attrType = type;
            }
         }
      } catch (OseeCoreException ex) {
         logger.error(ex, "Error getting attribute type with name [%s]", attributeName);
      }
      return attrType;
   }

   @Override
   public <T> T getSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, T defaultReturnValue) {
      return getArtifact(atsObject).getSoleAttributeValue(attributeType, defaultReturnValue);

   }

   @Override
   public Collection<String> getAttributesToStringList(IAtsObject atsObject, AttributeTypeId attributeType) {
      return getArtifact(atsObject).getAttributeValues(attributeType);
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, AttributeTypeId attributeType) {
      return getArtifact(workItem).isAttributeTypeValid(attributeType);
   }

   @Override
   public String getSoleAttributeValueAsString(IAtsObject atsObject, AttributeTypeId attributeType, String defaultValue) {
      return getArtifact(atsObject).getSoleAttributeValue(attributeType, defaultValue);
   }

   @Override
   public String getSoleAttributeValueAsString(ArtifactId artifact, AttributeTypeId attributeType, String defaultValue) {
      return getArtifact(artifact).getSoleAttributeValue(attributeType, defaultValue);
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, Object value) {
      // Sets on Server need to be through transaction
      throw new OseeStateException(
         "Invalid: Must use setSoleAttributeValue(IAtsWorkItem workItem, AttributeTypeId attributeType, Object value, IAtsChangeSet changes)");
   }

   @Override
   public int getAttributeCount(IAtsWorkItem workItem, AttributeTypeId attributeType) {
      return getArtifact(workItem).getAttributeCount(attributeType);
   }

   @Override
   public int getAttributeCount(IAtsObject atsObject, AttributeTypeId attributeType) {
      return getArtifact(atsObject).getAttributeCount(attributeType);
   }

   @Override
   public int getAttributeCount(ArtifactId artifact, AttributeTypeId attributeType) {
      return getArtifact(artifact).getAttributeCount(attributeType);
   }

   @Override
   public void addAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, Object value) {
      // Sets on Server need to be through transaction
      throw new OseeStateException("Not Implemented");
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> Collection<IAttribute<T>> getAttributes(IAtsWorkItem workItem, AttributeTypeId attributeType) {
      Collection<IAttribute<T>> attrs = new ArrayList<>();
      for (AttributeReadable<Object> attr : getArtifact(workItem).getAttributes(attributeType)) {
         attrs.add((IAttribute<T>) attr);
      }
      return attrs;
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType) {
      // Sets on Server need to be through transaction
      throw new OseeStateException(
         "Invalid: Must use deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, IAtsChangeSet changes)");
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, IAtsChangeSet changes) {
      changes.deleteSoleAttribute(workItem, attributeType);
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, Object value, IAtsChangeSet changes) {
      changes.setSoleAttributeValue(atsObject, attributeType, value);
   }

   @Override
   public void addAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, Object value, IAtsChangeSet changes) {
      changes.addAttribute(workItem, attributeType, value);
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, Object value, IAtsChangeSet changes) {
      changes.deleteAttribute(workItem, attributeType, value);
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<T> attr, AttributeTypeId attributeType, T value, IAtsChangeSet changes) {
      changes.setValue(workItem, attr, attributeType, value);
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr, IAtsChangeSet changes) {
      changes.deleteAttribute(workItem, attr);
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) { // Sets on Server need to be through transaction
      throw new OseeStateException(
         "Invalid: Must use deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, IAtsChangeSet changes)");
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, AttributeTypeId attributeType, T value) {
      // Sets on Server need to be through transaction
      throw new OseeStateException(
         "Invalid: Must use deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, IAtsChangeSet changes)");
   }

   @Override
   public <T> T getSoleAttributeValue(ArtifactId artifact, AttributeTypeId attributeType, T defaultValue) {
      return getArtifact(artifact).getSoleAttributeValue(attributeType, defaultValue);
   }

   @Override
   public <T> Collection<T> getAttributeValues(ArtifactId artifact, AttributeTypeId attributeType) {
      return getArtifact(artifact).getAttributeValues(attributeType);
   }

   @Override
   public <T> Collection<T> getAttributeValues(IAtsObject atsObject, AttributeTypeId attributeType) {
      return getAttributeValues(atsObject.getStoreObject(), attributeType);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact, AttributeTypeId attributeType) {
      Assert.isNotNull(artifact, "Artifact can not be null");
      Assert.isNotNull(attributeType, "Attribute Type can not be null");
      List<IAttribute<T>> attributes = new LinkedList<>();
      for (AttributeReadable<Object> attr : ((ArtifactReadable) artifact).getAttributes(attributeType)) {
         attributes.add((IAttribute<T>) attr);
      }
      return attributes;
   }

   @Override
   public Collection<String> getAttributesToStringList(ArtifactId artifact, AttributeTypeId attributeType) {
      return ((ArtifactReadable) artifact).getAttributeValues(attributeType);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact) {
      List<IAttribute<T>> attributes = new LinkedList<>();
      for (AttributeReadable<Object> attr : getArtifact(artifact).getAttributes()) {
         attributes.add((IAttribute<T>) attr);
      }
      return attributes;
   }

   @Override
   public <T> Collection<IAttribute<T>> getAttributes(IAtsWorkItem workItem) {
      return getAttributes(workItem.getStoreObject());
   }

   public void setServices(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

}
