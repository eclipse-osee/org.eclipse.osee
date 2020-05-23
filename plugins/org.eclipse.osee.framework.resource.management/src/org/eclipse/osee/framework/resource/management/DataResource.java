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

package org.eclipse.osee.framework.resource.management;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class DataResource {

   private String contentType;
   private String encoding;
   private String extension;
   private String locator;

   public DataResource() {
   }

   public DataResource(String contentType, String encoding, String extension, String locator) {
      this.contentType = contentType;
      this.encoding = encoding;
      this.extension = extension;
      this.locator = locator;
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
      return "DataResource [contentType=" + contentType + ", encoding=" + encoding + ", extension=" + extension + ", locator=" + locator + "]";
   }
}
