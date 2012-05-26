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
package org.eclipse.osee.orcs.core.internal.transaction;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.data.WritableArtifact;

public class WritableArtifactProxy implements WritableArtifact {

   private volatile boolean isCopyRequired;
   private Artifact proxied;
   private Artifact originalArtifact;

   public WritableArtifactProxy(Artifact originalArtifact) {
      setProxiedObject(originalArtifact);
   }

   public Artifact getOriginal() {
      return originalArtifact;
   }

   void setProxiedObject(Artifact artifact) {
      isCopyRequired = true;
      this.originalArtifact = artifact;
      this.proxied = originalArtifact;
   }

   private synchronized Artifact getObjectForWrite() {
      if (isCopyRequired) {
         proxied = originalArtifact.copy();
         isCopyRequired = false;
      }
      return proxied;
   }

   @Override
   public int getId() {
      return proxied.getId();
   }

   @Override
   public IOseeBranch getBranch() {
      return proxied.getBranch();
   }

   @Override
   public String getHumanReadableId() {
      return proxied.getHumanReadableId();
   }

   @Override
   public int getTransactionId() {
      return proxied.getTransactionId();
   }

   @Override
   public IArtifactType getArtifactType() {
      return proxied.getArtifactType();
   }

   @Override
   public boolean isOfType(IArtifactType... otherTypes) {
      return proxied.isOfType(otherTypes);
   }

   @Override
   public Collection<IAttributeType> getAttributeTypes() throws OseeCoreException {
      return proxied.getAttributeTypes();
   }

   @Override
   public <T> List<ReadableAttribute<T>> getAttributes() throws OseeCoreException {
      return proxied.getAttributes();
   }

   @Override
   public <T> List<ReadableAttribute<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException {
      return proxied.getAttributes(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType) throws OseeCoreException {
      return proxied.getSoleAttributeAsString(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType, String defaultValue) throws OseeCoreException {
      return proxied.getSoleAttributeAsString(attributeType, defaultValue);
   }

   @Override
   public long getGammaId() {
      return proxied.getGammaId();
   }

   @Override
   public ModificationType getModificationType() {
      return proxied.getModificationType();
   }

   @Override
   public String getGuid() {
      return proxied.getGuid();
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      return proxied.matches(identities);
   }

   @Override
   public String getName() {
      return proxied.getName();
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
   public <T> void setSoleAttribute(IAttributeType attributeType, T value) throws OseeCoreException {
      getObjectForWrite().setSoleAttribute(attributeType, value);
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
   public void setAttributes(IAttributeType attributeType, Collection<String> values) throws OseeCoreException {
      getObjectForWrite().setAttributes(attributeType, values);
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

   //// GRAPH Stuff
   @Override
   public List<WritableArtifact> getChildren() throws OseeCoreException {
      return null;
   }

}
