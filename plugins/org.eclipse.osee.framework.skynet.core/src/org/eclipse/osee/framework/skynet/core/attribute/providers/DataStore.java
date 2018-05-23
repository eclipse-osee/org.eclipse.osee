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
package org.eclipse.osee.framework.skynet.core.attribute.providers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AttributeResourceProcessor;

/**
 * @author Roberto E. Escobar
 */
public class DataStore {
   private final AttributeResourceProcessor resourceProcessor;
   private String locator;
   private byte[] rawContent;
   private String contentType;
   private String encoding;
   private String extension;
   private boolean needToReadFromRemote;

   public DataStore(AttributeResourceProcessor resourceProcessor) {
      super();
      this.resourceProcessor = resourceProcessor;
      clear();
      this.needToReadFromRemote = true;
   }

   public String getLocator() {
      return this.locator;
   }

   public void setLocator(String locator) {
      this.locator = locator;
      needToReadFromRemote = true;
   }

   public boolean isLocatorValid() {
      return this.locator != null && this.locator.length() > 0;
   }

   public boolean isDataValid() {
      return this.rawContent != null && this.rawContent.length > 0;
   }

   public InputStream getInputStream() {
      return new ByteArrayInputStream(getContent());
   }

   public byte[] getContent() {
      if (needToReadFromRemote && isLocatorValid()) {
         resourceProcessor.acquire(this);
         needToReadFromRemote = false;
      }
      return this.rawContent;
   }

   public void setContent(byte[] rawContent, String extension, String contentType, String encoding) {
      this.rawContent = rawContent;
      this.contentType = Strings.intern(contentType);
      this.encoding = Strings.intern(encoding);
      this.extension = Strings.intern(extension);
   }

   public void copyTo(DataStore other) {
      other.rawContent = Arrays.copyOf(this.rawContent, this.rawContent.length);
      other.contentType = Strings.intern(this.contentType);
      other.encoding = Strings.intern(this.encoding);
   }

   public void persist(GammaId storageId) {
      if (this.rawContent != null && this.rawContent.length > 0) {
         resourceProcessor.saveResource(storageId, resourceProcessor.createStorageName(), this);
      }
   }

   public void purge() {
      if (isLocatorValid()) {
         resourceProcessor.purge(this);
      }
   }

   /**
    * @return the contentType
    */
   public String getContentType() {
      return contentType;
   }

   /**
    * @return the encoding
    */
   public String getEncoding() {
      return encoding;
   }

   public void clear() {
      setContent(null, "txt", "txt/plain", "UTF-8");
      setLocator(null);
   }

   /**
    * @return the extension
    */
   public String getExtension() {
      return extension;
   }
}
