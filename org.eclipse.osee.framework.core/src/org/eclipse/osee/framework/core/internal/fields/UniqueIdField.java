/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.internal.fields;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.IOseeStorable;
import org.eclipse.osee.framework.core.util.Compare;

/**
 * @author Roberto E. Escobar
 */
public final class UniqueIdField extends AbstractOseeField<Integer> {

   private Integer value;

   public UniqueIdField() {
      super();
      this.value = IOseeStorable.UNPERSISTTED_VALUE;
      isDirty = true;
   }

   @Override
   public void set(Integer value) throws OseeCoreException {
      if (get() == IOseeStorable.UNPERSISTTED_VALUE) {
         isDirty |= Compare.isDifferent(get(), value);
         this.value = value;
      } else {
         throw new OseeStateException("can not change the type id once it has been set");
      }
   }

   @Override
   public Integer get() {
      return value;
   }
}
