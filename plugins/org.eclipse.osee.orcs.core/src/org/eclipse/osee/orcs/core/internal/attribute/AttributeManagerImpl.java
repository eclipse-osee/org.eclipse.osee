/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.core.internal.attribute;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.MultipleItemsExist;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.ds.HasOrcsData;
import org.eclipse.osee.orcs.core.internal.util.MultiplicityState;
import org.eclipse.osee.orcs.core.internal.util.OrcsPredicates;

/**
 * @author Roberto E. Escobar
 */
public abstract class AttributeManagerImpl extends BaseId implements HasOrcsData<ArtifactTypeToken, ArtifactData>, AttributeManager, AttributeExceptionFactory, Iterable<Map.Entry<AttributeTypeToken, List<Attribute<?>>>> {

   private final HashCollection<AttributeTypeToken, Attribute<?>> attributes;
   private final String guid;
   private boolean isLoaded;
   private final ArtifactData artifactData;

   private final AttributeFactory attributeFactory;

   protected AttributeManagerImpl(ArtifactData artifactData, AttributeFactory attributeFactory) {
      super(artifactData);
      this.artifactData = artifactData;
      this.attributeFactory = attributeFactory;
      this.attributes = new HashCollection<>(true);
      guid = artifactData.getGuid();

   }

   private <T> List<Attribute<T>> filterAttributes(List<Attribute<?>> attributes, DeletionFlag deletionFlag) {

      if (attributes == null) {
         return java.util.Collections.emptyList();
      }
      if (deletionFlag == DeletionFlag.INCLUDE_DELETED) {
         return Collections.castAll(attributes);
      }
      List<Attribute<T>> notDeleted = new ArrayList<>(attributes.size());
      for (Attribute<?> attr : attributes) {
         if (!attr.isDeleted()) {
            notDeleted.add((Attribute<T>) attr);
         }
      }
      return notDeleted;
   }

   @Override
   public synchronized <T> void add(AttributeTypeToken attributeType, Attribute<T> attribute) {
      attributes.put(attributeType, attribute);
      attribute.getOrcsData().setArtifactId(this);
   }

   @Override
   public synchronized <T> void remove(AttributeTypeToken type, Attribute<T> attribute) {
      attributes.removeValue(type, attribute);
      attribute.getOrcsData().setArtifactId(ArtifactId.SENTINEL);
   }

   @Override
   public boolean isLoaded() {
      return isLoaded;
   }

   @Override
   public void setLoaded(boolean value) {
      this.isLoaded = value;
      if (value == true) {
         onLoaded();
      }
   }

   @Override
   public void setAttributesNotDirty() {
      attributes.getValues().forEach(Attribute::clearDirty);
   }

   @Override
   public boolean areAttributesDirty() {
      for (Attribute<?> attr : attributes.getValues()) {
         if (attr.isDirty()) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String getName() {
      String name;
      try {
         name = getSoleAttributeAsString(CoreAttributeTypes.Name);
      } catch (Exception ex) {
         name = Lib.exceptionToString(ex);
      }
      return name;
   }

   @Override
   public Collection<AttributeTypeToken> getExistingAttributeTypes() {
      Collection<AttributeTypeToken> notDeleted = new HashSet<>();
      for (Attribute<?> attr : attributes.getValues()) {
         if (!attr.isDeleted()) {
            notDeleted.add(attr.getAttributeType());
         }
      }
      return notDeleted;
   }

   @Override
   public <T> Attribute<T> getAttributeById(AttributeId attributeId) {
      return getAttributeById(attributeId, DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public <T> Attribute<T> getAttributeById(AttributeId attributeId, DeletionFlag deletionFlag) {
      Attribute<T> attribute = null;
      Optional<Attribute<T>> tryFind =
         Iterables.tryFind(getAttributes(deletionFlag), OrcsPredicates.attributeId(attributeId));
      if (tryFind.isPresent()) {
         attribute = tryFind.get();
      } else {
         throw new AttributeDoesNotExist("Attribute[%s] does not exist for %s", attributeId, getExceptionString());
      }
      return attribute;
   }

   @Override
   public <T> List<Attribute<T>> getAttributes() {
      return getAttributes(DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public <T> List<Attribute<T>> getAttributes(DeletionFlag deletionFlag) {
      return filterAttributes(attributes.getValues(), deletionFlag);
   }

   @Override
   public <T> List<Attribute<T>> getAttributes(AttributeTypeToken attributeType) {
      return getAttributes(attributeType, DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public <T> List<Attribute<T>> getAttributes(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      return filterAttributes(attributes.getValues(attributeType), deletionFlag);
   }

   @Override
   public int getAttributeCount(AttributeTypeToken attributeType) {
      return getAttributeCount(attributeType, DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public int getAttributeCount(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      return filterAttributes(attributes.getValues(attributeType), deletionFlag).size();
   }

   @Override
   public <T> List<T> getAttributeValues(AttributeTypeToken attributeType) {
      List<Attribute<T>> attributes = getAttributes(attributeType);

      List<T> values = new LinkedList<>();
      for (Attribute<T> attribute : attributes) {
         T value = attribute.getValue();
         if (value != null) {
            values.add(value);
         }
      }
      return values;
   }

   @Override
   public <T> List<T> getAttributeValues(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      List<Attribute<T>> attributes = getAttributes(attributeType, deletionFlag);

      List<T> values = new LinkedList<>();
      for (Attribute<T> attribute : attributes) {
         T value = attribute.getValue();
         if (value != null) {
            values.add(value);
         }
      }
      return values;
   }

   @Override
   public String getSoleAttributeAsString(AttributeTypeToken attributeType, String defaultValue) {
      String toReturn = defaultValue;
      List<Attribute<Object>> items = getAttributes(attributeType);
      if (!items.isEmpty()) {
         Attribute<Object> firstItem = items.iterator().next();
         toReturn = String.valueOf(firstItem.getValue());
      }
      return toReturn;
   }

   @Override
   public String getSoleAttributeAsString(AttributeTypeToken attributeType) {
      String toReturn = null;
      Object value = getSoleAttributeValue(attributeType);
      if (value instanceof InputStream) {
         InputStream inputStream = (InputStream) value;
         try {
            toReturn = Lib.inputStreamToString(inputStream);
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         } finally {
            try {
               inputStream.close();
            } catch (IOException ex) {
               OseeCoreException.wrapAndThrow(ex);
            }
         }
      } else {
         if (value != null) {
            toReturn = value.toString();
         }
      }
      return toReturn;
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType) {
      Attribute<T> attribute = getSoleAttribute(attributeType);
      return attribute.getValue();
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType, DeletionFlag flag, T defaultValue) {
      T value = defaultValue;
      Attribute<T> attribute = null;
      try {
         attribute = getSoleAttribute(attributeType, flag);
         value = attribute.getValue();
      } catch (AttributeDoesNotExist ex) {
         // do nothing
      }

      return value;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType, T defaultValue) {
      T value = defaultValue;
      List<Attribute<Object>> attributes = getAttributes(attributeType);
      if (attributes.size() == 1) {
         attributes.iterator().next();
         value = (T) getSoleAttribute(attributeType).getValue();
         if (value == null) {
            return defaultValue;
         }
      } else if (attributes.size() > 1) {
         throw createManyExistException(attributeType, attributes.size());
      }
      return value;
   }

   @Override
   public <T> void setSoleAttributeValue(AttributeTypeToken attributeType, T value) {
      Attribute<T> attribute = getOrCreateSoleAttribute(attributeType);
      attribute.setValue(value);
   }

   @Override
   public void setSoleAttributeFromString(AttributeTypeToken attributeType, String value) {
      getOrCreateSoleAttribute(attributeType).setFromString(value);
   }

   @Override
   public void setSoleAttributeFromStream(AttributeTypeToken attributeType, InputStream inputStream) {
      getOrCreateSoleAttribute(attributeType).setValueFromInputStream(inputStream);
   }

   @Override
   public void setAttributesFromStrings(AttributeTypeToken attributeType, String... values) {
      setAttributesFromStrings(attributeType, Arrays.asList(values));
   }

   /**
    * This method may take in a non string attribute type, and set the attributes from the collection of Strings using
    * the createAttributeFromString() method.
    */
   @Override
   public <T> void setAttributesFromStrings(AttributeTypeToken attributeType, Collection<String> values) {
      Set<String> uniqueItems = new LinkedHashSet<>(values);
      List<Attribute<T>> remainingAttributes = getAttributes(attributeType);
      List<String> remainingNewValues = new ArrayList<>(uniqueItems.size());

      // all existing attributes matching a new value will be left untouched
      for (String newValue : uniqueItems) {
         boolean found = false;
         for (Attribute<T> attribute : remainingAttributes) {
            if (newValue.equals(attribute.getValue().toString())) {
               remainingAttributes.remove(attribute);
               found = true;
               break;
            }
         }

         if (!found) {
            remainingNewValues.add(newValue);
         }
      }

      for (String newValue : remainingNewValues) {
         if (remainingAttributes.isEmpty()) {
            createAttributeFromString(attributeType, newValue);
         } else {
            int index = remainingAttributes.size() - 1;
            Attribute<T> attribute = remainingAttributes.get(index);
            attribute.setFromString(newValue);
            remainingAttributes.remove(index);
         }
      }

      for (Attribute<T> attribute : remainingAttributes) {
         attribute.delete();
      }
   }

   @Override
   public <T> void setAttributesFromValues(AttributeTypeToken attributeType, Collection<T> values) {
      Set<T> uniqueItems = new LinkedHashSet<>(values);
      List<Attribute<T>> remainingAttributes = getAttributes(attributeType);
      List<T> remainingNewValues = new ArrayList<>(uniqueItems.size());

      // all existing attributes matching a new value will be left untouched
      for (T newValue : uniqueItems) {
         boolean found = false;
         for (Attribute<T> attribute : remainingAttributes) {
            if (newValue.equals(attribute.getValue())) {
               remainingAttributes.remove(attribute);
               found = true;
               break;
            }
         }

         if (!found) {
            remainingNewValues.add(newValue);
         }
      }

      for (T newValue : remainingNewValues) {
         if (remainingAttributes.isEmpty()) {
            createAttribute(attributeType, newValue);
         } else {
            int index = remainingAttributes.size() - 1;
            Attribute<T> attribute = remainingAttributes.get(index);
            attribute.setValue(newValue);
            remainingAttributes.remove(index);
         }
      }

      for (Attribute<T> attribute : remainingAttributes) {
         attribute.delete();
      }
   }

   @Override
   public void deleteAttributesByArtifact() {
      attributes.getValues().forEach(Attribute::setArtifactDeleted);
   }

   @Override
   public void unDeleteAttributesByArtifact() {
      for (Attribute<?> attribute : attributes.getValues()) {
         if (ModificationType.ARTIFACT_DELETED == attribute.getModificationType()) {
            attribute.unDelete();
         }
      }
   }

   @Override
   public void deleteSoleAttribute(AttributeTypeToken attributeType) {
      Attribute<?> attribute = getSoleAttribute(attributeType);
      if (attribute != null) {
         deleteAttribute(attribute);
      }
   }

   @Override
   public void deleteAttributes(AttributeTypeToken attributeType) {
      List<Attribute<?>> values = attributes.getValues(attributeType);
      if (values == null) {
         return;
      }
      for (Attribute<?> attribute : values) {
         if (attribute.getOrcsData().getVersion().getTxCurrent().isCurrent() && !attribute.getModificationType().equals(
            ModificationType.DELETED)) {
            attribute.delete();
         }
      }
   }

   @Override
   public void deleteAttributesWithValue(AttributeTypeToken attributeType, Object value) {
      List<Attribute<?>> values = attributes.getValues(attributeType);
      if (values == null) {
         return;
      }
      for (Attribute<?> attribute : values) {
         if (attribute.getValue().equals(value)) {
            deleteAttribute(attribute);
            break;
         }
      }
   }

   private void deleteAttribute(Attribute<?> attribute) {
      checkMultiplicityCanDelete(attribute.getAttributeType());
      attribute.delete();
   }

   private void attributeCanBeCreated(AttributeTypeToken attributeType) {
      checkTypeValid(attributeType);
      checkMultiplicityCanAdd(attributeType);
   }

   @Override
   public <T> Attribute<T> createAttribute(AttributeTypeToken attributeType) {
      attributeCanBeCreated(attributeType);
      Attribute<T> attr = attributeFactory.createAttributeWithDefaults(this, getOrcsData(), attributeType);
      return attr;
   }

   @Override
   public <T> Attribute<T> createAttribute(AttributeTypeToken attributeType, T value) {
      Attribute<T> attribute = createAttribute(attributeType);
      attribute.setValue(value);
      return attribute;
   }

   @Override
   public <T> Attribute<T> createAttribute(AttributeTypeToken attributeType, AttributeId attributeId, T value) {
      attributeCanBeCreated(attributeType);
      Attribute<T> attr = attributeFactory.createAttributeWithDefaults(this, getOrcsData(), attributeType, attributeId);
      attr.setValue(value);
      return attr;
   }

   @Override
   public <T> Attribute<T> createAttributeFromString(AttributeTypeToken attributeType, String value) {
      Attribute<T> attribute = createAttribute(attributeType);
      attribute.setFromString(value);
      return attribute;
   }

   @Override
   public Iterator<Map.Entry<AttributeTypeToken, List<Attribute<?>>>> iterator() {
      return attributes.iterator();
   }

   //////////////////////////////////////////////////////////////

   private <T> Attribute<T> getOrCreateSoleAttribute(AttributeTypeToken attributeType) {
      List<Attribute<T>> filterAttributes = getAttributes(attributeType);
      int count = filterAttributes.size();
      if (count > 1) {
         throw new MultipleItemsExist("Multiple items found - total [%s]", count);
      } else if (count == 1) {
         return filterAttributes.iterator().next();
      }
      return createAttribute(attributeType);
   }

   /*
    * Exclude any hard deleted attributes, but include artifact deleted attributes
    */
   @Override
   public <T> Attribute<T> getSoleAttribute(AttributeTypeToken attributeType) {
      return getSoleAttribute(attributeType, DeletionFlag.EXCLUDE_DELETED);
   }

   /*
    * INCLUDE_DELETED: Includes all hard deleted attributes and artifact deleted attributes, EXCLUDE_DELETED: Excludes
    * all hard deleted attributes, but include artifact deleted attributes
    */
   @Override
   public <T> Attribute<T> getSoleAttribute(AttributeTypeToken attributeType, DeletionFlag flag) {
      Collection<Attribute<T>> filterAttributes = getAttributes(attributeType, flag);
      int size = filterAttributes.size();
      if (size == 1) {
         return filterAttributes.iterator().next();
      } else if (size < 1) {
         throw createDoesNotExistException(attributeType);
      } else {
         throw createManyExistException(attributeType, size);
      }
   }

   //////////////////////////////////////////////////////////////

   private <T> void checkTypeValid(AttributeTypeToken attributeType) {
      if (CoreAttributeTypes.Name.notEqual(attributeType)) {
         if (!isAttributeTypeValid(attributeType)) {
            throw new OseeArgumentException("The attribute type [%s] is not valid for artifacts [%s]", attributeType,
               getExceptionString());
         }
      }
   }

   private <T> void checkMultiplicityCanAdd(AttributeTypeToken attributeType) {
      checkMultiplicity(attributeType, getAttributeCount(attributeType) + 1);
   }

   private <T> void checkMultiplicityCanDelete(AttributeTypeToken attributeType) {
      checkMultiplicity(attributeType, getAttributeCount(attributeType) - 1);
   }

   private void checkMultiplicity(AttributeTypeToken attributeType, int count) {
      MultiplicityState state = getAttributeMuliplicityState(attributeType, count);
      switch (state) {
         case MAX_VIOLATION:
            throw new OseeStateException("Attribute type [%s] exceeds max occurrence rule on [%s]", attributeType,
               getExceptionString());
         case MIN_VIOLATION:
            throw new OseeStateException("Attribute type [%s] is less than min occurrence rule on [%s]", attributeType,
               getExceptionString());
         default:
            break;
      }
   }

   private MultiplicityState getAttributeMuliplicityState(AttributeTypeToken attributeType, int count) {
      MultiplicityState state = MultiplicityState.IS_VALID;
      ArtifactTypeToken artifactType = artifactData.getArtifactType();
      if (count > artifactType.getMax(attributeType)) {
         state = MultiplicityState.MAX_VIOLATION;
      } else if (count < artifactType.getMin(attributeType)) {
         state = MultiplicityState.MIN_VIOLATION;
      }
      return state;
   }

   //////////////////////////////////////////////////////////////

   private void onLoaded() {
      //      computeLastDateModified();
      meetMinimumAttributes();
   }

   private void meetMinimumAttributes() {
      for (AttributeTypeToken attributeType : getValidAttributeTypes()) {
         int missingCount = getRemainingAttributeCount(attributeType);
         for (int i = 0; i < missingCount; i++) {
            attributeFactory.createAttributeWithDefaults(this, getOrcsData(), attributeType);
         }
      }
   }

   private final <T> int getRemainingAttributeCount(AttributeTypeToken attributeType) {
      int minLimit = artifactData.getType().getMin(attributeType);
      return minLimit - getAttributeCount(attributeType);
   }

   //////////////////////////////////////////////////////////////

   @Override
   public MultipleAttributesExist createManyExistException(AttributeTypeId type, int count) {
      MultipleAttributesExist toReturn;
      if (type != null) {
         toReturn = new MultipleAttributesExist(
            "The attribute type [%s] has [%s] instances on [%s], but only [1] instance is allowed", type, count,
            getExceptionString());
      } else {
         toReturn = new MultipleAttributesExist(
            "Multiple items found - total instances [%s] on [%s], but only [1] instance is allowed", count,
            getExceptionString());
      }
      return toReturn;
   }

   @Override
   public AttributeDoesNotExist createDoesNotExistException(AttributeTypeId type) {
      AttributeDoesNotExist toReturn;
      if (type == null) {
         toReturn = new AttributeDoesNotExist("Attribute could not be found on [%s]", getExceptionString());
      } else {
         toReturn =
            new AttributeDoesNotExist("Attribute of type [%s] could not be found on [%s]", type, getExceptionString());
      }
      return toReturn;
   }

   @Override
   public String getGuid() {
      return guid;
   }
}