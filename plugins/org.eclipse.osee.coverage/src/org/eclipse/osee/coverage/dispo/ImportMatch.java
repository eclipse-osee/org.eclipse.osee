package org.eclipse.osee.coverage.dispo;

import java.util.Collection;
import org.eclipse.osee.coverage.merge.MatchType;
import org.eclipse.osee.coverage.model.ICoverage;

public class ImportMatch {

   private final ImportMatchType matchType;
   private final ICoverage fromItem;
   private final ICoverage toItem;
   private final String description;

   public ImportMatch(ImportMatchType matchType, ICoverage packageItem, ICoverage importItem) {
      this(matchType, packageItem, importItem, "");
   }

   public ImportMatch(ImportMatchType matchType, ICoverage fromItem, ICoverage toItem, String description, Object... parms) {
      this.matchType = matchType;
      this.fromItem = fromItem;
      this.toItem = toItem;
      this.description = String.format(description, parms);
   }

   public String getDescription() {
      return description;
   }

   public ImportMatchType getMatchType() {
      return matchType;
   }

   public ICoverage getFromItem() {
      return fromItem;
   }

   public ICoverage getToItem() {
      return toItem;
   }

   @Override
   public String toString() {
      return "Match Type>> " + matchType.toString() + "(" + description + ") - From Item>>" + fromItem.toString() + ") - To Item>>(" + (toItem != null ? fromItem.toString() : "[null])");
   }

   public static boolean isAllMatchType(Collection<MatchType> matchTypes, Collection<ImportMatch> matchItems) {
      for (ImportMatch matchItem : matchItems) {
         if (!matchTypes.contains(matchItem.getMatchType())) {
            return false;
         }
      }
      return true;
   }

   public boolean isMatch() {
      if (matchType == ImportMatchType.Match) {
         return true;
      }
      return false;
   }

   public boolean isMatchType(Collection<ImportMatchType> matchTypes) {
      if (!matchTypes.contains(matchType)) {
         return false;
      }
      return true;
   }
}
