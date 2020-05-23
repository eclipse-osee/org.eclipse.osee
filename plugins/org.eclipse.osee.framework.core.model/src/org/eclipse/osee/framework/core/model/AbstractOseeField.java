/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.core.model;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeField<T> implements IOseeField<T> {

   protected boolean isDirty;

   public AbstractOseeField() {
      isDirty = false;
   }

   @Override
   public abstract void set(T value);

   @Override
   public abstract T get();

   @Override
   public void clearDirty() {
      this.isDirty = false;
   }

   @Override
   public boolean isDirty() {
      return isDirty;
   }

}
