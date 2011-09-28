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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class DataStore {

   public static interface Resource {

      void acquire(DataStore dataStore) throws OseeCoreException;

      void save(int storageId, DataStore dataStore) throws OseeCoreException;

      //int gammaId, String artifactGuid, String extension

      void purge(DataStore dataStore) throws OseeCoreException;
   }

   private final Resource resource;
   private String locator;
   private byte[] rawContent;
   private String contentType;
   private String encoding;
   private String extension;
   private boolean needToReadFromRemote;

   public DataStore(Resource resource) {
      super();
      this.resource = resource;
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

   public InputStream getInputStream() throws OseeCoreException {
      return new ByteArrayInputStream(getContent());
   }

   public byte[] getContent() throws OseeCoreException {
      if (isLocatorValid() != false && needToReadFromRemote) {
         resource.acquire(this);
         needToReadFromRemote = false;
      }
      return this.rawContent;
   }

   public void setContent(byte[] rawContent, String extension, String contentType, String encoding) {
      this.rawContent = rawContent;
      this.contentType = contentType;
      this.encoding = encoding;
      this.extension = extension;
   }

   public void copyTo(DataStore other) {
      other.rawContent = Arrays.copyOf(this.rawContent, this.rawContent.length);
      other.contentType = this.contentType;
      other.encoding = this.encoding;
   }

   public void persist(int storageId) throws OseeCoreException {
      if (this.rawContent != null && this.rawContent.length > 0) {
         resource.save(storageId, this);
      }
   }

   public void purge() throws OseeCoreException {
      if (isLocatorValid() != false) {
         resource.purge(this);
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

   public String getFileName() throws OseeCoreException {
      //TODO: figure out how to provide the name
      //      return BinaryContentUtils.generateFileName(getIdentity());
      return "";
   }
}
