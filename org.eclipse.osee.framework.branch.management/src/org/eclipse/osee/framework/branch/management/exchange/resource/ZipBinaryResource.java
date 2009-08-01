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
package org.eclipse.osee.framework.branch.management.exchange.resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;

/**
 * @author Roberto E. Escobar
 */
public final class ZipBinaryResource implements IResource {

   private final IResourceLocator locator;
   private final File entry;

   public ZipBinaryResource(File entry, IResourceLocator locator) {
      checkNotNull("FileEntry", entry);
      checkNotNull("IResourceLocator", locator);
      this.entry = entry;
      this.locator = locator;
   }

   private void checkNotNull(String argName, Object object) {
      if (object == null) {
         throw new IllegalArgumentException(String.format("Argument was null - [%s]", argName));
      }
   }

   @Override
   public InputStream getContent() throws IOException {
      return new BufferedInputStream(new FileInputStream(entry));
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
      return Lib.getExtension(entry.toString()).equals("zip");
   }
}
