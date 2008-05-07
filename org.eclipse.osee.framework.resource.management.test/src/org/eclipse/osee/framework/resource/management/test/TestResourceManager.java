/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import junit.framework.TestCase;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceListener;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.Resource;
import org.eclipse.osee.framework.resource.management.StandardOptions;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestResourceManager extends TestCase {

   private URL testFileURL;

   public TestResourceManager() {
      testFileURL =
            getClass().getClassLoader().getResource("org/eclipse/osee/framework/resource/management/test/TestFile.txt");
   }

   public void testGettingTheResourceManager() {
      assertNotNull(Activator.getActivator().getResourceManager());
   }

   public void testGettingTheResourceLocatorManager() {
      assertNotNull(Activator.getActivator().getResourceLocatorManager());
   }

   public void testSaveAquireDelete() throws Exception {
      InputStream inputStream = null;
      IResourceLocator fileLocation = null;
      IResourceLocator actual = null;
      IResourceManager rm = Activator.getActivator().getResourceManager();
      IResourceLocatorManager rlg = Activator.getActivator().getResourceLocatorManager();
      try {
         fileLocation = rlg.generateResourceLocator("attr", "123456", "TestFile.txt");

         IResource file = new Resource(testFileURL.toURI(), false);
         actual = rm.save(fileLocation, file, new Options());
         assertNotNull(actual);
         IResource resource = rm.acquire(actual, new Options());
         assertNotNull(resource);
         inputStream = resource.getContent();
         InputStreamReader reader = new InputStreamReader(inputStream);
         char[] buffer = new char[inputStream.available()];
         reader.read(buffer);
         assertEquals(new String(buffer), "This is a test.");
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }

      int code = rm.delete(actual);
      assertTrue(code == IResourceManager.OK);
      assertNull(rm.acquire(actual, new Options()));
   }

   public void testSaveAquireDeleteZipExtension() throws Exception {
      InputStream inputStream = null;
      IResourceLocator fileLocation = null;
      IResourceLocator actual = null;
      IResourceManager rm = Activator.getActivator().getResourceManager();
      IResourceLocatorManager rlg = Activator.getActivator().getResourceLocatorManager();
      try {
         fileLocation = rlg.generateResourceLocator("attr", "123456", "TestFile.txt");

         IResource file = new Resource(testFileURL.toURI(), false);
         Options options = new Options();
         options.put(StandardOptions.CompressOnSave.name(), "true");

         actual = rm.save(fileLocation, file, options);
         assertTrue(actual.getLocation().toASCIIString().contains("TestFile.txt.zip"));
         assertNotNull(actual);
         IResource resource = rm.acquire(actual, new Options());
         assertNotNull(resource);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }

      int code = rm.delete(actual);
      assertTrue(code == IResourceManager.OK);
      assertNull(rm.acquire(actual, new Options()));
   }

   public void testListeners() throws Exception {
      IResourceManager rm = Activator.getActivator().getResourceManager();
      IResourceLocatorManager rlg = Activator.getActivator().getResourceLocatorManager();
      TestResourceListener listener = new TestResourceListener();
      rm.addResourceListener(listener);

      checkListener(listener, false, false, false);

      IResourceLocator fileLocation = rlg.generateResourceLocator("attr", "123456", "TestFile.txt");

      IResource file = new Resource(testFileURL.toURI(), false);

      IResourceLocator actual = rm.save(fileLocation, file, new Options());
      assertNotNull(actual);

      checkListener(listener, false, true, false);

      listener.reset();
      IResource resource = rm.acquire(fileLocation, new Options());
      assertNotNull(resource);
      checkListener(listener, true, false, false);

      listener.reset();
      int code = rm.delete(actual);
      assertTrue(code == IResourceManager.OK);
      checkListener(listener, false, false, true);
      assertNull(rm.acquire(fileLocation, new Options()));
   }

   public void testForFailOnOverwriteOfFile() throws Exception {
      IResourceLocator fileLocation = null;
      IResourceLocator actual = null;
      IResourceManager rm = Activator.getActivator().getResourceManager();
      IResourceLocatorManager rlg = Activator.getActivator().getResourceLocatorManager();
      fileLocation = rlg.generateResourceLocator("attr", "123456", "TestFile.txt");
      IResource file = new Resource(testFileURL.toURI(), false);
      actual = rm.save(fileLocation, file, new Options());

      boolean overwrote = true;
      try {
         actual = rm.save(fileLocation, file, new Options());
      } catch (IOException ex) {
         overwrote = false;
      }
      assertFalse(overwrote);

      Options options = new Options();
      options.put(StandardOptions.Overwrite.name(), true);
      actual = rm.save(fileLocation, file, options);

      rm.delete(actual);

      assertFalse(rm.exists(actual));

   }

   private void checkListener(TestResourceListener listener, boolean acquire, boolean save, boolean delete) {
      assertEquals(acquire, listener.postAcquire);
      assertEquals(acquire, listener.preAcquire);
      assertEquals(save, listener.preSave);
      assertEquals(save, listener.postSave);
      assertEquals(delete, listener.preDelete);
      assertEquals(delete, listener.postDelete);
   }

   private final class TestResourceListener implements IResourceListener {
      public boolean postAcquire;
      public boolean preAcquire;
      public boolean preSave;
      public boolean postSave;
      public boolean preDelete;
      public boolean postDelete;
      public IResource resource;
      public IResourceLocator locator;

      public TestResourceListener() {
         reset();
      }

      public void reset() {
         postAcquire = false;
         preAcquire = false;
         preSave = false;
         postSave = false;
         preDelete = false;
         postDelete = false;
         this.resource = null;
         this.locator = null;
      }

      @Override
      public void onPostAcquire(IResource resource) {
         postAcquire = true;
         this.resource = resource;
      }

      @Override
      public void onPreAcquire(IResourceLocator locator) {
         preAcquire = true;
         this.locator = locator;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.resource.management.IResourceListener#onPostDelete(org.eclipse.osee.framework.resource.management.IResourceLocator)
       */
      @Override
      public void onPostDelete(IResourceLocator locator) {
         postDelete = true;
         this.locator = locator;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.resource.management.IResourceListener#onPostSave(org.eclipse.osee.framework.resource.management.IResourceLocator, org.eclipse.osee.framework.resource.management.IResource, org.eclipse.osee.framework.resource.management.Options)
       */
      @Override
      public void onPostSave(IResourceLocator locator, IResource resource, Options options) {
         postSave = true;
         this.resource = resource;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.resource.management.IResourceListener#onPreDelete(org.eclipse.osee.framework.resource.management.IResourceLocator)
       */
      @Override
      public void onPreDelete(IResourceLocator locator) {
         preDelete = true;
         this.locator = locator;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.resource.management.IResourceListener#onPreSave(org.eclipse.osee.framework.resource.management.IResourceLocator, org.eclipse.osee.framework.resource.management.IResource, org.eclipse.osee.framework.resource.management.Options)
       */
      @Override
      public void onPreSave(IResourceLocator locator, IResource resource, Options options) {
         preSave = true;
         this.locator = locator;
      }
   };

}
