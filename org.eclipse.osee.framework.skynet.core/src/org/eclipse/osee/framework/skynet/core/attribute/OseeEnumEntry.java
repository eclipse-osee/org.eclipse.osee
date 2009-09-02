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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BaseOseeType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumEntry extends BaseOseeType implements Comparable<OseeEnumEntry> {
   private final int ordinal;
   private final OseeTypeCache cache;

   public OseeEnumEntry(String guid, String name, int ordinal, OseeTypeCache cache) {
      super(guid, name);
      this.ordinal = ordinal;
      this.cache = cache;
   }

   public int ordinal() {
      return ordinal;
   }

   public Pair<String, Integer> asPair() {
      return new Pair<String, Integer>(getName(), ordinal());
   }

   public OseeEnumType getDeclaringClass() throws OseeCoreException {
      return cache.getEnumTypeData().getEnumType(this);
   }

   @Override
   public boolean equals(Object object) {
      if (object instanceof OseeEnumEntry) {
         OseeEnumEntry other = (OseeEnumEntry) object;
         return super.equals(other) && ordinal == other.ordinal;
      }
      return false;
   }

   @Override
   public int hashCode() {
      final int prime = 37;
      int result = super.hashCode();
      result = prime * result + ordinal;
      return result;
   }

   @Override
   public String toString() {
      String className;
      try {
         if (getDeclaringClass() != null) {
            className = getDeclaringClass().getName();
         } else {
            className = "empty";
         }
      } catch (OseeCoreException ex) {
         className = Lib.exceptionToString(ex);
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return String.format("[%s].[%s:%s]", className, getName(), ordinal);
   }

   @Override
   public int compareTo(OseeEnumEntry other) {
      return this.ordinal() - other.ordinal();
   }
}
