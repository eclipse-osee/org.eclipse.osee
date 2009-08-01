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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author Roberto E. Escobar
 */
public class Resource implements IResource {
   private URI uri;
   private boolean isCompressed;

   public Resource(URI uri, boolean isCompressed) {
      this.uri = uri;
      this.isCompressed = isCompressed;
   }

   public InputStream getContent() throws IOException {
      return uri.toURL().openStream();
   }

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
