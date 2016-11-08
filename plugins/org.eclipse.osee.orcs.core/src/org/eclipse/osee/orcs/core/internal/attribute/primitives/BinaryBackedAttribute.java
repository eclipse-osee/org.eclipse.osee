/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import org.eclipse.osee.orcs.core.ds.BinaryDataProxy;

/**
 * @author Roberto E. Escobar
 */
public abstract class BinaryBackedAttribute<T> extends AttributeImpl<T> {

   public BinaryBackedAttribute(Long id) {
      super(id);
   }

   @Override
   public BinaryDataProxy getDataProxy() {
      // this cast is always safe since the the data provider passed in the constructor to
      // the super class is of type  IBinaryAttributeDataProvider
      return (BinaryDataProxy) super.getDataProxy();
   }
}
