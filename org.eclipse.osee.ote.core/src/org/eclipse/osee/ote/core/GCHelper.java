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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class GCHelper {

   private final ConcurrentHashMap<String, WeakReference<Object>> map;
   private static GCHelper singleton;

   /**
    * 
    */
   public GCHelper() {
      super();
      map = new ConcurrentHashMap<String, WeakReference<Object>>(1024);
   }

   public static GCHelper getGCHelper() {
      if (singleton == null) {
         singleton = new GCHelper();
      }
      return singleton;
   }

   public void addRefWatch(Object obj) {
      String key = obj.toString() + " [ " + obj.hashCode() + " ] ";
      map.put(key, new WeakReference<Object>(obj));
   }

   public void printLiveReferences() {

      StringBuilder builder = new StringBuilder(8000);
      writeGCResults(builder);
      OseeLog.log(GCHelper.class, Level.INFO, builder.toString());
   }

   public void writeGCResults(StringBuilder builder) {
      TreeSet<String> live = new TreeSet<String>();
      System.gc();
      for (Map.Entry<String, WeakReference<Object>> entry : map.entrySet()) {
         if (entry.getValue().get() != null) {
            Object objectToFind = entry.getValue().get();
            live.add(entry.getKey());
         }
      }

      builder.append("Live References:\n");
      for (String key : live) {
         builder.append("   ");
         builder.append(key);
         builder.append("\n");
      }
   }

   public List getInstancesOfType(Class type) {
      List<Object> listOfObjects = new ArrayList<Object>();
      System.gc();
      for (Map.Entry<String, WeakReference<Object>> entry : map.entrySet()) {
         if (entry.getValue().get() != null) {
            if (type.isInstance(entry.getValue().get())){
               listOfObjects.add( entry.getValue().get());
            }
         }
      }
      return listOfObjects;
   }
   
   public void collectGarbage() {
      long memBefore = Runtime.getRuntime().freeMemory();
      long timeBefore = System.currentTimeMillis();
      Runtime.getRuntime().runFinalization();
      Runtime.getRuntime().gc();
      System.out.println("Garbage Collection --- Memory Released: " + (Runtime.getRuntime().freeMemory() - memBefore) + " -- Time Elapsed: " + (System.currentTimeMillis() - timeBefore));
   }

   public void collectGarbageAndPrintStats() {
      new Thread(new Runnable() {

         public void run() {
            try {
               Thread.sleep(5000);
               collectGarbage();
               printLiveReferences();
            } catch (InterruptedException ex) {
            }

         }

      }).start();
   }

}
