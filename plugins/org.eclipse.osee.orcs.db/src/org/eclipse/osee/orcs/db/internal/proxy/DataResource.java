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
package org.eclipse.osee.orcs.db.internal.proxy;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;

/**
 * @author Roberto E. Escobar
 */
public class DataResource {

   private ResourceNameResolver resolver;
   private String contentType;
   private String encoding;
   private String extension;
   private String locator;

   public DataResource() {
   }

   public DataResource(String contentType, String encoding, String extension, String locator) {
      super();
      this.contentType = contentType;
      this.encoding = encoding;
      this.extension = extension;
      this.locator = locator;
   }

   public void setResolver(ResourceNameResolver resolver) {
      this.resolver = resolver;
   }

   public ResourceNameResolver getResolver() {
      return resolver;
   }

   public String getContentType() {
      return contentType;
   }

   public String getEncoding() {
      return encoding;
   }

   public String getExtension() {
      return extension;
   }

   public String getLocator() {
      return locator;
   }

   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   public void setExtension(String extension) {
      this.extension = extension;
   }

   public void setLocator(String locator) {
      this.locator = locator;
   }

   public boolean isLocatorValid() {
      return Strings.isValid(getLocator());
   }

   @Override
   public String toString() {
      return "DataResource [resolver=" + resolver + ", contentType=" + contentType + ", encoding=" + encoding + ", extension=" + extension + ", locator=" + locator + "]";
   }
}
