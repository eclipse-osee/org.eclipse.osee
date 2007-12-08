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
package org.eclipse.osee.framework.ui.plugin.util.db.data;

/**
 * @author Roberto E. Escobar
 */
public enum ConstraintTypes {
   PRIMARY_KEY, FOREIGN_KEY, UNIQUE, CHECK;

   public String toString() {
      String toReturn = super.toString();
      toReturn = toReturn.replaceAll("_", " ");
      return toReturn;
   }

   public static ConstraintTypes textToType(String text) {
      ConstraintTypes[] typesArray = ConstraintTypes.values();
      for (ConstraintTypes type : typesArray) {
         if (type.toString().equals(text.toUpperCase())) {
            return type;
         }
      }
      return null;
   }
}
