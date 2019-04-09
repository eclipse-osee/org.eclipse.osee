/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
public abstract class AttributeManagerImpl extends BaseId implements HasOrcsData<ArtifactTypeToken, ArtifactData>, AttributeManager, AttributeExceptionFactory, Iterable<Map.Entry<AttributeTypeId, List<Attribute<Object>>>> {

   private final HashCollection<AttributeTypeId, Attribute<Object>> attributes;
   private final String guid;
   private boolean isLoaded;

   private final AttributeFactory attributeFactory;

   protected AttributeManagerImpl(ArtifactData artifactData, AttributeFactory attributeFactory) {
      super(artifactData.getId());
      this.attributeFactory = attributeFactory;
      this.attributes = new HashCollection<>(true);
      guid = artifactData.getGuid();
   }

   private List<Attribute<Object>> filterAttributes(List<Attribute<Object>> attributes, DeletionFlag deletionFlag) {

      if (attributes == null) {
         return java.util.Collections.emptyList();
      }
      if (deletionFlag == DeletionFlag.INCLUDE_DELETED) {
         return attributes;
      }
      List<Attribute<Object>> notDeleted = new ArrayList<>(attributes.size());
      for (Attribute<Object> attr : attributes) {
         if (!attr.isDeleted()) {
            notDeleted.add(attr);
         }
      }
      return notDeleted;
   }

   @Override
   public synchronized void add(AttributeTypeId attributeType, Attribute<?> attribute) {
      attributes.put(attributeType, (Attribute<Object>) attribute);
      attribute.getOrcsData().setArtifactId(this);
   }

   @Override
   public synchronized void remove(AttributeTypeId type, Attribute<?> attribute) {
      attributes.removeValue(type, (Attribute<Object>) attribute);
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
      for (Attribute<?> attribute : attributes.getValues()) {
         attribute.clearDirty();
      }
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
   public int getMaximumAttributeTypeAllowed(AttributeTypeId attributeType) {
      int result = -1;
      if (isAttributeTypeValid(attributeType)) {
         result = attributeFactory.getMaxOccurrenceLimit(attributeType);
      }
      return result;
   }

   @Override
   public int getMinimumAttributeTypeAllowed(AttributeTypeId attributeType) {
      int result = -1;
      if (isAttributeTypeValid(attributeType)) {
         result = attributeFactory.getMinOccurrenceLimit(attributeType);
      }
      return result;
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
   public Attribute<Object> getAttributeById(AttributeId attributeId) {
      return getAttributeById(attributeId, DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public Attribute<Object> getAttributeById(AttributeId attributeId, DeletionFlag deletionFlag) {
      Attribute<Object> attribute = null;
      Optional<Attribute<Object>> tryFind =
         Iterables.tryFind(getAttributes(deletionFlag), OrcsPredicates.attributeId(attributeId));
      if (tryFind.isPresent()) {
         attribute = tryFind.get();
      } else {
         throw new AttributeDoesNotExist("Attribute[%s] does not exist for %s", attributeId, getExceptionString());
      }
      return attribute;
   }

   @Override
   public List<Attribute<Object>> getAttributes() {
      return filterAttributes(attributes.getValues(), DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public List<Attribute<Object>> getAttributes(DeletionFlag deletionFlag) {
      return filterAttributes(attributes.getValues(), deletionFlag);
   }

   @Override
   public <T> List<Attribute<T>> getAttributes(AttributeTypeId attributeType) {
      return getAttributes(attributeType, DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public <T> List<Attribute<T>> getAttributes(AttributeTypeId attributeType, DeletionFlag deletionFlag) {
      return Collections.castAll(filterAttributes(attributes.getValues(attributeType), deletionFlag));
   }

   @Override
   public int getAttributeCount(AttributeTypeId attributeType) {
      return getAttributeCount(attributeType, DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public int getAttributeCount(AttributeTypeId attributeType, DeletionFlag deletionFlag) {
      return filterAttributes(attributes.getValues(attributeType), deletionFlag).size();
   }

   @Override
   public <T> List<T> getAttributeValues(AttributeTypeId attributeType) {
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
   public String getSoleAttributeAsString(AttributeTypeId attributeType, String defaultValue) {
      String toReturn = defaultValue;
      List<Attribute<Object>> items = getAttributes(attributeType);
      if (!items.isEmpty()) {
         Attribute<Object> firstItem = items.iterator().next();
         toReturn = String.valueOf(firstItem.getValue());
      }
      return toReturn;
   }

   @Override
   public String getSoleAttributeAsString(AttributeTypeId attributeType) {
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
   public <T> T getSoleAttributeValue(AttributeTypeId attributeType) {
      Attribute<T> attribute = getSoleAttribute(attributeType);
      return attribute.getValue();
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeId attributeType, DeletionFlag flag, T defaultValue) {
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

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeId attributeType, T defaultValue) {
      T value = defaultValue;
      Attribute<T> attribute = null;
      try {
         attribute = getSoleAttribute(attributeType);
         value = attribute.getValue();
         if (value == null) {
            return defaultValue;
         }
      } catch (AttributeDoesNotExist ex) {
         // do nothing
      }

      return value;
   }

   @Override
   public <T> void setSoleAttributeValue(AttributeTypeToken attributeType, T value) {
      Attribute<T> attribute = (Attribute<T>) getOrCreateSoleAttribute(attributeType);
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
   public void setAttributesFromStrings(AttributeTypeToken attributeType, Collection<String> values) {
      Set<String> uniqueItems = new LinkedHashSet<>(values);
      List<Attribute<Object>> remainingAttributes = getAttributes(attributeType);
      List<String> remainingNewValues = new ArrayList<>(uniqueItems.size());

      // all existing attributes matching a new value will be left untouched
      for (String newValue : uniqueItems) {
         boolean found = false;
         for (Attribute<Object> attribute : remainingAttributes) {
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
            Attribute<Object> attribute = remainingAttributes.get(index);
            attribute.setFromString(newValue);
            remainingAttributes.remove(index);
         }
      }

      for (Attribute<Object> attribute : remainingAttributes) {
         attribute.delete();
      }
   }

   @Override
   public <T> void setAttributesFromValues(AttributeTypeToken attributeType, Collection<T> values) {
      Set<T> uniqueItems = new LinkedHashSet<>(values);
      List<Attribute<Object>> remainingAttributes = getAttributes(attributeType);
      List<T> remainingNewValues = new ArrayList<>(uniqueItems.size());

      // all existing attributes matching a new value will be left untouched
      for (T newValue : uniqueItems) {
         boolean found = false;
         for (Attribute<Object> attribute : remainingAttributes) {
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
            Attribute<Object> attribute = remainingAttributes.get(index);
            attribute.setValue(newValue);
            remainingAttributes.remove(index);
         }
      }

      for (Attribute<Object> attribute : remainingAttributes) {
         attribute.delete();
      }
   }

   @Override
   public void deleteAttributesByArtifact() {
      for (Attribute<?> attribute : attributes.getValues()) {
         attribute.setArtifactDeleted();
      }
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
   public void deleteSoleAttribute(AttributeTypeId attributeType) {
      Attribute<?> attribute = getSoleAttribute(attributeType);
      if (attribute != null) {
         deleteAttribute(attribute);
      }
   }

   @Override
   public void deleteAttributes(AttributeTypeId attributeType) {
      List<Attribute<Object>> values = attributes.getValues(attributeType);
      if (values == null) {
         return;
      }
      for (Attribute<?> attribute : values) {
         attribute.delete();
      }
   }

   @Override
   public void deleteAttributesWithValue(AttributeTypeId attributeType, Object value) {
      List<Attribute<Object>> values = attributes.getValues(attributeType);
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

   @Override
   public <T> Attribute<T> createAttribute(AttributeTypeToken attributeType) {
      checkTypeValid(attributeType);
      checkMultiplicityCanAdd(attributeType);
      Attribute<Object> attr = attributeFactory.createAttributeWithDefaults(this, getOrcsData(), attributeType);
      return (Attribute<T>) attr;
   }

   @Override
   public <T> Attribute<T> createAttribute(AttributeTypeToken attributeType, T value) {
      Attribute<T> attribute = createAttribute(attributeType);
      attribute.setValue(value);
      return attribute;
   }

   private <T> Attribute<T> createAttributeFromString(AttributeTypeToken attributeType, String value) {
      Attribute<T> attribute = createAttribute(attributeType);
      attribute.setFromString(value);
      return attribute;
   }

   @Override
   public Iterator<Map.Entry<AttributeTypeId, List<Attribute<Object>>>> iterator() {
      return attributes.iterator();
   }

   //////////////////////////////////////////////////////////////

   private Attribute<Object> getOrCreateSoleAttribute(AttributeTypeToken attributeType) {
      List<Attribute<Object>> filterAttributes = getAttributes(attributeType);
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
   public <T> Attribute<T> getSoleAttribute(AttributeTypeId attributeType) {
      return getSoleAttribute(attributeType, DeletionFlag.EXCLUDE_DELETED);
   }

   /*
    * INCLUDE_DELETED: Includes all hard deleted attributes and artifact deleted attributes, EXCLUDE_DELETED: Excludes
    * all hard deleted attributes, but include artifact deleted attributes
    */
   @Override
   public <T> Attribute<T> getSoleAttribute(AttributeTypeId attributeType, DeletionFlag flag) {
      Collection<Attribute<Object>> filterAttributes = getAttributes(attributeType, flag);
      int size = filterAttributes.size();
      if (size == 1) {
         return (Attribute<T>) filterAttributes.iterator().next();
      } else if (size < 1) {
         throw createDoesNotExistException(attributeType);
      } else {
         throw createManyExistException(attributeType, size);
      }
   }

   //////////////////////////////////////////////////////////////

   private void checkTypeValid(AttributeTypeId attributeType) {
      if (CoreAttributeTypes.Name.notEqual(attributeType)) {
         if (!isAttributeTypeValid(attributeType)) {
            throw new OseeArgumentException("The attribute type [%s] is not valid for artifacts [%s]", attributeType,
               getExceptionString());
         }
      }
   }

   private void checkMultiplicityCanAdd(AttributeTypeId attributeType) {
      checkMultiplicity(attributeType, getAttributeCount(attributeType) + 1);
   }

   private void checkMultiplicityCanDelete(AttributeTypeId attributeType) {
      checkMultiplicity(attributeType, getAttributeCount(attributeType) - 1);
   }

   private void checkMultiplicity(AttributeTypeId attributeType, int count) {
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

   private MultiplicityState getAttributeMuliplicityState(AttributeTypeId attributeType, int count) {
      MultiplicityState state = MultiplicityState.IS_VALID;
      if (count > attributeFactory.getMaxOccurrenceLimit(attributeType)) {
         state = MultiplicityState.MAX_VIOLATION;
      } else if (count < attributeFactory.getMinOccurrenceLimit(attributeType)) {
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

   private final int getRemainingAttributeCount(AttributeTypeId attributeType) {
      int minLimit = attributeFactory.getMinOccurrenceLimit(attributeType);
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