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
package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.HasId;
import org.eclipse.osee.framework.core.data.RelationalConstants;

/**
 * @author Roberto E. Escobar
 */
public abstract class OrcsObjectImpl<T extends Number> implements HasId<T> {

   private T id = null;

   @SuppressWarnings("unchecked")
   protected OrcsObjectImpl() {
      super();
      setLocalId((T) RelationalConstants.DEFAULT_ITEM_ID);
   }

   @Override
   public T getLocalId() {
      return id;
   }

   public void setLocalId(T id) {
      this.id = id;
   }

   @Override
   public String toString() {
      return "OrcsObject [id=" + id + "]";
   }
}
