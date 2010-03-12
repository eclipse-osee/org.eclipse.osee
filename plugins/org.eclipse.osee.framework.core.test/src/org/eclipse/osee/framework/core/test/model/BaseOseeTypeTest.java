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
package org.eclipse.osee.framework.core.test.model;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.test.mocks.MockAbstractOseeType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link AbstractOseeTest}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class BaseOseeTypeTest extends AbstractOseeTypeTest<MockAbstractOseeType> {

   public BaseOseeTypeTest(MockAbstractOseeType type, String guid, String name) {
      super(type, guid, name);
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 2; index++) {
         String guid = GUID.create();
         String name = "index: " + index;
         data.add(new Object[] {new MockAbstractOseeType(guid, name), guid, name});
      }
      return data;
   }
}
