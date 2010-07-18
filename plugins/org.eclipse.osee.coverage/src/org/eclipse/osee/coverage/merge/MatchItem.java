/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
      return "Match Type>> " + matchType.toString() + " - Import Item>>" + importItem.toString() + ") - Package Item>>(" + (packageItem != null ? packageItem.toString() : "[null])");
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
