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
package org.eclipse.osee.orcs.core.internal.attribute;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.AbstractProxy;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Roberto E. Escobar
 */
public class AttributeReadableProxy<T> extends AbstractProxy<Attribute<T>> implements AttributeReadable<T> {

   public AttributeReadableProxy(Attribute<T> proxied) {
      super(proxied);
   }

   private AttributeReadable<T> getObjectForRead() {
      return getProxiedObject();
   }

   @Override
   public int getId() {
      return getObjectForRead().getId();
   }

   @Override
   public long getGammaId() {
      return getObjectForRead().getGammaId();
   }

   @Override
   public ModificationType getModificationType() {
      return getObjectForRead().getModificationType();
   }

   @Override
   public IAttributeType getAttributeType() {
      return getObjectForRead().getAttributeType();
   }

   @Override
   public boolean isOfType(IAttributeType otherAttributeType) {
      return getObjectForRead().isOfType(otherAttributeType);
   }

   @Override
   public T getValue() throws OseeCoreException {
      return getObjectForRead().getValue();
   }

   @Override
   public String getDisplayableString() throws OseeCoreException {
      return getObjectForRead().getDisplayableString();
   }

   @Override
   public boolean isDeleted() {
      return getObjectForRead().isDeleted();
   }

}
