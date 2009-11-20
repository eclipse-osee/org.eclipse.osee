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
package org.eclipse.osee.framework.core.test.fields;

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
