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

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.resource.management.DataResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link DataResource}
 *
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class DataResourceTest {

   private final DataResource resource;
   protected final String contentType;
   protected final String encoding;
   protected final String extension;
   protected final String locator;

   public DataResourceTest(String contentType, String encoding, String extension, String locator) {
      super();
      this.contentType = contentType;
      this.encoding = encoding;
      this.extension = extension;
      this.locator = locator;
      resource = createResource();
   }

   protected DataResource createResource() {
      return new DataResource(contentType, encoding, extension, locator);
   }

   protected DataResource getResource() {
      return resource;
   }

   @Test
   public void testGetAndSetContentType() {
      String original = resource.getContentType();
      Assert.assertEquals(contentType, original);

      resource.setContentType("testContentType");
      Assert.assertEquals("testContentType", resource.getContentType());

      resource.setContentType(original);
   }

   @Test
   public void testGetAndSetEncoding() {
      String original = resource.getEncoding();
      Assert.assertEquals(encoding, original);

      resource.setEncoding("testEncoding");
      Assert.assertEquals("testEncoding", resource.getEncoding());

      resource.setEncoding(original);
   }

   @Test
   public void testGetAndSetExtension() {
      String original = resource.getExtension();
      Assert.assertEquals(extension, original);

      resource.setExtension("testExtension");
      Assert.assertEquals("testExtension", resource.getExtension());

      resource.setExtension(original);
   }

   @Test
   public void testGetAndSetLocator() {
      String original = resource.getLocator();
      Assert.assertEquals(locator, original);
      Assert.assertTrue(resource.isLocatorValid());

      resource.setLocator("testLocator");
      Assert.assertEquals("testLocator", resource.getLocator());
      Assert.assertTrue(resource.isLocatorValid());

      resource.setLocator(null);
      Assert.assertFalse(resource.isLocatorValid());

      resource.setLocator("");
      Assert.assertFalse(resource.isLocatorValid());

      resource.setLocator(original);
   }

   @Parameters
   public static Collection<Object[]> data() {
      Collection<Object[]> data = new ArrayList<>();

      data.add(new Object[] {"application/zip", "ISO-8859-1", ".zip", "http://hello.com"});
      data.add(new Object[] {"txt/plain", "UTF-8", ".txt", "attr://123/1123/1231/hello.txt"});
      return data;
   }
}
