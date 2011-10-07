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
package org.eclipse.osee.orcs.db.mocks;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import org.eclipse.osee.framework.resource.management.IResource;

/**
 * @author Roberto E. Escobar
 */
public class MockResource implements IResource {
   private final byte[] backing;
   private final boolean isCompressed;
   private final URI uri;
   private final String name;

   public MockResource(String name, URI uri, byte[] backing, boolean isCompressed) {
      this.name = name;
      this.backing = backing;
      this.isCompressed = isCompressed;
      this.uri = uri;
   }

   @Override
   public InputStream getContent() {
      return new ByteArrayInputStream(backing);
   }

   @Override
   public URI getLocation() {
      return uri;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public boolean isCompressed() {
      return isCompressed;
   }
}