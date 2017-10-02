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
