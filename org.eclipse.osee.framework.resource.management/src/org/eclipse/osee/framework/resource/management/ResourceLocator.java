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

import java.net.URI;

/**
 * @author Roberto E. Escobar
 */
public class ResourceLocator implements IResourceLocator {

   private URI uri;

   public ResourceLocator(URI uri) {
      if (uri == null) {
         throw new IllegalArgumentException("URI was null.");
      }
      this.uri = uri;
   }

   public URI getLocation() {
      return uri;
   }

   public String getProtocol() {
      return uri.getScheme();
   }

   public String toString() {
      return uri.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResourceLocator#getRawPath()
    */
   @Override
   public String getRawPath() {
      String toReturn = uri.getSchemeSpecificPart();
      if (toReturn.startsWith("//") != false) {
         toReturn = toReturn.substring(2, toReturn.length());
      }
      return toReturn;
   }
}
