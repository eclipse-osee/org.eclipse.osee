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

import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Roberto E. Escobar
 */
public class AttributeReadOnlyImpl<T> extends AbstractProxied<Attribute<T>> implements AttributeReadable<T> {

   public AttributeReadOnlyImpl(ExternalArtifactManager proxyManager, OrcsSession session, Attribute<T> proxiedObject) {
      super(proxyManager, session, proxiedObject);
   }

   @Override
   public Long getId() {
      return getProxiedObject().getId();
   }

   @Override
   public boolean isDeleted() {
      return getProxiedObject().isDeleted();
   }

   @Override
   public long getGammaId() {
      return getProxiedObject().getGammaId();
   }

   @Override
   public ModificationType getModificationType() {
      return getProxiedObject().getModificationType();
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return getProxiedObject().getAttributeType();
   }

   @Override
   public boolean isOfType(AttributeTypeId otherAttributeType) {
      return getProxiedObject().isOfType(otherAttributeType);
   }

   @Override
   public T getValue() {
      return getProxiedObject().getValue();
   }

   @Override
   public String getDisplayableString() {
      return getProxiedObject().getDisplayableString();
   }

}