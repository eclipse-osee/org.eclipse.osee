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
