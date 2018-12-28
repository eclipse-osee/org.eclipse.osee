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
package org.eclipse.osee.framework.core.model;

import org.eclipse.osee.framework.jdk.core.type.FullyNamed;
import org.eclipse.osee.framework.jdk.core.type.HasDescription;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumEntry implements FullyNamed, HasDescription {

   private int ordinal;
   private String description, name;

   public OseeEnumEntry(String name, int ordinal, String description) {
      this.name = name;
      this.ordinal = ordinal;
      this.description = description;
   }

   public int ordinal() {
      return ordinal;
   }

   public void setOrdinal(int ordinal) {
      this.ordinal = ordinal;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String getDescription() {
      return description;
   }

   public Pair<String, Integer> asPair() {
      return new Pair<>(getName(), ordinal());
   }

   @Override
   public boolean equals(Object object) {
      if (object instanceof OseeEnumEntry) {
         OseeEnumEntry other = (OseeEnumEntry) object;
         return super.equals(other) && ordinal() == other.ordinal();
      }
      return false;
   }

   @Override
   public int hashCode() {
      final int prime = 37;
      int result = super.hashCode();
      result = prime * result + ordinal();
      return result;
   }

   @Override
   public String toString() {
      return String.format("%s:%s%s", getName(), ordinal(), toStringDescription());
   }

   private String toStringDescription() {
      String description = "";
      if (Strings.isValid(getDescription())) {
         description = " - " + getDescription();
      }
      return description;
   }

   @Override
   public String getName() {
      return name;
   }
}