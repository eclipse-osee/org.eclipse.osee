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
package org.eclipse.osee.framework.resource.management;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

/**
 * @author Roberto E. Escobar
 */
public class ByteStreamResource implements IResource {

   private final IResourceLocator locator;
   private final byte[] rawContent;
   private final boolean isCompressed;

   public ByteStreamResource(IResourceLocator locator, byte[] rawContent, boolean isCompressed) {
      this.locator = locator;
      this.rawContent = rawContent;
      this.isCompressed = isCompressed;
   }

   @Override
   public InputStream getContent() {
      return new ByteArrayInputStream(rawContent);
   }

   @Override
   public URI getLocation() {
      return locator.getLocation();
   }

   @Override
   public String getName() {
      String path = locator.getLocation().toASCIIString();
      int index = path.lastIndexOf("/");
      if (index != -1 && index + 1 < path.length()) {
         path = path.substring(index + 1, path.length());
      }
      return path;
   }

   @Override
   public boolean isCompressed() {
      return isCompressed;
   }
}