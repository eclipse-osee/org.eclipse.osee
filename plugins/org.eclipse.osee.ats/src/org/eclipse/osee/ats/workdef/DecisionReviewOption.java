/*
 * Created on Jan 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import java.util.ArrayList;
import java.util.List;

public class DecisionReviewOption {

   public String name;
   public boolean followupRequired;
   public List<String> userIds;
   public List<String> userNames;

   public DecisionReviewOption(String name) {
      this(name, false, new ArrayList<String>());
   }

   public DecisionReviewOption(String name, boolean isFollowupRequired, List<String> userIds) {
      this.name = name;
      this.followupRequired = isFollowupRequired;
      if (userIds == null) {
         this.userIds = new ArrayList<String>();
      } else {
         this.userIds = userIds;
      }
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<String> getUserIds() {
      return userIds;
   }

   public void setUserIds(List<String> userIds) {
      this.userIds = userIds;
   }

   public boolean isFollowupRequired() {
      return followupRequired;
   }

   public void setFollowupRequired(boolean followupRequired) {
      this.followupRequired = followupRequired;
   }

   @Override
   public String toString() {
      return name + (followupRequired ? " - Followup Required" : " - No Followup Required");
   }

   public List<String> getUserNames() {
      return userNames;
   }

   public void setUserNames(List<String> userNames) {
      this.userNames = userNames;
   }

}
