/*
 * Created on Apr 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.change;

import java.io.Serializable;

/**
 * @author Ryan D. Brooks
 */
public enum ModificationType implements Serializable {
   NEW("New", 1), CHANGE("Modified", 2), DELETE("Deleted", 3);

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