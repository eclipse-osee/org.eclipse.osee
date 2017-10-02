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
package org.eclipse.osee.framework.core.model.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.model.IOseeField;

/**
 * @author Roberto E. Escobar
 */
public class OseeFieldTestData<T> {
   private final IOseeField<T> field;
   private final Object initExpectedValue;
   private final boolean initExpectedDirty;

   private final Collection<FieldGetSetTestData<T>> testDatas;

   public OseeFieldTestData(IOseeField<T> field, Object initExpectedValue, boolean initExpectedDirty, FieldGetSetTestData<T>... testDatas) {
      this.field = field;
      this.initExpectedValue = initExpectedValue;
      this.initExpectedDirty = initExpectedDirty;
      this.testDatas = new ArrayList<>();
      if (testDatas != null && testDatas.length > 0) {
         this.testDatas.addAll(Arrays.asList(testDatas));
      }
   }

   public IOseeField<T> getField() {
      return field;
   }

   public Object getInitExpectedValue() {
      return initExpectedValue;
   }

   public boolean isInitExpectedDirty() {
      return initExpectedDirty;
   }

   public Collection<FieldGetSetTestData<T>> getTestDatas() {
      return testDatas;
   }

   public void doSetValue(FieldGetSetTestData<T> testData)  {
      getField().set(testData.getSetValue());
   }
}