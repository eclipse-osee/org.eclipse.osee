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
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.orcs.db.mocks.MockDataHandler;
import org.eclipse.osee.orcs.db.mocks.MockLog;
import org.eclipse.osee.orcs.db.mocks.MockResourceManager;
import org.eclipse.osee.orcs.db.mocks.MockResourceNameResolver;
import org.eclipse.osee.orcs.db.mocks.Utility;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link UriDataProxy}
 *
 * @author Roberto E. Escobar
 */
public class UriDataProxyTest {
   private final IResourceManager resourceManager = new MockResourceManager();

   @Test
   public void testSetDisplayable() throws Exception {
      UriDataProxy proxy = new UriDataProxy();
      proxy.setDisplayableString("hello");

      Assert.assertEquals("hello", proxy.getDisplayableString());
   }

   private VarCharDataProxy createProxy(byte[] zippedData) {
      VarCharDataProxy proxy = new VarCharDataProxy();
      Storage storage = new Storage(resourceManager, proxy);
      storage.setLocator("validPath");
      storage.setContent(zippedData, null, null, null);
      proxy.setLogger(new MockLog());
      proxy.setStorage(storage);
      return proxy;
   }

   @Test
   public void testGetSetData() {

      VarCharDataProxy proxy = createProxy(null);
      Storage storage = proxy.getStorage();

      Assert.assertFalse(storage.isLocatorValid());

      proxy.setData("dummy", "locator");
      Assert.assertTrue(storage.isLocatorValid());
      Assert.assertEquals("locator", storage.getLocator());

      Assert.assertEquals("", proxy.getRawValue());
      Assert.assertEquals("locator", proxy.getUri());
   }

   @Test
   public void testSetGetValue() throws Exception {
      String data = Utility.generateData(1000);
      byte[] zippedData = Utility.asZipped(data, "internalFileName");

      MockDataHandler handler = new MockDataHandler();
      handler.setContent(zippedData);

      Storage storage = new Storage(handler);
      Assert.assertFalse(storage.isLocatorValid());
      Assert.assertEquals("", storage.getLocator());

      UriDataProxy proxy = new UriDataProxy();
      proxy.setLogger(new MockLog());
      proxy.setStorage(storage);

      // No content until valid locator
      Assert.assertNull(proxy.getValueAsBytes());
      Assert.assertEquals("", proxy.getValueAsString());

      proxy.setData("", "locator");
      Assert.assertTrue(storage.isLocatorValid());
      Assert.assertEquals("locator", storage.getLocator());

      ByteBuffer buffer = proxy.getValueAsBytes();
      Assert.assertTrue(Arrays.equals(data.getBytes("UTF-8"), buffer.array()));

      String actual = proxy.getValueAsString();
      Assert.assertEquals(data, actual);

      // No set when same data
      Assert.assertFalse(proxy.setValue(buffer));
      Assert.assertFalse(proxy.setValue(actual));

      // Set Null ByteBuffer
      Assert.assertTrue(proxy.setValue((ByteBuffer) null));
      Assert.assertEquals(null, proxy.getValueAsBytes());
      Assert.assertEquals("", proxy.getValueAsString());
      Assert.assertTrue(storage.isLocatorValid());
      Assert.assertEquals("locator", storage.getLocator());
      Assert.assertFalse(storage.isDataValid());
      Assert.assertEquals(null, storage.getContent());
      Assert.assertEquals("txt", storage.getExtension());
      Assert.assertEquals("txt/plain", storage.getContentType());
      Assert.assertEquals("UTF-8", storage.getEncoding());

      // Save long data
      MockResourceNameResolver resolver = new MockResourceNameResolver("remoteStorageName", "internalFileName");
      proxy.setResolver(resolver);

      // Set none-null string value
      Assert.assertTrue(proxy.setValue(data));
      Assert.assertEquals(data, proxy.getValueAsString());
      ByteBuffer buffer1 = proxy.getValueAsBytes();
      Assert.assertTrue(Arrays.equals(data.getBytes("UTF-8"), buffer1.array()));

      Assert.assertTrue(storage.isLocatorValid());
      Assert.assertTrue(storage.isDataValid());
      Assert.assertEquals("locator", storage.getLocator());
      Assert.assertTrue(Arrays.equals(zippedData, storage.getContent()));

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] compressedData = storage.getContent();
      String fileName = Lib.decompressStream(new ByteArrayInputStream(compressedData), outputStream);
      Assert.assertEquals(resolver.getInternalFileName(), fileName);
      Assert.assertEquals(data, outputStream.toString("UTF-8"));

      Assert.assertEquals("zip", storage.getExtension());
      Assert.assertEquals("application/zip", storage.getContentType());
      Assert.assertEquals("ISO-8859-1", storage.getEncoding());

      // Set Null String
      Assert.assertTrue(proxy.setValue((String) null));
      Assert.assertEquals(null, proxy.getValueAsBytes());
      Assert.assertEquals("", proxy.getValueAsString());
      Assert.assertTrue(storage.isLocatorValid());
      Assert.assertEquals("locator", storage.getLocator());
      Assert.assertFalse(storage.isDataValid());
      Assert.assertEquals(null, storage.getContent());
      Assert.assertEquals("txt", storage.getExtension());
      Assert.assertEquals("txt/plain", storage.getContentType());
      Assert.assertEquals("UTF-8", storage.getEncoding());

      // Set none-null ByteBuffer value
      Assert.assertTrue(proxy.setValue(ByteBuffer.wrap(data.getBytes("UTF-8"))));
      Assert.assertEquals(data, proxy.getValueAsString());
      ByteBuffer buffer2 = proxy.getValueAsBytes();
      Assert.assertTrue(Arrays.equals(data.getBytes("UTF-8"), buffer2.array()));

      Assert.assertTrue(storage.isLocatorValid());
      Assert.assertTrue(storage.isDataValid());
      Assert.assertEquals("locator", storage.getLocator());
      Assert.assertTrue(Arrays.equals(zippedData, storage.getContent()));

      ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
      byte[] compressedData1 = storage.getContent();
      String fileName1 = Lib.decompressStream(new ByteArrayInputStream(compressedData1), outputStream1);
      Assert.assertEquals(resolver.getInternalFileName(), fileName1);
      Assert.assertEquals(data, outputStream.toString("UTF-8"));

      Assert.assertEquals("zip", storage.getExtension());
      Assert.assertEquals("application/zip", storage.getContentType());
      Assert.assertEquals("ISO-8859-1", storage.getEncoding());
   }

   @Test(expected = OseeCoreException.class)
   public void testSetValueException() throws Exception {
      MockDataHandler handler = new MockDataHandler();

      Storage storage = new Storage(handler);
      Assert.assertFalse(storage.isLocatorValid());
      Assert.assertEquals("", storage.getLocator());

      UriDataProxy proxy = new UriDataProxy();
      proxy.setLogger(new MockLog());
      proxy.setStorage(storage);

      proxy.setValue("this is my data");
   }

   @Test
   public void testPersist() throws Exception {
      String data = Utility.generateData(1000);
      byte[] zippedData = Utility.asZipped(data, "internalFileName");

      MockDataHandler handler = new MockDataHandler();
      handler.setContent(zippedData);

      Storage storage = new Storage(handler);
      Assert.assertFalse(storage.isLocatorValid());
      Assert.assertEquals("", storage.getLocator());

      UriDataProxy proxy = new UriDataProxy();
      proxy.setLogger(new MockLog());
      proxy.setStorage(storage);

      // No call to save if data is not valid
      Assert.assertFalse(storage.isDataValid());
      proxy.persist();
      Assert.assertFalse(handler.isSave());
      Assert.assertEquals(-1, handler.getStorageId());

      // Save Data
      MockResourceNameResolver resolver = new MockResourceNameResolver("remoteStorageName", "internalFileName");
      proxy.setResolver(resolver);

      proxy.setValue(data);
      Assert.assertEquals(data, proxy.getValueAsString());
      Assert.assertTrue(storage.isDataValid());

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] compressedData = storage.getContent();
      String fileName = Lib.decompressStream(new ByteArrayInputStream(compressedData), outputStream);
      Assert.assertEquals(resolver.getInternalFileName(), fileName);
      Assert.assertEquals(data, outputStream.toString("UTF-8"));

      proxy.setGamma(51, true);
      proxy.persist();
      Assert.assertTrue(handler.isSave());
      Assert.assertEquals(51, handler.getStorageId());
      Assert.assertEquals(data, proxy.getValueAsString());
   }

   @Test
   public void testPurge() throws Exception {
      String data = Utility.generateData(1000);
      byte[] zippedData = Utility.asZipped(data, "myTest.txt");

      MockDataHandler handler = new MockDataHandler();
      handler.setContent(zippedData);

      Storage storage = new Storage(handler);

      Assert.assertFalse(storage.isLocatorValid());
      Assert.assertEquals("", storage.getLocator());

      UriDataProxy proxy = new UriDataProxy();
      proxy.setLogger(new MockLog());
      proxy.setStorage(storage);

      Assert.assertFalse(storage.isLocatorValid());
      proxy.purge();
      Assert.assertFalse(handler.isDelete());

      storage.setLocator("hello");
      Assert.assertTrue(storage.isLocatorValid());
      proxy.purge();
      Assert.assertTrue(handler.isDelete());
   }
}
