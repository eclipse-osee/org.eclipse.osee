package org.eclipse.osee.framework.core.applicability;

import applicability.ApplicabilityParseSubstituteAndSanitize;

public class TestMainJadenDelete {
   public static void main(String[] args) {
      ApplicabilityParseSubstituteAndSanitize parser = new ApplicabilityParseSubstituteAndSanitize();

      // Sample input and configuration
      String input = "A name. ``Feature[ARB=Excluded]`` Some text. ``End Feature``";
      String filename = "";
      String fileextension = "md";
      String configJson =
         "{\"name\":\"config_name\",\"group\":\"group_name\",\"features\":[\"ARB=Excluded\"],\"substitutions\":[]}";

      // Call the native method
      String output = parser.parseSubstituteAndSanitizeApplicability(input, filename, fileextension, configJson);

      // Print the result
      System.out.println("Processed Output: " + output);
   }
}
