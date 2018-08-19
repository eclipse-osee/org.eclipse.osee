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
package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeContainer;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Ryan D. Brooks
 */
public abstract class AttributeImpl<T> implements Comparable<AttributeImpl<T>>, Attribute<T> {
   private AttributeTypes attributeTypeCache;
   private Reference<AttributeContainer> containerReference;
   private String defaultValue;
   private Log logger;
   private AttributeData<T> attributeData;

   @Override
   public void internalInitialize(AttributeTypes attributeTypeCache, Reference<AttributeContainer> containerReference, AttributeData<T> attributeData, boolean isDirty, boolean setDefaultValue) {
      this.attributeTypeCache = attributeTypeCache;
      this.containerReference = containerReference;
      this.attributeData = attributeData;

      if (setDefaultValue) {
         setToDefaultValue();
      }
      getOrcsData().calculateDirtyState(isDirty);
      uponInitialize();
   }

   protected Log getLogger() {
      return logger;
   }

   @Override
   public AttributeData<T> getOrcsData() {
      return attributeData;
   }

   @Override
   public void setOrcsData(AttributeData<T> data) {
      this.attributeData = data;
   }

   /**
    * Base implementation does nothing. Subclasses may override to do setup that depends on the attribute state data.
    */
   protected void uponInitialize() {
      // provided for subclass implementation
   }

   private void markAsNewOrChanged() {
      if (isInDb()) {
         markAsChanged(ModificationType.MODIFIED);
      } else {
         markAsChanged(ModificationType.NEW);
      }
   }

   @Override
   public void setValue(T value) {
      if (subClassSetValue(value)) {
         markAsNewOrChanged();
      }
   }

   @Override
   public boolean setFromString(String value) {
      Conditions.checkNotNull(value, "Attribute value", "attribute id [%s]", getId());
      boolean response = subClassSetValue(convertStringToValue(value));
      if (response) {
         markAsNewOrChanged();
      }
      return response;
   }

   @Override
   public final void resetToDefaultValue() {
      getOrcsData().setModType(ModificationType.MODIFIED);
      setToDefaultValue();
   }

   protected void setToDefaultValue() {
      if (defaultValue != null) {
         subClassSetValue(convertStringToValue(defaultValue));
      }
   }

   @Override
   public boolean setValueFromInputStream(InputStream value) {
      Conditions.checkNotNull(value, "Attribute value", "attribute id [%s]", getId());
      boolean success = false;
      try {
         success = setFromString(Lib.inputStreamToString(value));
         if (success) {
            markAsNewOrChanged();
         }
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return success;
   }

   /**
    * Subclasses must provide an implementation of this method and in general should not override the other set value
    * methods
    */
   protected abstract boolean subClassSetValue(T value);

   @Override
   public abstract T getValue();

   @Override
   public String getDisplayableString() {
      return getDataProxy().getDisplayableString();
   }

   @Override
   public String toString() {
      try {
         return getDisplayableString();
      } catch (OseeCoreException ex) {
         return Lib.exceptionToString(ex);
      }
   }

   public DataProxy getDataProxy() {
      return getOrcsData().getDataProxy();
   }

   /**
    * @return <b>true</b> if this attribute is dirty
    */
   @Override
   public boolean isDirty() {
      return getOrcsData().isDirty();
   }

   protected void markAsChanged(ModificationType modificationType) {
      setDirtyFlag(true);
      getOrcsData().setModType(modificationType);
   }

   @Override
   public void clearDirty() {
      setDirtyFlag(false);
   }

   private void setDirtyFlag(boolean dirty) {
      getOrcsData().calculateDirtyState(dirty);
   }

   @Override
   public AttributeContainer getContainer() {
      if (containerReference.get() == null) {
         throw new OseeStateException("Attribute parent has been garbage collected");
      }
      return containerReference.get();
   }

   protected String getDefaultValueFromMetaData() {
      return attributeTypeCache.getDefaultValue(getAttributeType());
   }

   /**
    * @return attributeType Attribute Type Information
    */
   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeData.getType();
   }

   /**
    * Currently this method provides support for quasi attribute type inheritance
    *
    * @return whether this attribute's type or any of its super-types are the specified type
    */
   @Override
   public boolean isOfType(AttributeTypeId otherAttributeType) {
      return getAttributeType().equals(otherAttributeType);
   }

   @Override
   public void unDelete() {
      getOrcsData().setModType(getOrcsData().getPreviousModType());
   }

   /**
    * Deletes the attribute
    */
   @Override
   public final void setArtifactDeleted() {
      markAsChanged(ModificationType.ARTIFACT_DELETED);
   }

   /**
    * Deletes the attribute
    */
   @Override
   public final void delete() {
      if (isInDb()) {
         markAsChanged(ModificationType.DELETED);
      } else {
         getContainer().remove(getAttributeType(), this);
      }
   }

   @Override
   public ModificationType getModificationType() {
      return getOrcsData().getModType();
   }

   @Override
   public boolean isDeleteAllowed() {
      try {
         return !isDeleted() && getContainer().getAttributeCount(
            getAttributeType()) > attributeTypeCache.getMinOccurrences(getAttributeType());
      } catch (OseeCoreException ex) {
         return false;
      }
   }

   /**
    * Purges the attribute from the database.
    */
   public void purge() {
      getDataProxy().purge();
   }

   public void markAsPurged() {
      getOrcsData().setModType(ModificationType.DELETED);
      setDirtyFlag(false);
   }

   /**
    * @return true if in data store
    */
   public boolean isInDb() {
      return getGammaId().isValid();
   }

   @Override
   public GammaId getGammaId() {
      return getOrcsData().getVersion().getGammaId();
   }

   public void internalSetGammaId(GammaId gammaId) {
      getOrcsData().getVersion().setGammaId(gammaId);
   }

   public void internalSetAttributeId(int attrId) {
      getOrcsData().setLocalId(attrId);
   }

   /**
    * artifact.persist(); artifact.reloadAttributesAndRelations(); Will need to be called afterwards to see replaced
    * data in memory
    */
   public void replaceWithVersion(GammaId gammaId) {
      internalSetModificationType(ModificationType.REPLACED_WITH_VERSION);
      getOrcsData().getVersion().setGammaId(gammaId);
      setDirtyFlag(true);
   }

   /**
    * @param modificationType the modificationType to set
    */
   public void internalSetModificationType(ModificationType modificationType) {
      Conditions.checkNotNull(modificationType, "modification type");
      getOrcsData().setModType(modificationType);
   }

   public void internalSetDeletedFromRemoteEvent() {
      internalSetModificationType(ModificationType.DELETED);
   }

   @Override
   public int compareTo(AttributeImpl<T> other) {
      return toString().compareTo(other.toString());
   }

   @Override
   public Long getId() {
      return getOrcsData().getId();
   }

   @Override
   public String convertToStorageString(T rawValue) {
      return rawValue == null ? "" : rawValue.toString();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Id) {
         return getId().equals(((Id) obj).getId());
      }
      if (obj instanceof Long) {
         return getId().equals(obj);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return getId().hashCode();
   }
}