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

import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumEntry extends AbstractOseeType<String> {
   private final static String ENUM_ENTRY_ORDINAL_FIELD = "osee.enum.entry.ordinal.field";
   private final static String ENUM_ENTRY_DESCRIPTION_FIELD = "osee.enum.entry.description.field";

   public OseeEnumEntry(String guid, String name, int ordinal, String description) {
      super(guid, name);
      addField(ENUM_ENTRY_ORDINAL_FIELD, new OseeField<Integer>());
      setOrdinal(ordinal);
      addField(ENUM_ENTRY_DESCRIPTION_FIELD, new OseeField<String>());
      setDescription(description);
   }

   public int ordinal() {
      return getFieldValueLogException(Integer.MIN_VALUE, ENUM_ENTRY_ORDINAL_FIELD);
   }

   public void setOrdinal(int ordinal) {
      setFieldLogException(ENUM_ENTRY_ORDINAL_FIELD, ordinal);
   }

   public void setDescription(String description) {
      setFieldLogException(ENUM_ENTRY_DESCRIPTION_FIELD, Strings.isValid(description) ? description : "");
   }

   @Override
   public String getDescription() {
      return getFieldValueLogException("", ENUM_ENTRY_DESCRIPTION_FIELD);
   }

   public Pair<String, Integer> asPair() {
      return new Pair<String, Integer>(getName(), ordinal());
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
}