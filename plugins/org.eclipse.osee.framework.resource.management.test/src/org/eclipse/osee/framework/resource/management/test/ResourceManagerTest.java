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
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorProvider;
import org.eclipse.osee.framework.resource.management.IResourceProvider;
import org.eclipse.osee.framework.resource.management.ResourceManager;
import org.eclipse.osee.framework.resource.management.test.mocks.MockLocatorProvider;
import org.eclipse.osee.framework.resource.management.test.mocks.MockResourceLocator;
import org.eclipse.osee.framework.resource.management.test.mocks.ResourceProviderAdaptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Resource Manager Test {@link ResourceManager}
 * 
 * @author Andrew M. Finkbeiner
 */
public class ResourceManagerTest {

   private IResourceLocatorProvider provider1;
   private IResourceLocatorProvider provider2;
   private IResourceLocatorProvider provider3;
   private ResourceManager manager;

   @Before
   public void setup() {
      provider1 = new MockLocatorProvider("protocol1");
      provider2 = new MockLocatorProvider("protocol2");
      provider3 = new MockLocatorProvider("protocol3");

      manager = new ResourceManager();
      manager.addResourceLocatorProvider(provider1);
      manager.addResourceLocatorProvider(provider2);
      manager.addResourceLocatorProvider(provider3);
   }

   //   private IResourceManager resourceManager;
   //   private IResourceLocatorManager locatorManager;
   //
   //   private URL testFileURL;
   //
   //   @Before
   //   public void setup() {
   //      resourceManager = new ResourceManager();
   //      locatorManager = new ResourceLocatorManager();
   //      testFileURL =
   //            getClass().getClassLoader().getResource("org/eclipse/osee/framework/resource/management/test/TestFile.txt");
   //   }
   @Test
   public void testAddRemoveProvider() {
      ResourceManager resourceManagerX = new ResourceManager();
      IResourceProvider provider1 = new ResourceProviderAdaptor();
      Assert.assertTrue(resourceManagerX.addResourceProvider(provider1));
      Assert.assertFalse(resourceManagerX.addResourceProvider(provider1)); // Add again
      Assert.assertTrue(resourceManagerX.removeResourceProvider(provider1));
   }

   @Test
   public void testExists() {

      //      public boolean exists(IResourceLocator locator) ;
   }

   @Test
   public void testAcquire() {
      //   public IResource acquire(IResourceLocator locator, Options options) ;
   }

   @Test
   public void testSave() {
      //   public IResourceLocator save(final IResourceLocator locatorHint, final IResource resource, final Options options) ;
   }

   @Test
   public void testDelete() {
      //   public int delete(IResourceLocator locator) ;
   }

   @Test
   public void testEmptyProvider() {
      ResourceManager manager = new ResourceManager();
      Assert.assertEquals(0, manager.getProtocols().size());

      try {
         manager.getResourceLocator("protocol");
         Assert.fail("This line should not be executed");
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof OseeNotFoundException);
      }
      try {
         manager.generateResourceLocator(null, null, null);
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof OseeNotFoundException);
      }
   }

   @Test
   public void testAddAndRemove() {
      ResourceManager testManager = new ResourceManager();

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
      List<String> actual = new ArrayList<>(manager.getProtocols());
      Assert.assertEquals(3, actual.size());

      Collections.sort(actual);
      int index = 0;
      Assert.assertEquals("protocol1", actual.get(index++));
      Assert.assertEquals("protocol2", actual.get(index++));
      Assert.assertEquals("protocol3", actual.get(index++));
   }

   @Test
   public void testGenerateLocator()  {
      // Test Protocol not found
      try {
         manager.generateResourceLocator("dummyProcotol", "", "");
         Assert.fail("This line should not be executed");
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof OseeNotFoundException);
      }

      IResourceLocator locator = manager.generateResourceLocator("protocol1", "ABCDE", "name1");
      Assert.assertTrue(locator instanceof MockResourceLocator);
      MockResourceLocator mockLocator = (MockResourceLocator) locator;
      Assert.assertEquals("ABCDE", mockLocator.getSeed());
      Assert.assertEquals("name1", mockLocator.getName());
      Assert.assertNull(mockLocator.getRawPath());
   }

   @Test
   public void testGetLocator()  {
      // Test Protocol not found
      try {
         manager.getResourceLocator("dummyProcotol://hello");
         Assert.fail("This line should not be executed");
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof OseeNotFoundException);
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

   //   @org.junit.Test
   //   public void testSaveAquireDelete() throws Exception {
   //      InputStream inputStream = null;
   //      IResourceLocator fileLocation = null;
   //      IResourceLocator actual = null;
   //      IResourceManager rm = resourceManager;
   //      IResourceLocatorManager rlg = locatorManager;
   //      try {
   //         fileLocation = rlg.generateResourceLocator("attr", "123456", "TestFile.txt");
   //
   //         IResource file = new Resource(testFileURL.toURI(), false);
   //         actual = rm.save(fileLocation, file, new Options());
   //         Assert.assertNotNull(actual);
   //         IResource resource = rm.acquire(actual, new Options());
   //         Assert.assertNotNull(resource);
   //         inputStream = resource.getContent();
   //         InputStreamReader reader = new InputStreamReader(inputStream);
   //         char[] buffer = new char[inputStream.available()];
   //         reader.read(buffer);
   //         Assert.assertEquals(new String(buffer), "This is a test.");
   //      } finally {
   //         if (inputStream != null) {
   //            inputStream.close();
   //         }
   //      }
   //
   //      int code = rm.delete(actual);
   //      Assert.assertTrue(code == IResourceManager.OK);
   //      Assert.assertNull(rm.acquire(actual, new Options()));
   //   }
   //
   //   @org.junit.Test
   //   public void testSaveAquireDeleteZipExtension() throws Exception {
   //      InputStream inputStream = null;
   //      IResourceLocator fileLocation = null;
   //      IResourceLocator actual = null;
   //      IResourceManager rm = resourceManager;
   //      IResourceLocatorManager rlg = locatorManager;
   //      try {
   //         fileLocation = rlg.generateResourceLocator("attr", "123456", "TestFile.txt");
   //
   //         IResource file = new Resource(testFileURL.toURI(), false);
   //         Options options = new Options();
   //         options.put(StandardOptions.CompressOnSave.name(), "true");
   //
   //         actual = rm.save(fileLocation, file, options);
   //         Assert.assertTrue(actual.getLocation().toASCIIString().contains("TestFile.txt.zip"));
   //         Assert.assertNotNull(actual);
   //         IResource resource = rm.acquire(actual, new Options());
   //         Assert.assertNotNull(resource);
   //      } finally {
   //         if (inputStream != null) {
   //            inputStream.close();
   //         }
   //      }
   //
   //      int code = rm.delete(actual);
   //      Assert.assertTrue(code == IResourceManager.OK);
   //      Assert.assertNull(rm.acquire(actual, new Options()));
   //   }
   //
   //   @org.junit.Test
   //   public void testListeners() throws Exception {
   //      IResourceManager rm = resourceManager;
   //      IResourceLocatorManager rlg = locatorManager;
   //      TestResourceListener listener = new TestResourceListener();
   //      rm.addResourceListener(listener);
   //
   //      checkListener(listener, false, false, false);
   //
   //      IResourceLocator fileLocation = rlg.generateResourceLocator("attr", "123456", "TestFile.txt");
   //
   //      IResource file = new Resource(testFileURL.toURI(), false);
   //
   //      IResourceLocator actual = rm.save(fileLocation, file, new Options());
   //      Assert.assertNotNull(actual);
   //
   //      checkListener(listener, false, true, false);
   //
   //      listener.reset();
   //      IResource resource = rm.acquire(fileLocation, new Options());
   //      Assert.assertNotNull(resource);
   //      checkListener(listener, true, false, false);
   //
   //      listener.reset();
   //      int code = rm.delete(actual);
   //      Assert.assertTrue(code == IResourceManager.OK);
   //      checkListener(listener, false, false, true);
   //      Assert.assertNull(rm.acquire(fileLocation, new Options()));
   //   }
   //
   //   @org.junit.Test
   //   public void testForFailOnOverwriteOfFile() throws Exception {
   //      IResourceLocator fileLocation = null;
   //      IResourceLocator actual = null;
   //      IResourceManager rm = resourceManager;
   //      IResourceLocatorManager rlg = locatorManager;
   //      fileLocation = rlg.generateResourceLocator("attr", "123456", "TestFile.txt");
   //      IResource file = new Resource(testFileURL.toURI(), false);
   //      actual = rm.save(fileLocation, file, new Options());
   //
   //      boolean overwrote = true;
   //      try {
   //         actual = rm.save(fileLocation, file, new Options());
   //      } catch (OseeCoreException ex) {
   //         overwrote = false;
   //      }
   //      Assert.assertFalse(overwrote);
   //
   //      Options options = new Options();
   //      options.put(StandardOptions.Overwrite.name(), true);
   //      actual = rm.save(fileLocation, file, options);
   //
   //      rm.delete(actual);
   //
   //      Assert.assertFalse(rm.exists(actual));
   //   }

   //   private void checkListener(MockResourceListener listener, boolean acquire, boolean save, boolean delete) {
   //      Assert.assertEquals(acquire, listener.isPostAcquire());
   //      Assert.assertEquals(acquire, listener.isPreAcquire());
   //      Assert.assertEquals(save, listener.isPreSave());
   //      Assert.assertEquals(save, listener.isPostSave());
   //      Assert.assertEquals(delete, listener.isPreDelete());
   //      Assert.assertEquals(delete, listener.isPostDelete());
   //   }

}
