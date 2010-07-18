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
package org.eclipse.osee.ote.container;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.osee.framework.ui.workspacebundleloader.JarChangeResourceListener;
import org.eclipse.osee.ote.core.BundleInfo;
import org.eclipse.osee.ote.core.OteBundleLocator;
import org.eclipse.osee.ote.runtimemanager.OteUserLibsNature;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class OteClasspathContainer implements IClasspathContainer, IUserLibListener {
   public final static Path ID = new Path("OTE Classpath Container from the extension point");

   private Collection<ClassPathDescription> classPaths;

   private List<BundleInfo> betterPaths;

   private ServiceTracker tracker;

   private OteBundleLocator locator;

   private JavaProject javaProject;

   private LibraryChangeProvider<OteUserLibsNature> userLibListener;

   private JarChangeResourceListener<OteUserLibsNature> userLibResourceListener;

   public OteClasspathContainer(IPath path, IJavaProject javaProject) throws CoreException {
      this.javaProject = (JavaProject) javaProject;

      try {
         BundleContext context = OteContainerActivator.getDefault().getContext();
         tracker = new ServiceTracker(context, OteBundleLocator.class.getName(), null);
         tracker.open(true);
         Object obj = tracker.waitForService(10000);
         locator = (OteBundleLocator)obj;
         
         OteContainerActivator.getDefault().getLibraryChangeProvider().addListener(this);
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }

   }

   /**
    * @param path
    * @return
    */
   private String findProjectRoot(String path) {
      File fileForPath = new File(path);
      File projectFile = recursivelyFindProjectFile(fileForPath);
      return projectFile.getAbsolutePath();
   }

   /**
    * @param fileForPath
    * @return
    */
   private File recursivelyFindProjectFile(File file) {

      if( file == null )
         return file;

      if(fileIsDirectoryWithBin(file)) {
         return file;
      } else {
         return recursivelyFindProjectFile(file.getParentFile());
      }
   }

   /**
    * @param file
    * @return
    */
   private boolean fileIsDirectoryWithBin(File file) {
      if( file.isDirectory() )
      {
         File binChildFile = new File( file.getAbsoluteFile() + "/bin");
         if( binChildFile.exists())
            return true;
      }
      return false;
   }

   /**
    * @param absolutePath
    * @return
    */
   private String getWorkspaceRelativePath(String absolutePath) {
      return absolutePath;
   }

   @Override
   public IClasspathEntry[] getClasspathEntries() {
      List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
      Collection<BundleInfo> runtimeLibUrls;
      try {
         runtimeLibUrls = locator.getRuntimeLibs();
         classPaths = new ArrayList<ClassPathDescription>();
         betterPaths = new ArrayList<BundleInfo>();
         for( BundleInfo info : runtimeLibUrls )
         {

            String binaryFilePath = info.getSystemLocation().getFile();

            if(info.isSystemLibrary())
            {
               entries.add(JavaCore.newLibraryEntry(new Path(binaryFilePath),new Path(binaryFilePath), new Path("/")));
            } else {
               File projectFilePath = recursivelyFindProjectFile(new File(binaryFilePath));
               binaryFilePath = "/" + projectFilePath.getName();

               entries.add(JavaCore.newProjectEntry( new Path(binaryFilePath)));
            }
         }

      }
      catch (Exception ex) {
         ex.printStackTrace();
      }

      IClasspathEntry[] retVal = new IClasspathEntry[entries.size()];
      return entries.toArray(retVal);
   }

   @Override
   public String getDescription() {
      return "OTE Classpath Container";
   }

   @Override
   public int getKind() {
      return IClasspathContainer.K_APPLICATION;
   }

   @Override
   public IPath getPath() {
      return ID;
   }

   private class ClassPathDescription {
      private String sourcePath, binaryPath;

      /**
       * @param sourcePath
       * @param binaryPath
       */
      public ClassPathDescription(String binaryPath, String sourcePath) {
         super();
         this.sourcePath = sourcePath;
         this.binaryPath = binaryPath;
      }

      /**
       * @return the sourcePath
       */
      public String getSourcePath() {
         return sourcePath;
      }

      /**
       * @return the binaryPath
       */
      public String getBinaryPath() {
         return binaryPath;
      }


   }

   @SuppressWarnings("restriction")
   @Override
   public void libraryChanged() {
   }

}
