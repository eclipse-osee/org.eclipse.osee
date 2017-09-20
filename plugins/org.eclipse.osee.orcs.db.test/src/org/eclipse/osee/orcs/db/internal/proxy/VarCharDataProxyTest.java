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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.db.mocks.MockDataHandler;
import org.eclipse.osee.orcs.db.mocks.MockLog;
import org.eclipse.osee.orcs.db.mocks.MockResourceNameResolver;
import org.eclipse.osee.orcs.db.mocks.Utility;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link VarCharDataProxy}
 *
 * @author Roberto E. Escobar
 */
public class VarCharDataProxyTest {

   @Test(expected = UnsupportedOperationException.class)
   public void testSetDisplayable() throws Exception {
      VarCharDataProxy proxy = new VarCharDataProxy();
      proxy.setDisplayableString("hello");
   }

   @Test
   public void testGetBigStringValue() throws Exception {
      String rawData = Utility.generateData(4001);
      byte[] zippedData = Utility.asZipped(rawData, "testData.txt");

      MockDataHandler handler = new MockDataHandler();
      handler.setContent(zippedData);

      Storage storage = new Storage(handler);
      storage.setLocator("validPath");

      VarCharDataProxy proxy = new VarCharDataProxy();
      proxy.setLogger(new MockLog());
      proxy.setStorage(storage);

      String value = proxy.getValueAsString();
      Assert.assertEquals(rawData, value);

      String displayable = proxy.getDisplayableString();
      Assert.assertEquals(rawData, displayable);
   }

   @Test
   public void testSetGetData() throws Exception {
      String longData = Utility.generateData(JdbcConstants.JDBC__MAX_VARCHAR_LENGTH + 1);
      byte[] zippedData = Utility.asZipped(longData, "myTest.txt");

      String shortStringData = Utility.generateData(JdbcConstants.JDBC__MAX_VARCHAR_LENGTH);

      MockDataHandler handler = new MockDataHandler();
      handler.setContent(zippedData);

      Storage storage = new Storage(handler);
      Assert.assertFalse(storage.isLocatorValid());
      Assert.assertEquals("", storage.getLocator());

      VarCharDataProxy proxy = new VarCharDataProxy();
      proxy.setLogger(new MockLog());
      proxy.setStorage(storage);

      // Short String data
      proxy.setData(shortStringData, "");
      Assert.assertFalse(storage.isLocatorValid());
      Assert.assertEquals("", storage.getLocator());

      String actual = proxy.getValueAsString();
      Assert.assertEquals(shortStringData, actual);

      checkData(proxy, shortStringData, "");

      // Long String data
      proxy.setData(shortStringData, "valid");
      Assert.assertTrue(storage.isLocatorValid());
      Assert.assertEquals("valid", storage.getLocator());

      String actual1 = proxy.getValueAsString();
      Assert.assertEquals(longData, actual1);

      checkData(proxy, shortStringData, "valid");
   }

   @Test
   public void testSetValue() throws Exception {
      String longData = Utility.generateData(JdbcConstants.JDBC__MAX_VARCHAR_LENGTH + 1);
      byte[] zippedData = Utility.asZipped(longData, "myTest.txt");

      MockDataHandler handler = new MockDataHandler();
      handler.setContent(zippedData);

      Storage storage = new Storage(handler);
      Assert.assertFalse(storage.isLocatorValid());
      Assert.assertEquals("", storage.getLocator());

      VarCharDataProxy proxy = new VarCharDataProxy();
      proxy.setLogger(new MockLog());
      proxy.setStorage(storage);

      Assert.assertTrue(proxy.setValue(null));
      Assert.assertTrue(proxy.setValue(null));

      Assert.assertTrue(proxy.setValue("hello"));
      Assert.assertFalse(proxy.setValue("hello"));
   }

   private static void checkData(DataProxy proxy, String dbValue, String locator) {
      Assert.assertEquals(dbValue, proxy.getRawValue());
      Assert.assertEquals(locator, proxy.getUri());
   }

   @Test
   public void testPersist() throws Exception {
      String longData = Utility.generateData(JdbcConstants.JDBC__MAX_VARCHAR_LENGTH + 1);
      byte[] zippedData = Utility.asZipped(longData, "myTest.txt");

      String shortData = Utility.generateData(JdbcConstants.JDBC__MAX_VARCHAR_LENGTH);

      MockDataHandler handler = new MockDataHandler();
      handler.setContent(zippedData);

      Storage storage = new Storage(handler);

      Assert.assertFalse(storage.isLocatorValid());
      Assert.assertEquals("", storage.getLocator());

      VarCharDataProxy proxy = new VarCharDataProxy();
      proxy.setLogger(new MockLog());
      proxy.setStorage(storage);

      // No call to save if data is not valid
      Assert.assertFalse(storage.isDataValid());
      proxy.persist();
      Assert.assertFalse(handler.isSave());
      Assert.assertEquals(-1, handler.getStorageId());

      // No call to save if short Data
      proxy.setValue(shortData);
      Assert.assertEquals(shortData, proxy.getValueAsString());
      Assert.assertFalse(storage.isDataValid());
      proxy.persist();
      Assert.assertFalse(handler.isSave());
      Assert.assertEquals(-1, handler.getStorageId());

      // Save long data
      MockResourceNameResolver resolver = new MockResourceNameResolver("remoteStorageName", "internalFileName");
      proxy.setResolver(resolver);

      proxy.setValue(longData);
      Assert.assertEquals(longData, proxy.getValueAsString());
      Assert.assertTrue(storage.isDataValid());

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] compressedData = storage.getContent();
      String fileName = Lib.decompressStream(new ByteArrayInputStream(compressedData), outputStream);
      Assert.assertEquals(resolver.getInternalFileName(), fileName);
      Assert.assertEquals(longData, outputStream.toString("UTF-8"));

      proxy.setGamma(51, true);
      proxy.persist();
      Assert.assertTrue(handler.isSave());
      Assert.assertEquals(51, handler.getStorageId());
      Assert.assertEquals(longData, proxy.getValueAsString());
   }

   @Test(expected = OseeCoreException.class)
   public void testPersistResolverException() throws Exception {
      String longData = Utility.generateData(JdbcConstants.JDBC__MAX_VARCHAR_LENGTH + 1);
      byte[] zippedData = Utility.asZipped(longData, "myTest.txt");

      MockDataHandler handler = new MockDataHandler();
      handler.setContent(zippedData);

      Storage storage = new Storage(handler);

      Assert.assertFalse(storage.isLocatorValid());
      Assert.assertEquals("", storage.getLocator());

      VarCharDataProxy proxy = new VarCharDataProxy();
      proxy.setLogger(new MockLog());
      proxy.setStorage(storage);

      // Save long data
      proxy.setValue(longData);
      Assert.assertEquals(longData, proxy.getValueAsString());
      Assert.assertTrue(storage.isDataValid());
      proxy.persist();
   }

   @Test
   public void testPurge() throws Exception {
      String longData = Utility.generateData(JdbcConstants.JDBC__MAX_VARCHAR_LENGTH + 1);
      byte[] zippedData = Utility.asZipped(longData, "myTest.txt");

      MockDataHandler handler = new MockDataHandler();
      handler.setContent(zippedData);

      Storage storage = new Storage(handler);

      Assert.assertFalse(storage.isLocatorValid());
      Assert.assertEquals("", storage.getLocator());

      VarCharDataProxy proxy = new VarCharDataProxy();
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
