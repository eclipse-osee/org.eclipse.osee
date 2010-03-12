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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.eclipse.osee.framework.ui.workspacebundleloader.IJarChangeListener;
import org.eclipse.osee.framework.ui.workspacebundleloader.JarCollectionNature;

/**
 * @author Robert A. Fisher
 *
 */
public class JarListenerStub<T extends JarCollectionNature> implements IJarChangeListener<T> {

   private final Object bundleSynchronizer;
   private final Set<String> newBundles;
   private final Set<String> changedBundles;
   private final Set<String> removedBundles;

   public JarListenerStub() {
      this.bundleSynchronizer = new Object();
      this.newBundles = new HashSet<String>();
      this.changedBundles = new HashSet<String>();
      this.removedBundles = new HashSet<String>();
   }
   
   @Override
   public void handleBundleAdded(URL url) {
      try {
         String bundleName = getBundleNameFromJar(url);
         synchronized (bundleSynchronizer) {
            newBundles.add(bundleName);
            changedBundles.remove(bundleName);
            removedBundles.remove(bundleName);
         }
         System.out.println("Bundle added:" + bundleName);
      } catch (IOException ex) {
      }
   }

   @Override
   public void handleBundleChanged(URL url) {
      try {
         String bundleName = getBundleNameFromJar(url);
         synchronized (bundleSynchronizer) {
            changedBundles.add(bundleName);
            newBundles.remove(bundleName);
            removedBundles.remove(bundleName);
         }
         System.out.println("Bundle changed:" + bundleName);
      } catch (IOException ex) {
      }
   }

   @Override
   public void handleBundleRemoved(URL url) {
      try {
         String bundleName = getBundleNameFromJar(url);
         synchronized (bundleSynchronizer) {
            removedBundles.add(bundleName);
            newBundles.remove(bundleName);
            changedBundles.remove(bundleName);
         }
         System.out.println("Bundle removed:" + bundleName);
      } catch (IOException ex) {
      }
   }

   @Override
   public void handleNatureClosed(T nature) {
      System.out.println("Project closed: " + nature.getProject().getName());
      for (URL url : nature.getBundles()) {
         handleBundleRemoved(url);
      }
   }

   @Override
   public void handlePostChange() {
      System.out.println("Bunch of changes just finished");
   }
   
   private <S extends Object> Set<S> duplicateAndClear(Set<S> set) {
      synchronized (bundleSynchronizer) {
         Set<S> returnBundles = new HashSet<S>(set);
         set.clear();
         return returnBundles;
      }
   }

   /**
    * @return the newBundles
    */
   public Set<String> consumeNewBundles() {
      return duplicateAndClear(newBundles);
   }

   /**
    * @return the changedBundles
    */
   public Set<String> consumeChangedBundles() {
      return duplicateAndClear(changedBundles);
   }

   /**
    * @return the removedBundles
    */
   public Set<String> consumeRemovedBundles() {
      return duplicateAndClear(removedBundles);
   }

   /**
    * @param url
    * @return
    * @throws IOException
    */
   private String getBundleNameFromJar(URL url) throws IOException {
      File file;
      try {
         file = new File(url.toURI());
      } catch(URISyntaxException ex) {
         file = new File(url.getPath());
      }

      JarFile jarFile = new JarFile(file);
      Manifest jarManifest = jarFile.getManifest();
      return BundleInfo.generateBundleName(jarManifest);
   }

}