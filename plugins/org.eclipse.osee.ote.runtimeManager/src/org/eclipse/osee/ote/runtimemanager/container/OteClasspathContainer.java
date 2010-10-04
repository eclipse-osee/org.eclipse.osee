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
package org.eclipse.osee.ote.runtimemanager.container;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osee.ote.core.BundleInfo;
import org.eclipse.osee.ote.core.OteBundleLocator;
import org.eclipse.osee.ote.runtimemanager.RuntimeManager;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteClasspathContainer implements IClasspathContainer {
   public final static Path ID = new Path("OTE Library");
   private ServiceTracker tracker;
   private OteBundleLocator locator;
   private final IJavaProject javaProject;
   private final IPath containerPath;

   private static final List<OteClasspathContainer> activeContainers = new ArrayList<OteClasspathContainer>();

   private static boolean classpathEnabled;
   static {
      classpathEnabled = System.getProperty("ote.container.activate") != null;
   }

   public OteClasspathContainer(IPath path, IJavaProject javaProject) {
      this.javaProject = javaProject;
      this.containerPath = path;

      try {
         BundleContext context = RuntimeManager.getDefault().getContext();
         tracker = new ServiceTracker(context, OteBundleLocator.class.getName(), null);
         tracker.open(true);
         initializeBundleLocator();

      } catch (Exception ex) {
         ex.printStackTrace();
      }

      activeContainers.add(this);
   }

   private void initializeBundleLocator() throws InterruptedException {
      Object obj = tracker.waitForService(1);
      locator = (OteBundleLocator) obj;
   }

   public OteClasspathContainer(OteClasspathContainer oteClasspathContainer) {
      this(oteClasspathContainer.containerPath, oteClasspathContainer.javaProject);
   }

   private File recursivelyFindProjectFile(File file) {

      if (file == null) {
         return file;
      }

      if (fileIsDirectoryWithBin(file)) {
         return file;
      } else {
         return recursivelyFindProjectFile(file.getParentFile());
      }
   }

   private boolean fileIsDirectoryWithBin(File file) {
      if (file.isDirectory()) {
         File binChildFile = new File(file.getAbsoluteFile() + "/bin");
         if (binChildFile.exists()) {
            return true;
         }
      }
      return false;
   }

   @Override
   public IClasspathEntry[] getClasspathEntries() {
      List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
      Collection<BundleInfo> runtimeLibUrls;
      if (classpathEnabled) {
         try {
            lazyLoadLocator();
            if (locator == null) {
               return entries.toArray(new IClasspathEntry[0]);
            }

            runtimeLibUrls = locator.getRuntimeLibs();
            for (BundleInfo info : runtimeLibUrls) {
               String binaryFilePath = info.getFile().getCanonicalPath();

               if (info.isSystemLibrary()) {
                  entries.add(JavaCore.newLibraryEntry(new Path(binaryFilePath), new Path(binaryFilePath),
                     new Path("/")));
               } else {
                  File projectFilePath = recursivelyFindProjectFile(new File(binaryFilePath));
                  if (!projectMatchesClasspathFile(projectFilePath)) {
                     binaryFilePath = "/" + projectFilePath.getName();

                     entries.add(JavaCore.newProjectEntry(new Path(binaryFilePath)));
                  }
               }
            }

         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      IClasspathEntry[] retVal = new IClasspathEntry[entries.size()];
      return entries.toArray(retVal);
   }

   private void lazyLoadLocator() throws InterruptedException {
      if (locator == null) {
         initializeBundleLocator();
      }
   }

   private boolean projectMatchesClasspathFile(File projectFilePath) {
      String projectBeingResolvedName = javaProject.getPath().toString();
      String classpathFilePath = projectFilePath.getName();
      if (projectBeingResolvedName.contains(classpathFilePath)) {
         return true;
      }

      return false;
   }

   @Override
   public String getDescription() {
      return ID.segment(0);
   }

   @Override
   public int getKind() {
      return IClasspathContainer.K_APPLICATION;
   }

   @Override
   public IPath getPath() {
      return ID;
   }

   public static void refreshAll() {
      if (classpathEnabled) {
         for (OteClasspathContainer container : activeContainers.toArray(new OteClasspathContainer[0])) {
            container.refresh();
         }
      }
   }

   public void refresh() {
      if (classpathEnabled) {
         try {
            activeContainers.remove(this);
            if (javaProject.isOpen()) {
               JavaCore.setClasspathContainer(containerPath, new IJavaProject[] {javaProject},
                  new IClasspathContainer[] {new OteClasspathContainer(this)}, null);
            }
            // new Thread(new Runnable() {
            //
            // @Override
            // public void run() {
            // try {
            // Thread.sleep(10000);
            // }
            // catch (InterruptedException ex) {
            // ex.printStackTrace();
            // }
            // catch (Exception ex) {
            // ex.printStackTrace();
            // }
            // }
            //
            // }).start();
         } catch (Exception ex) {
         }
      }
   }

}
