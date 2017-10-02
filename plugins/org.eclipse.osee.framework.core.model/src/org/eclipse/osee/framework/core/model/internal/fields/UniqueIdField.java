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
package org.eclipse.osee.framework.core.model.internal.fields;

import org.eclipse.osee.framework.core.model.AbstractOseeField;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Compare;

/**
 * @author Roberto E. Escobar
 */
public final class UniqueIdField extends AbstractOseeField<Long> {

   private Long value;

   public UniqueIdField() {
      super();
      this.value = Id.SENTINEL;
      isDirty = true;
   }

   @Override
   public void set(Long value)  {
      if (Id.SENTINEL.equals(get())) {
         isDirty |= Compare.isDifferent(get(), value);
         this.value = value;
      } else {
         throw new OseeStateException("can not change the type id once it has been set");
      }
   }

   @Override
   public Long get() {
      return value;
   }
}
