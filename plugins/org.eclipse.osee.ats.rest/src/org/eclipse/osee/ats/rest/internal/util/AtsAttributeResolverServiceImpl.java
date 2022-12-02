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
import org.eclipse.osee.ats.core.util.AbstractAtsAttributeResolverServiceImpl;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeResolverServiceImpl extends AbstractAtsAttributeResolverServiceImpl {

   public AtsAttributeResolverServiceImpl(AtsApi atsApi) {
      super(atsApi);
   }

   private ArtifactReadable getArtifact(IAtsObject atsObject) {
      return (ArtifactReadable) atsApi.getQueryService().getArtifact(atsObject);
   }

   private ArtifactReadable getArtifact(ArtifactId artifact) {
      return (ArtifactReadable) atsApi.getQueryService().getArtifact(artifact);
   }

   @Override
   public <T> T getSoleAttributeValue(IAtsObject atsObject, AttributeTypeToken attributeType, T defaultReturnValue) {
      return getArtifact(atsObject).getSoleAttributeValue(attributeType, defaultReturnValue);

   }

   @Override
   public List<String> getAttributesToStringList(IAtsObject atsObject, AttributeTypeToken attributeType) {
      List<String> values = new ArrayList<>();
      for (Object obj : getArtifact(atsObject).getAttributeValues(attributeType)) {
         values.add(obj.toString());
      }
      return values;
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, AttributeTypeToken attributeType) {
      return getArtifact(workItem).isAttributeTypeValid(attributeType);
   }

   @Override
   public String getSoleAttributeValueAsString(IAtsObject atsObject, AttributeTypeToken attributeType, String defaultValue) {
      return getArtifact(atsObject).getAttributeValuesAsString(attributeType);
   }

   @Override
   public String getSoleAttributeValueAsString(ArtifactId artifact, AttributeTypeToken attributeType, String defaultValue) {
      return getArtifact(artifact).getSoleAttributeValue(attributeType, defaultValue);
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, Object value) {
      // Sets on Server need to be through transaction
      throw new OseeStateException(
         "Invalid: Must use setSoleAttributeValue(IAtsWorkItem workItem, AttributeTypeId attributeType, Object value, IAtsChangeSet changes)");
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
      // Sets on Server need to be through transaction
      throw new OseeStateException("Not Implemented");
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> Collection<IAttribute<T>> getAttributes(IAtsObject atsObject, AttributeTypeToken attributeType) {
      Collection<IAttribute<T>> attrs = new ArrayList<>();
      for (AttributeReadable<Object> attr : getArtifact(atsObject).getAttributes(attributeType)) {
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
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType, IAtsChangeSet changes) {
      changes.deleteSoleAttribute(workItem, attributeType);
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeToken attributeType, Object value, IAtsChangeSet changes) {
      changes.setSoleAttributeValue(atsObject, attributeType, value);
   }

   @Override
   public void addAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType, Object value, IAtsChangeSet changes) {
      changes.addAttribute(workItem, attributeType, value);
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType, Object value, IAtsChangeSet changes) {
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
   public <T> T getSoleAttributeValue(ArtifactId artifact, AttributeTypeToken attributeType, T defaultValue) {
      return getArtifact(artifact).getSoleAttributeValue(attributeType, defaultValue);
   }

   @Override
   public <T> Collection<T> getAttributeValues(ArtifactId artifact, AttributeTypeToken attributeType) {
      return getArtifact(artifact).getAttributeValues(attributeType);
   }

   @Override
   public <T> Collection<T> getAttributeValues(IAtsObject atsObject, AttributeTypeToken attributeType) {
      return getAttributeValues(atsObject.getStoreObject(), attributeType);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact, AttributeTypeToken attributeType) {
      Assert.isNotNull(artifact, "Artifact can not be null");
      Assert.isNotNull(attributeType, "Attribute Type can not be null");
      List<IAttribute<T>> attributes = new LinkedList<>();
      for (AttributeReadable<Object> attr : ((ArtifactReadable) artifact).getAttributes(attributeType)) {
         attributes.add((IAttribute<T>) attr);
      }
      return attributes;
   }

   @Override
   public List<String> getAttributesToStringList(ArtifactId artifact, AttributeTypeToken attributeType) {
      List<String> values = new ArrayList<>();
      for (Object value : ((ArtifactReadable) artifact).getAttributeValues(attributeType)) {
         values.add(value.toString());
      }
      return values;
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

   @Override
   public List<String> getAttributesToStringListFromArt(ArtifactToken artifact, AttributeTypeToken attributeType) {
      return ((ArtifactReadable) artifact).getAttributeValues(attributeType);
   }

   @Override
   public List<String> getAttributesToStringListFromArt(ArtifactToken artifact, AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      return ((ArtifactReadable) artifact).getAttributeValues(attributeType, deletionFlag);
   }
}
