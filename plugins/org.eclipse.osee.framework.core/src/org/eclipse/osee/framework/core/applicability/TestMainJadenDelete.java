package org.eclipse.osee.framework.core.applicability;

import applicability.ApplicabilityParseSubstituteAndSanitize;

public class TestMainJadenDelete {
   public static void main(String[] args) {
      ApplicabilityParseSubstituteAndSanitize parser = new ApplicabilityParseSubstituteAndSanitize();

      // Sample input and configuration
      String input = "A name. ``Feature[ARB=Included]`` Some text. ``End Feature``";
      String startSyntax = "``";
      String endSyntax = "``";
      String configJson =
         "{\"name\":\"config_name\",\"group\":\"group_name\",\"features\":[\"ARB=Excluded\"],\"substitutions\":[]}";

      // Call the native method
      String output = parser.parseSubstituteAndSanitizeApplicability(input, startSyntax, endSyntax, configJson);

      // Print the result
      System.out.println("Processed Output: " + output);
   }
}
