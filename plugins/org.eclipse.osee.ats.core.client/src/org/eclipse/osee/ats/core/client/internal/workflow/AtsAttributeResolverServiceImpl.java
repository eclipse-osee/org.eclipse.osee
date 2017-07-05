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
package org.eclipse.osee.ats.core.client.internal.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.Assert;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.workflow.AbstractAtsAttributeResolverService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.AttributeTypeToXWidgetName;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeResolverServiceImpl extends AbstractAtsAttributeResolverService {

   @Override
   public boolean isAttributeNamed(String attributeName) throws OseeCoreException {
      return AttributeTypeManager.typeExists(attributeName);
   }

   @Override
   public String getUnqualifiedName(String attributeName) {
      return getAttributeType(attributeName).getUnqualifiedName();
   }

   @Override
   public void setXWidgetNameBasedOnAttributeName(String attributeName, IAtsWidgetDefinition widgetDef) {
      try {
         if (!Strings.isValid(widgetDef.getXWidgetName())) {
            widgetDef.setXWidgetName(AttributeTypeToXWidgetName.getXWidgetName(getAttributeType(attributeName)));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public String getDescription(String attributeName) {
      return getAttributeType(attributeName).getDescription();
   }

   @Override
   public AttributeTypeToken getAttributeType(String attributeName) {
      try {
         return AttributeTypeManager.getType(attributeName);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public <T> T getSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, T defaultReturnValue) throws OseeCoreException {
      return AtsClientService.get().getArtifact(atsObject).getSoleAttributeValue(attributeType, defaultReturnValue);

   }

   @Override
   public Collection<String> getAttributesToStringList(IAtsObject atsObject, AttributeTypeId attributeType) throws OseeCoreException {
      return AtsClientService.get().getArtifact(atsObject).getAttributesToStringList(attributeType);
   }

   @Override
   public Collection<String> getAttributesToStringList(ArtifactId artifact, AttributeTypeId attributeType) {
      return ((Artifact) artifact).getAttributesToStringList(attributeType);
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, AttributeTypeId attributeType) throws OseeCoreException {
      return AtsClientService.get().getArtifact(workItem).isAttributeTypeValid(attributeType);
   }

   @Override
   public String getSoleAttributeValueAsString(IAtsObject atsObject, AttributeTypeId attributeType, String defaultValue) throws OseeCoreException {
      String result = defaultValue;
      Artifact artifact = AtsClientService.get().getArtifact(atsObject);
      if (artifact != null) {
         result = artifact.getSoleAttributeValueAsString(attributeType, defaultValue);
      }
      return result;
   }

   @Override
   public String getSoleAttributeValueAsString(ArtifactId artifact, AttributeTypeId attributeType, String defaultValue) {
      String result = defaultValue;
      Artifact art = AtsClientService.get().getArtifact(artifact);
      if (artifact != null) {
         result = art.getSoleAttributeValueAsString(attributeType, defaultValue);
      }
      return result;
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, Object value) throws OseeCoreException {
      AtsClientService.get().getArtifact(atsObject).setSoleAttributeValue(attributeType, value);
   }

   @Override
   public int getAttributeCount(IAtsWorkItem workItem, AttributeTypeId attributeType) throws OseeCoreException {
      return AtsClientService.get().getArtifact(workItem).getAttributeCount(attributeType);
   }

   @Override
   public int getAttributeCount(IAtsObject atsObject, AttributeTypeId attributeType) throws OseeCoreException {
      return AtsClientService.get().getArtifact(atsObject).getAttributeCount(attributeType);
   }

   @Override
   public int getAttributeCount(ArtifactId artifact, AttributeTypeId attributeType) throws OseeCoreException {
      return AtsClientService.get().getArtifact(artifact).getAttributeCount(attributeType);
   }

   @Override
   public void addAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, Object value) throws OseeCoreException {
      AtsClientService.get().getArtifact(workItem).addAttribute(attributeType, value);
   }

   @SuppressWarnings({"unchecked", "deprecation"})
   @Override
   public <T> Collection<IAttribute<T>> getAttributes(IAtsWorkItem workItem, AttributeTypeId attributeType) throws OseeCoreException {
      List<IAttribute<T>> attrs = new ArrayList<>();
      for (Attribute<Object> attr : AtsClientService.get().getArtifact(workItem).getAttributes(attributeType)) {
         attrs.add((IAttribute<T>) attr);
      }
      return attrs;
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType) throws OseeCoreException {
      AtsClientService.get().getArtifact(workItem).deleteSoleAttribute(attributeType);
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, AttributeTypeId attributeType, T value) throws OseeCoreException {
      @SuppressWarnings("unchecked")
      Attribute<T> attribute = (Attribute<T>) attr;
      attribute.setValue(value);
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) throws OseeCoreException {
      Artifact artifact = AtsClientService.get().getArtifact(workItem);
      Attribute<?> attribute = (Attribute<?>) attr;
      Attribute<?> attributeById = artifact.getAttributeById(attribute.getId(), false);
      attributeById.delete();
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, IAtsChangeSet changes) throws OseeCoreException {
      if (changes != null) {
         changes.deleteSoleAttribute(workItem, attributeType);
      } else {
         deleteSoleAttribute(workItem, attributeType);
      }
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, Object value, IAtsChangeSet changes) throws OseeCoreException {
      if (changes != null) {
         changes.setSoleAttributeValue(atsObject, attributeType, value);
      } else {
         setSoleAttributeValue(atsObject, attributeType, value);
      }
   }

   @Override
   public void addAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, Object value, IAtsChangeSet changes) throws OseeCoreException {
      if (changes != null) {
         changes.addAttribute(workItem, attributeType, value);
      } else {
         AtsClientService.get().getArtifact(workItem).addAttribute(attributeType, value);
      }
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, Object value, IAtsChangeSet changes) throws OseeCoreException {
      if (changes != null) {
         changes.deleteAttribute(workItem, attributeType, value);
      } else {
         AtsClientService.get().getArtifact(workItem).deleteAttribute(attributeType, value);
      }
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, AttributeTypeId attributeType, T value, IAtsChangeSet changes) throws OseeCoreException {
      if (changes != null) {
         changes.setValue(workItem, attr, attributeType, value);
      } else {
         @SuppressWarnings("unchecked")
         Attribute<T> attribute = (Attribute<T>) attr;
         attribute.setValue(value);
      }
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr, IAtsChangeSet changes) throws OseeCoreException {
      if (changes != null) {
         changes.deleteAttribute(workItem, attr);
      } else {
         Artifact artifact = AtsClientService.get().getArtifact(workItem);
         Attribute<?> attribute = (Attribute<?>) attr;
         Attribute<?> attributeById = artifact.getAttributeById(attribute.getId(), false);
         attributeById.delete();
      }
   }

   @Override
   public <T> T getSoleAttributeValue(ArtifactId artifact, AttributeTypeId attributeType, T defaultValue) {
      return AtsClientService.get().getArtifact(artifact).getSoleAttributeValue(attributeType, defaultValue);
   }

   @Override
   public <T> Collection<T> getAttributeValues(ArtifactId artifact, AttributeTypeId attributeType) {
      Assert.isNotNull(artifact, "Artifact can not be null");
      Assert.isNotNull(attributeType, "Attribute Type can not be null");
      return AtsClientService.get().getArtifact(artifact).getAttributeValues(attributeType);
   }

   @Override
   public Collection<Object> getAttributeValues(IAtsObject atsObject, AttributeTypeId attributeType) {
      Assert.isNotNull(atsObject, "ATS Object can not be null");
      Assert.isNotNull(attributeType, "Attribute Type can not be null");
      return getAttributeValues(atsObject.getStoreObject(), attributeType);
   }

   @SuppressWarnings({"unchecked", "deprecation"})
   @Override
   public <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact, AttributeTypeId attributeType) throws OseeCoreException {
      Assert.isNotNull(artifact, "Artifact can not be null");
      Assert.isNotNull(attributeType, "Attribute Type can not be null");
      List<IAttribute<T>> attributes = new LinkedList<>();
      for (Attribute<Object> attr : AtsClientService.get().getArtifact(artifact).getAttributes(attributeType)) {
         attributes.add((IAttribute<T>) attr);
      }
      return attributes;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact) {
      List<IAttribute<T>> attributes = new LinkedList<>();
      for (Attribute<?> attr : AtsClientService.get().getArtifact(artifact).getAttributes()) {
         attributes.add((IAttribute<T>) attr);
      }
      return attributes;
   }

   @Override
   public <T> Collection<IAttribute<T>> getAttributes(IAtsWorkItem workItem) throws OseeCoreException {
      return getAttributes(workItem.getStoreObject());
   }

}
