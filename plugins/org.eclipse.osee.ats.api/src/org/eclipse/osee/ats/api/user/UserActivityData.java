/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.user;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * @author Donald G. Dunne
 */
public class UserActivityData {

   public static UserActivityData SENTINEL = new UserActivityData();

   UserToken userTok = UserToken.SENTINEL;
   ArtifactToken userArt = ArtifactToken.SENTINEL;
   boolean firstNotifySent = false;
   boolean secondNotifySent = false;
   boolean active = false;
   boolean deactivating = false;
   boolean ignoreNameUpdate = false;
   boolean ignoreDuplicateNames = false;
   List<UserUsageStatus> usageStatus = new ArrayList<>();
   UserActivityAction actionNeeded = UserActivityAction.Not_Set_Fix_This;
   AtsUser wssoUser = null;
   List<String> tags = new ArrayList<>();

   public boolean isFirstNotifySent() {
      return firstNotifySent;
   }

   public void setFirstNotifySent(boolean firstNotifySent) {
      this.firstNotifySent = firstNotifySent;
   }

   public boolean isSecondNotifySent() {
      return secondNotifySent;
   }

   public void setSecondNotifySent(boolean secondNotifySent) {
      this.secondNotifySent = secondNotifySent;
   }

   public boolean isActive() {
      return active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public UserToken getUserTok() {
      return userTok;
   }

   public void setUserTok(UserToken userTok) {
      this.userTok = userTok;
   }

   public ArtifactToken getUserArt() {
      return userArt;
   }

   public void setUserArt(ArtifactToken userArt) {
      this.userArt = userArt;
   }

   public UserUsageStatus getEarliestActivityStatus() {
      UserUsageStatus earliestActStat = UserUsageStatus.SENTINEL;
      List<UserUsageStatus> validDayStatuses =
         usageStatus.stream().filter(uus -> uus.getDaysSince() >= 0).collect(Collectors.toList());
      for (UserUsageStatus stat : validDayStatuses) {
         if (earliestActStat.isInValid() || stat.getDaysSince() < earliestActStat.getDaysSince()) {
            earliestActStat = stat;
         }
      }
      return earliestActStat;
   }

   public int getEarliestActivityStatusDaysSince() {
      return getEarliestActivityStatus().getDaysSince();
   }

   public boolean isValid() {
      return userTok.isValid();
   }

   public boolean isInValid() {
      return !isValid();
   }

   public AtsUser getWssoUser() {
      return wssoUser;
   }

   public void setWssoUser(AtsUser wssoUser) {
      this.wssoUser = wssoUser;
   }

   @Override
   public String toString() {
      return "UAD [name=" + userTok.getName() + //
         ", id=" + userTok.getIdString() + //
         ", active=" + active + //
         ", daysSinceUse=" + getEarliestActivityStatus() + //
         ", activityStatus=" + usageStatus + //
         ", action=" + actionNeeded.getName() + //
         ", firstNotifySent=" + firstNotifySent + //
         ", secondNotifySent=" + secondNotifySent + //
         "]";
   }

   public UserActivityAction getActionNeeded() {
      return actionNeeded;
   }

   public void setActionNeeded(UserActivityAction actionNeeded) {
      this.actionNeeded = actionNeeded;
   }

   public boolean isIgnoreSystemUser() {
      return actionNeeded.equals(UserActivityAction.Ignore_System_User);
   }

   public boolean isIgnoreByStaticId() {
      return actionNeeded.equals(UserActivityAction.Ignore_By_Static_Id);
   }

   public boolean isIgnoreCauseRecentUse() {
      return actionNeeded.equals(UserActivityAction.Ignore_Active_Cause_Recent_Use_Or_Reactivated);
   }

   public boolean isIgnoreAlreadyActiveInOsee() {
      return actionNeeded.equals(UserActivityAction.Ignore_Already_InActive_In_OSEE);
   }

   public void addUsageType(UserUsageType usageType, int daysSince) {
      this.usageStatus.add(new UserUsageStatus(usageType, daysSince));
   }

   public boolean isIgnoreNameUpdate() {
      return ignoreNameUpdate;
   }

   public void setIgnoreNameUpdate(boolean ignoreNameUpdate) {
      this.ignoreNameUpdate = ignoreNameUpdate;
   }

   public boolean isIgnoreDuplicateNames() {
      return ignoreDuplicateNames;
   }

   public void setIgnoreDuplicateNames(boolean ignoreDuplicateNames) {
      this.ignoreDuplicateNames = ignoreDuplicateNames;
   }

   public List<String> getTags() {
      return tags;
   }

   public void setTags(List<String> tags) {
      this.tags = tags;
   }

   public boolean isNotSet() {
      return actionNeeded.equals(UserActivityAction.Not_Set_Fix_This);
   }

   public List<UserUsageStatus> getUsageStatus() {
      return usageStatus;
   }

   public void setUsageStatus(List<UserUsageStatus> usageStatus) {
      this.usageStatus = usageStatus;
   }

   public boolean isDeactivating() {
      return deactivating;
   }

   public void setDeactivating(boolean deactivating) {
      this.deactivating = deactivating;
   }

}
