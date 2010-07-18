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

import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CoverageOptionManagerTest {

   @Test
   public void testCoverageOptionManagerListOfCoverageOption() throws OseeArgumentException {
      CoverageOptionManager manager = new CoverageOptionManager();
      manager.add(new CoverageOption("Test_Unit", "This is the description", true));
      manager.add(new CoverageOption("New_One", "This is the description", false));

      Assert.assertEquals(2, manager.get().size());
      Assert.assertEquals(1, manager.getEnabled().size());
      try {
         manager.add(new CoverageOption("New_One", "Another descr", true));
         Assert.fail("This should not be allowed");
      } catch (Exception ex) {
         // do nothing; exception expected
      }

      String xml = manager.toXml();
      CoverageOptionManager newManager = new CoverageOptionManager(xml);

      Assert.assertEquals(2, newManager.get().size());
      Assert.assertEquals(1, newManager.getEnabled().size());
      try {
         manager.add(new CoverageOption("New_One", "Another descr", true));
         Assert.fail("This should not be allowed");
      } catch (Exception ex) {
         // do nothing; exception expected
      }
      for (CoverageOption option : manager.get()) {
         CoverageOption newOption = newManager.get(option.getName());
         Assert.assertNotNull(newOption);
         Assert.assertEquals(option.getName(), newOption.getName());
         Assert.assertEquals(option.getDescription(), newOption.getDescription());
         Assert.assertEquals(option.isEnabled(), newOption.isEnabled());
      }

   }

   @Test
   public void testCoverageOptionManagerDefault() {
      CoverageOptionManager defaultCoverageOptionManager = CoverageOptionManagerDefault.instance();
      Assert.assertEquals(5, defaultCoverageOptionManager.get().size());
      Assert.assertNotNull(defaultCoverageOptionManager.get("Test_Unit"));
      try {
         defaultCoverageOptionManager.add(new CoverageOption("New One", "Another descr", true));
         Assert.fail("This should not be allowed");
      } catch (Exception ex) {
         // do nothing; exception expected
      }
   }
}
