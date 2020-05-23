/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.model.fields;

/**
 * @author Roberto E. Escobar
 */
public class FieldGetSetTestData<T> {
   private final T setValue;
   private final T expectedValue;
   private final boolean expectedDirty;
   private final boolean clearBeforeRun;
   private final Class<? extends Throwable> error;

   public FieldGetSetTestData(boolean clearBeforeRun, T setValue, T expectedValue, boolean expectedDirty, Class<? extends Throwable> error) {
      this.clearBeforeRun = clearBeforeRun;
      this.setValue = setValue;
      this.expectedValue = expectedValue;
      this.expectedDirty = expectedDirty;
      this.error = error;
   }

   public FieldGetSetTestData(boolean clearBeforeRun, T setValue, T expectedValue, boolean expectedDirty) {
      this(clearBeforeRun, setValue, expectedValue, expectedDirty, null);
   }

   public boolean isClearBeforeRun() {
      return clearBeforeRun;
   }

   public T getSetValue() {
      return setValue;
   }

   public T getExpectedValue() {
      return expectedValue;
   }

   public boolean isExpectedDirty() {
      return expectedDirty;
   }

   public boolean throwsError() {
      return getError() != null;
   }

   public Class<? extends Throwable> getError() {
      return error;
   }
}
