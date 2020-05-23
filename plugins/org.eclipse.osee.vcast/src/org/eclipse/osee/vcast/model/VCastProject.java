/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.vcast.model;

/**
 * @author Shawn F. Cook
 */
public class VCastProject {

   private final int id;
   private final String name;
   private final String path;

   public VCastProject(int id, String name, String path) {
      this.id = id;
      this.name = name;
      this.path = path;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getPath() {
      return path;
   }

}
