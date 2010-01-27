/*
 * Created on Jan 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.store.TestUnitStore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class TestUnitStoreTest {

   public static CoverageItem item, item2;

   @BeforeClass
   public static void setup() throws OseeCoreException {
      item = new CoverageItem(null, CoverageOptionManagerDefault.Test_Unit, "1");
      item2 = new CoverageItem(null, CoverageOptionManagerDefault.Test_Unit, "2");
      TestUnitStore.instance().instance().clearTestUnitNames();
   }

   @AfterClass
   public static void cleanup() throws OseeCoreException {
      TestUnitStore.instance().instance().clearTestUnitNames();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.store.TestUnitStore#addTestUnitNameToDb(java.lang.String, java.lang.String)}.
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testAddTestUnitNameToDb() throws OseeCoreException {
      int count = TestUnitStore.instance().getNameCount();
      Assert.assertEquals(0, count);
      TestUnitStore.instance().getNameId("This.java", true);
      int newCount = TestUnitStore.instance().getNameCount();
      Assert.assertEquals(1, newCount);

      Integer nameId = TestUnitStore.instance().getNameId("NotThis.java", false);
      Assert.assertNull(nameId);
      // Count should not have changed
      newCount = TestUnitStore.instance().getNameCount();
      Assert.assertEquals(1, newCount);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.store.TestUnitStore#setTestUnits(org.eclipse.osee.coverage.model.CoverageUnit, java.util.Collection)}
    * .
    */
   @Test
   public void testSetTestUnits() throws OseeCoreException {
      List<String> names = Arrays.asList("Now.java", "Is.java", "The.java", "Time.java");
      TestUnitStore.instance().setTestUnits(item, names);
      Integer id = TestUnitStore.instance().getNameId("Now.java", false);
      Assert.assertNotNull(id);
      Assert.assertEquals(2, id.intValue());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.store.TestUnitStore#getTestUnits(org.eclipse.osee.coverage.model.CoverageUnit)}.
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testGetTestUnits() throws OseeCoreException {
      Collection<String> names = TestUnitStore.instance().getTestUnitNames(item);
      Assert.assertEquals(4, names.size());

      TestUnitStore.instance().setTestUnits(item2, Arrays.asList("Time.java", "The.java", "NewOne.java"));

      // ensure that only unique name entries exist
      Assert.assertEquals(6, TestUnitStore.instance().getNameCount());

      // Ensure that can retrieve items specific to coverageItem
      names = TestUnitStore.instance().getTestUnitNames(item);
      Assert.assertEquals(4, names.size());
      names = TestUnitStore.instance().getTestUnitNames(item2);
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
      Assert.assertEquals(3, TestUnitStore.instance().getTestUnitNames(item2).size());
      TestUnitStore.instance().removeTestUnits(item2, Arrays.asList("Time.java"));
      Assert.assertEquals(2, TestUnitStore.instance().getTestUnitNames(item2).size());
      TestUnitStore.instance().removeTestUnits(item2);
      Assert.assertEquals(0, TestUnitStore.instance().getTestUnitNames(item2).size());
   }

}
