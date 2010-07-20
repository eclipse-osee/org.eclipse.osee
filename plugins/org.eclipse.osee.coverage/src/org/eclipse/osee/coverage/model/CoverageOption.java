/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.model;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class CoverageOption {

   public String name;
   public String description;
   public boolean enabled;
   public static String TAG = "CvgOpt";

   public CoverageOption(String name, String description, boolean enabled) {
      this.name = name;
      this.description = description;
      this.enabled = enabled;
   }

   public CoverageOption(String name, String description) {
      this(name, description, true);
   }

   public CoverageOption(String name) {
      this(name, "", true);
   }

   public String getName() {
      return name;
   }

   public String getNameDesc() {
      if (Strings.isValid(description)) {
         return String.format("%s - %s", name, description);
      }
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public String toString() {
      return String.format("[%s-%s]", name, enabled);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      CoverageOption other = (CoverageOption) obj;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      return true;
   }

}
