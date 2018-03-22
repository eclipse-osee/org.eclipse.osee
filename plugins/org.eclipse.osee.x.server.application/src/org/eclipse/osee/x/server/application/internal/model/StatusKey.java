/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.x.server.application.internal.model;

/**
 * @author Donald G. Dunne
 */
public enum StatusKey {
   ServerUri("URI", false),
   HeapMemoryAllocated("Heap Mem Alloc", false),
   HeapMemoryUsed("Heap Mem-Used", false),
   HeapMemoryMax("Heap Mem-Max", false),
   NonHeapMemoryAllocated("Non-Heap Mem Alloc", false),
   NonHeapMemoryUsed("Non-Heap Mem-Used", false),
   NonHeapMemoryMax("Non-Heap Mem-Max", false),
   ActiveThreads("Thread Activity", true),
   ServerId("ID", true),
   StartTime("Start", true),
   CodeLocation("Code Loc", true),
   BinaryDataPath("Binary", true),
   AuthenticationScheme("Auth-Scheme", true),
   AuthenticationSchemeSupported("Auth-Supported", true),
   SupportedVersions("Versions", true),
   SystemLoad("SystemLoad", false),
   Unknown("Unknown", true),
   GarbageCollector("GC", true),
   UpTime("UpTime", false);

   private final String shortName;
   private final boolean details;

   private StatusKey(String shortName, boolean details) {
      this.shortName = shortName;
      this.details = details;
   }

   public boolean isDetails() {
      return details;
   }

   public String getShortName() {
      return shortName;
   }
}