/*
 * Created on Sep 27, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs;

public enum QueryOption {

   IncludeCache,
   ExcludeCache("Will reload the artifacts from the database regardless of whether in cache."),
   //
   CaseInsitive,
   CaseSensitive,
   //
   IncludeDeleted,
   ExcludeDeleted,
   //
   IncludeInheritedArtifactTypes,
   ExcludeInheritedArtifactTypes,
   //
   AllWordsAnyOrder("<now and time> matches <now time and>, <and time now>, <and_time now>"),
   AllWordsExactOrder("<now and time> matches <now and time>, <now_and time> NOT <and now time>"),
   AllWordsExactOrderMatchTokens("<now_and_time> matches <now_and_time> NOT <now and_time>"),
   FullAttributeValueFullMatch("attr.getStringValue().equals(searchStr)");

   private final String description;

   private QueryOption(String description) {
      this.description = description;
   }

   private QueryOption() {
      this(null);
   }

}
