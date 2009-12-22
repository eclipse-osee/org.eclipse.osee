/*
 * Created on Dec 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

/**
 * @author Donald G. Dunne
 */
public class CoverageOption {

   public String name;
   public String description;
   public boolean enabled;
   public static String TAG = "CvgOpt";

   public CoverageOption(String name, String description, boolean enabled) {
      this.name = name;
      this.description = description;
      this.enabled = enabled;
   }

   public CoverageOption(String name, String description) {
      this(name, description, true);
   }

   public CoverageOption(String name) {
      this(name, "", true);
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public String toString() {
      return String.format("[%s-%s]", name, enabled);
   }
}
