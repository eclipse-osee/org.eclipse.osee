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
package org.eclipse.osee.rest.admin.internal;

import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

/**
 * @author Roberto E. Escobar
 */
public class BundleHttpContext implements HttpContext {

   private final Iterable<Bundle> bundles;

   public BundleHttpContext(Iterable<Bundle> bundles) {
      this.bundles = bundles;
   }

   @Override
   public URL getResource(String path) {
      // find first bundle that has an entry for the path
      URL toReturn = null;
      for (Bundle bundle : bundles) {
         toReturn = bundle.getEntry(path);
         if (toReturn != null) {
            return toReturn;
         }
      }
      return toReturn;
   }

   @Override
   public String getMimeType(String name) {
      return null;
   }

   @Override
   public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) {
      // Assume the container has already performed authentication
      return true;
   }

}