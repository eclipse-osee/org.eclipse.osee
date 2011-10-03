package org.eclipse.osee.display.api.data;

public class SearchResultMatch {
   private final String attributeType;
   private final String matchHint;
   private final int manyMatches;

   public SearchResultMatch(String attributeType, String matchHint, int manyMatches) {
      this.attributeType = attributeType;
      this.matchHint = matchHint;
      this.manyMatches = manyMatches;
   }

   public String getAttributeType() {
      return attributeType;
   }

   public String getMatchHint() {
      return matchHint;
   }

   public int getManyMatches() {
      return manyMatches;
   }

}