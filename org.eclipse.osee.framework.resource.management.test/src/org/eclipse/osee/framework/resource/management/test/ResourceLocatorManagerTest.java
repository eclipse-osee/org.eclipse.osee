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
package org.eclipse.osee.framework.resource.management.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceLocatorProvider;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;
import org.eclipse.osee.framework.resource.management.internal.ResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.test.mocks.MockLocatorProvider;
import org.eclipse.osee.framework.resource.management.test.mocks.MockResourceLocator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Cases for {@link ResourceLocatorManager}
 * 
 * @author Roberto E. Escobar
 */
public class ResourceLocatorManagerTest {

   private IResourceLocatorProvider provider1;
   private IResourceLocatorProvider provider2;
   private IResourceLocatorProvider provider3;
   private IResourceLocatorManager manager;

   @Before
   public void setup() {
      provider1 = new MockLocatorProvider("protocol1");
      provider2 = new MockLocatorProvider("protocol2");
      provider3 = new MockLocatorProvider("protocol3");

      manager = new ResourceLocatorManager();
      manager.addResourceLocatorProvider(provider1);
      manager.addResourceLocatorProvider(provider2);
      manager.addResourceLocatorProvider(provider3);
   }

   @Test
   public void testEmptyProvider() {
      IResourceLocatorManager manager = new ResourceLocatorManager();
      Assert.assertEquals(0, manager.getProtocols().size());

      try {
         manager.getResourceLocator("protocol");
         Assert.fail("This line should not be executed");
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof OseeStateException);
      }
      try {
         manager.generateResourceLocator(null, null, null);
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof OseeStateException);
      }
   }

   @Test
   public void testAddAndRemove() {
      IResourceLocatorManager testManager = new ResourceLocatorManager();

      Assert.assertTrue(testManager.addResourceLocatorProvider(provider1));
      Assert.assertTrue(testManager.addResourceLocatorProvider(provider2));

      Assert.assertEquals(2, testManager.getProtocols().size());

      Assert.assertFalse(testManager.addResourceLocatorProvider(provider2)); // Add the same one again
      Assert.assertEquals(2, testManager.getProtocols().size());

      Assert.assertTrue(testManager.removeResourceLocatorProvider(provider1));
      Assert.assertEquals(1, testManager.getProtocols().size());

      Assert.assertFalse(testManager.removeResourceLocatorProvider(provider1));// Remove the same one again
      Assert.assertEquals(1, testManager.getProtocols().size());

      Assert.assertTrue(testManager.removeResourceLocatorProvider(provider2));
      Assert.assertEquals(0, testManager.getProtocols().size());
   }

   @Test
   public void testGetProtocols() {
      List<String> actual = new ArrayList<String>(manager.getProtocols());
      Assert.assertEquals(3, actual.size());

      Collections.sort(actual);
      int index = 0;
      Assert.assertEquals("protocol1", actual.get(index++));
      Assert.assertEquals("protocol2", actual.get(index++));
      Assert.assertEquals("protocol3", actual.get(index++));
   }

   @Test
   public void testGenerateLocator() throws OseeCoreException {
      // Test Protocol not found
      try {
         manager.generateResourceLocator("dummyProcotol", "", "");
         Assert.fail("This line should not be executed");
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof MalformedLocatorException);
      }

      IResourceLocator locator = manager.generateResourceLocator("protocol1", "ABCDE", "name1");
      Assert.assertTrue(locator instanceof MockResourceLocator);
      MockResourceLocator mockLocator = (MockResourceLocator) locator;
      Assert.assertEquals("ABCDE", mockLocator.getSeed());
      Assert.assertEquals("name1", mockLocator.getName());
      Assert.assertNull(mockLocator.getRawPath());
   }

   @Test
   public void testGetLocator() throws OseeCoreException {
      // Test Protocol not found
      try {
         manager.getResourceLocator("dummyProcotol://hello");
         Assert.fail("This line should not be executed");
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof MalformedLocatorException);
      }

      IResourceLocator locator = manager.getResourceLocator("protocol1://one/two/three");
      Assert.assertEquals("protocol1", locator.getProtocol());
      Assert.assertEquals("protocol1://one/two/three", locator.getRawPath());

      locator = manager.getResourceLocator("protocol2://1");
      Assert.assertEquals("protocol2", locator.getProtocol());
      Assert.assertEquals("protocol2://1", locator.getRawPath());

      locator = manager.getResourceLocator("protocol3");
      Assert.assertEquals("protocol3", locator.getProtocol());
   }
}
