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
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.eclipse.osee.framework.jdk.core.util.io.streams.StreamCatcher;

/**
 * @author Donald G. Dunne
 */
public class WordCompareTest {

   static public File getFile(String description) {
      File toReturn = new File("");

      JFileChooser chooser = new JFileChooser();
      chooser.setSelectedFile(toReturn);
      chooser.setFileFilter(new FileFilter() {

         @Override
         public String getDescription() {
            return "Select an Xml File";
         }

         public boolean accept(File file) {
            return file.isDirectory() || (file.isFile() && file.getName().endsWith(".xml"));
         }
      });

      int returnVal = chooser.showDialog(null, description);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         toReturn = chooser.getSelectedFile();
      }

      return toReturn;
   }

   private static URL getClassLocation(final Class<?> classToFind) {
      URL result = null;
      if (classToFind == null) {
         throw new IllegalArgumentException("Class is null");
      }
      final String classAsResource = classToFind.getName().replace('.', '/').concat(".class");
      final ProtectionDomain pd = classToFind.getProtectionDomain();
      if (pd != null) {
         final CodeSource cs = pd.getCodeSource();
         if (cs != null) {
            result = cs.getLocation();
         }
         if (result != null) {
            // Convert a code source location into a full class file location
            if (result.getProtocol().equals("file")) {
               try {
                  if (result.toExternalForm().endsWith(".jar") || result.toExternalForm().endsWith(".zip"))
                     result = new URL("jar:".concat(result.toExternalForm()).concat("!/").concat(classAsResource));
                  else if (new File(result.getFile()).isDirectory()) result = new URL(result, classAsResource);
               } catch (MalformedURLException ignore) {
               }
            }
         }
      }
      if (result == null) {
         // Try to find class definition as a resource
         final ClassLoader classLoader = classToFind.getClassLoader();
         result =
               classLoader != null ? classLoader.getResource(classAsResource) : ClassLoader.getSystemResource(classAsResource);
      }
      return result;
   }

   public static File getFileSystemPath(String entry) {
      File toReturn = null;
      URL url = getClassLocation(WordCompareTest.class);
      if (url != null) {
         File temp = new File(url.getFile());
         int cnt = 12;
         while (!temp.getName().equals("bin") && cnt >= 0) {
            temp = temp.getParentFile();
            cnt--;
         }
         temp = temp.getParentFile();
         toReturn = new File(temp.getAbsolutePath() + File.separator + entry);
      }
      return toReturn;
   }

   public static void main(String[] args) {

      try {
         String diffPath = System.getProperty("user.home") + File.separator + "DiffResults";
         File baseFile = getFile("Select Base Xml File To Compare");
         if (!baseFile.exists()) {
            throw new IllegalStateException("baseFile doesn't exist => " + baseFile);
         }

         File newerFile = getFile("Select Newer Xml File To Compare");
         if (!newerFile.exists()) {
            throw new IllegalStateException("newerFile doesn't exist => " + newerFile);
         }

         File vbDiffScript = getFileSystemPath("support" + File.separator + "compareDocs.vbs");
         if (!vbDiffScript.exists()) {
            throw new IllegalStateException("vbDiffScript doesn't exist => " + vbDiffScript);
         }

         // quotes are neccessary because of Runtime.exec wraps the last element in quotes...crazy
         String cmd[] =
               {
                     "cmd",
                     "/s /c",
                     "\"" + vbDiffScript.getPath() + "\"",
                     "/author:CoolOseeUser\" /diffPath:\"" + diffPath + "\" /detectFormatChanges:true /ver1:\"" + baseFile.getAbsolutePath() + "\" /ver2:\"" + newerFile.getAbsolutePath()};

         Process proc = Runtime.getRuntime().exec(cmd);

         StreamCatcher errorCatcher = new StreamCatcher(proc.getErrorStream(), "ERROR");
         StreamCatcher outputCatcher = new StreamCatcher(proc.getInputStream(), "OUTPUT");

         errorCatcher.start();
         outputCatcher.start();
         proc.waitFor();
      } catch (Exception ex) {
         ex.printStackTrace();
      }

   }

}
