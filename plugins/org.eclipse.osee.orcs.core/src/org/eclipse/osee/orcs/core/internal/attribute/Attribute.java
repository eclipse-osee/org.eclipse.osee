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

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.HasOrcsData;
import org.eclipse.osee.orcs.core.internal.artifact.AttributeManager;
import org.eclipse.osee.orcs.data.AttributeWriteable;

/**
 * @author Ryan D. Brooks
 */
public abstract class Attribute<T> implements HasOrcsData<AttributeData>, Comparable<Attribute<T>>, AttributeWriteable<T> {
   private AttributeTypeCache attributeTypeCache;
   private Reference<AttributeManager> containerReference;
   private boolean dirty;
   private String defaultValue;
   private Log logger;
   private AttributeData attributeData;

   public void internalInitialize(AttributeTypeCache attributeTypeCache, Reference<AttributeManager> containerReference, AttributeData attributeData, boolean isDirty, boolean setDefaultValue) throws OseeCoreException {
      this.attributeTypeCache = attributeTypeCache;
      this.containerReference = containerReference;
      this.attributeData = attributeData;

      if (setDefaultValue) {
         setToDefaultValue();
      }
      dirty = isDirty;
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
   @SuppressWarnings("unused")
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
      // TODO Artifact Checks      
      //      if (attributeType.getName().equals("Name") && !value.equals(getValue())) {
      //         // Confirm artifact is fit to rename
      //         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
      //            IStatus result = check.isRenamable(Arrays.asList(getArtifact()));
      //            if (!result.isOK()) {
      //               throw new OseeCoreException(result.getMessage());
      //            }
      //         }
      //      }

      if (subClassSetValue(value)) {
         markAsNewOrChanged();
      }
   }

   @Override
   public boolean setFromString(String value) throws OseeCoreException {
      // TODO Artifact Checks 
      //      if (attributeType.equals(CoreAttributeTypes.Name) && !value.equals(getValue())) {
      //         // Confirm artifact is fit to rename
      //         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
      //            IStatus result = check.isRenamable(Arrays.asList(getArtifact()));
      //            if (!result.isOK()) {
      //               throw new OseeCoreException(result.getMessage());
      //            }
      //         }
      //      }

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
      try {
         boolean response = setFromString(Lib.inputStreamToString(value));
         if (response) {
            markAsNewOrChanged();
         }
         return response;
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return false; // unreachable since wrapAndThrow() always throws an exception
      }
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
      return dirty;
   }

   protected void markAsChanged(ModificationType modificationType) {
      setDirtyFlag(true);
      getOrcsData().setModType(modificationType);
   }

   public void clearDirty() {
      setDirtyFlag(false);
   }

   private void setDirtyFlag(boolean dirty) {
      this.dirty = dirty;
      //      try {
      //         Artifact artifact = getArtifact();
      //         ArtifactCache.updateCachedArtifact(artifact.getArtId(), artifact.getBranch().getId());
      //      } catch (OseeCoreException ex) {
      //         OseeLog.log(Attribute.class, Level.SEVERE, ex);
      //      }
   }

   public AttributeManager getContainer() throws OseeStateException {
      if (containerReference.get() == null) {
         throw new OseeStateException("Attribute parent has been garbage collected");
      }
      return containerReference.get();
   }

   protected String getDefaultValueFromMetaData() throws OseeCoreException {
      return getType().getDefaultValue();
   }

   private AttributeType getType() throws OseeCoreException {
      return attributeTypeCache.getByGuid(getOrcsData().getTypeUuid());
   }

   /**
    * @return attributeType Attribute Type Information
    * @throws OseeCoreException
    */
   @Override
   public IAttributeType getAttributeType() throws OseeCoreException {
      return attributeTypeCache.getByGuid(getOrcsData().getTypeUuid());
   }

   /**
    * Currently this method provides support for quasi attribute type inheritance
    * 
    * @return whether this attribute's type or any of its super-types are the specified type
    * @throws OseeCoreException
    */
   @Override
   public boolean isOfType(IAttributeType otherAttributeType) throws OseeCoreException {
      return getAttributeType().equals(otherAttributeType);
   }

   public void resetModType() {
      getOrcsData().setModType(ModificationType.MODIFIED);
   }

   /**
    * Deletes the attribute
    */
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
   public boolean canDelete() {
      try {
         return getContainer().getAttributeCount(getAttributeType()) > getType().getMinOccurrences();
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
   public int getId() {
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

   @Override
   public boolean isDeleted() {
      return getModificationType().isDeleted();
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
   public int compareTo(Attribute<T> other) {
      return toString().compareTo(other.toString());
   }
}