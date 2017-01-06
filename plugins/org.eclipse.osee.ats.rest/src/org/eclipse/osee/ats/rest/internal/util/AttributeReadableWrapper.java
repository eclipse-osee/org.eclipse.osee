/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Donald G. Dunne
 */
public class AttributeReadableWrapper<T> implements IAttribute<T> {

   private final AttributeReadable<T> attr;

   public AttributeReadableWrapper(AttributeReadable<T> attr) {
      this.attr = attr;
   }

   @Override
   public T getValue() throws OseeCoreException {
      return attr.getValue();
   }

   @Override
   public Object getData() {
      return attr;
   }

   @Override
   public void delete() throws OseeCoreException {
      throw new UnsupportedOperationException("delete not supported on server");
   }

   @Override
   public void setValue(T value) throws OseeCoreException {
      throw new UnsupportedOperationException("setValue not supported on server");
   }

   @Override
   public Long getId() {
      return attr.getId();
   }

   @Override
   public AttributeTypeToken getAttrType() {
      return attr.getAttributeType();
   }

}
