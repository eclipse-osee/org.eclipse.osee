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
package org.eclipse.osee.framework.ui.plugin.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class AJavaProject {

   private static Map<IJavaProject, IClasspathEntry[]> cachedPath = new HashMap<IJavaProject, IClasspathEntry[]>();

   
   private static IClasspathEntry[] localGetResolvedClasspath(IJavaProject javaProject) throws JavaModelException {
	   IClasspathEntry[] paths = cachedPath.get(javaProject);
       if(paths == null){
      	 paths = javaProject.getResolvedClasspath(true);
      	 cachedPath.put(javaProject, paths);
       }
       return paths;
   }

   public static IJavaProject getJavaProject(File file) {
      IFile ifile = AWorkspace.fileToIFile(file);
      return JavaCore.create(ifile.getProject());
   }

   public static ArrayList<URL> getAllJavaProjectDependancies(IJavaProject javaProject) {
      ArrayList<URL> urls = new ArrayList<URL>();
      ArrayList<File> files = getJavaProjectProjectDependancies(javaProject);
      for (int i = 0; i < files.size(); i++) {
         try {
            urls.add(files.get(i).toURL());
         } catch (MalformedURLException ex) {
            ex.printStackTrace();
         }
      }
      return urls;
   }

   public static String getClassFilePath(String file) {
      String classFile = null;
      IJavaProject javaProject = getJavaProject(new File(file));
      //ArrayList<File> urls = new ArrayList<File>();
      try {
    	 
         IClasspathEntry[] paths = localGetResolvedClasspath(javaProject);
         
         for (int i = 0; i < paths.length; i++) {
            if (paths[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
               File projectlocation = javaProject.getProject().getLocation().toFile();
               File projecttricky = javaProject.getProject().getFullPath().toFile();
               IPath output = paths[i].getOutputLocation();
               File fileLocation;
               if (output == null) {
                  fileLocation = new File(paths[i].getPath().toFile().getPath().replace("src", "bin"));
               } else {
                  fileLocation = paths[i].getOutputLocation().toFile();
               }
               File javaFileLocation = paths[i].getPath().toFile();
               String realClassLocation =
                     fileLocation.toString().replace(projecttricky.toString(), projectlocation.toString());
               String realJavaLocation =
                     javaFileLocation.toString().replace(projecttricky.toString(), projectlocation.toString());
               String packagePath = file.replace(realJavaLocation, "");
               packagePath = packagePath.replace(".java", ".class");
               File theclassfile = new File(realClassLocation, packagePath);
               if (theclassfile.exists()) {
                  classFile = theclassfile.getAbsolutePath();
                  break;
               }
            }
         }

      } catch (JavaModelException ex) {
         ex.printStackTrace();
      }
      if (classFile == null) {
         try {
            String packageName = "";
            File java = new File(file);
            packageName = getJavaPackage(java);
            packageName = packageName.replace(".", File.separator);
            packageName += File.separator + java.getName();
            classFile = file.replace(packageName, "");
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return classFile;
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
      String packageName = "";
      String javaFileContent = Lib.fileToString(javaFile);
      Pattern pattern = Pattern.compile(".*package\\s*(.*?);.*", Pattern.DOTALL);
      Matcher match = pattern.matcher(javaFileContent);
      if (match.matches()) {
         packageName = match.group(1);
      }
      return packageName;
   }

   public static ArrayList<File> getJavaProjectProjectDependancies(IJavaProject javaProject) {
      ArrayList<File> urls = new ArrayList<File>();
      try {
         IClasspathEntry[] paths = localGetResolvedClasspath(javaProject);
         for (int i = 0; i < paths.length; i++) {
            if (paths[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
               if (paths[i].getPath().toFile().exists()) {
        //          urls.add(paths[i].getPath().toFile());
               } else {
                  File f = null;
                  f = new File(AWorkspace.getWorkspacePath().concat(paths[i].getPath().toOSString()));
                  if (f != null && f.exists()) {
                     urls.add(f);
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
                  fileLocation = new File(paths[i].getPath().toFile().getPath().replace("src", "bin"));
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