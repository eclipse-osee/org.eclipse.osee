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
package org.eclipse.osee.ats.workflow.internal;

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
import org.eclipse.osee.ats.core.workflow.AbstractAtsAttributeResolverService;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
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
   public boolean isAttributeNamed(String attributeName) {
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
   public <T> T getSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, T defaultReturnValue) {
      return getArtifact(atsObject).getSoleAttributeValue(attributeType, defaultReturnValue);

   }

   @Override
   public Collection<String> getAttributesToStringList(IAtsObject atsObject, AttributeTypeId attributeType) {
      return getArtifact(atsObject).getAttributesToStringList(attributeType);
   }

   @Override
   public Collection<String> getAttributesToStringList(ArtifactId artifact, AttributeTypeId attributeType) {
      return ((Artifact) artifact).getAttributesToStringList(attributeType);
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, AttributeTypeId attributeType) {
      return getArtifact(workItem.getStoreObject()).isAttributeTypeValid(attributeType);
   }

   @Override
   public String getSoleAttributeValueAsString(IAtsObject atsObject, AttributeTypeId attributeType, String defaultValue) {
      String result = defaultValue;
      Artifact artifact = getArtifact(atsObject.getArtifactId());
      if (artifact != null) {
         result = artifact.getSoleAttributeValueAsString(attributeType, defaultValue);
      }
      return result;
   }

   @Override
   public String getSoleAttributeValueAsString(ArtifactId artifact, AttributeTypeId attributeType, String defaultValue) {
      String result = defaultValue;
      Artifact art = (Artifact) AtsClientService.get().getQueryService().getArtifact(artifact);
      if (art != null) {
         result = art.getSoleAttributeValueAsString(attributeType, defaultValue);
      }
      return result;
   }

   private Artifact getArtifact(IAtsObject atsObject) {
      return getArtifact(atsObject.getStoreObject());
   }

   private Artifact getArtifact(ArtifactId artifact) {
      if (artifact instanceof Artifact) {
         return (Artifact) artifact;
      }
      ArtifactId art = AtsClientService.get().getQueryService().getArtifact(artifact);
      if (art instanceof Artifact) {
         return (Artifact) art;
      }
      return null;
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, Object value) {
      getArtifact(atsObject).setSoleAttributeValue(attributeType, value);
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
      getArtifact(workItem).addAttribute(attributeType, value);
   }

   @SuppressWarnings({"unchecked", "deprecation"})
   @Override
   public <T> Collection<IAttribute<T>> getAttributes(IAtsWorkItem workItem, AttributeTypeId attributeType) {
      List<IAttribute<T>> attrs = new ArrayList<>();
      for (Attribute<Object> attr : getArtifact(workItem).getAttributes(attributeType)) {
         attrs.add((IAttribute<T>) attr);
      }
      return attrs;
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType) {
      getArtifact(workItem).deleteSoleAttribute(attributeType);
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, AttributeTypeId attributeType, T value) {
      @SuppressWarnings("unchecked")
      Attribute<T> attribute = (Attribute<T>) attr;
      attribute.setValue(value);
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) {
      Artifact artifact = getArtifact(workItem);
      Attribute<?> attribute = (Attribute<?>) attr;
      Attribute<?> attributeById = artifact.getAttributeById(attribute.getId(), false);
      attributeById.delete();
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, IAtsChangeSet changes) {
      if (changes != null) {
         changes.deleteSoleAttribute(workItem, attributeType);
      } else {
         deleteSoleAttribute(workItem, attributeType);
      }
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, Object value, IAtsChangeSet changes) {
      if (changes != null) {
         changes.setSoleAttributeValue(atsObject, attributeType, value);
      } else {
         setSoleAttributeValue(atsObject, attributeType, value);
      }
   }

   @Override
   public void addAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, Object value, IAtsChangeSet changes) {
      if (changes != null) {
         changes.addAttribute(workItem, attributeType, value);
      } else {
         getArtifact(workItem).addAttribute(attributeType, value);
      }
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, Object value, IAtsChangeSet changes) {
      if (changes != null) {
         changes.deleteAttribute(workItem, attributeType, value);
      } else {
         getArtifact(workItem).deleteAttribute(attributeType, value);
      }
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<T> attr, AttributeTypeId attributeType, T value, IAtsChangeSet changes) {
      if (changes != null) {
         changes.setValue(workItem, attr, attributeType, value);
      } else {
         Attribute<T> attribute = (Attribute<T>) attr;
         attribute.setValue(value);
      }
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr, IAtsChangeSet changes) {
      if (changes != null) {
         changes.deleteAttribute(workItem, attr);
      } else {
         Artifact artifact = getArtifact(workItem);
         Attribute<?> attribute = (Attribute<?>) attr;
         Attribute<?> attributeById = artifact.getAttributeById(attribute.getId(), false);
         attributeById.delete();
      }
   }

   @Override
   public <T> T getSoleAttributeValue(ArtifactId artifact, AttributeTypeId attributeType, T defaultValue) {
      return getArtifact(artifact).getSoleAttributeValue(attributeType, defaultValue);
   }

   @Override
   public <T> Collection<T> getAttributeValues(ArtifactId artifact, AttributeTypeId attributeType) {
      Assert.isNotNull(artifact, "Artifact can not be null");
      Assert.isNotNull(attributeType, "Attribute Type can not be null");
      return getArtifact(artifact).getAttributeValues(attributeType);
   }

   @Override
   public <T> Collection<T> getAttributeValues(IAtsObject atsObject, AttributeTypeId attributeType) {
      Assert.isNotNull(atsObject, "ATS Object can not be null");
      Assert.isNotNull(attributeType, "Attribute Type can not be null");
      return getAttributeValues(atsObject.getStoreObject(), attributeType);
   }

   @SuppressWarnings({"unchecked", "deprecation"})
   @Override
   public <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact, AttributeTypeId attributeType) {
      Assert.isNotNull(artifact, "Artifact can not be null");
      Assert.isNotNull(attributeType, "Attribute Type can not be null");
      List<IAttribute<T>> attributes = new LinkedList<>();
      for (Attribute<Object> attr : getArtifact(artifact).getAttributes(attributeType)) {
         attributes.add((IAttribute<T>) attr);
      }
      return attributes;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact) {
      List<IAttribute<T>> attributes = new LinkedList<>();
      for (Attribute<?> attr : getArtifact(artifact).getAttributes()) {
         attributes.add((IAttribute<T>) attr);
      }
      return attributes;
   }

   @Override
   public <T> Collection<IAttribute<T>> getAttributes(IAtsWorkItem workItem) {
      return getAttributes(workItem.getStoreObject());
   }

}
