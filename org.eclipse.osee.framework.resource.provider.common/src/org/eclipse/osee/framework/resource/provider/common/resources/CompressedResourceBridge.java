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
package org.eclipse.osee.framework.resource.provider.common.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.eclipse.osee.framework.resource.management.IResource;

/**
 * @author Roberto E. Escobar
 */
public class CompressedResourceBridge implements IResource {
   private byte[] backing;
   private boolean isCompressed;
   private URI uri;

   public CompressedResourceBridge(byte[] backing, URI uri, boolean isCompressed) {
      this.backing = backing;
      this.isCompressed = isCompressed;
      this.uri = uri;
   }

   @Override
   public InputStream getContent() throws IOException {
      return new ByteArrayInputStream(backing);
   }

   @Override
   public URI getLocation() {
      return uri;
   }

   @Override
   public String getName() {
      String value = uri.toASCIIString();
      return value.substring(value.lastIndexOf("/") + 1, value.length());
   }

   @Override
   public boolean isCompressed() {
      return isCompressed;
   }

}
