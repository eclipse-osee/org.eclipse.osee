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
package org.eclipse.osee.orcs.db.internal.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.util.ResourceLocator;
import org.eclipse.osee.orcs.db.mocks.MockResource;
import org.eclipse.osee.orcs.db.mocks.MockResourceManager;
import org.eclipse.osee.orcs.db.mocks.MockResourceNameResolver;
import org.eclipse.osee.orcs.db.mocks.Utility;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link ResourceHandler}
 * 
 * @author Roberto E. Escobar
 */
public class ResourceHandlerTest {

   @Test(expected = OseeCoreException.class)
   public void testAcquireException() throws OseeCoreException {
      DataResource resource = new DataResource();
      ResourceHandler handler = new ResourceHandler(null);
      handler.acquire(resource);
   }

   @Test(expected = OseeCoreException.class)
   public void testAcquireException2() throws OseeCoreException {
      DataResource resource = new DataResource();
      resource.setLocator("MyLocator");
      MockResourceManager resMgr = new MockResourceManager();
      ResourceHandler handler = new ResourceHandler(resMgr);
      handler.acquire(resource);
   }

   @Test
   public void testAcquire() throws Exception {
      String data = Utility.generateData(1000);
      byte[] zippedData = Utility.asZipped(data, "internalFileName");

      DataResource resource = new DataResource();
      resource.setLocator("MyLocator");
      resource.setResolver(new MockResourceNameResolver("storageName", "internalFileName"));

      final URI uri = new URI("file://mylocation.zip");
      final ResourceLocator locator = new ResourceLocator(uri);
      final MockResource remoteResource = new MockResource("data.zip", uri, zippedData, true);
      MockResourceManager resMgr = new MockResourceManager() {

         @Override
         public IResource acquire(IResourceLocator actualLoc, PropertyStore options) {
            Assert.assertEquals(locator, actualLoc);
            return remoteResource;
         }

         @Override
         public IResourceLocator getResourceLocator(String path) {
            Assert.assertEquals("MyLocator", path);
            return locator;
         }
      };
      ResourceHandler handler = new ResourceHandler(resMgr);
      byte[] actual = handler.acquire(resource);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      String fileName = Lib.decompressStream(new ByteArrayInputStream(actual), outputStream);
      Assert.assertEquals("internalFileName", fileName);
      Assert.assertEquals(data, outputStream.toString("UTF-8"));

      Assert.assertEquals("zip", resource.getExtension());
      Assert.assertEquals("application/zip", resource.getContentType());
      Assert.assertEquals("ISO-8859-1", resource.getEncoding());
   }

   @Test
   public void testSave() throws Exception {
      String data = Utility.generateData(1000);
      final byte[] zippedData = Utility.asZipped(data, "internalFileName");

      final int storageId = 45;
      DataResource resource = new DataResource("application/osee", "UTF-8", "osee", "attr://123/123/hello.txt");
      resource.setResolver(new MockResourceNameResolver("storageName", "internalFileName"));
      final URI uri = new URI("file://mylocation.zip");
      final ResourceLocator locator = new ResourceLocator(uri);
      MockResourceManager resMgr = new MockResourceManager() {

         @Override
         public IResourceLocator save(IResourceLocator actualLoc, IResource resource, PropertyStore options) throws OseeCoreException {
            Assert.assertEquals(locator, actualLoc);
            Assert.assertEquals(false, resource.isCompressed());
            try {
               byte[] data = Lib.inputStreamToBytes(resource.getContent());
               Assert.assertTrue(Arrays.equals(zippedData, data));
            } catch (IOException ex) {
               OseeExceptions.wrapAndThrow(ex);
            }
            return locator;
         }

         @Override
         public IResourceLocator generateResourceLocator(String protocol, String seed, String name) {
            Assert.assertEquals("attr", protocol);
            Assert.assertEquals(String.valueOf(storageId), seed);
            Assert.assertEquals("storageName.osee", name);
            return locator;
         }
      };

      ResourceHandler handler = new ResourceHandler(resMgr);
      Assert.assertEquals("attr://123/123/hello.txt", resource.getLocator());
      handler.save(storageId, resource, zippedData);
      Assert.assertEquals("file://mylocation.zip", resource.getLocator());
   }

   @Test(expected = OseeCoreException.class)
   public void testDeleteException() throws OseeCoreException {
      DataResource resource = new DataResource();
      ResourceHandler handler = new ResourceHandler(null);
      handler.delete(resource);
   }

   @Test(expected = OseeCoreException.class)
   public void testDeleteException2() throws OseeCoreException {
      DataResource resource = new DataResource();
      resource.setLocator("MyLocator");
      MockResourceManager resMgr = new MockResourceManager();
      ResourceHandler handler = new ResourceHandler(resMgr);
      handler.delete(resource);
   }

   @Test(expected = OseeDataStoreException.class)
   public void testDeleteFail() throws Exception {
      DataResource resource = new DataResource();
      resource.setLocator("MyLocator");
      resource.setResolver(new MockResourceNameResolver("storageName", "internalFileName"));

      final URI uri = new URI("file://mylocation");
      final ResourceLocator locator = new ResourceLocator(uri);
      MockResourceManager resMgr = new MockResourceManager() {
         @Override
         public int delete(IResourceLocator actualLoc) {
            Assert.assertEquals(locator, actualLoc);
            return IResourceManager.FAIL;
         }

         @Override
         public IResourceLocator getResourceLocator(String path) {
            Assert.assertEquals("MyLocator", path);
            return locator;
         }
      };
      ResourceHandler handler = new ResourceHandler(resMgr);
      handler.delete(resource);
   }

   @Test(expected = OseeDataStoreException.class)
   public void testDeleteResourceNotFound() throws Exception {
      DataResource resource = new DataResource();
      resource.setLocator("MyLocator");
      resource.setResolver(new MockResourceNameResolver("storageName", "internalFileName"));

      final URI uri = new URI("file://mylocation");
      final ResourceLocator locator = new ResourceLocator(uri);
      MockResourceManager resMgr = new MockResourceManager() {
         @Override
         public int delete(IResourceLocator actualLoc) {
            Assert.assertEquals(locator, actualLoc);
            return IResourceManager.RESOURCE_NOT_FOUND;
         }

         @Override
         public IResourceLocator getResourceLocator(String path) {
            Assert.assertEquals("MyLocator", path);
            return locator;
         }
      };
      ResourceHandler handler = new ResourceHandler(resMgr);
      handler.delete(resource);
   }

   @Test
   public void testDeleteOk() throws Exception {
      DataResource resource = new DataResource();
      resource.setLocator("MyLocator");
      resource.setResolver(new MockResourceNameResolver("storageName", "internalFileName"));

      final URI uri = new URI("file://mylocation");
      final ResourceLocator locator = new ResourceLocator(uri);
      MockResourceManager resMgr = new MockResourceManager() {
         @Override
         public int delete(IResourceLocator actualLoc) {
            Assert.assertEquals(locator, actualLoc);
            return IResourceManager.OK;
         }

         @Override
         public IResourceLocator getResourceLocator(String path) {
            Assert.assertEquals("MyLocator", path);
            return locator;
         }
      };
      ResourceHandler handler = new ResourceHandler(resMgr);
      handler.delete(resource);
   }

}
