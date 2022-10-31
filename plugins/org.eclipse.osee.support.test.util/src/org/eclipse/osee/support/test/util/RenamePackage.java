/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.support.test.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/*
*
* @author Baily Roberts
*
*/

public class RenamePackage {

   static String[] classes;
   static int classCount = 0;
   static int classCounter = 0;

   public static void main(String[] args) {
      Scanner scan = new Scanner(System.in);

      System.out.println("What package do you want to rename?");
      String oldPackage = scan.nextLine();
      System.out.println("What name do you want to rename the package to?");

      String newPackageName = scan.nextLine();

      int pluginIndex = System.getProperty("user.dir").indexOf("plugins");
      String pluginsPath = System.getProperty("user.dir").substring(0, pluginIndex + 8);
      String featPath = pluginsPath.substring(0, pluginsPath.length() - 8) + "features";

      File pluginsDir = new File(pluginsPath);
      File featDir = new File(featPath);
      File[] fileDir = pluginsDir.listFiles();

      for (int i = 0; i < fileDir.length; i++) {

         renameOSGIINFDirectory(new File(fileDir[i] + "\\OSGI-INF"), oldPackage, newPackageName);
         renameManifest(fileDir[i], oldPackage, newPackageName);
         renameJavaImports(new File(fileDir[i] + "\\src"), oldPackage, newPackageName);

      }
      File oldPackageTest = new File(pluginsDir + "\\" + oldPackage + ".test");
      if (oldPackageTest.isDirectory()) {

         findTestJavaFiles(new File(oldPackageTest + "\\src"), oldPackage + ".test", newPackageName + ".test");
         renameManifest(new File(oldPackageTest + ""), oldPackage + ".test", newPackageName + ".test");
         renameFiles(oldPackage + ".test", newPackageName + ".test", pluginsDir, featDir);

      }

      renameFiles(oldPackage, newPackageName, pluginsDir, featDir);
   }

   protected static void renameFiles(String oldPackage, String newPackageName, File pluginsDir, File featDir) {
      renameOtherFiles(pluginsDir, oldPackage, newPackageName, ".project");
      renameOtherFiles(pluginsDir, oldPackage, newPackageName, "plugin.xml");
      renameFeatures(featDir, oldPackage, newPackageName);
      renamePom(pluginsDir, oldPackage, newPackageName);

      renameBinSubpackages(pluginsDir, oldPackage, newPackageName);
      if (oldPackage.contains("test")) {
         renameTestSubpackages(pluginsDir, oldPackage, newPackageName);

      } else {
         renameSubpackages(pluginsDir, oldPackage, newPackageName);
      }
      deleteSubPackages(pluginsDir, oldPackage);

      renamePackage(pluginsDir, oldPackage, newPackageName);

      deleteRemnents(new File(pluginsDir + "\\" + oldPackage));
   }

   protected static void findTestJavaFiles(File path, String oldPackageName, String newPackageName) {
      File ats = new File(path.getAbsolutePath());
      File[] atsPackages = ats.listFiles();
      if (atsPackages == null) {
         return;
      }

      String oldPackageWithoutTest = oldPackageName;

      for (int i = 0; i < atsPackages.length; i++) {

         if (atsPackages[i].isDirectory()) {
            findTestJavaFiles(atsPackages[i], oldPackageName, newPackageName);
         } else if (atsPackages[i].getAbsolutePath().contains("java")) {
            if (atsPackages[i].getAbsolutePath().contains("refactor")) {
               return;
            }

            renameTestJavaFiles(atsPackages[i], oldPackageWithoutTest, newPackageName);

         }
      }
   }

   protected static void renameTestJavaFiles(File path, String oldPackageName, String newPackageName) {
      File old, temp;
      old = new File(path.getAbsolutePath());

      temp = new File(path.getAbsolutePath().substring(0, path.getAbsolutePath().lastIndexOf("\\")) + "\\test.java");
      if (old.isFile() == false) {
         return;
      }
      oldPackageName = oldPackageName.substring(0, oldPackageName.indexOf("test") - 1);
      newPackageName = newPackageName.substring(0, newPackageName.indexOf("test") - 1);

      FileReader fr;
      BufferedReader br = null;
      FileWriter fw;
      BufferedWriter bw = null;
      String tempLine;
      boolean isChanged = false;

      try {

         br = new BufferedReader(new FileReader(old.getAbsolutePath()));

         bw = new BufferedWriter(new FileWriter(temp.getAbsolutePath()));

         String line = br.readLine();

         while (line != null) {
            if (line.contains(oldPackageName)) {

               isChanged = true;
               line = line.replace(oldPackageName, newPackageName);

            }
            bw.write(line + "\n");
            line = br.readLine();
         }
      } catch (Exception e) {
         System.out.println("exception: " + e);
      } finally {
         try {
            if (br != null) {
               br.close();
            }
         } catch (IOException e) {
            System.err.println("IOException: " + e);
         }
         try {
            if (bw != null) {
               bw.close();
            }
         } catch (IOException e) {
            System.err.println("IOException: " + e);

         }
         if (isChanged == true) {
            File oldFile = new File(old.getAbsolutePath());
            oldFile.delete();

            File newF = new File(temp.getAbsolutePath());
            newF.renameTo(old);
         } else {
            old.delete();
         }
      }

   }

   protected static void renameOSGIINFDirectory(File path, String oldPackageName, String newPackageName) {
      if (path == null || !path.isDirectory()) {
         return;
      }

      File[] OSGIINFFiles = path.listFiles();
      if (OSGIINFFiles == null) {
         return;
      }
      for (int i = 0; i < OSGIINFFiles.length; i++) {
         if (OSGIINFFiles[i].getAbsolutePath().contains("xml")) {
            renameOtherFiles(OSGIINFFiles[i], oldPackageName, newPackageName, "OSGIINF");
         }
      }
   }

   protected static void deleteSubPackages(File path, String oldPackageName) {
      File srcDir = new File(path.getAbsolutePath() + "\\" + oldPackageName + "\\src\\");
      String[] dirs = oldPackageName.split("\\.");
      File temp;
      String subPackagePath = "";
      int count;
      for (int i = dirs.length; i > 0; i--) {
         subPackagePath = "";
         count = 0;
         while (count < i) {
            subPackagePath += dirs[count] + "\\";
            count++;
         }
         temp = new File(srcDir.getAbsolutePath() + "\\" + subPackagePath);
         if (temp.isDirectory()) {
            if (temp.list().length == 0) {
               temp.delete();
            }
         }

      }
   }

   protected static void deleteRemnents(File path) {
      File toDelete = new File(path + "");
      if (toDelete.isDirectory()) {
         File[] dirs = toDelete.listFiles();
         for (int i = 0; i < dirs.length; i++) {
            if (dirs[i].isFile() == true) {
               dirs[i].delete();
            } else {
               deleteRemnents(dirs[i]);
            }
         }

      }

      toDelete.delete();

   }

   protected static void renameBinSubpackages(File path, String oldPackageName, String newPackageName) {
      String oldPack = oldPackageName.replace(".", "\\");
      String srcDir = path + "\\" + oldPackageName + "\\bin\\";
      String[] newDirs = newPackageName.split("\\.");
      for (int i = 0; i < newDirs.length - 1; i++) {
         srcDir += newDirs[i] + "\\";
         if (new File(srcDir).isDirectory() == false) {
            new File(srcDir).mkdir();
         }
      }
      srcDir += newDirs[newDirs.length - 1];

      File oldDir = new File(path + "\\" + oldPackageName + "\\bin\\" + oldPack);
      File newDir = new File(srcDir);
      oldDir.renameTo(newDir);

   }

   protected static void renameTestSubpackages(File path, String oldPackageName, String newPackageName) {
      String oldPack = oldPackageName.replace(".", "\\");
      String srcDir = path + "\\" + oldPackageName + "\\src\\";
      String[] newDirs = newPackageName.split("\\.");
      for (int i = 0; i < newDirs.length - 1; i++) {
         srcDir += newDirs[i] + "\\";

         if (new File(srcDir).isDirectory() == false) {
            new File(srcDir).mkdir();
         }
      }
      srcDir += newDirs[newDirs.length - 1];
      if (srcDir.substring(srcDir.lastIndexOf("\\")).contains("test")) {
         if (new File(srcDir).isDirectory()) {
            renameSubpackages(path, oldPackageName, newPackageName);

         } else {
            String oldDirPath = path + "\\" + oldPackageName + "\\src\\" + oldPack;
            File oldDir = new File(oldDirPath.substring(0, oldDirPath.lastIndexOf("test") - 1));
            File newDir = new File(srcDir.substring(0, srcDir.lastIndexOf("test") - 1));
            if (newDir.isDirectory() == true) {
               newDir.delete();
            }
            oldDir.renameTo(newDir);
         }

      }

   }

   protected static void renameSubpackages(File path, String oldPackageName, String newPackageName) {
      String oldPack = oldPackageName.replace(".", "\\");
      String srcDir = path + "\\" + oldPackageName + "\\src\\";
      String[] newDirs = newPackageName.split("\\.");
      for (int i = 0; i < newDirs.length - 1; i++) {
         srcDir += newDirs[i] + "\\";

         if (new File(srcDir).isDirectory() == false) {
            new File(srcDir).mkdir();
         }
      }
      srcDir += newDirs[newDirs.length - 1];

      File oldDir = new File(path + "\\" + oldPackageName + "\\src\\" + oldPack);
      File newDir = new File(srcDir);
      oldDir.renameTo(newDir);

   }

   protected static void renameFeatures(File path, String oldPackageName, String newPackageName) {
      File[] features = path.listFiles();
      String feature = "";
      for (int i = 0; i < features.length; i++) {
         feature = features[i].getAbsolutePath();
         renameOtherFiles(features[i], oldPackageName, newPackageName, "feature");
      }

   }

   protected static void renameOtherFiles(File path, String oldPackageName, String newPackageName, String type) {
      File old, temp;
      String packageName = "";
      boolean inMainPackageToChange = false;

      boolean pastParentTags = false;
      boolean pastBuildTags = false;
      String packageWithoutTest = "";
      if (type == "feature") {
         old = new File(path.getAbsolutePath() + "\\feature.xml");
         temp = new File(path.getAbsolutePath() + "\\test.xml");
         if (!old.isFile()) {
            return;
         }
      } else if (type == "pom") {
         old = new File(path.getAbsolutePath() + "\\" + oldPackageName + "\\pom.xml");
         temp = new File(path.getAbsolutePath() + "\\" + oldPackageName + "\\testpom.xml");
         if (oldPackageName.contains("test")) {
            packageWithoutTest = oldPackageName.substring(0, oldPackageName.indexOf("test") - 1);

         }
      } else if (type == "parentPom") {
         old = new File(path.getAbsolutePath() + "\\pom.xml");
         temp = new File(path.getAbsolutePath() + "\\testpom.xml");
      } else if (type == "manifest") {
         int src = path.getAbsolutePath().lastIndexOf("\\");
         packageName = path.getAbsolutePath().substring(src + 1);

         if (packageName.equals(oldPackageName)) {
            inMainPackageToChange = true;
         }
         old = new File(path.getAbsolutePath() + "\\META-INF\\MANIFEST.MF");
         if (old.isFile() == false) {
            return;
         }
         temp = new File(path.getAbsolutePath() + "\\META-INF\\TEST.MF");
      } else if (type == "java") {
         old = new File(path.getAbsolutePath());
         int index = path.getAbsolutePath().lastIndexOf("\\");
         String t = path.getAbsolutePath().substring(0, index);
         temp = new File(t + "\\test.java");
         packageName = checkIfInMainPackage(path);
         if (packageName.equals(oldPackageName)) {
            inMainPackageToChange = true;
         }
      } else if (type == "OSGIINF") {
         old = new File(path.getAbsolutePath());
         int index = path.getAbsolutePath().lastIndexOf("\\");
         String t = path.getAbsolutePath().substring(0, index);
         temp = new File(t + "\\test.xml");
      }

      else {
         old = new File(path.getAbsolutePath() + "\\" + oldPackageName + "\\" + type);
         if (old.isFile() == false) {
            return;
         }
         temp = new File(path.getAbsolutePath() + "\\" + oldPackageName + "\\test" + type);
      }
      BufferedReader br = null;
      BufferedWriter bw = null;
      boolean isChanged = false;

      try {

         br = new BufferedReader(new FileReader(old.getAbsolutePath()));

         bw = new BufferedWriter(new FileWriter(temp.getAbsolutePath()));

         String line = br.readLine();

         while (line != null) {

            if (line.contains("<testClass>")) {
               isChanged = true;
               line = line.replace(oldPackageName.substring(0, oldPackageName.indexOf("test") - 1),
                  newPackageName.substring(0, newPackageName.lastIndexOf("test") - 1));
            }
            if (line.contains(oldPackageName)) {

               if (type != "pom" || pastParentTags == true) {
                  int pluginIndex = line.indexOf("plugins");
                  int modIndex = line.indexOf("</module>");
                  if (pluginIndex > 0 && modIndex > 0) {
                     String module = line.substring(pluginIndex + 8, modIndex);
                     if (module.length() <= oldPackageName.length()) {

                        line = line.replace(oldPackageName, newPackageName);

                        isChanged = true;

                     }
                  } else {
                     if (line.contains("Bundle-SymbolicName")) {
                        int semiColonIndex = line.indexOf(";");
                        String bundleName = line.substring(21, semiColonIndex);
                        if (bundleName.length() == oldPackageName.length()) {
                           isChanged = true;
                           line = line.replace(oldPackageName, newPackageName);

                        }
                     } else {

                        if (!line.contains("package") || oldPackageName.length() == packageName.length()) {

                           if (line.contains("import")) {
                              int lastPeriodIndex = line.lastIndexOf(".");
                              int semiColonIndex = line.indexOf(";");
                              String javaClass = line.substring(lastPeriodIndex + 1, semiColonIndex);
                              String srcPath =
                                 path.getAbsolutePath().substring(0, path.getAbsolutePath().indexOf("src") + 3);
                              classCount = 0;
                              classCounter = 0;
                              countJavaNames(new File(srcPath));
                              classesToArray(new File(srcPath));

                              isChanged = true;
                              line = line.replace(oldPackageName, newPackageName);

                           } else {
                              if (!line.contains("Export-Package") || inMainPackageToChange) {
                                 isChanged = true;
                                 line = line.replace(oldPackageName, newPackageName);
                              }
                           }
                        }

                     }
                  }
               }

            }
            if (line.contains("</parent>")) {
               pastParentTags = true;
            }
            bw.write(line + "\n");
            line = br.readLine();
         }
      } catch (Exception e) {
         System.err.println("Exception: " + e);
      } finally {
         try {
            if (br != null) {
               br.close();
            }
         } catch (IOException e) {
            System.err.println("Exception: " + e);

         }
         try {
            if (bw != null) {
               bw.close();
            }
         } catch (IOException e) {
            System.err.println("Exception: " + e);
         }
         if (isChanged == true) {
            File oldFile = new File(old.getAbsolutePath());
            oldFile.delete();

            temp.renameTo(old);
         } else {
            temp.delete();
         }
      }

   }

   private static String checkIfInMainPackage(File path) {
      String packageName;
      int pluginsIndex = path.getAbsolutePath().indexOf("plugins");
      int src = path.getAbsolutePath().indexOf("src");
      packageName = path.getAbsolutePath().substring(pluginsIndex + 8, src - 1);
      return packageName;
   }

   protected static void renameJavaImports(File path, String oldPackageName, String newPackageName) {

      File ats = new File(path.getAbsolutePath());
      File[] Packages = ats.listFiles();
      if (Packages == null) {
         return;
      }
      String oldPackageWithoutTest = oldPackageName;

      for (int i = 0; i < Packages.length; i++) {
         if (Packages[i].isDirectory()) {
            renameJavaImports(Packages[i], oldPackageName, newPackageName);
         } else if (Packages[i].getAbsolutePath().contains("java")) {
            if (Packages[i].getAbsolutePath().contains("RenamePackage")) {
               return;
            }

            renameOtherFiles(Packages[i], oldPackageWithoutTest, newPackageName, "java");

         }
      }

   }

   protected static void countJavaNames(File path) {
      File ats = new File(path.getAbsolutePath());
      File[] atsPackages = ats.listFiles();
      for (int i = 0; i < atsPackages.length; i++) {
         if (atsPackages[i].isDirectory()) {
            countJavaNames(atsPackages[i]);
         } else if (atsPackages[i].getAbsolutePath().contains("java")) {
            classCount++;
         }
      }
      classes = new String[classCount];

   }

   protected static void classesToArray(File path) {
      File ats = new File(path.getAbsolutePath());
      File[] atsPackages = ats.listFiles();
      for (int i = 0; i < atsPackages.length; i++) {
         if (atsPackages[i].isDirectory()) {
            classesToArray(atsPackages[i]);
         } else if (atsPackages[i].getAbsolutePath().contains("java")) {
            classes[classCounter] = atsPackages[i].getAbsolutePath();
            classCounter++;
         }
      }

   }

   private static boolean checkJavaClasses(String packageName) {

      for (int i = 0; i < classes.length; i++) {
         if (classes[i].contains(packageName) == true) {
            return true;
         }
      }
      return false;
   }

   protected static boolean checkJavaClassNames(File path, String oldPackageName) {
      File ats = new File(path.getAbsolutePath());
      File[] atsPackages = ats.listFiles();
      String[] javaClassArray = new String[classCount];
      int arrayCounter = 0;
      if (atsPackages == null) {
         return false;
      }
      for (int i = 0; i < atsPackages.length; i++) {
         if (atsPackages[i].isDirectory()) {
            checkJavaClassNames(atsPackages[i], oldPackageName);
         } else if (atsPackages[i].getAbsolutePath().contains("java")) {

            javaClassArray[arrayCounter] = atsPackages[i].getAbsolutePath();
            arrayCounter++;

         }
      }

      for (int i = 0; i < javaClassArray.length; i++) {

         if (javaClassArray[i].contains(oldPackageName)) {
            return true;
         }
      }

      return false;
   }

   protected static void renamePackage(File path, String oldPackageName, String newPackageName) {
      File oldFile = new File(path.getAbsolutePath() + "\\" + oldPackageName);
      File newFile = new File(path.getAbsolutePath() + "\\" + newPackageName);
      oldFile.renameTo(newFile);
   }

   public static String getPluginFromPath(File path) {
      int index = path.getAbsolutePath().indexOf("plugins");
      String packageName = path.getAbsolutePath().substring(index + 7);
      return packageName;
   }

   protected static void renameManifest(File path, String oldPackageName, String newPackageName) {
      renameOtherFiles(path, oldPackageName, newPackageName, "manifest");

   }

   protected static void renamePom(File path, String oldPackageName, String newPackageName) {
      renameOtherFiles(path, oldPackageName, newPackageName, "pom");
      try (FileReader fr = new FileReader(path + "\\" + oldPackageName + "\\pom.xml");
         BufferedReader br = new BufferedReader(fr)) {

         String line = br.readLine();
         while (line != null) {
            if (line.contains("<relativePath>")) {
               int tagIndex = line.lastIndexOf("<relativePath>");
               int pluginIndex = line.indexOf("plugin");

               String parentPom = line.substring(tagIndex + 28, line.indexOf("</relativePath"));
               renameOtherFiles(new File(path + "\\" + parentPom), oldPackageName, newPackageName, "parentPom");
            }
            line = br.readLine();

         }
         br.close();

      } catch (Exception ex) {
         System.err.println("Exception: " + ex);
      }
   }
}
