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
import java.util.Collection;
import org.eclipse.osee.framework.core.model.internal.fields.UniqueIdField;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link UniqueIdField}
 *
 * @author Roberto E. Escobar
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class UniqueIdFieldTest extends BaseOseeFieldTest {

   public UniqueIdFieldTest(OseeFieldTestData<?> test) {
      super(test);
   }

   @SuppressWarnings("unchecked")
   @Parameters
   public static Collection<Object[]> data() {
      Collection<Object[]> data = new ArrayList<>();
      data.add(new Object[] {new OseeFieldTestData<>(new UniqueIdField(), //
         Id.SENTINEL, true, //
         new FieldGetSetTestData<>(false, Id.SENTINEL, Id.SENTINEL, true), //
         new FieldGetSetTestData<>(true, Id.SENTINEL, Id.SENTINEL, false), //
         new FieldGetSetTestData<>(false, 100L, 100L, true), //
         new FieldGetSetTestData<>(false, 200L, 100L, true, OseeStateException.class), //
         new FieldGetSetTestData<>(true, 300L, 100L, false, OseeStateException.class)//
         )});
      return data;
   }
}
