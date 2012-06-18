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

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.AbstractProxy;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

public class ReadableArtifactProxy extends AbstractProxy<ArtifactImpl> implements ArtifactReadable {

   public ReadableArtifactProxy(ArtifactImpl originalArtifact) {
      super(originalArtifact);
   }

   private ArtifactReadable getObjectForRead() {
      return getProxiedObject();
   }

   @Override
   public int getLocalId() {
      return getObjectForRead().getLocalId();
   }

   @Override
   public IOseeBranch getBranch() {
      return getObjectForRead().getBranch();
   }

   @Override
   public String getHumanReadableId() {
      return getObjectForRead().getHumanReadableId();
   }

   @Override
   public int getTransactionId() {
      return getObjectForRead().getTransactionId();
   }

   @Override
   public IArtifactType getArtifactType() {
      return getObjectForRead().getArtifactType();
   }

   @Override
   public boolean isOfType(IArtifactType... otherTypes) {
      return getObjectForRead().isOfType(otherTypes);
   }

   @Override
   public Collection<IAttributeType> getExistingAttributeTypes() throws OseeCoreException {
      return getObjectForRead().getExistingAttributeTypes();
   }

   @Override
   public <T> List<AttributeReadable<T>> getAttributes() throws OseeCoreException {
      return getObjectForRead().getAttributes();
   }

   @Override
   public <T> List<AttributeReadable<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException {
      return getObjectForRead().getAttributes(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType) throws OseeCoreException {
      return getObjectForRead().getSoleAttributeAsString(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType, String defaultValue) throws OseeCoreException {
      return getObjectForRead().getSoleAttributeAsString(attributeType, defaultValue);
   }

   @Override
   public String getGuid() {
      return getObjectForRead().getGuid();
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      return getObjectForRead().matches(identities);
   }

   @Override
   public String getName() {
      return getObjectForRead().getName();
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public boolean isAttributeTypeValid(IAttributeType attributeType) throws OseeCoreException {
      return getObjectForRead().isAttributeTypeValid(attributeType);
   }

   @Override
   public <T> T getSoleAttributeValue(IAttributeType attributeType) throws OseeCoreException {
      return getObjectForRead().getSoleAttributeValue(attributeType);
   }

}
