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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactChecks;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.IArtifactCheck;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Ryan D. Brooks
 */
public abstract class Attribute<T> {
   private final AttributeType attributeType;
   private final Artifact artifact;
   private IAttributeDataProvider attributeDataProvider;
   private int attrId;
   private int gammaId;
   private boolean deleted;
   private boolean dirty;

   protected Attribute(AttributeType attributeType, Artifact artifact) {
      this.attributeType = attributeType;
      this.artifact = artifact;
   }

   public void setValue(T value) throws OseeCoreException {
      if (attributeType.getName().equals("Name") && !value.equals(getValue())) {
         // Confirm artifact is fit to rename
         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
            Result result = check.isRenamable(Arrays.asList(artifact));
            if (result.isFalse()) throw new OseeCoreException(result.getText());
         }
      }

      if (subClassSetValue(value)) {
         setDirty();
      }
   }

   public boolean setFromString(String value) throws OseeCoreException {
      if (attributeType.getName().equals("Name") && !value.equals(getValue())) {
         // Confirm artifact is fit to rename
         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
            Result result = check.isRenamable(Arrays.asList(artifact));
            if (result.isFalse()) {
               throw new OseeCoreException(result.getText());
            }
         }
      }

      boolean response = subClassSetValue(convertStringToValue(value));
      if (response) {
         setDirty();
      }
      return response;
   }

   protected abstract T convertStringToValue(String value) throws OseeCoreException;

   public final void initializeToDefaultValue() throws OseeCoreException {
      subClassSetValue(convertStringToValue(getAttributeType().getDefaultValue()));
      dirty = true;
   }

   public boolean setValueFromInputStream(InputStream value) throws OseeCoreException {
      try {
         return setFromString(Lib.inputStreamToString(value));
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }

   /**
    * Subclasses must provide an implementation of this method and in general should not override the other set value
    * methods
    * 
    * @param value
    * @throws OseeCoreException
    */
   protected abstract boolean subClassSetValue(T value) throws OseeCoreException;

   public abstract T getValue() throws OseeCoreException;

   public String getDisplayableString() throws OseeCoreException {
      return getAttributeDataProvider().getDisplayableString();
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      try {
         builder.append(getDisplayableString());
      } catch (OseeCoreException ex) {
         builder.append(Lib.exceptionToString(ex));
      }
      return builder.toString();
   }

   /**
    * @param attributeDataProvider the attributeDataProvider to set
    * @throws OseeCoreException
    */
   public void setAttributeDataProvider(IAttributeDataProvider attributeDataProvider) throws OseeCoreException {
      this.attributeDataProvider = attributeDataProvider;
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

   private void setDirty() {
      dirty = true;

      // Kick Local Event
      try {
         OseeEventManager.kickArtifactModifiedEvent(this, ArtifactModType.Changed, artifact);
      } catch (Exception ex) {
         // do nothing
      }
   }

   public void setNotDirty() {
      dirty = false;
   }

   public Artifact getArtifact() {
      return artifact;
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
    * Currently this method provides support for quasi artifact type inheritance
    * 
    * @param artifactType
    * @return whether this artifact's type or any of its super-types are the specified type
    */
   public boolean isOfType(String otherAttributeTypeName) {
      String attributeTypeName = attributeType.getName();
      return attributeTypeName.equals(otherAttributeTypeName);
   }

   /**
    * Deletes the attribute
    */
   public final void delete() {
      deleted = true;
      dirty = true;
   }

   public boolean canDelete() {
      try {
         return artifact.getAttributeCount(attributeType.getName()) > attributeType.getMinOccurrences();
      } catch (OseeCoreException ex) {
         return false;
      }
   }

   /**
    * Purges the attribute from the database.
    */
   public void purge() throws OseeCoreException {
      getAttributeDataProvider().purge();
   }

   public void markAsPurged() {
      deleted = true;
      dirty = false;
   }

   /**
    * @return true if in datastore
    */
   public boolean isInDb() {
      return getGammaId() > 0;
   }

   /**
    * @return Returns the attrId.
    */
   public int getAttrId() {
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
   public boolean isDeleted() {
      return deleted;
   }

   /**
    * Called from remote events to mark this attribute as deleted
    */
   public void internalSetDeleted() {
      this.deleted = true;
      this.dirty = false;
   }

   public void revert() throws OseeCoreException {
      DbTransaction dbTransaction = new DbTransaction() {
         @Override
         protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
            ArtifactPersistenceManager.revertAttribute(connection, Attribute.this);
         }
      };
      dbTransaction.execute();
   }

   public static Attribute<?> initializeAttribute(Artifact artifact, int atttributeTypeId, int attributeId, int gammaId, Object... data) throws OseeCoreException {
      AttributeType attributeType = AttributeTypeManager.getType(atttributeTypeId);
      Attribute<?> attribute = artifact.createAttribute(attributeType, false);
      attribute.getAttributeDataProvider().loadData(data);
      attribute.internalSetAttributeId(attributeId);
      attribute.internalSetGammaId(gammaId);
      return attribute;
   }
}