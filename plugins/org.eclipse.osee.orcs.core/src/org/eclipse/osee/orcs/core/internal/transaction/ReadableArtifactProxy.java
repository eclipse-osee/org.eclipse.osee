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

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;

public class ReadableArtifactProxy implements ReadableArtifact {

   public Artifact proxiedObject;

   public ReadableArtifactProxy(Artifact proxiedObject) {
      this.proxiedObject = proxiedObject;
   }

   public Artifact getProxiedOject() {
      return proxiedObject;
   }

   @Override
   public int getId() {
      return proxiedObject.getId();
   }

   @Override
   public IOseeBranch getBranch() {
      return proxiedObject.getBranch();
   }

   @Override
   public String getHumanReadableId() {
      return proxiedObject.getHumanReadableId();
   }

   @Override
   public int getTransactionId() {
      return proxiedObject.getTransactionId();
   }

   @Override
   public IArtifactType getArtifactType() {
      return proxiedObject.getArtifactType();
   }

   @Override
   public boolean isOfType(IArtifactType... otherTypes) {
      return proxiedObject.isOfType(otherTypes);
   }

   @Override
   public Collection<IAttributeType> getAttributeTypes() throws OseeCoreException {
      return proxiedObject.getAttributeTypes();
   }

   @Override
   public <T> List<ReadableAttribute<T>> getAttributes() throws OseeCoreException {
      return proxiedObject.getAttributes();
   }

   @Override
   public <T> List<ReadableAttribute<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException {
      return proxiedObject.getAttributes(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType) throws OseeCoreException {
      return proxiedObject.getSoleAttributeAsString(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType, String defaultValue) throws OseeCoreException {
      return proxiedObject.getSoleAttributeAsString(attributeType, defaultValue);
   }

   @Override
   public long getGammaId() {
      return proxiedObject.getGammaId();
   }

   @Override
   public ModificationType getModificationType() {
      return proxiedObject.getModificationType();
   }

   @Override
   public String getGuid() {
      return proxiedObject.getGuid();
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      return proxiedObject.matches(identities);
   }

   @Override
   public String getName() {
      return proxiedObject.getName();
   }
}
