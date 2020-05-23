/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.internal.types.impl;

import org.eclipse.osee.framework.jdk.core.type.NamedIdDescription;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.EnumEntry;

/**
 * @author Roberto E. Escobar
 */
public final class EnumEntryImpl extends NamedIdDescription implements EnumEntry {

   public EnumEntryImpl(String name, int ordinal, String description) {
      super(Long.valueOf(ordinal), name, description);
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(getName());
      builder.append(":");
      builder.append(getIdString());
      String description = getDescription();
      if (Strings.isValid(description)) {
         builder.append(" - ");
         builder.append(description);
      }
      return builder.toString();
   }
}