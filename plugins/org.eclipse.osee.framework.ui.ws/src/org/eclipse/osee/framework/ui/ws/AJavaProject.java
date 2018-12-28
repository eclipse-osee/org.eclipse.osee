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
package org.eclipse.osee.framework.ui.ws;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public final class AJavaProject {

   private static final Map<IJavaProject, IClasspathEntry[]> cachedPath =
      new HashMap<>();

   private static final Pattern JAVA_PACKAGE_PATTERN = Pattern.compile(".*?package\\s*(.*?);.*", Pattern.DOTALL);
   private static final Matcher JAVA_PACKAGE_MATCHER = JAVA_PACKAGE_PATTERN.matcher("");

   private AJavaProject() {
      // Private to prevent cons
   }

   private static IClasspathEntry[] localGetResolvedClasspath(IJavaProject javaProject) throws JavaModelException {
      IClasspathEntry[] paths = cachedPath.get(javaProject);
      if (paths == null) {
         paths = javaProject.getResolvedClasspath(true);
         cachedPath.put(javaProject, paths);
      }
      return paths;
   }

   public static String getClassName(String file) {
      String classname = null;
      try {
         String packageName = "";
         File java = new File(file);
         packageName = getJavaPackage(java);
         if (packageName.length() > 0) {
            packageName += ".";
         }
         packageName += java.getName().replace(".java", "");
         classname = packageName;
      } catch (IOException e) {
         e.printStackTrace();
      }
      return classname;
   }

   public static String getJavaPackage(File javaFile) throws IOException {
      String javaFileContent = Lib.fileToString(javaFile);
      Matcher matcher = JAVA_PACKAGE_MATCHER;
      matcher.reset(javaFileContent);
      return matcher.matches() ? matcher.group(1) : "";
   }

   public static ArrayList<File> getJavaProjectProjectDependancies(IJavaProject javaProject) {
      ArrayList<File> urls = new ArrayList<>();
      try {
         IClasspathEntry[] paths = localGetResolvedClasspath(javaProject);
         for (int i = 0; i < paths.length; i++) {
            if (paths[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
               if (paths[i].getPath().toFile().exists()) {
                  //          urls.add(paths[i].getPath().toFile());
               } else {
                  File file = null;
                  file = new File(AWorkspace.getWorkspacePath().concat(paths[i].getPath().toOSString()));
                  if (file.exists()) {
                     urls.add(file);
                  }
               }
            } else if (paths[i].getEntryKind() == IClasspathEntry.CPE_PROJECT) {
               urls.add(new File(AWorkspace.getWorkspacePath().concat(
                  paths[i].getPath().toFile().getPath().concat(File.separator + "bin" + File.separator))));
            } else if (paths[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
               File projectlocation = javaProject.getProject().getLocation().toFile();
               File projecttricky = javaProject.getProject().getFullPath().toFile();
               IPath output = paths[i].getOutputLocation();
               File fileLocation;
               if (output == null) {
                  fileLocation = javaProject.getOutputLocation().toFile();
               } else {
                  fileLocation = paths[i].getOutputLocation().toFile();
               }
               String realLocation =
                  fileLocation.toString().replace(projecttricky.toString(), projectlocation.toString());
               urls.add(new File(realLocation));
            }
         }

      } catch (JavaModelException ex) {
         ex.printStackTrace();
      }
      return urls;
   }
}