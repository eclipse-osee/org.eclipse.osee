/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.mim.types;

/**
 * @author Luciano T. Vaglienti
 */
public class ResolvedStructurePath {
   public static final ResolvedStructurePath SENTINEL = new ResolvedStructurePath();
   private String Name;
   /**
    * @return the name
    */
   public String getName() {
      return Name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      Name = name;
   }

   /**
    * @return the path
    */
   public String getPath() {
      return path;
   }

   /**
    * @param path the path to set
    */
   public void setPath(String path) {
      this.path = path;
   }
   private String path;
   public ResolvedStructurePath(String name, String path) {
      this.setName(name);
      this.setPath(path);
   }

   public ResolvedStructurePath() {
      super();
   }

}
