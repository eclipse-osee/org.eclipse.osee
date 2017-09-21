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
import org.eclipse.osee.framework.core.enums.ModificationType;
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
   private AttributeData attributeData;

   @Override
   public void internalInitialize(AttributeTypes attributeTypeCache, Reference<AttributeContainer> containerReference, AttributeData attributeData, boolean isDirty, boolean setDefaultValue) throws OseeCoreException {
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
   public AttributeData getOrcsData() {
      return attributeData;
   }

   @Override
   public void setOrcsData(AttributeData data) {
      this.attributeData = data;
   }

   /**
    * Base implementation does nothing. Subclasses may override to do setup that depends on the attribute state data.
    */
   protected void uponInitialize() throws OseeCoreException {
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
   public void setValue(T value) throws OseeCoreException {
      if (subClassSetValue(value)) {
         markAsNewOrChanged();
      }
   }

   @Override
   public boolean setFromString(String value) throws OseeCoreException {
      boolean response = subClassSetValue(convertStringToValue(value));
      if (response) {
         markAsNewOrChanged();
      }
      return response;
   }

   protected abstract T convertStringToValue(String value) throws OseeCoreException;

   @Override
   public final void resetToDefaultValue() throws OseeCoreException {
      getOrcsData().setModType(ModificationType.MODIFIED);
      setToDefaultValue();
   }

   protected void setToDefaultValue() throws OseeCoreException {
      if (defaultValue != null) {
         subClassSetValue(convertStringToValue(defaultValue));
      }
   }

   @Override
   public boolean setValueFromInputStream(InputStream value) throws OseeCoreException {
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
   protected abstract boolean subClassSetValue(T value) throws OseeCoreException;

   @Override
   public abstract T getValue() throws OseeCoreException;

   @Override
   public String getDisplayableString() throws OseeCoreException {
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
   public AttributeContainer getContainer() throws OseeStateException {
      if (containerReference.get() == null) {
         throw new OseeStateException("Attribute parent has been garbage collected");
      }
      return containerReference.get();
   }

   protected String getDefaultValueFromMetaData() throws OseeCoreException {
      return attributeTypeCache.getDefaultValue(getAttributeType());
   }

   /**
    * @return attributeType Attribute Type Information
    * @throws OseeCoreException
    */
   @Override
   public AttributeTypeToken getAttributeType() throws OseeCoreException {
      return attributeTypeCache.get(getOrcsData().getTypeUuid());
   }

   /**
    * Currently this method provides support for quasi attribute type inheritance
    *
    * @return whether this attribute's type or any of its super-types are the specified type
    * @throws OseeCoreException
    */
   @Override
   public boolean isOfType(AttributeTypeId otherAttributeType) throws OseeCoreException {
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
    *
    * @throws OseeStateException
    */
   @Override
   public final void delete() throws OseeCoreException {
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
   public void purge() throws OseeCoreException {
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
      return getGammaId() > 0;
   }

   /**
    * @return Returns the attrId.
    */
   @Override
   public Integer getLocalId() {
      return getOrcsData().getLocalId();
   }

   @Override
   public long getGammaId() {
      return getOrcsData().getVersion().getGammaId();
   }

   public void internalSetGammaId(int gammaId) {
      getOrcsData().getVersion().setGammaId(gammaId);
   }

   public void internalSetAttributeId(int attrId) {
      getOrcsData().setLocalId(attrId);
   }

   /**
    * artifact.persist(); artifact.reloadAttributesAndRelations(); Will need to be called afterwards to see replaced
    * data in memory
    *
    * @throws OseeCoreException
    */
   public void replaceWithVersion(int gammaId) throws OseeCoreException {
      internalSetModificationType(ModificationType.REPLACED_WITH_VERSION);
      getOrcsData().getVersion().setGammaId(gammaId);
      setDirtyFlag(true);
   }

   /**
    * @param modificationType the modificationType to set
    * @throws OseeCoreException
    */
   public void internalSetModificationType(ModificationType modificationType) throws OseeCoreException {
      Conditions.checkNotNull(modificationType, "modification type");
      getOrcsData().setModType(modificationType);
   }

   public void internalSetDeletedFromRemoteEvent() throws OseeCoreException {
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

}