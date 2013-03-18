/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
import org.eclipse.osee.framework.jdk.core.util.io.streams.StreamPumper;
import org.eclipse.osee.ote.OTEConfiguration;
import org.eclipse.osee.ote.OTEConfigurationItem;
import org.eclipse.osee.ote.OTEConfigurationStatus;
import org.eclipse.osee.ote.OTEStatusCallback;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

public class OTEBundleLoader {

   private final Collection<Bundle> installedBundles;
   private final Collection<Bundle> runningBundles;
   private final Map<String, String> bundleNameToMd5Map;

   private final BundleContext context;

   public OTEBundleLoader() {
      this.context = FrameworkUtil.getBundle(getClass()).getBundleContext();
      this.installedBundles = new LinkedList<Bundle>();
      this.runningBundles = new LinkedList<Bundle>();
      this.bundleNameToMd5Map = new HashMap<String, String>();
   }

   public boolean install(OTEConfiguration oteConfiguration, OTEStatusCallback<OTEConfigurationStatus> statusCallback) {
      boolean pass = true;
      for (OTEConfigurationItem bundleDescription : oteConfiguration.getItems()) {
         String bundleName = bundleDescription.getSymbolicName();
         try {
            boolean exists = false;
            for (Bundle bundle : runningBundles) {
               if (bundle.getSymbolicName().equals(bundleName)) {
                  exists = true;
                  break;
               }
            }
            if (!exists) {
               Bundle bundle = Platform.getBundle(bundleDescription.getSymbolicName());
               if (bundle == null) {
                  Bundle installedBundle;
                  InputStream bundleData = acquireSystemLibraryStream(bundleDescription);
                  installedBundle = context.installBundle("OTE-" + bundleName, bundleData);
                  bundleData.close();
                  bundleNameToMd5Map.put(bundleName, bundleDescription.getMd5Digest());
                  installedBundles.add(installedBundle);
               }
            }
            statusCallback.log("installed " + bundleName);
         } catch (Throwable th) {
            statusCallback.error(String.format("Unable to load [%s].", bundleName), th);
            pass = false;
         } finally {
            statusCallback.incrememtUnitsWorked(1);
         }
      }
      return pass;
   }

   public boolean start(OTEStatusCallback<OTEConfigurationStatus> statusCallback) {
      boolean pass = true;
      Iterator<Bundle> iter = installedBundles.iterator();
      while (iter.hasNext()) {
         Bundle bundle = iter.next();
         try {
            String entry = bundle.getHeaders().get("Fragment-Host");
            if (entry == null) {
               bundle.start();
            }
            // We got here because bundle.start did not exception
            runningBundles.add(bundle);
            iter.remove();
            statusCallback.log("started " + bundle.getSymbolicName());
         } catch (BundleException ex) {
            pass = false;
            statusCallback.error("Failed to start " + bundle.getSymbolicName(), ex);
         } finally {
            statusCallback.incrememtUnitsWorked(1);
         }
      }
      return pass;
   }

   public boolean uninstall(OTEStatusCallback<OTEConfigurationStatus> statusCallback) {
      boolean result = true;
      for (Bundle bundle : installedBundles) {
         try {
            bundle.uninstall();
         } catch (BundleException ex) {
            result = false;
            statusCallback.error("Failed to uninstall " + bundle.getSymbolicName(), ex);
         }
      }
      installedBundles.clear();

      for (Bundle bundle : runningBundles) {
         try {
            bundle.stop();
            bundle.uninstall();
         } catch (BundleException ex) {
            result = false;
            statusCallback.error("Failed to stop and uninstall " + bundle.getSymbolicName(), ex);
         }
      }
      runningBundles.clear();
      
      return result;
   }

   private InputStream acquireSystemLibraryStream(OTEConfigurationItem bundleDescription) throws Exception {
      File dir = getJarCache();
      File anticipatedJarFile = new File(dir, bundleDescription.getSymbolicName() + "_" + bundleDescription.getVersion() + ".jar");
      ensureJarFileOnDisk(bundleDescription, anticipatedJarFile);
      return new FileInputStream(anticipatedJarFile);
   }

   private void ensureJarFileOnDisk(OTEConfigurationItem bundleDescription, File anticipatedJarFile) throws Exception {
      // assume MD5 matches until we can check the file
      boolean md5Matches = true;

      if (anticipatedJarFile.exists()) {
         InputStream in = new FileInputStream(anticipatedJarFile);
         String diskMd5Digest = ChecksumUtil.createChecksumAsString(in, "MD5");
         in.close();
         md5Matches = diskMd5Digest.equals(bundleDescription.getMd5Digest());
      }

      if (!md5Matches || !anticipatedJarFile.exists()) {
         InputStream servedBundleIn = new URL(bundleDescription.getLocationUrl()).openConnection().getInputStream();
         OutputStream cachedFileOut = new FileOutputStream(anticipatedJarFile);
         StreamPumper.pumpData(servedBundleIn, cachedFileOut);
         cachedFileOut.close();
         servedBundleIn.close();
      }
   }

   private File getJarCache() {
      String path = System.getProperty("user.home") + File.separator + "OTESERVER";
      File jarCache = new File(path, "runtimeCache");
      if (!jarCache.exists()) {
         if (!jarCache.mkdirs()) {
            throw new RuntimeException("Could not create JAR cache at " + jarCache.getAbsolutePath());
         }
      }
      if (!jarCache.isDirectory()) {
         throw new IllegalStateException("the JAR cache is not a directory! Path=" + jarCache.getAbsolutePath());
      }
      return jarCache;
   }
   
   void clearJarCache() {
      File[] jars = getJarCache().listFiles(new FileFilter() {
         @Override
         public boolean accept(File file) {
            return !file.isDirectory() && file.getAbsolutePath().endsWith(".jar");
         }
      });
      for(File jar:jars){
         jar.delete();
      }
   }

   public void dispose() {
      bundleNameToMd5Map.clear();
      installedBundles.clear();
      runningBundles.clear();
   }

   public boolean installed() {
      return installedBundles.size() > 0 || runningBundles.size() > 0;
   }

}
