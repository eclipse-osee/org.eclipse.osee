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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.event.model.AttributeChange;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public abstract class Attribute<T> implements Comparable<Attribute<T>> {
   private AttributeType attributeType;
   private WeakReference<Artifact> artifactRef;
   private IAttributeDataProvider attributeDataProvider;
   private int attrId;
   private int gammaId;
   private boolean dirty;
   private ModificationType modificationType;

   void internalInitialize(IAttributeType attributeType, Artifact artifact, ModificationType modificationType, boolean markDirty, boolean setDefaultValue) throws OseeCoreException {
      this.attributeType = AttributeTypeManager.getType(attributeType);
      this.artifactRef = new WeakReference<Artifact>(artifact);
      this.modificationType = modificationType;

      try {
         Class<? extends IAttributeDataProvider> providerClass =
            AttributeTypeManager.getAttributeProviderClass(this.attributeType);
         Constructor<? extends IAttributeDataProvider> providerConstructor =
            providerClass.getConstructor(new Class[] {Attribute.class});
         attributeDataProvider = providerConstructor.newInstance(new Object[] {this});
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      if (setDefaultValue) {
         setToDefaultValue();
      }

      dirty = markDirty;
      uponInitialize();
   }

   /**
    * Base implementation does nothing. Subclasses may override to do setup that depends on the attribute state data.
    */
   @SuppressWarnings("unused")
   protected void uponInitialize() throws OseeCoreException {
      // provided for subclass implementation
   }

   public AttributeChange createAttributeChangeFromSelf() throws OseeDataStoreException {
      AttributeChange attributeChange = new AttributeChange();

      attributeChange.setAttrTypeGuid(attributeType.getGuid());
      attributeChange.setGammaId(gammaId);
      attributeChange.setAttributeId(attrId);
      attributeChange.setModTypeGuid(AttributeEventModificationType.getType(modificationType).getGuid());
      for (Object obj : attributeDataProvider.getData()) {
         if (obj == null) {
            attributeChange.getData().add("");
         } else if (obj instanceof String) {
            attributeChange.getData().add((String) obj);
         } else {
            OseeLog.log(Activator.class, Level.SEVERE, "Unhandled data type " + obj.getClass().getSimpleName());
         }
      }

      return attributeChange;
   }

   public void internalInitialize(IAttributeType attributeType, Artifact artifact, ModificationType modificationType, int attributeId, int gammaId, boolean markDirty, boolean setDefaultValue) throws OseeCoreException {
      internalInitialize(attributeType, artifact, modificationType, markDirty, setDefaultValue);
      this.attrId = attributeId;
      this.gammaId = gammaId;
   }

   private void markAsNewOrChanged() {
      if (isInDb()) {
         markAsChanged(ModificationType.MODIFIED);
      } else {
         markAsChanged(ModificationType.NEW);
      }
   }

   public void setValue(T value) throws OseeCoreException {
      checkIsRenameable(value);
      if (subClassSetValue(value)) {
         markAsNewOrChanged();
      }
   }

   public boolean setFromString(String value) throws OseeCoreException {
      T toSet = convertStringToValue(value);
      checkIsRenameable(toSet);
      boolean response = subClassSetValue(toSet);
      if (response) {
         markAsNewOrChanged();
      }
      return response;
   }

   private void checkIsRenameable(T value) throws OseeCoreException {
      if (attributeType.equals(CoreAttributeTypes.Name) && !value.equals(getValue())) {
         // Confirm artifact is fit to rename
         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
            IStatus result = check.isRenamable(Arrays.asList(getArtifact()));
            if (!result.isOK()) {
               throw new OseeCoreException(result.getMessage());
            }
         }
      }
   }

   protected abstract T convertStringToValue(String value) throws OseeCoreException;

   public final void resetToDefaultValue() throws OseeCoreException {
      modificationType = ModificationType.MODIFIED;
      setToDefaultValue();
   }

   protected void setToDefaultValue() throws OseeCoreException {
      String defaultValue = getAttributeType().getDefaultValue();
      if (defaultValue != null) {
         subClassSetValue(convertStringToValue(defaultValue));
      }
   }

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

   public abstract T getValue() throws OseeCoreException;

   public String getDisplayableString() throws OseeCoreException {
      return getAttributeDataProvider().getDisplayableString();
   }

   @Override
   public String toString() {
      try {
         return getDisplayableString();
      } catch (OseeCoreException ex) {
         return Lib.exceptionToString(ex);
      }
   }

   public IAttributeDataProvider getAttributeDataProvider() {
      return attributeDataProvider;
   }

   /**
    * @return <b>true</b> if this attribute is dirty
    */
   public boolean isDirty() {
      return dirty;
   }

   protected void markAsChanged(ModificationType modificationType) {
      setDirtyFlag(true);
      this.modificationType = modificationType;
   }

   public void setNotDirty() {
      setDirtyFlag(false);
   }

   private void setDirtyFlag(boolean dirty) {
      this.dirty = dirty;
      try {
         Artifact artifact = getArtifact();
         ArtifactCache.updateCachedArtifact(artifact.getArtId(), artifact.getFullBranch().getId());
      } catch (OseeCoreException ex) {
         OseeLog.log(Attribute.class, Level.SEVERE, ex);
      }
   }

   public Artifact getArtifact() throws OseeStateException {
      if (artifactRef.get() == null) {
         throw new OseeStateException("Artifact has been garbage collected");
      }
      return artifactRef.get();
   }

   /**
    * @return the attribute name/value description
    */
   public String getNameValueDescription() {
      return attributeType.getName() + ": " + toString();
   }

   /**
    * @return attributeType Attribute Type Information
    */
   public AttributeType getAttributeType() {
      return attributeType;
   }

   /**
    * Currently this method provides support for quasi attribute type inheritance
    * 
    * @return whether this attribute's type or any of its super-types are the specified type
    */
   public boolean isOfType(IAttributeType otherAttributeType) {
      return attributeType.equals(otherAttributeType);
   }

   public void resetModType() {
      this.modificationType = ModificationType.MODIFIED;
   }

   /**
    * Deletes the attribute
    */
   public final void setArtifactDeleted() {
      markAsChanged(ModificationType.ARTIFACT_DELETED);
   }

   /**
    * Deletes the attribute
    */
   public final void delete() throws OseeCoreException {
      if (isInDb()) {
         markAsChanged(ModificationType.DELETED);
      } else {
         getArtifact().deleteAttribute(this);
      }
   }

   public ModificationType getModificationType() {
      return modificationType;
   }

   public boolean canDelete() {
      try {
         return getArtifact().getAttributeCount(attributeType) > attributeType.getMinOccurrences();
      } catch (OseeCoreException ex) {
         return false;
      }
   }

   /**
    * Purges attribute binary data
    */
   void purge() throws OseeCoreException {
      getAttributeDataProvider().purge();
   }

   public void markAsPurged() {
      modificationType = ModificationType.DELETED;
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
   public int getId() {
      return attrId;
   }

   public int getGammaId() {
      return gammaId;
   }

   public void internalSetGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   public void internalSetAttributeId(int attrId) {
      this.attrId = attrId;
   }

   /**
    * @return the deleted
    */
   public boolean isDeleted() throws OseeStateException {
      try {
         return modificationType.isDeleted();
      } catch (NullPointerException ex) {
         OseeLog.logf(Activator.class, Level.SEVERE, ex,
            "Unexpected null modification type for artifact attribute [%d] gamma [%d] on artifact [%s]", getId(),
            getGammaId(), getArtifact().getSafeName());
      }
      return false;
   }

   /**
    * artifact.persist(); artifact.reloadAttributesAndRelations(); Will need to be called afterwards to see replaced
    * data in memory
    */
   public void replaceWithVersion(int gammaId) {
      modificationType = ModificationType.REPLACED_WITH_VERSION;
      this.gammaId = gammaId;
      setDirtyFlag(true);
   }

   public void introduce(Attribute<?> sourceAttr) {
      int sourceGamma = sourceAttr.getGammaId();
      if (gammaId != sourceGamma) {
         replaceWithVersion(sourceGamma);
      } else if (!sourceAttr.getModificationType().equals(modificationType)) {
         markAsChanged(sourceAttr.getModificationType());
      }
   }

   /**
    * @param modificationType the modificationType to set
    */
   public void internalSetModificationType(ModificationType modificationType) {
      this.modificationType = modificationType;
   }

   public void internalSetDeletedFromRemoteEvent() {
      internalSetModificationType(ModificationType.DELETED);
   }

   @Override
   public int compareTo(Attribute<T> other) {
      return toString().compareTo(other.toString());
   }
}