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
package org.eclipse.osee.ats.core.client.internal.workdef;

import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * @author Donald G. Dunne
 */
public class AttributeWrapper<T> implements IAttribute<T> {

   private final Attribute<T> attr;

   public AttributeWrapper(Attribute<T> attr) {
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
      attr.delete();
   }

   @Override
   public void setValue(T value) throws OseeCoreException {
      attr.setValue(value);
   }

   @Override
   public Long getId() {
      return attr.getId();
   }

   @Override
   public IAttributeType getAttrType() {
      return attr.getAttributeType();
   }
}