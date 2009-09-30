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
package org.eclipse.osee.framework.skynet.core.types.field;

/**
 * @author Roberto E. Escobar
 */
public final class OseeField<T> extends AbstractOseeField<T> {

   private T object;

   public OseeField(T initValue) {
      super();
      set(initValue);
   }

   public OseeField() {
      super();
   }

   @Override
   public void set(T value) {
      isDirty |= ChangeUtil.isDifferent(get(), value);
      this.object = value;
   }

   @Override
   public T get() {
      return object;
   }
}
