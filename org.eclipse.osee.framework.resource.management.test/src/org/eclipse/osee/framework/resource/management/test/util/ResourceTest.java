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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.util.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
/**
 * Test Cases for {@link Resource}
 * 
 * @author Roberto E. Escobar
 */
public class ResourceTest {

   private IResource resource;
   private final String expectedName;
   private final boolean expectedCompressed;
   private final String rawPath;

   public ResourceTest(String rawPath, String expectedName, boolean expectedCompressed) {
      this.rawPath = rawPath;
      this.expectedName = expectedName;
      this.expectedCompressed = expectedCompressed;
      construct();
   }

   private void construct() {
      try {
         this.resource = new Resource(new URI(rawPath), expectedCompressed);
      } catch (Exception ex) {
         Assert.fail("Failed Resource creation");
      }
      Assert.assertNotNull(resource);
   }

   @Test
   public void testIsCompressed() throws IOException {
      Assert.assertEquals(expectedCompressed, resource.isCompressed());
   }

   @Test
   public void testGetName() throws IOException {
      Assert.assertEquals(expectedName, resource.getName());
   }

   @Test
   public void testGetLocation() throws IOException {
      Assert.assertEquals(rawPath, resource.getLocation().toASCIIString());
   }

   @Parameters
   public static Collection<Object[]> data() throws URISyntaxException {
      List<Object[]> data = new ArrayList<Object[]>();
      data.add(new Object[] {"file://1/sample.file", "sample.file", true});
      data.add(new Object[] {"http://www.url.com", "www.url.com", false,});
      data.add(new Object[] {"http://www.url.com/hello", "hello", false,});

      return data;
   }
}
