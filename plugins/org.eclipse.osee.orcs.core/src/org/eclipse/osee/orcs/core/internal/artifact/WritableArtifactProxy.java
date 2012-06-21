/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.artifact;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.orcs.core.internal.transaction.TxVisitable;
import org.eclipse.osee.orcs.core.internal.transaction.TxVisitor;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.data.AttributeWriteable;

public class WritableArtifactProxy extends ReadableArtifactProxy implements ArtifactWriteable, TxVisitable {

   private boolean isCopyRequired;
   private ArtifactImpl original;
   private volatile boolean isWriteAllowed;

   public WritableArtifactProxy(ArtifactImpl originalArtifact) {
      super(originalArtifact);
      isWriteAllowed = true;
   }

   public ArtifactImpl getOriginal() {
      return original;
   }

   @Override
   public void setProxiedObject(ArtifactImpl artifact) {
      isCopyRequired = true;
      this.original = artifact;
      super.setProxiedObject(artifact);
   }

   @Override
   public void setWriteState(boolean isWriteAllowed) {
      this.isWriteAllowed = isWriteAllowed;
   }

   @Override
   public boolean isWriteAllowed() {
      return isWriteAllowed;
   }

   private synchronized ArtifactImpl getObjectForWrite() throws OseeCoreException {
      if (!isWriteAllowed()) {
         throw new OseeAccessDeniedException("The artifact being accessed has been invalidated");
      }
      if (isCopyRequired) {
         try {
            ArtifactImpl copy = getOriginal().clone();
            super.setProxiedObject(copy);
            isCopyRequired = false;
         } catch (CloneNotSupportedException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      return getProxiedObject();
   }

   @Override
   public void accept(TxVisitor visitor) throws OseeCoreException {
      visitor.visit(getProxiedObject());
   }

   @Override
   public void setName(String name) throws OseeCoreException {
      getObjectForWrite().setName(name);
   }

   @Override
   public void setArtifactType(IArtifactType artifactType) throws OseeCoreException {
      getObjectForWrite().setArtifactType(artifactType);
   }

   @Override
   public void createAttribute(IAttributeType attributeType) throws OseeCoreException {
      getObjectForWrite().createAttribute(attributeType);
   }

   @Override
   public <T> void createAttribute(IAttributeType attributeType, T value) throws OseeCoreException {
      getObjectForWrite().createAttribute(attributeType, value);
   }

   @Override
   public void createAttributeFromString(IAttributeType attributeType, String value) throws OseeCoreException {
      getObjectForWrite().createAttributeFromString(attributeType, value);
   }

   @Override
   public <T> void setSoleAttributeValue(IAttributeType attributeType, T value) throws OseeCoreException {
      getObjectForWrite().setSoleAttributeValue(attributeType, value);
   }

   @Override
   public void setSoleAttributeFromStream(IAttributeType attributeType, InputStream inputStream) throws OseeCoreException {
      getObjectForWrite().setSoleAttributeFromStream(attributeType, inputStream);
   }

   @Override
   public void setSoleAttributeFromString(IAttributeType attributeType, String value) throws OseeCoreException {
      getObjectForWrite().setSoleAttributeFromString(attributeType, value);
   }

   @Override
   public <T> void setAttributesFromValues(IAttributeType attributeType, Collection<T> values) throws OseeCoreException {
      getObjectForWrite().setAttributesFromValues(attributeType, values);
   }

   @Override
   public void setAttributesFromStrings(IAttributeType attributeType, Collection<String> values) throws OseeCoreException {
      getObjectForWrite().setAttributesFromStrings(attributeType, values);
   }

   @Override
   public void deleteSoleAttribute(IAttributeType attributeType) throws OseeCoreException {
      getObjectForWrite().deleteSoleAttribute(attributeType);
   }

   @Override
   public void deleteAttributes(IAttributeType attributeType) throws OseeCoreException {
      getObjectForWrite().deleteAttributes(attributeType);
   }

   @Override
   public void deleteAttributesWithValue(IAttributeType attributeType, Object value) throws OseeCoreException {
      getObjectForWrite().deleteAttributesWithValue(attributeType, value);
   }

   @Override
   public <T> List<AttributeWriteable<T>> getWriteableAttributes() throws OseeCoreException {
      return getObjectForWrite().getWriteableAttributes();
   }

   @Override
   public <T> List<AttributeWriteable<T>> getWriteableAttributes(IAttributeType attributeType) throws OseeCoreException {
      return getObjectForWrite().getWriteableAttributes();
   }

   @Override
   public boolean isDirty() throws OseeCoreException {
      return getObjectForWrite().isDirty();
   }

   @Override
   public boolean isDeleted() throws OseeCoreException {
      return getObjectForWrite().isDeleted();
   }
}
