/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.types.impl;

import org.eclipse.osee.framework.jdk.core.type.FullyNamedIdentity;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.EnumEntry;

/**
 * @author Roberto E. Escobar
 */
public final class EnumEntryImpl extends FullyNamedIdentity<String> implements EnumEntry {

   private final int ordinal;

   public EnumEntryImpl(String name, int ordinal, String description) {
      super(name, name, description);
      this.ordinal = ordinal;
   }

   @Override
   public int ordinal() {
      return ordinal;
   }

   @Override
   public boolean equals(Object object) {
      if (object instanceof EnumEntry) {
         EnumEntry other = (EnumEntry) object;
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
      StringBuilder builder = new StringBuilder();
      builder.append(getName());
      builder.append(":");
      builder.append(ordinal());
      String description = getDescription();
      if (Strings.isValid(description)) {
         builder.append(" - ");
         builder.append(description);
      }
      return builder.toString();
   }

}
