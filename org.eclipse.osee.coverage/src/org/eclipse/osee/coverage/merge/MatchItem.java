/*
 * Created on Nov 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.merge;

import java.util.Collection;
import org.eclipse.osee.coverage.model.ICoverage;

/**
 * @author Donald G. Dunne
 */
public class MatchItem {

   private final MatchType matchType;
   private final ICoverage packageItem;
   private final ICoverage importItem;
   public static MatchItem No_Match_Item = new MatchItem(MatchType.No_Match__Name_Or_Order_Num, null, null);

   public MatchItem(MatchType matchType, ICoverage packageItem, ICoverage importItem) {
      this.matchType = matchType;
      this.packageItem = packageItem;
      this.importItem = importItem;
   }

   public MatchType getMatchType() {
      return matchType;
   }

   public ICoverage getPackageItem() {
      return packageItem;
   }

   public ICoverage getImportItem() {
      return importItem;
   }

   @Override
   public String toString() {
      return matchType.toString() + " - " + importItem.toString();
   }

   public static boolean isAllMatchType(Collection<MatchType> matchTypes, Collection<MatchItem> matchItems) {
      for (MatchItem matchItem : matchItems) {
         if (!matchTypes.contains(matchItem.getMatchType())) {
            return false;
         }
      }
      return true;
   }

   public boolean isMatch() {
      if (matchType.toString().startsWith("Match__")) {
         return true;
      }
      return false;
   }

   public boolean isMatchType(Collection<MatchType> matchTypes) {
      if (!matchTypes.contains(matchType)) {
         return false;
      }
      return true;
   }
}
