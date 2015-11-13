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
package org.eclipse.osee.ote.core;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeURLClassLoader extends URLClassLoader {

   private final String name;
   private final ExportClassLoader exportClassLoader;

   public OseeURLClassLoader(String name, URL[] urls, ClassLoader parent) {
      super(urls, parent);
      this.name = name;
      GCHelper.getGCHelper().addRefWatch(this);
      exportClassLoader = ExportClassLoader.getInstance();
      
   }

   public OseeURLClassLoader(String name, URL[] urls) {
      super(urls);
      GCHelper.getGCHelper().addRefWatch(this);
      this.name = name;
      exportClassLoader = ExportClassLoader.getInstance();
   }

   public OseeURLClassLoader(String name, URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
      super(urls, parent, factory);
      GCHelper.getGCHelper().addRefWatch(this);
      this.name = name;
      exportClassLoader = ExportClassLoader.getInstance();
   }

   @Override
   public Class<?> loadClass(String clazz) throws ClassNotFoundException{
      try {
         return exportClassLoader.loadClass(clazz);
      } catch (Exception ex2) {
         int timesTriedToLoad = 0;
         while(timesTriedToLoad < 10){
            try {
               return super.loadClass(clazz);
            } catch (ClassNotFoundException ex) {
               System.out.println("Retrying to load from OseeURLClassLoader for class = "+ clazz);
               timesTriedToLoad++; //Try to load again
               try {
                  Thread.sleep(1);
               } catch (InterruptedException ex1) {
                  OseeLog.log(OseeURLClassLoader.class, Level.SEVERE, ex1.toString(), ex1);
               }
            }
         }
         throw new ClassNotFoundException("Class = " + clazz);
      }
   }
   
   @Override
   public String toString() {
      return this.getClass().getName() + " [ " + name + " ] ";
   }
}
