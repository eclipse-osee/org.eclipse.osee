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
package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;
import java.util.logging.Level;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Robert A. Fisher
 */
public class JarCollectionNature implements IProjectNature {

   private final Name BUNDLE_PATH_ATTRIBUTE;
   private boolean isClosing;

   protected IProject project;

   /**
    * @param BUNDLE_PATH_ATTRIBUTE the name of the attribute in the MANIFEST.MF to look at when looking for the path to
    * the jars being provided.
    */
   public JarCollectionNature(String BUNDLE_PATH_ATTRIBUTE) {
      super();
      this.BUNDLE_PATH_ATTRIBUTE = new Name(BUNDLE_PATH_ATTRIBUTE);
      this.isClosing = false;
   }

   /**
    * @return the isClosing
    */
   public boolean isClosing() {
      return isClosing;
   }

   /**
    * @param isClosing the isClosing to set
    */
   public void setClosing(boolean isClosing) {
      this.isClosing = isClosing;
   }

   @Override
   public void configure() {
      // do nothing
   }

   @Override
   public void deconfigure() {
      // do nothing
   }

   @Override
   public IProject getProject() {
      return project;
   }

   @Override
   public void setProject(IProject project) {
      this.project = project;
   }

   public Collection<URL> getBundles() {
      Collection<URL> urls = new ArrayList<>();

      if (isClosing) {
         return urls;
      }

      IPath[] paths = getProjectRelativeBundlePaths();
      for (IPath path : paths) {
         IPath pluginsPath = project.getLocation().append(path);
         File pluginDir = pluginsPath.toFile();
         File[] jars = pluginDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
               return name.endsWith(".jar");
            }
         });

         if (jars != null) {
            for (File jar : jars) {
               try {
                  urls.add(jar.toURI().toURL());
               } catch (MalformedURLException ex) {
                  OseeLog.log(JarCollectionNature.class, Level.SEVERE, ex);
               }
            }
         }
      }
      return urls;
   }

   public IPath[] getProjectRelativeBundlePaths() {
      Manifest manifest = getManifestFile();
      Path[] paths;
      Attributes mainAttributes = manifest.getMainAttributes();
      String pathString;
      if (mainAttributes.containsKey(BUNDLE_PATH_ATTRIBUTE)) {
         pathString = mainAttributes.getValue(BUNDLE_PATH_ATTRIBUTE);
         String[] tempPaths = pathString.split(",");
         paths = new Path[tempPaths.length];
         for (int i = 0; i < paths.length; i++) {
            paths[i] = new Path(tempPaths[i].trim());
         }
      } else {
         paths = new Path[1];
         paths[0] = new Path("plugins");
      }
      return paths;
   }

   private Manifest getManifestFile() {
      try {
         File manifestFile = project.getLocation().append("META-INF").append("MANIFEST.MF").toFile();
         if (manifestFile.exists()) {
            return new Manifest(new FileInputStream(manifestFile));
         } else {
            return null;
         }
      } catch (Exception ex) {
         ex.printStackTrace();
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   protected static <T extends JarCollectionNature> Collection<T> getWorkspaceProjects(String natureId, Class<T> clazz) throws CoreException {
      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      IWorkspaceRoot workspaceRoot = workspace.getRoot();
      IProject[] projects = workspaceRoot.getProjects();

      Collection<T> natures = new LinkedList<>();

      for (IProject project : projects) {
         if (project.isOpen()) {
            IProjectNature nature = project.getNature(natureId);
            if (nature != null) {
               JarCollectionNature jarNature = (JarCollectionNature) nature;
               natures.add((T) jarNature);
            }
         }
      }

      return natures;
   }
}