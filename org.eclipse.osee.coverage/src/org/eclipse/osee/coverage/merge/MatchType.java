package org.eclipse.osee.coverage.merge;

public enum MatchType {
   // Item matches in name and method number
   Match__Name_And_Method,
   // Item matches in name only
   Match__Name_Only,
   // Items are CoveragePackageBase types
   Match__Coverage_Base,

   // Item has no match; __<reason>
   No_Match__Namespace,
   No_Match__Name,
   No_Match__Name_Or_Method_Num;

   public static boolean isNoMatch(MatchType matchType) {
      return matchType == No_Match__Name || matchType == No_Match__Name_Or_Method_Num || matchType == No_Match__Namespace;
   }

};
