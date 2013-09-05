/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.proxy.impl;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeId;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Megumi Telles
 */
public class ArtifactReadOnlyImpl implements ArtifactReadable {

   private final Artifact proxiedObject;
   private final OrcsSession session;
   private final ExternalArtifactManager proxyManager;

   public ArtifactReadOnlyImpl(ExternalArtifactManager proxyManager, OrcsSession session, Artifact proxiedObject) {
      super();
      this.proxiedObject = proxiedObject;
      this.session = session;
      this.proxyManager = proxyManager;
   }

   @Override
   public String getGuid() {
      return getProxiedObject().getGuid();
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      return false;
   }

   @Override
   public String getName() {
      return getProxiedObject().getName();
   }

   @Override
   public int getLocalId() {
      return getProxiedObject().getLocalId();
   }

   @Override
   public IOseeBranch getBranch() throws OseeCoreException {
      return proxiedObject.getBranch();
   }

   @Override
   public int getTransaction() {
      return proxiedObject.getTransaction();
   }

   @Override
   public int getAttributeCount(IAttributeType type) throws OseeCoreException {
      return getProxiedObject().getAttributeCount(type);
   }

   @Override
   public int getAttributeCount(IAttributeType type, DeletionFlag deletionFlag) throws OseeCoreException {
      return getProxiedObject().getAttributeCount(type, deletionFlag);
   }

   @Override
   public boolean isAttributeTypeValid(IAttributeType attributeType) throws OseeCoreException {
      return getProxiedObject().isAttributeTypeValid(attributeType);
   }

   @Override
   public Collection<? extends IAttributeType> getValidAttributeTypes() throws OseeCoreException {
      return getProxiedObject().getValidAttributeTypes();
   }

   @Override
   public Collection<? extends IAttributeType> getExistingAttributeTypes() throws OseeCoreException {
      return getProxiedObject().getExistingAttributeTypes();
   }

   @Override
   public <T> T getSoleAttributeValue(IAttributeType attributeType) throws OseeCoreException {
      return getProxiedObject().getSoleAttributeValue(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType) throws OseeCoreException {
      return getProxiedObject().getSoleAttributeAsString(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType, String defaultValue) throws OseeCoreException {
      return getProxiedObject().getSoleAttributeAsString(attributeType, defaultValue);
   }

   @Override
   public <T> List<T> getAttributeValues(IAttributeType attributeType) throws OseeCoreException {
      return getProxiedObject().getAttributeValues(attributeType);
   }

   @Override
   public boolean isDeleted() {
      return getProxiedObject().isDeleted();
   }

   @Override
   public String getHumanReadableId() {
      return getProxiedObject().getHumanReadableId();
   }

   @Override
   public IArtifactType getArtifactType() throws OseeCoreException {
      return getProxiedObject().getArtifactType();
   }

   @Override
   public boolean isOfType(IArtifactType... otherTypes) throws OseeCoreException {
      return getProxiedObject().isOfType(otherTypes);
   }

   @Override
   public boolean equals(Object obj) {
      return proxiedObject.equals(obj);
   }

   @Override
   public int hashCode() {
      return proxiedObject.hashCode();
   }

   @Override
   public String toString() {
      return proxiedObject.toString();
   }

   public Artifact getProxiedObject() {
      return proxiedObject;
   }

   protected OrcsSession getSession() {
      return session;
   }

   public ExternalArtifactManager getProxyManager() {
      return proxyManager;
   }

   @Override
   public List<? extends AttributeReadable<Object>> getAttributes() throws OseeCoreException {
      return getProxiedObject().getAttributes();
   }

   @Override
   public <T> List<? extends AttributeReadable<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException {
      return getProxiedObject().getAttributes(attributeType);
   }

   @Override
   public List<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag) throws OseeCoreException {
      return getProxiedObject().getAttributes(deletionFlag);
   }

   @Override
   public <T> List<? extends AttributeReadable<T>> getAttributes(IAttributeType attributeType, DeletionFlag deletionFlag) throws OseeCoreException {
      return getProxiedObject().getAttributes(attributeType, deletionFlag);
   }

   @Override
   public AttributeReadable<Object> getAttributeById(AttributeId attributeId) throws OseeCoreException {
      return getProxiedObject().getAttributeById(attributeId);
   }

}
