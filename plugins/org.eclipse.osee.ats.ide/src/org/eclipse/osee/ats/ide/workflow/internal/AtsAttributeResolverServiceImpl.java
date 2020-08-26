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

package org.eclipse.osee.ats.ide.workflow.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeResolverServiceImpl implements IAttributeResolver {

   @Override
   public <T> T getSoleAttributeValue(IAtsObject atsObject, AttributeTypeToken attributeType, T defaultReturnValue) {
      return getArtifact(atsObject).getSoleAttributeValue(attributeType, defaultReturnValue);

   }

   @Override
   public List<String> getAttributesToStringList(IAtsObject atsObject, AttributeTypeToken attributeType) {
      return getArtifact(atsObject).getAttributesToStringList(attributeType);
   }

   @Override
   public List<String> getAttributesToStringList(ArtifactId artifact, AttributeTypeToken attributeType) {
      return AtsApiService.get().getQueryServiceIde().getArtifact(artifact).getAttributesToStringList(
         attributeType);
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, AttributeTypeToken attributeType) {
      return getArtifact(workItem.getStoreObject()).isAttributeTypeValid(attributeType);
   }

   @Override
   public String getSoleAttributeValueAsString(IAtsObject atsObject, AttributeTypeToken attributeType, String defaultValue) {
      String result = defaultValue;
      Artifact artifact = getArtifact(atsObject.getArtifactId());
      if (artifact != null) {
         result = artifact.getSoleAttributeValueAsString(attributeType, defaultValue);
      }
      return result;
   }

   @Override
   public String getSoleAttributeValueAsString(ArtifactId artifact, AttributeTypeToken attributeType, String defaultValue) {
      String result = defaultValue;
      Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(artifact);
      if (art != null) {
         result = art.getSoleAttributeValueAsString(attributeType, defaultValue);
      }
      return result;
   }

   private Artifact getArtifact(IAtsObject atsObject) {
      Artifact art = null;
      if (atsObject instanceof ArtifactId) {
         art = getArtifact((ArtifactId) atsObject);
      }
      if (art == null) {
         art = getArtifact(atsObject.getStoreObject());
      }
      return art;
   }

   private Artifact getArtifact(ArtifactId artifact) {
      if (artifact instanceof Artifact) {
         return (Artifact) artifact;
      }
      ArtifactId art = AtsApiService.get().getQueryService().getArtifact(artifact);
      if (art instanceof Artifact) {
         return AtsApiService.get().getQueryServiceIde().getArtifact(art);
      }
      return null;
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, Object value) {
      getArtifact(atsObject).setSoleAttributeValue(attributeType, value);
   }

   @Override
   public int getAttributeCount(IAtsWorkItem workItem, AttributeTypeToken attributeType) {
      return getArtifact(workItem).getAttributeCount(attributeType);
   }

   @Override
   public int getAttributeCount(IAtsObject atsObject, AttributeTypeToken attributeType) {
      return getArtifact(atsObject).getAttributeCount(attributeType);
   }

   @Override
   public int getAttributeCount(ArtifactId artifact, AttributeTypeToken attributeType) {
      return getArtifact(artifact).getAttributeCount(attributeType);
   }

   @Override
   public void addAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, Object value) {
      getArtifact(workItem).addAttribute(attributeType, value);
   }

   @SuppressWarnings({"unchecked"})
   @Override
   public <T> Collection<IAttribute<T>> getAttributes(IAtsObject atsObject, AttributeTypeToken attributeType) {
      List<IAttribute<T>> attrs = new ArrayList<>();
      for (Attribute<Object> attr : getArtifact(atsObject).getAttributes(attributeType)) {
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
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType, IAtsChangeSet changes) {
      if (changes != null) {
         changes.deleteSoleAttribute(workItem, attributeType);
      } else {
         deleteSoleAttribute(workItem, attributeType);
      }
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeToken attributeType, Object value, IAtsChangeSet changes) {
      if (changes != null) {
         changes.setSoleAttributeValue(atsObject, attributeType, value);
      } else {
         setSoleAttributeValue(atsObject, attributeType, value);
      }
   }

   @Override
   public void addAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType, Object value, IAtsChangeSet changes) {
      if (changes != null) {
         changes.addAttribute(workItem, attributeType, value);
      } else {
         getArtifact(workItem).addAttribute(attributeType, value);
      }
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType, Object value, IAtsChangeSet changes) {
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
   public <T> T getSoleAttributeValue(ArtifactId artifact, AttributeTypeToken attributeType, T defaultValue) {
      if (getArtifact(artifact) != null) {
         return getArtifact(artifact).getSoleAttributeValue(attributeType, defaultValue);
      }
      return null;
   }

   @Override
   public <T> Collection<T> getAttributeValues(ArtifactId artifact, AttributeTypeToken attributeType) {
      Assert.isNotNull(artifact, "Artifact can not be null");
      Assert.isNotNull(attributeType, "Attribute Type can not be null");
      return getArtifact(artifact).getAttributeValues(attributeType);
   }

   @Override
   public <T> Collection<T> getAttributeValues(IAtsObject atsObject, AttributeTypeToken attributeType) {
      Assert.isNotNull(atsObject, "ATS Object can not be null");
      Assert.isNotNull(attributeType, "Attribute Type can not be null");
      return getAttributeValues(atsObject.getStoreObject(), attributeType);
   }

   @SuppressWarnings({"unchecked"})
   @Override
   public <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact, AttributeTypeToken attributeType) {
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

   @Override
   public List<String> getAttributesToStringListFromArt(ArtifactToken artifact, AttributeTypeToken attributeType) {
      return getArtifact(artifact).getAttributesToStringList(attributeType);
   }

   @Override
   public List<String> getAttributesToStringListFromArt(ArtifactToken artifact, AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      return getArtifact(artifact).getAttributesToStringList(attributeType, deletionFlag);
   }

}
