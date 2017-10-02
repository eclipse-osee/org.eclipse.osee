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
package org.eclipse.osee.orcs.db.internal.resource;

import java.net.URI;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorProvider;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;
import org.eclipse.osee.framework.resource.management.util.ResourceLocator;

/**
 * @author Roberto E. Escobar
 */
public class ExchangeLocatorProvider implements IResourceLocatorProvider {

   @Override
   public IResourceLocator generateResourceLocator(String seed, String name)  {
      URI uri = null;
      try {
         uri = new URI(generatePath(name));
      } catch (Exception ex) {
         throw new MalformedLocatorException(ex);
      }
      return new ResourceLocator(uri);
   }

   @Override
   public IResourceLocator getResourceLocator(String path)  {
      URI uri = null;
      if (isPathValid(path) != false) {
         try {
            uri = new URI(path);
         } catch (Exception ex) {
            throw new MalformedLocatorException(ex);
         }
      } else {
         throw new MalformedLocatorException("Invalid path hint: [%s]", path);
      }
      return new ResourceLocator(uri);
   }

   @Override
   public String getSupportedProtocol() {
      return ResourceConstants.EXCHANGE_RESOURCE_PROTOCOL;
   }

   @Override
   public boolean isValid(String protocol) {
      return Strings.isValid(protocol) != false && protocol.startsWith(getSupportedProtocol()) != false;
   }

   private boolean isPathValid(String value) {
      return Strings.isValid(value) && value.startsWith(getSupportedProtocol() + "://");
   }

   private String generatePath(String name) throws MalformedLocatorException {
      StringBuilder builder = new StringBuilder();
      builder.append(getSupportedProtocol());
      builder.append("://");
      if (Strings.isValid(name)) {
         builder.append(name);
      } else {
         throw new MalformedLocatorException("Invalid arguments during locator generation.");
      }
      return builder.toString();
   }
}
