/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.messaging.id;

import java.io.Serializable;

/**
 * @author Andrew M. Finkbeiner
 */
public class StringName implements Name, Serializable {
   private static final long serialVersionUID = -7215226960243262972L;
   private final String name;

   public StringName(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name;
   }

   @Override
   public boolean equals(Object arg0) {
      if (arg0 instanceof StringName) {
         return name.equals(((StringName) arg0).name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return name.hashCode();
   }

}
