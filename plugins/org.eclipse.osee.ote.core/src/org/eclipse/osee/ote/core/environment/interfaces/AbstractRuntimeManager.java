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
package org.eclipse.osee.ote.core.environment.interfaces;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;
import org.eclipse.osee.framework.jdk.core.util.io.streams.StreamPumper;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.OseeURLClassLoader;
import org.eclipse.osee.ote.core.ReturnStatus;
import org.eclipse.osee.ote.core.environment.BundleConfigurationReport;
import org.eclipse.osee.ote.core.environment.BundleDescription;
import org.eclipse.osee.ote.core.environment.BundleResolveException;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AbstractRuntimeManager implements IRuntimeLibraryManager {

   private static final String OTE_ACTIVATION_POLICY = "OTE-ActivationPolicy";
   private final Collection<Bundle> installedBundles;
   private final Collection<Bundle> runningBundles;
   private final Map<String, byte[]> bundleNameToMd5Map;
   private final HashMap<String, File> availableJars;
   private ClassLoader runtimeLibraryLoader;
   private URLClassLoader scriptClassLoader;

   private volatile boolean cleanUpNeeded = true;

   private final BundleContext context;
   private final PackageAdmin packageAdmin;

   public AbstractRuntimeManager(PackageAdmin packageAdmin, BundleContext context) {
      this.context = context;
      this.packageAdmin = packageAdmin;
      this.installedBundles = new LinkedList<Bundle>();
      this.runningBundles = new LinkedList<Bundle>();
      this.availableJars = new HashMap<String, File>(32);
      this.bundleNameToMd5Map = new HashMap<String, byte[]>();
      this.runtimeLibraryLoader = null;
      this.scriptClassLoader = null;
   }

   private final List<RuntimeLibraryListener> listeners = new ArrayList<RuntimeLibraryListener>();

   private String[] currentJarVersions = null;

   private URLClassLoader loader = null;

   protected URLClassLoader getClassLoader(String[] versions) throws IOException {

      ExportClassLoader classLoader = new ExportClassLoader(packageAdmin);

      ArrayList<URL> classpaths = new ArrayList<URL>();
      for (String version : versions) {
         File jar = getAvailableJar(version);
         if (jar != null) {
            classpaths.add(jar.toURI().toURL());
         } else {
            OseeLog.log(AbstractRuntimeManager.class, Level.FINE, "The null jar file for version " + version);
         }
      }
      loader =
         new OseeURLClassLoader("Runtime Library ClassLoader", classpaths.toArray(new URL[classpaths.size()]),
            classLoader);

      return loader;
   }

   protected URLClassLoader getLoader() {
      return loader;
   }

   @Override
   public boolean isMessageJarAvailable(String version) {
      boolean retVal = false;

      // For efficiency, first check the already known available Jars.
      // If the desired version is not available, then update that list
      // and check it again.
      if (availableJars.containsKey(version)) {
         retVal = true;
      } else {
         updateAvailableJars();
         if (availableJars.containsKey(version)) {
            retVal = true;
         }
      }

      return retVal;
   }

   //TODO MAKE SURE TO CHECK BUNDLE STATE IS RESOLVED OR ACTIVE
   @Override
   public boolean isBundleAvailable(String symbolicName, String version, byte[] md5Digest) {
      Bundle installedBundle = Platform.getBundle(symbolicName);
      if (installedBundle != null && !installedBundles.contains(installedBundle)) {
         return true;
      } else {
         Bundle[] bundles = Platform.getBundles(symbolicName, version);
         if (bundles == null) {
            return false;
         }
         for (Bundle bundle : bundles) {
            String bundleSymbolicName = bundle.getSymbolicName();
            if (bundleSymbolicName.equals(symbolicName) && bundle.getHeaders().get("Bundle-Version").equals(version)) {
               if (bundleNameToMd5Map.containsKey(bundleSymbolicName)) {
                  // check for bundle binary equality
                  if (Arrays.equals(bundleNameToMd5Map.get(bundleSymbolicName), md5Digest)) {
                     return true;
                  }
               } else {
                  // we do not have a md5 hash for this bundle so we need to create one
                  try {
                     InputStream in = new FileInputStream(FileLocator.getBundleFile(bundle));
                     try {
                        byte[] digest = ChecksumUtil.createChecksum(in, "MD5");
                        if (Arrays.equals(digest, md5Digest)) {
                           bundleNameToMd5Map.put(bundle.getSymbolicName(), digest);
                           return true;
                        }
                     } finally {
                        in.close();
                     }
                  } catch (Exception e) {
                     OseeLog.log(AbstractRuntimeManager.class, Level.SEVERE,
                        "could not determine binary equality of bundles", e);
                  }
               }
            }
         }
      }
      return false;
   }

   @Override
   public BundleConfigurationReport checkBundleConfiguration(Collection<BundleDescription> bundles) throws Exception {
      List<BundleDescription> missing = new ArrayList<BundleDescription>();
      List<BundleDescription> versionMismatch = new ArrayList<BundleDescription>();
      List<BundleDescription> partOfInstallation = new ArrayList<BundleDescription>();
      for (BundleDescription bundleDescription : bundles) {
         boolean exists = false;
         for (Bundle bundle : runningBundles) {
            String bundleSymbolicName = bundle.getSymbolicName();
            if (bundleSymbolicName.equals(bundleDescription.getSymbolicName())) {
               exists = true;
               if (bundleNameToMd5Map.containsKey(bundleSymbolicName)) {
                  if (bundle.getHeaders().get("Bundle-Version").equals(bundleDescription.getVersion()) && Arrays.equals(
                     bundleNameToMd5Map.get(bundleSymbolicName), bundleDescription.getMd5Digest())) {

                  } else {
                     versionMismatch.add(bundleDescription);
                  }
               } else {
                  versionMismatch.add(bundleDescription);
               }
            }
         }
         if (!exists) {
            Bundle bundle = Platform.getBundle(bundleDescription.getSymbolicName());
            if (bundle == null) {
               missing.add(bundleDescription);
            } else {
               partOfInstallation.add(bundleDescription);
            }
         }
      }
      return new BundleConfigurationReport(missing, versionMismatch, partOfInstallation);
   }

   @Override
   public void loadBundles(Collection<BundleDescription> bundles) throws Exception {
      cleanUpNeeded = true;
      for (BundleDescription bundleDescription : bundles) {

         String bundleName = bundleDescription.getSymbolicName();
         try {

            boolean exists = false;

            for (Bundle bundle : runningBundles) {
               if (bundle.getSymbolicName().equals(bundleName)) {
                  bundle.update();
                  exists = true;
                  break;
               }
            }

            if (!exists) {
               Bundle bundle = Platform.getBundle(bundleDescription.getSymbolicName());
               if (bundle == null) {
                  Bundle installedBundle;
                  if (bundleDescription.isLocalFile()) {
                     installedBundle = context.installBundle(bundleDescription.getLocation());
                  } else {
                     InputStream bundleData = getBundleInputStream(bundleDescription);
                     installedBundle = context.installBundle("OTE-" + bundleName, bundleData);
                     bundleData.close();
                  }
                  bundleNameToMd5Map.put(bundleName, bundleDescription.getMd5Digest());
                  installedBundles.add(installedBundle);
               }
            }

         } catch (Throwable th) {
            OseeLog.log(AbstractRuntimeManager.class, Level.SEVERE, String.format("Unable to load [%s].", bundleName),
               th);
         }
      }

      if (runtimeLibraryLoader == null) {
         runtimeLibraryLoader = getClassLoader(new String[0]);
      }
      scriptClassLoader =
         new OseeURLClassLoader("Script ClassLoader", Lib.getUrlFromString(new String[] {""}),
            this.runtimeLibraryLoader);
      transitionInstalledBundles();
   }

   /**
    * @return
    * @throws IOException
    */
   private InputStream getBundleInputStream(BundleDescription bundleDescription) throws IOException {
      if (bundleDescription.isSystemLibrary()) {
         return acquireSystemLibraryStream(bundleDescription);
      } else {
         return acquireUserLibraryStream(bundleDescription);
      }
   }

   /**
    * @return
    * @throws IOException
    */
   private InputStream acquireUserLibraryStream(BundleDescription bundleDescription) throws IOException {
      return bundleDescription.getBundleData();
   }

   /**
    * @return
    * @throws IOException
    */
   private InputStream acquireSystemLibraryStream(BundleDescription bundleDescription) throws IOException {
      try {
         File dir = getJarCache();
         File anticipatedJarFile =
            new File(dir, bundleDescription.getSymbolicName() + "_" + bundleDescription.getVersion() + ".jar");

         ensureJarFileOnDisk(bundleDescription, anticipatedJarFile);

         OseeLog.log(AbstractRuntimeManager.class, Level.FINEST,
            String.format("Looking for [%s] on disk.", anticipatedJarFile.getAbsolutePath()));

         return new FileInputStream(anticipatedJarFile);
      } catch (Exception ex) {
         OseeLog.log(
            AbstractRuntimeManager.class,
            Level.WARNING,
            "Failed to acquire system lib from cache. " + "Fell back to direct acquisition from server with out caching",
            ex);

         return bundleDescription.getBundleData();
      }
   }

   private void ensureJarFileOnDisk(BundleDescription bundleDescription, File anticipatedJarFile) throws IOException, FileNotFoundException, NoSuchAlgorithmException {
      // assume MD5 matches until we can check the file
      boolean md5Matches = true;

      if (anticipatedJarFile.exists()) {
         InputStream in = new FileInputStream(anticipatedJarFile);
         byte[] diskMd5Digest = ChecksumUtil.createChecksum(in, "MD5");
         in.close();

         md5Matches = Arrays.equals(diskMd5Digest, bundleDescription.getMd5Digest());
      }

      if (!md5Matches || !anticipatedJarFile.exists()) {
         InputStream servedBundleIn = bundleDescription.getBundleData();
         OutputStream cachedFileOut = new FileOutputStream(anticipatedJarFile);

         StreamPumper.pumpData(servedBundleIn, cachedFileOut);

         cachedFileOut.close();
         servedBundleIn.close();
      }
   }

   private void initClassloadersWithNoURLs() {
      if (runtimeLibraryLoader == null && scriptClassLoader == null) {
         try {
            runtimeLibraryLoader = getClassLoader(new String[0]);
            scriptClassLoader =
               new OseeURLClassLoader("Script ClassLoader", Lib.getUrlFromString(new String[] {""}),
                  this.runtimeLibraryLoader);
         } catch (IOException ex) {
            OseeLog.log(AbstractRuntimeManager.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public void updateBundles(Collection<BundleDescription> bundles) throws BundleException, IOException {
      for (BundleDescription bundle : bundles) {
         for (Bundle runningBundle : runningBundles) {
            if (runningBundle.getSymbolicName().equals(bundle.getSymbolicName())) {
               InputStream bundleData = getBundleInputStream(bundle);
               runningBundle.update(bundleData);
               bundleData.close();
               bundleNameToMd5Map.put(bundle.getSymbolicName(), bundle.getMd5Digest());
            }
         }
      }

      packageAdmin.refreshPackages(null);
      try {
         Thread.sleep(10000);
      } catch (InterruptedException ex) {
      }
   }

   /**
    * checks the file system for new jar files
    */
   private void updateAvailableJars() {
      File dir = getJarCache();
      File[] files = dir.listFiles(new MatchFilter(".*\\.jar"));
      if (files == null) {
         System.out.println("path=[" + dir.getAbsolutePath() + "]");
         return;
      }
      for (File file : files) {
         try {
            String version = Lib.getJarFileVersion(file.getAbsolutePath());
            availableJars.put(version, file);
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
   }

   private File getJarCache() {
      String path = System.getProperty("user.home") + File.separator + TestEnvironment.class.getName();
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

   /**
    * @throws BundleException
    * @throws BundleResolveException
    */
   private void transitionInstalledBundles() throws BundleException, BundleResolveException {
      Iterator<Bundle> iter = installedBundles.iterator();

      // Make sure that all installed bundles have been resolved so that
      // the export class loader has access to their classes if necessary.
      try {
         resolveBundles();
      } catch (Throwable th) {
         th.printStackTrace();
         OseeLog.log(AbstractRuntimeManager.class, Level.SEVERE, th);
      }

      while (iter.hasNext()) {
         Bundle bundle = iter.next();

         try {
            bundle.getHeaders().get(OTE_ACTIVATION_POLICY);
            //            if ("early".equalsIgnoreCase(oteActivationPolicy)) {
            //               bundle.start();
            //            }
            bundle.start();

            // We got here because bundle.start did not exception
            runningBundles.add(bundle);
         } catch (BundleException ex) {
            OseeLog.log(AbstractRuntimeManager.class, Level.SEVERE, ex);
            //            throw new BundleException("Error trying to start bundle " + bundle.getSymbolicName() + ": " + ex, ex);
         } finally {
            iter.remove();
         }
      }
   }

   /**
    * @throws BundleException
    * @throws BundleResolveException
    */
   private void resolveBundles() throws BundleResolveException {
      // Note: This is done one by one for simpler debugging when some
      //            bundles don't resolve

      Collection<BundleException> resolveExceptions = new LinkedList<BundleException>();
      Bundle[] bundleArray = new Bundle[1];
      for (Bundle bundle : installedBundles) {
         // Prior calls to resolveBundles may have forced this bundle
         // to resolve so don't waste time recalling the resolve
         if (bundle.getState() != Bundle.INSTALLED) {
            continue;
         }
         bundleArray[0] = bundle;
         boolean resolved = packageAdmin.resolveBundles(bundleArray);
         if (!resolved) {
            try {
               bundle.start();

               // If resolve failed then the call to start should have forced a BundleException
               // and this code should never be reached
               OseeLog.log(AbstractRuntimeManager.class, Level.SEVERE,
                  "Forced to start bundle " + bundle.getSymbolicName() + " to get it resolved, should never occur!");
            } catch (BundleException ex) {
               resolveExceptions.add(new BundleException(
                  "Error trying to resolve bundle " + bundle.getSymbolicName() + ": " + ex, ex));
            }
         }
      }

      if (!resolveExceptions.isEmpty()) {
         throw new BundleResolveException("Unable to resolve all runtime bundles", resolveExceptions);
      }
   }

   private ReturnStatus checkCurrentJarVersions(String[] jarVersions) {
      computeRunningJarVersions();
      if (this.currentJarVersions == null) {
         return new ReturnStatus("No jar's currently loaded", false);
      }
      List<String> nonMatchingVersions = new ArrayList<String>();
      for (String version : jarVersions) {
         if (Arrays.binarySearch(this.currentJarVersions, version) < 0) {
            nonMatchingVersions.add(version);
         }
      }
      if (nonMatchingVersions.size() > 0) {
         return new ReturnStatus(String.format(
            "Bundle versions [%s] were not found in the currently configured environment that is running with [%s].",
            Arrays.deepToString(nonMatchingVersions.toArray()), Arrays.deepToString(currentJarVersions)), false);
      }
      return new ReturnStatus(String.format("Jar Versions [%s] are already loaded.", Arrays.deepToString(jarVersions)),
         true);
   }

   private void computeRunningJarVersions() {
      List<String> versions = new ArrayList<String>();
      for (Bundle bundle : runningBundles) {
         String versionStr =
            OteUtil.generateBundleVersionString((String) bundle.getHeaders().get("Implementation-Version"),
               bundle.getSymbolicName(), (String) bundle.getHeaders().get("Bundle-Version"),
               bundleNameToMd5Map.get(bundle.getSymbolicName()));
         versions.add(versionStr);
      }
      if (versions.size() > 0) {
         currentJarVersions = versions.toArray(new String[versions.size()]);
         Arrays.sort(currentJarVersions);
      }
   }

   @Override
   public ReturnStatus isRunningJarVersions(String[] versions) {
      return checkCurrentJarVersions(versions);
   }

   @Override
   public void addJarToClassLoader(byte[] jarData) throws IOException {
      File dir = getJarCache();
      File jar = File.createTempFile("runtimeLibrary_", ".jar", dir);
      Lib.writeBytesToFile(jarData, jar);
      availableJars.put(Lib.getJarFileVersion(jar.getAbsolutePath()), jar);
   }

   @Override
   public void resetScriptLoader(String[] classPaths) throws Exception {
      cleanUpNeeded = true;
      initClassloadersWithNoURLs();
      if (scriptClassLoader == null) {
         throw new IllegalStateException("Script Class Loader not yet created");
      }
      if (scriptClassLoader != null) {
         // TODO do we need some cleanup here
      }
      scriptClassLoader =
         new OseeURLClassLoader("Script ClassLoader", Lib.getUrlFromString(classPaths), this.runtimeLibraryLoader);
   }

   @Override
   public Class<?> loadFromScriptClassLoader(String path) throws ClassNotFoundException {
      cleanUpNeeded = true;
      initClassloadersWithNoURLs();
      if (scriptClassLoader == null) {
         throw new IllegalStateException("Script Class Loader not yet created");
      }
      Class<?> scriptClass = scriptClassLoader.loadClass(path);
      GCHelper.getGCHelper().addRefWatch(scriptClass);
      return scriptClass;
   }

   @Override
   public Class<?> loadClass(String name, Version version) throws ClassNotFoundException {
      ExportedPackage[] exportedPackages = packageAdmin.getExportedPackages(getPackageFromClass(name));
      for (ExportedPackage exportedPackage : exportedPackages) {
         Bundle bundle = exportedPackage.getExportingBundle();
         if (bundle.getVersion().equals(version)) {
            return bundle.loadClass(name);
         }
      }
      return null;
   }

   private String getPackageFromClass(String clazz) {
      int index = clazz.lastIndexOf(".");
      if (index > 0) {
         return clazz.substring(0, index);
      } else {
         return "";
      }
   }

   @Override
   public Class<?> loadFromRuntimeLibraryLoader(String path) throws ClassNotFoundException {
      cleanUpNeeded = true;
      initClassloadersWithNoURLs();
      if (runtimeLibraryLoader == null) {
         throw new IllegalStateException("The message/runtime library loader has not been configured");
      }
      Class<?> clazz = runtimeLibraryLoader.loadClass(path);
      GCHelper.getGCHelper().addRefWatch(clazz);
      return clazz;
   }

   @Override
   public void cleanup() {
      if (!cleanUpNeeded) {
         return;
      }
      cleanUpNeeded = false;

      for (Bundle bundle : installedBundles) {
         try {
            bundle.uninstall();
         } catch (BundleException ex) {
            OseeLog.log(AbstractRuntimeManager.class, Level.SEVERE, ex);
         }
      }
      installedBundles.clear();

      for (Bundle bundle : runningBundles) {
         try {
            bundle.stop();
            bundle.uninstall();
         } catch (BundleException ex) {
            OseeLog.log(AbstractRuntimeManager.class, Level.SEVERE, ex);
         }
      }
      runningBundles.clear();
      bundleNameToMd5Map.clear();
      if (packageAdmin != null) {
         packageAdmin.refreshPackages(null);
      }

      runtimeLibraryLoader = null;
      scriptClassLoader = null;
      availableJars.clear();
   }

   protected File getAvailableJar(String version) {
      return availableJars.get(version);
   }

   @Override
   public Element toXml(Document doc) {
      Element el = doc.createElement("RuntimeVersions");

      if (currentJarVersions != null) {
         for (String version : currentJarVersions) {
            Element versionEl = doc.createElement("Version");
            versionEl.appendChild(doc.createTextNode(version));
            el.appendChild(versionEl);
         }
      }
      for (Bundle bundle : runningBundles) {
         String version = (String) bundle.getHeaders().get("Bundle-Version");
         if (version != null) {
            Element versionEl = doc.createElement("Version");
            versionEl.appendChild(doc.createTextNode(bundle.getSymbolicName() + version));
            el.appendChild(versionEl);
         }
         String implVersion = (String) bundle.getHeaders().get("Implementation-Version");
         if (implVersion != null) {
            Element versionEl = doc.createElement("Version");
            versionEl.appendChild(doc.createTextNode(bundle.getSymbolicName() + implVersion));
            el.appendChild(versionEl);
         }
      }
      return el;
   }

   @Override
   public void addRuntimeLibraryListener(RuntimeLibraryListener listener) {
      listeners.add(listener);
   }

   @Override
   public void removeRuntimeLibraryListener(RuntimeLibraryListener listener) {
      listeners.remove(listener);
   }

}
