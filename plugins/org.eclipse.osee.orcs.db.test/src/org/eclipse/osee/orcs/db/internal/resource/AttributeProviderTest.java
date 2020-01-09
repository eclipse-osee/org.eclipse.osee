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

package org.eclipse.osee.orcs.db.internal.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.IResourceProvider;
import org.eclipse.osee.framework.resource.management.util.ResourceLocator;
import org.eclipse.osee.orcs.SystemProperties;
import org.eclipse.osee.orcs.db.mocks.MockResource;
import org.eclipse.osee.orcs.db.mocks.MockSystemPreferences;
import org.eclipse.osee.orcs.db.mocks.Utility;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test Case for {@link AttributeProvider}
 *
 * @author Roberto E. Escobar
 */
public class AttributeProviderTest {

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @Test
   public void testGetSupportedProtocols() {
      IResourceProvider provider = new AttributeProvider();
      Collection<String> protocols = provider.getSupportedProtocols();
      Assert.assertEquals(1, protocols.size());
      Assert.assertEquals("attr", protocols.iterator().next());
   }

   @Test
   public void testIsValid() throws URISyntaxException {
      IResourceProvider provider = new AttributeProvider();
      Assert.assertFalse(provider.isValid(null));
      Assert.assertFalse(provider.isValid(new ResourceLocator(new URI("http://hello"))));
      Assert.assertTrue(provider.isValid(new ResourceLocator(new URI("attr://hello"))));
   }

   @Test(expected = OseeCoreException.class)
   public void testInitializationException1() throws Exception {
      AttributeProvider provider = new AttributeProvider();
      provider.getAttributeDataPath();
   }

   @Test(expected = OseeCoreException.class)
   public void testInitializationException2() throws Exception {
      AttributeProvider provider = new AttributeProvider();
      provider.getBinaryDataPath();
   }

   @Test(expected = OseeCoreException.class)
   public void testInitializationException3() throws Exception {
      MockSystemPreferences properties = new MockSystemPreferences() {
         @Override
         public String getValue(String key) {
            Assert.assertEquals(OseeClient.OSEE_APPLICATION_SERVER_DATA, key);
            return null;
         }
      };
      AttributeProvider provider = new AttributeProvider();
      provider.setSystemProperties(properties);
      provider.start();
      provider.getAttributeDataPath();
   }

   @Test
   public void testOps() throws Exception {
      String rawData = Utility.generateData(4001);
      byte[] zippedData = Utility.asZipped(rawData, "testData.txt");

      SystemProperties properties = new MockSystemPreferences() {
         @Override
         public String getValue(String key) {
            Assert.assertEquals(OseeClient.OSEE_APPLICATION_SERVER_DATA, key);
            return folder.getRoot().getAbsolutePath();
         }
      };

      AttributeProvider provider = new AttributeProvider();
      provider.setSystemProperties(properties);
      provider.start();

      IResource resource = new MockResource("testData.txt", new URI("file://path"), zippedData, true);
      IResourceLocator locator = new ResourceLocator(new URI("attr://123/456/testData.zip"));

      PropertyStore options = new PropertyStore();
      IResourceLocator savedLocator = provider.save(locator, resource, options);

      Assert.assertEquals("123/456/testData.zip", savedLocator.getRawPath());
      Assert.assertEquals("attr://123/456/testData.zip", savedLocator.getLocation().toASCIIString());
      Assert.assertEquals(ResourceConstants.ATTRIBUTE_RESOURCE_PROTOCOL, savedLocator.getProtocol());

      //check file was written
      File file = new File(folder.getRoot().getAbsolutePath() + "/attr/123/456/", "testData.zip");
      Assert.assertTrue(file.exists());

      byte[] actual = Lib.fileToBytes(file);
      Assert.assertTrue(Arrays.equals(zippedData, actual));

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      String fileName = Lib.decompressStream(new ByteArrayInputStream(actual), outputStream);
      Assert.assertEquals("testData.txt", fileName);
      Assert.assertEquals(rawData, outputStream.toString("UTF-8"));

      Assert.assertTrue(provider.exists(savedLocator));

      IResource remoteResource = provider.acquire(savedLocator, options);

      byte[] resourceBytes = null;
      InputStream inputStream = null;
      try {
         inputStream = remoteResource.getContent();
         resourceBytes = Lib.inputStreamToBytes(inputStream);
      } finally {
         Lib.close(inputStream);
      }

      Assert.assertTrue(Arrays.equals(zippedData, resourceBytes));

      ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
      String fileName1 = Lib.decompressStream(new ByteArrayInputStream(resourceBytes), outputStream1);
      Assert.assertEquals("testData.txt", fileName1);
      Assert.assertEquals(rawData, outputStream1.toString("UTF-8"));

      Assert.assertNotNull(resourceBytes);

      int result = provider.delete(savedLocator);
      Assert.assertEquals(IResourceManager.OK, result);

      Assert.assertFalse(provider.exists(savedLocator));
      Assert.assertFalse(file.exists());
   }
}
