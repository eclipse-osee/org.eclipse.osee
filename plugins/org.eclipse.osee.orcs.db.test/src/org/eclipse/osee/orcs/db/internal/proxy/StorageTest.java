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

import java.io.IOException;
import java.util.Arrays;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;
import org.eclipse.osee.orcs.db.mocks.MockDataHandler;
import org.eclipse.osee.orcs.db.mocks.MockResourceNameResolver;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link Storage}
 *
 * @author Roberto E. Escobar
 */
public class StorageTest extends DataResourceTest {

   private static final byte[] DATA_SET_1 = new byte[] {1, 2, 3, 4, 5, 6};
   private static final byte[] DATA_SET_2 = new byte[] {7, 8, 9, 10, 12, 13};

   private MockDataHandler handler;
   private final byte[] rawContent = DATA_SET_1;

   public StorageTest(String contentType, String encoding, String extension, String locator) {
      super(contentType, encoding, extension, locator);
   }

   @Override
   protected DataResource createResource() {
      handler = new MockDataHandler();
      Storage storage = new Storage(handler);
      storage.setContent(rawContent, extension, contentType, encoding);
      storage.setLocator(locator);
      return storage;
   }

   @Override
   protected Storage getResource() {
      return (Storage) super.getResource();
   }

   @Test
   public void testGetSetResolver() {
      Storage storage = getResource();

      ResourceNameResolver actual = storage.getResolver();
      Assert.assertNull(actual);

      MockResourceNameResolver resolver = new MockResourceNameResolver("storageName", "internalFileName");
      storage.setResolver(resolver);

      actual = storage.getResolver();
      Assert.assertEquals(resolver, actual);
   }

   @Test
   public void testIsDataValid() {
      Storage storage = getResource();
      Assert.assertFalse(storage.isDataValid());

      storage.setContent(DATA_SET_1, extension, contentType, encoding);
      Assert.assertTrue(storage.isDataValid());

      storage.setContent(new byte[0], extension, contentType, encoding);
      Assert.assertFalse(storage.isDataValid());
   }

   @Test
   public void testLoading() throws OseeCoreException {
      MockDataHandler loader = new MockDataHandler();
      loader.setContent(DATA_SET_1);

      Storage store = new Storage(loader);

      Assert.assertFalse(store.isLoadingAllowed());
      Assert.assertFalse(store.isLocatorValid());
      Assert.assertFalse(store.isInitialized());

      Assert.assertNull(store.getContent());
      Assert.assertFalse(store.isInitialized());

      store.setLocator("");
      Assert.assertFalse(store.isLocatorValid());
      Assert.assertFalse(store.isInitialized());
      Assert.assertFalse(store.isLoadingAllowed());

      store.setLocator(null);
      Assert.assertFalse(store.isLocatorValid());
      Assert.assertFalse(store.isInitialized());
      Assert.assertFalse(store.isLoadingAllowed());

      // Valid Locator - loading allowed
      store.setLocator("path");
      Assert.assertTrue(store.isLocatorValid());
      Assert.assertFalse(store.isInitialized());
      Assert.assertTrue(store.isLoadingAllowed());

      Assert.assertEquals(DATA_SET_1, store.getContent());
      Assert.assertFalse(store.isLoadingAllowed());
      Assert.assertEquals(store, loader.getResource());
      Assert.assertTrue(store.isInitialized());

      // Second Acquire not allowed
      loader.setContent(DATA_SET_2);
      Assert.assertEquals(DATA_SET_1, store.getContent());

      // upon change of locator - acquire allowed again
      store.setLocator("another location");
      Assert.assertTrue(store.isLoadingAllowed());
      Assert.assertFalse(store.isInitialized());
      Assert.assertEquals(DATA_SET_2, store.getContent());
      Assert.assertTrue(store.isInitialized());
   }

   @Test
   public void testGetInputStream() throws OseeCoreException, IOException {
      MockDataHandler loader = new MockDataHandler();
      loader.setContent(DATA_SET_1);

      Storage store = new Storage(loader);
      Assert.assertNull(store.getContent());

      store.setContent(DATA_SET_2, "ext", "ctype", "UTF-8");
      Assert.assertEquals(DATA_SET_2, store.getContent());
      Assert.assertFalse(loader.isAcquire());
      Assert.assertFalse(loader.isSave());
      Assert.assertFalse(loader.isDelete());

      // Multiple Reads
      Assert.assertTrue(Arrays.equals(DATA_SET_2, Lib.inputStreamToBytes(store.getInputStream())));
      Assert.assertTrue(Arrays.equals(DATA_SET_2, Lib.inputStreamToBytes(store.getInputStream())));

      // Input after acquire
      store.setLocator("path");
      Assert.assertTrue(Arrays.equals(DATA_SET_1, Lib.inputStreamToBytes(store.getInputStream())));
      Assert.assertTrue(loader.isAcquire());
      Assert.assertFalse(loader.isSave());
      Assert.assertFalse(loader.isDelete());
   }

   @Test
   public void testSetContentAndCopyTo() throws OseeCoreException {
      Storage store = new Storage(null);
      Assert.assertNull(store.getContent());
      Assert.assertFalse(store.isLoadingAllowed());

      store.setContent(DATA_SET_1, "ext", "ctype", "UTF-8");
      Assert.assertEquals(DATA_SET_1, store.getContent());
      Assert.assertEquals("ext", store.getExtension());
      Assert.assertEquals("ctype", store.getContentType());
      Assert.assertEquals("UTF-8", store.getEncoding());

      Storage store2 = new Storage(null);
      store2.setContent(DATA_SET_2, extension, contentType, encoding);
      Assert.assertEquals(DATA_SET_2, store2.getContent());
      Assert.assertEquals(extension, store2.getExtension());
      Assert.assertEquals(contentType, store2.getContentType());
      Assert.assertEquals(encoding, store2.getEncoding());

      store.copyTo(store2);
      Assert.assertTrue(Arrays.equals(DATA_SET_1, store2.getContent()));
      Assert.assertEquals("ext", store2.getExtension());
      Assert.assertEquals("ctype", store2.getContentType());
      Assert.assertEquals("UTF-8", store2.getEncoding());

      getResource().copyTo(store2);
      Assert.assertEquals(null, store2.getContent());
      Assert.assertEquals(extension, store2.getExtension());
      Assert.assertEquals(contentType, store2.getContentType());
      Assert.assertEquals(encoding, store2.getEncoding());
   }

   @Test
   public void testClean() throws OseeCoreException {
      MockDataHandler loader = new MockDataHandler();
      loader.setContent(DATA_SET_1);

      Storage store = new Storage(loader);
      store.setLocator("path");
      Assert.assertEquals(DATA_SET_1, store.getContent());

      store.setContent(DATA_SET_2, extension, contentType, encoding);
      Assert.assertEquals(DATA_SET_2, store.getContent());
      Assert.assertEquals(extension, store.getExtension());
      Assert.assertEquals(contentType, store.getContentType());
      Assert.assertEquals(encoding, store.getEncoding());

      Assert.assertEquals("path", store.getLocator());

      store.clear();
      Assert.assertNull(store.getContent());
      Assert.assertEquals("txt", store.getExtension());
      Assert.assertEquals("txt/plain", store.getContentType());
      Assert.assertEquals("UTF-8", store.getEncoding());

      Assert.assertEquals("", store.getLocator());
   }

   @Test
   public void testPersist() throws OseeCoreException {
      handler.reset();
      Storage storage = getResource();
      Assert.assertFalse(storage.isDataValid());
      storage.persist(45);
      Assert.assertNull(handler.getResource());

      Assert.assertFalse(handler.isAcquire());
      Assert.assertFalse(handler.isSave());
      Assert.assertFalse(handler.isDelete());

      storage.setContent(rawContent, extension, contentType, encoding);
      Assert.assertTrue(storage.isDataValid());
      storage.persist(45);
      Assert.assertEquals(storage, handler.getResource());
      Assert.assertEquals(45, handler.getStorageId());
      Assert.assertEquals(rawContent, handler.getContent());

      Assert.assertFalse(handler.isAcquire());
      Assert.assertTrue(handler.isSave());
      Assert.assertFalse(handler.isDelete());
   }

   @Test
   public void testPurge() throws OseeCoreException {
      handler.reset();
      Storage storage = getResource();
      Assert.assertTrue(storage.isLocatorValid());
      storage.purge();
      Assert.assertEquals(storage, handler.getResource());

      Assert.assertFalse(handler.isAcquire());
      Assert.assertFalse(handler.isSave());
      Assert.assertTrue(handler.isDelete());

      handler.reset();
      storage.setLocator(null);
      Assert.assertFalse(storage.isLocatorValid());
      storage.purge();
      Assert.assertNull(handler.getResource());

      Assert.assertFalse(handler.isAcquire());
      Assert.assertFalse(handler.isSave());
      Assert.assertFalse(handler.isDelete());

      storage.setLocator(locator);
      Assert.assertTrue(storage.isLocatorValid());
   }

}
