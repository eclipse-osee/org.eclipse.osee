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
import java.io.InputStream;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.internal.CompressedResourceBridge;
import org.eclipse.osee.framework.resource.management.util.Resources;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Cases for {@link Resources} {@link CompressedResourceBridge}
 * 
 * @author Roberto E. Escobar
 */
public class ResourcesTest {

   @Test
   public void testResourceFromBytes() {
      checkResourceFromBytes("file://myfile.txt", "myfile.txt", false, new byte[] {0, 1, 2, 3});
      checkResourceFromBytes("http://a/b/c/d/e/file.zip", "file.zip", true, new byte[] {4, 5, 6, 7, 8, 9});
   }

   @Ignore
   @Test
   public void testCompressResource() {
      Assert.fail("Error - implement this");
      //      Resources.compressResource(resource)
      //      Resources.decompressResource(resource)
   }

   @Ignore
   @Test
   public void testDeCompressResource() {
      Assert.fail("Error - implement this");
      //      Resources.compressResource(resource)
   }

   private void checkResourceFromBytes(String path, String name, boolean isCompressed, byte[] data) {
      IResource resource = Resources.createResourceFromBytes(data, path, isCompressed);
      Assert.assertEquals(isCompressed, resource.isCompressed());
      Assert.assertEquals(path, resource.getLocation().toASCIIString());
      Assert.assertEquals(name, resource.getName());
      InputStream inputStream = null;
      try {
         inputStream = resource.getContent();
         byte[] actuals = Lib.inputStreamToBytes(inputStream);
         Assert.assertArrayEquals(data, actuals);
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         Lib.close(inputStream);
      }
   }
}
