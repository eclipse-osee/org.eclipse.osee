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
    * {@link org.eclipse.osee.coverage.model.SimpleTestUnitProvider#getTestUnits(org.eclipse.osee.coverage.model.CoverageItem)}
    * .
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testAddGetTestUnits() {
      provider.addTestUnit(coverageItem1, "Test Unit 1");
      provider.addTestUnit(coverageItem1, "Test Unit 2");
      provider.addTestUnit(coverageItem1, "Test Unit 3");
      provider.addTestUnit(coverageItem1, "Test Unit 3");
      provider.addTestUnit(coverageItem1, "Test Unit 4");

      Assert.assertEquals(4, provider.getTestUnits(coverageItem1).size());

      // Should equal cause CoverageItems are "equal" (guids are same)
      CoverageItem coverageItem1b =
         new CoverageItem(coverageItem1.getGuid(), null, CoverageOptionManager.Not_Covered, "1");
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
         CoverageItem.createCoverageItem(null, coverageItem1.toXml(), CoverageOptionManagerDefault.instance(),
            new SimpleTestUnitProvider());
      Assert.assertEquals(coverageItem1.getName(), newCoverageItem.getName());
      Assert.assertEquals(coverageItem1.getGuid(), newCoverageItem.getGuid());
      Assert.assertEquals(coverageItem1.getOrderNumber(), newCoverageItem.getOrderNumber());
      Assert.assertEquals(coverageItem1.getCoverageMethod(), newCoverageItem.getCoverageMethod());
      Assert.assertEquals(coverageItem1.getFileContents(), newCoverageItem.getFileContents());
      Assert.assertEquals(coverageItem1.getRationale(), newCoverageItem.getRationale());
   }

}
