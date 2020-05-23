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

package org.eclipse.osee.framework.server.ide.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;

/**
 * @author Roberto E. Escobar
 */
public class ServletResourceBridge implements IResource {

   private static final String IS_COMPRESSED = "is.compressed";

   private final HttpServletRequest request;
   private final IResourceLocator locator;

   public ServletResourceBridge(HttpServletRequest request, IResourceLocator locator) {
      this.request = request;
      this.locator = locator;
   }

   @Override
   public InputStream getContent() {
      try {
         return request.getInputStream();
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
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
      return Boolean.valueOf(request.getParameter(IS_COMPRESSED));
   }

}
