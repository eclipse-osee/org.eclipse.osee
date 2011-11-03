/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.mvp;

/**
 * @author Roberto E. Escobar
 */
public class CustomMessageType implements MessageType {

   public String name;
   public int level;

   public CustomMessageType(String name, int level) {
      this.name = name;
      this.level = level;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public int getLevel() {
      return level;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + level;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      CustomMessageType other = (CustomMessageType) obj;
      if (level != other.level) {
         return false;
      }
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "MessageType [name=" + name + ", level=" + level + "]";
   }

}
