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
   ServerState("State", false),
   MemoryAllocated("Mem Alloc", false),
   MemoryUsed("Mem-Used", false),
   MemoryMax("Mem-Max", false),
   ActiveThreads("Threads", false),
   JobManager("Jobs", false),
   CurrentJob("Curr Job", false),
   CurrentTasks("Curr Tasks", true),
   ServerId("ID", true),
   StartTime("Start", true),
   CodeLocation("Start", true),
   BinaryDataPath("Binary", true),
   AuthenticationScheme("Auth-Scheme", true),
   AuthenticationSchemeSupported("Auth-Supported", true),
   SupportedVersions("Versions", true),
   Unknown("Unknown", true);

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
