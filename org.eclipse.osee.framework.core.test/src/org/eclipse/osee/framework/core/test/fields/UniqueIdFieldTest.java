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

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.internal.fields.UniqueIdField;
import org.eclipse.osee.framework.core.model.IOseeStorable;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link UniqueIdField}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class UniqueIdFieldTest extends BaseOseeFieldTest {

   public UniqueIdFieldTest(OseeFieldTestData<?> test) {
      super(test);
   }

   @SuppressWarnings("unchecked")
   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      Collection<Object[]> data = new ArrayList<Object[]>();
      data.add(new Object[] {new OseeFieldTestData<Integer>(new UniqueIdField(), //
            IOseeStorable.UNPERSISTTED_VALUE, true, //
            new FieldGetSetTestData<Integer>(false, IOseeStorable.UNPERSISTTED_VALUE,
                  IOseeStorable.UNPERSISTTED_VALUE, true), //
            new FieldGetSetTestData<Integer>(true, IOseeStorable.UNPERSISTTED_VALUE,
                  IOseeStorable.UNPERSISTTED_VALUE, false), //
            new FieldGetSetTestData<Integer>(false, 100, 100, true), //
            new FieldGetSetTestData<Integer>(false, 200, 100, true, OseeStateException.class),//
            new FieldGetSetTestData<Integer>(true, 300, 100, false, OseeStateException.class)//
      )});
      return data;
   }
}
