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
package org.eclipse.osee.framework.resource.management.test.util;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.util.ResourceLocator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Cases for {@link ResourceLocator}
 * 
 * @author Roberto E. Escobar
 */
public class ResourceLocatorTest {

   @Test
   public void testGetRawPath() {
      IResourceLocator locator = createLocator("http://blah/www.myuri.com");
      Assert.assertEquals("blah/www.myuri.com", locator.getRawPath());

      locator = createLocator("file://a/b/c/d/e/f/x");
      Assert.assertEquals("a/b/c/d/e/f/x", locator.getRawPath());
   }

   @Test
   public void testGetProtocol() {
      IResourceLocator locator = createLocator("http://blah/www.myuri.com");
      Assert.assertEquals("http", locator.getProtocol());

      locator = createLocator("file://blah");
      Assert.assertEquals("file", locator.getProtocol());

      locator = createLocator("protocol://blah");
      Assert.assertEquals("protocol", locator.getProtocol());
   }

   @Test
   public void testGetLocation() {
      IResourceLocator locator = createLocator("http://blah/www.myuri.com");
      Assert.assertEquals("http://blah/www.myuri.com", locator.getLocation().toASCIIString());

      locator = createLocator("protocol://a/b/c/d/e/f/location");
      Assert.assertEquals("protocol://a/b/c/d/e/f/location", locator.getLocation().toASCIIString());

      locator = createLocator("x://a/b/c/");
      Assert.assertEquals("x://a/b/c/", locator.getLocation().toASCIIString());
   }

   @Test
   public void testNull() {
      try {
         new ResourceLocator(null);
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }
   }

   private IResourceLocator createLocator(String path) {
      IResourceLocator locator = null;
      try {
         URI uri = new URI(path);
         locator = new ResourceLocator(uri);
      } catch (URISyntaxException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return locator;
   }
}
