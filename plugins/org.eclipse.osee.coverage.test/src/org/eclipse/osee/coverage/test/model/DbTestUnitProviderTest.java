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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.store.DbTestUnitProvider;
import org.eclipse.osee.coverage.store.TestUnitStore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class DbTestUnitProviderTest {

   public static CoverageItem item, item2;
   public static DbTestUnitProvider dbTestUnitProvider;

   @BeforeClass
   public static void setup() throws OseeCoreException {
      item = new CoverageItem(null, CoverageOptionManagerDefault.Test_Unit, "1");
      item2 = new CoverageItem(null, CoverageOptionManagerDefault.Test_Unit, "2");
      dbTestUnitProvider = DbTestUnitProvider.instance();
      TestUnitStore.clearStore();
   }

   @AfterClass
   public static void cleanup() throws OseeCoreException {
      TestUnitStore.clearStore();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.store.TestUnitStore#setTestUnits(org.eclipse.osee.coverage.model.CoverageUnit, java.util.Collection)}
    * .
    */
   @Test
   public void testSetTestUnits() throws OseeCoreException {
      List<String> names = Arrays.asList("Now.java", "Is.java", "The.java", "Time.java");
      dbTestUnitProvider.setTestUnits(item, names);
      Integer id = TestUnitStore.getTestUnitId("Now.java", false);
      Assert.assertNotNull(id);
      Assert.assertEquals(1, id.intValue());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.store.TestUnitStore#getTestUnits(org.eclipse.osee.coverage.model.CoverageUnit)}.
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testGetTestUnits() throws OseeCoreException {
      Collection<String> names = dbTestUnitProvider.getTestUnits(item);
      Assert.assertEquals(4, names.size());

      dbTestUnitProvider.setTestUnits(item2, Arrays.asList("Time.java", "The.java", "NewOne.java"));

      // ensure that only unique name entries exist
      Assert.assertEquals(5, TestUnitStore.getTestUnitCount());

      // Ensure that can retrieve items specific to coverageItem
      names = dbTestUnitProvider.getTestUnits(item);
      Assert.assertEquals(4, names.size());
      names = dbTestUnitProvider.getTestUnits(item2);
      Assert.assertEquals(3, names.size());

   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.store.TestUnitStore#removeTestUnitsFromDb(java.lang.String, java.util.List)}.
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testRemoveTestUnitsFromDb() throws OseeCoreException {
      Assert.assertEquals(3, dbTestUnitProvider.getTestUnits(item2).size());
      dbTestUnitProvider.removeTestUnits(item2, Arrays.asList("Time.java"));
      Assert.assertEquals(2, dbTestUnitProvider.getTestUnits(item2).size());
      dbTestUnitProvider.removeTestUnits(item2);
      Assert.assertEquals(0, dbTestUnitProvider.getTestUnits(item2).size());
   }

}
