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

import org.eclipse.osee.framework.jdk.core.util.Compare;

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
      isDirty |= Compare.isDifferent(get(), value);
      this.object = value;
   }

   @Override
   public T get() {
      return object;
   }
}
