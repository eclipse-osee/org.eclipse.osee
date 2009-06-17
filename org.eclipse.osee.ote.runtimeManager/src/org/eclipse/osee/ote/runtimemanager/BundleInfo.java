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
package org.eclipse.osee.ote.runtimemanager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;

/** 
 * @author Robert A. Fisher
 *
 */
public class BundleInfo {
   private final String symbolicName;
   private final String version;
   private final URL systemLocation;
   private final URL bundleServerLocation;
   private final File file;
   private final Manifest manifest;
   private final boolean systemLibrary;
   private byte[] md5Digest;

   /**
    * @param symbolicName
    * @param version
    * @param systemLocation
    * @throws IOException 
    */
   public BundleInfo(URL systemLocation, String bundleServerBaseLocation, boolean systemLibrary) throws IOException {
      File tmpFile;
      try {
         tmpFile = new File(systemLocation.toURI());
       } catch(URISyntaxException ex) {
         tmpFile = new File(systemLocation.getPath());
       }
      this.file = tmpFile;

      JarFile jarFile = new JarFile(file);
      this.manifest = jarFile.getManifest();
      this.symbolicName = generateBundleName(manifest);
      this.version = manifest.getMainAttributes().getValue("Bundle-Version");

      this.systemLocation = systemLocation;
      this.bundleServerLocation = new URL(bundleServerBaseLocation + symbolicName);
      this.systemLibrary = systemLibrary;
      this.md5Digest = null;
   }

   /**
    * @param jarManifest
    * @return the name of the bundle
    */
   public static String generateBundleName(Manifest jarManifest) {
      String nameEntry = jarManifest.getMainAttributes().getValue("Bundle-SymbolicName");
      
      // Sometimes there's a semicolon then extra info - ignore this
      int index = nameEntry.indexOf(';');
      if (index != -1) {
         nameEntry = nameEntry.substring(0, index);
      }
      
      return nameEntry;
   }

   /**
    * @return the symbolicName
    */
   public String getSymbolicName() {
      return symbolicName;
   }

   /**
    * @return the version
    */
   public String getVersion() {
      return version;
   }

   /**
    * @return the location
    */
   public URL getSystemLocation() {
      return systemLocation;
   }

   /**
    * @return the bundleServerLocation
    */
   public URL getServerBundleLocation() {
      return bundleServerLocation;
   }

   /**
    * @return the file
    */
   public File getFile() {
      return file;
   }

   /**
    * @return the manifest
    */
   public Manifest getManifest() {
      return manifest;
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return getSymbolicName() + ":" + getVersion();
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
      // Do lazy calculation of this since it can be costly
      // and does not get read for all bundle info's
      if (md5Digest == null) {
         try {
            InputStream in = systemLocation.openStream();
            
            md5Digest = ChecksumUtil.createChecksum(in, "MD5");
            
            in.close();
         } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Always expect MD5 to be available", ex);
         } catch (IOException ex) {
            throw new IllegalStateException("Always expect local jar file to be available", ex);
         }
      }
      return md5Digest;
   }
}
