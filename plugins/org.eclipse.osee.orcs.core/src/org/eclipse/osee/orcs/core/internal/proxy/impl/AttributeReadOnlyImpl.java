/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.proxy.impl;

import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;

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
   public GammaId getGammaId() {
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
   public T getValue() {
      return getProxiedObject().getValue();
   }

   @Override
   public String getDisplayableString() {
      return getProxiedObject().getDisplayableString();
   }
}