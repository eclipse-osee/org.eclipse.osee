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
package org.eclipse.osee.framework.plugin.core.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.internal.Activator;

/**
 * @author Ken J. Aguilar
 */
public class PathResourceFinder extends ResourceFinder {

   private final HashSet<JarFile> jars = new HashSet<>(128);
   private final HashSet<String> dirs = new HashSet<>(128);
   private final HashMap<String, JarFile[]> map = new HashMap<>(128);
   private final boolean trees;
   private final ClassServerPermissions perm;
   private static final int NUMBER_OF_FILE_READ_ATTEMPTS = 20;

   public PathResourceFinder(String[] dirsToAdd, boolean trees) {
      this.trees = trees;
      perm = new ClassServerPermissions();
      if (dirsToAdd != null) {
         addPaths(dirsToAdd);
      }
   }

   @Override
   public byte[] find(String path) throws IOException {

      int i = path.indexOf('/');
      if (i > 0) {
         JarFile[] jfs = map.get(path.substring(0, i));
         if (jfs != null) {
            String jpath = path.substring(i + 1);
            for (i = 0; i < jfs.length; i++) {
               JarEntry je = jfs[i].getJarEntry(jpath);
               if (je != null) {
                  return getBytes(jfs[i].getInputStream(je), je.getSize());
               }
            }
         }
      }
      synchronized (jars) {
         Iterator<JarFile> it = jars.iterator();
         while (it.hasNext()) {
            JarFile jar = it.next();
            JarEntry je = jar.getJarEntry(path);
            if (je != null) {
               return getBytes(jar.getInputStream(je), je.getSize());
            }
         }
      }
      boolean exists = false;
      File f = null;
      synchronized (dirs) {
         for (int j = 0; j < NUMBER_OF_FILE_READ_ATTEMPTS; j++) { // we'll retry in case there is a
            // compile going on
            Iterator<String> it = dirs.iterator();
            while (it.hasNext()) {
               String dirString = it.next();
               f = new File(dirString + File.separatorChar + path.replace('/', File.separatorChar));
               if (f.exists()) {
                  exists = true;
                  break;
               }
            }
            if (!exists) {
               try {
                  synchronized (this) {
                     this.wait(1000);
                  }
                  XConsoleLogger.err(String.format("trying to find :%s %d", path, j));
               } catch (InterruptedException ex) {
                  // do nothing
               }
            } else {
               break;
            }
         }
      }

      if (exists && f != null) {
         if (perm.implies(new FilePermission(f.getPath(), "read"))) {
            try {
               return getBytes(new FileInputStream(f), f.length());
            } catch (FileNotFoundException e) {
               // do nothing
            }
         }
      }
      return null;
   }

   public void addPaths(String[] paths) {

      for (int i = 0; i < paths.length; i++) {
         String path = paths[i];

         if (path.startsWith("file:\\")) {
            path = path.substring(6);
         }

         if (path.endsWith(".jar")) {
            try {
               synchronized (jars) {
                  jars.add(new JarFile(new File(path)));
               }
            } catch (Exception ex) {
               ex.printStackTrace();
               continue;
            }
         } else {
            if (dirs.add(path)) {
               perm.add(new FilePermission(path + File.separator + '-', "read"));
            }
         }

         if (trees) {
            File fdir = new File(path);
            String[] files = fdir.list();
            if (files != null) {
               try {
                  URL base = fdir.toURI().toURL();
                  for (int j = 0; j < files.length; j++) {
                     String jar = files[j];
                     if (jar.endsWith(".jar") || jar.endsWith(".zip")) {
                        ArrayList<JarFile> jfs = new ArrayList<>(10);
                        try {
                           addJar(jar, jfs, base);
                           map.put(jar.substring(0, jar.length() - 4), jfs.toArray(new JarFile[jfs.size()]));
                        } catch (IOException ex) {
                           ex.printStackTrace();
                        }
                     }
                  }
               } catch (MalformedURLException ex) {
                  ex.printStackTrace();
               }
            }
         }
      }
   }

   /** Add transitive Class-Path JARs to jfs. */
   private void addJar(String jar, ArrayList<JarFile> jfs, URL base) throws IOException {
      base = new URL(base, jar);
      jar = base.getFile().replace('/', File.separatorChar);
      for (int i = jfs.size(); --i >= 0;) {
         if (jar.equals(jfs.get(i).getName())) {
            return;
         }
      }

      JarFile jf = new JarFile(jar);
      jfs.add(jf);
      try {
         Manifest man = jf.getManifest();
         if (man == null) {
            return;
         }
         Attributes attrs = man.getMainAttributes();
         if (attrs == null) {
            return;
         }
         String val = attrs.getValue(Attributes.Name.CLASS_PATH);
         if (val == null) {
            return;
         }
         for (StringTokenizer st = new StringTokenizer(val); st.hasMoreTokens();) {
            addJar(st.nextToken(), jfs, base);
         }
      } catch (IOException ex) {
         jfs.remove(jf);
         jf.close();
         throw ex;
      }
   }

   /**
    * Finds the jarFile if it is being served.
    *
    * @param name The name of the jar file
    * @return The jar represented as a File, or null if the jar was not found.
    */
   public File getJarFile(String name) {
      File jarFileReturn = null;
      synchronized (jars) {
         Iterator<JarFile> it = jars.iterator();
         while (it.hasNext()) {
            JarFile jarFile = it.next();
            if (jarFile.getName().endsWith(File.separator + name)) {
               jarFileReturn = new File(jarFile.getName());
               break;
            }
         }
      }
      return jarFileReturn;
   }

   public void removeJarFile(String name) {
      synchronized (jars) {
         Iterator<JarFile> it = jars.iterator();
         while (it.hasNext()) {
            JarFile jarFile = it.next();
            if (jarFile.getName().endsWith(File.separator + name)) {
               try {
                  OseeLog.log(Activator.class, Level.INFO, "removing JAR file " + name);
                  jarFile.close();
               } catch (IOException ex) {
                  // do nothing
               }
               it.remove();
               return;
            }
         }
      }
   }

   @Override
   public void dispose() {
      synchronized (jars) {
         OseeLog.log(Activator.class, Level.INFO, "disposing path resource finder's cached JAR files");
         Iterator<JarFile> it = jars.iterator();
         while (it.hasNext()) {
            JarFile jarFile = it.next();
            try {
               jarFile.close();
            } catch (IOException ex) {
               ex.printStackTrace();
            }
         }
         jars.clear();
      }
   }
}
