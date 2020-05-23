/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.resource.management.util;

import java.net.URI;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.resource.management.IResourceLocator;

/**
 * @author Roberto E. Escobar
 */
public class ResourceLocator implements IResourceLocator {

   private final URI uri;

   public ResourceLocator(URI uri) {
      if (uri == null) {
         throw new OseeArgumentException("URI was null.");
      }
      this.uri = uri;
   }

   @Override
   public URI getLocation() {
      return uri;
   }

   @Override
   public String getProtocol() {
      return uri.getScheme();
   }

   @Override
   public String toString() {
      return uri.toString();
   }

   @Override
   public String getRawPath() {
      String toReturn = uri.getSchemeSpecificPart();
      if (toReturn.startsWith("//") != false) {
         toReturn = toReturn.substring(2, toReturn.length());
      }
      return toReturn;
   }
}
