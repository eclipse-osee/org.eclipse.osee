/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.jdk.core.type;

/**
 * @author Ryan D. Brooks
 */
public class NamedBase implements Named {
   private String name;

   public NamedBase(String name) {
      this.name = name;
   }

   public NamedBase() {
      this.name = Named.SENTINEL;
   }

   @Override
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name == null ? super.toString() : name;
   }

   public static <T extends Named> T fromName(String name, T[] tokens) {
      for (T token : tokens) {
         if (token.getName().equals(name)) {
            return token;
         }
      }
      throw new OseeArgumentException("Value with name [%s] does not exist", name);
   }
}