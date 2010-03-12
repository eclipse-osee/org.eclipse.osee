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
package org.eclipse.osee.ote.core.environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.jar.Manifest;

/** 
 * Describes the location where a bundle can be acquired from and provides
 * the manifest of the bundle so that an educated decision can be made about
 * whether a bundle is needed or not.
 * 
 * @author Robert A. Fisher
 *
 */
public class BundleDescription implements Serializable {
   private static final long serialVersionUID = 546754001181908641L;
   
   private final URL location;
   private final boolean systemLibrary;
   private final byte[] manifestData;
   private final byte[] md5Digest;
   private transient Manifest manifest;
   
   /**
    * Deserialization constructor
    */
   protected BundleDescription() {
      this.location = null;
      this.manifestData = null;
      this.manifest = null;
      this.systemLibrary = false;
      this.md5Digest = null;
   }
   
   public BundleDescription(URL systemLocation, URL serverLocation, Manifest manifest, boolean systemLibrary, byte[] md5Digest) {
      if (systemLocation == null)
         throw new IllegalArgumentException("systemLocation must not be null");
      if (serverLocation == null)
         throw new IllegalArgumentException("serverLocation must not be null");
      if (manifest == null)
         throw new IllegalArgumentException("manifest must not be null");
      if (md5Digest == null)
         throw new IllegalArgumentException("md5Digest must not be null");
      
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try {
         manifest.write(out);
      } catch (IOException ex) {
         throw new IllegalStateException("ByteArrayOutputStream never should throw IOException", ex);
      }
      
      this.location = serverLocation;
      this.manifestData = out.toByteArray();
      this.systemLibrary = systemLibrary;
      this.md5Digest = md5Digest;
   }

   public InputStream getBundleData() throws IOException {
      return location.openConnection().getInputStream();
   }

   /**
    * @return the manifestData
    */
   public Manifest getManifest() {
      try {
         if (manifest == null) {
            manifest = new Manifest(new ByteArrayInputStream(manifestData));
         }
         return manifest;
      } catch (IOException ex) {
         throw new IllegalStateException("ByteArrayInputStream never should throw IOException", ex);
      }
   }
   
   public String getSymbolicName() {
      return getManifestEntry("Bundle-SymbolicName");
   }
   
   public String getVersion() {
      return getManifestEntry("Bundle-Version");
   }
   
   private String getManifestEntry(String attribute) {
      String entry = getManifest().getMainAttributes().getValue(attribute).trim();
      
      // Sometimes there's a semicolon then extra info - ignore this
      int index = entry.indexOf(';');
      if (index != -1) {
         entry = entry.substring(0, index);
      }
      
      return entry;
   }

   /**
    * @return the systemLibrary
    */
   public boolean isSystemLibrary() {
      return systemLibrary;
   }

   /**
    * @return the md5Digest
    */
   public byte[] getMd5Digest() {
      return md5Digest;
   }
}
