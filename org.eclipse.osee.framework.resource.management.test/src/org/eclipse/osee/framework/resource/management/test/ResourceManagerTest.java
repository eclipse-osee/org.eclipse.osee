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

import org.eclipse.osee.framework.resource.management.IResourceListener;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.IResourceProvider;
import org.eclipse.osee.framework.resource.management.internal.ResourceManager;
import org.eclipse.osee.framework.resource.management.test.mocks.MockResourceListener;
import org.eclipse.osee.framework.resource.management.test.mocks.ResourceProviderAdaptor;
import org.junit.Assert;
import org.junit.Test;

/**
 * Resource Manager Test {@link ResourceManager}
 * 
 * @author Andrew M. Finkbeiner
 */
public class ResourceManagerTest {

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
      IResourceManager resourceManagerX = new ResourceManager();
      IResourceProvider provider1 = new ResourceProviderAdaptor();
      Assert.assertTrue(resourceManagerX.addResourceProvider(provider1));
      Assert.assertFalse(resourceManagerX.addResourceProvider(provider1)); // Add again
      Assert.assertTrue(resourceManagerX.removeResourceProvider(provider1));
   }

   @Test
   public void testAddRemoveListener() {
      IResourceManager resourceManagerX = new ResourceManager();
      IResourceListener listener = new MockResourceListener();
      Assert.assertTrue(resourceManagerX.addResourceListener(listener));
      Assert.assertFalse(resourceManagerX.addResourceListener(listener)); // Add again
      Assert.assertTrue(resourceManagerX.removeResourceListener(listener));
      Assert.assertFalse(resourceManagerX.removeResourceListener(listener)); // Add again
   }

   @Test
   public void testExists() {

      //      public boolean exists(IResourceLocator locator) throws OseeCoreException;
   }

   @Test
   public void testAcquire() {
      //   public IResource acquire(IResourceLocator locator, Options options) throws OseeCoreException;
   }

   @Test
   public void testSave() {
      //   public IResourceLocator save(final IResourceLocator locatorHint, final IResource resource, final Options options) throws OseeCoreException;
   }

   @Test
   public void testDelete() {
      //   public int delete(IResourceLocator locator) throws OseeCoreException;
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

   private void checkListener(MockResourceListener listener, boolean acquire, boolean save, boolean delete) {
      Assert.assertEquals(acquire, listener.isPostAcquire());
      Assert.assertEquals(acquire, listener.isPreAcquire());
      Assert.assertEquals(save, listener.isPreSave());
      Assert.assertEquals(save, listener.isPostSave());
      Assert.assertEquals(delete, listener.isPreDelete());
      Assert.assertEquals(delete, listener.isPostDelete());
   }

}
