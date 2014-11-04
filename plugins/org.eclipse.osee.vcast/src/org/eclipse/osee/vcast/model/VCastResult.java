/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vcast.model;

/**
 * @author Shawn F. Cook
 */
public class VCastResult {

   private final int id;
   private final String name;
   private final int projectId;
   private final String path;
   private final String fullname;
   private final boolean enabled;
   private final boolean imported;

   public VCastResult(int id, String name, int projectId, String path, String fullname, boolean enabled, boolean imported) {
      super();
      this.id = id;
      this.name = name;
      this.projectId = projectId;
      this.path = path;
      this.fullname = fullname;
      this.enabled = enabled;
      this.imported = imported;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public int getProjectId() {
      return projectId;
   }

   public String getPath() {
      return path;
   }

   public String getFullname() {
      return fullname;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public boolean isImported() {
      return imported;
   }

}
