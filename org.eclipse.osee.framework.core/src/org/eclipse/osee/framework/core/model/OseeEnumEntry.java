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

import org.eclipse.osee.framework.core.internal.fields.OseeField;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumEntry extends AbstractOseeType implements Comparable<OseeEnumEntry> {
   private final static String ENUM_ENTRY_ORDINAL_FIELD = "osee.enum.entry.ordinal.field";

   public OseeEnumEntry(String guid, String name, int ordinal) {
      super(guid, name);
      addField(ENUM_ENTRY_ORDINAL_FIELD, new OseeField<Integer>());
      setOrdinal(ordinal);
   }

   public int ordinal() {
      return getFieldValueLogException(Integer.MIN_VALUE, ENUM_ENTRY_ORDINAL_FIELD);
   }

   public void setOrdinal(int ordinal) {
      setFieldLogException(ENUM_ENTRY_ORDINAL_FIELD, ordinal);
   }

   public Pair<String, Integer> asPair() {
      return new Pair<String, Integer>(getName(), ordinal());
   }

   //   public OseeEnumType getDeclaringClass() throws OseeCoreException {
   //      return getCache().getEnumType(this);
   //   }

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
      return String.format("%s:%s", getName(), ordinal());
   }

   @Override
   public int compareTo(OseeEnumEntry other) {
      return this.ordinal() - other.ordinal();
   }
}
