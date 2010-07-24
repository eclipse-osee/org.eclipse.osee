/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.test.model;

import junit.framework.Assert;
import org.eclipse.osee.coverage.store.TestUnitStore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class TestUnitStoreTest {

   @BeforeClass
   public static void setup() throws OseeCoreException {
      TestUnitStore.clearStore();
   }

   @AfterClass
   public static void cleanup() throws OseeCoreException {
      TestUnitStore.clearStore();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.store.TestUnitStore#addTestUnitNameToDb(java.lang.String, java.lang.String)}.
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testAddTestUnitNameToDb() throws OseeCoreException {
      int count = TestUnitStore.getTestUnitCount();
      Assert.assertEquals(0, count);
      Integer thisJavaId = TestUnitStore.getTestUnitId("This.java", true);
      int newCount = TestUnitStore.getTestUnitCount();
      Assert.assertEquals(1, newCount);

      Integer nameId = TestUnitStore.getTestUnitId("NotThis.java", false);
      Assert.assertNull(nameId);
      // Count should not have changed
      newCount = TestUnitStore.getTestUnitCount();
      Assert.assertEquals(1, newCount);

      String name = TestUnitStore.getTestUnitName(thisJavaId);
      Assert.assertEquals("This.java", name);
   }

}
