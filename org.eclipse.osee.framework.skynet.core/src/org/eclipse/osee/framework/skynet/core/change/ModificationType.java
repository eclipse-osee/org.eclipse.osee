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

package org.eclipse.osee.framework.skynet.core.change;

import java.io.Serializable;

/**
 * @author Ryan D. Brooks
 */
public enum ModificationType implements Serializable {
   NEW("New", 1), CHANGE("Modified", 2), DELETED("Deleted", 3), MERGED("Merged", 4), ARTIFACT_DELETED("Artifact Deleted", 5);

   private int value;
   private String displayName;

   ModificationType(String displayName, int value) {
      this.displayName = displayName;
      this.value = value;
   }

   /**
    * @return Returns the value.
    */
   public int getValue() {
      return value;
   }

   public String toString() {
      return String.valueOf(value);
   }

   public String getDisplayName() {
      return displayName;
   }

   /**
    * @param value The value of the ModificationType to get.
    * @return The ModificationType that has the value passed.
    */
   public static ModificationType getMod(int value) {
      for (ModificationType modtype : values())
         if (modtype.getValue() == value) return modtype;
      return null;
   }
}