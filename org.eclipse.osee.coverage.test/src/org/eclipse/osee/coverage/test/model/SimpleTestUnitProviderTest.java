/*
 * Created on Jan 6, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test.model;

import junit.framework.Assert;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.SimpleTestUnitProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class SimpleTestUnitProviderTest {

   public static SimpleTestUnitProvider provider;
   public static CoverageItem coverageItem1, coverageItem2;

   @BeforeClass
   public static void setUp() {
      provider = new SimpleTestUnitProvider();
      coverageItem1 = new CoverageItem(null, CoverageOptionManager.Test_Unit, "1");
      coverageItem2 = new CoverageItem(null, CoverageOptionManager.Not_Covered, "2");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.SimpleTestUnitProvider#addTestUnitName(org.eclipse.osee.coverage.model.CoverageItem, java.lang.String)}
    * .
    */
   @Test
   public void testAddTestUnitName() {
      provider.addTestUnitName(coverageItem1, "Test Unit 1");
      provider.addTestUnitName(coverageItem1, "Test Unit 2");
      provider.addTestUnitName(coverageItem1, "Test Unit 3");
      provider.addTestUnitName(coverageItem1, "Test Unit 3");
      provider.addTestUnitName(coverageItem1, "Test Unit 4");
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.SimpleTestUnitProvider#getTestUnits(org.eclipse.osee.coverage.model.CoverageItem)}
    * .
    */
   @Test
   public void testGetTestUnits() {
      Assert.assertEquals(4, provider.getTestUnits(coverageItem1).size());

      // Should equal cause CoverageItems are "equal" (guids are same)
      CoverageItem coverageItem1b = new CoverageItem(null, CoverageOptionManager.Not_Covered, "1");
      coverageItem1b.setGuid(coverageItem1.getGuid());
      Assert.assertEquals(4, provider.getTestUnits(coverageItem1b).size());

      Assert.assertEquals(0, provider.getTestUnits(coverageItem2).size());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.SimpleTestUnitProvider#toXml(org.eclipse.osee.coverage.model.CoverageItem)}
    * .
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testToFromXml() throws OseeCoreException {
      CoverageItem newCoverageItem =
            new CoverageItem(null, coverageItem1.toXml(), CoverageOptionManagerDefault.instance());
      Assert.assertEquals(coverageItem1.getName(), newCoverageItem.getName());
      Assert.assertEquals(coverageItem1.getGuid(), newCoverageItem.getGuid());
      Assert.assertEquals(coverageItem1.getOrderNumber(), newCoverageItem.getOrderNumber());
      Assert.assertEquals(coverageItem1.getCoverageMethod(), newCoverageItem.getCoverageMethod());
      Assert.assertEquals(coverageItem1.getFileContents(), newCoverageItem.getFileContents());
      Assert.assertEquals(coverageItem1.getRationale(), newCoverageItem.getRationale());
   }

}
