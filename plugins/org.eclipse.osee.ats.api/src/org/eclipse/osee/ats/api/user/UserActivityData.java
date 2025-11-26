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

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * @author Donald G. Dunne
 */
public class UserActivityData {

   public static UserActivityData SENTINEL = new UserActivityData();

   UserToken userTok = UserToken.SENTINEL;
   ArtifactToken userArt = ArtifactToken.SENTINEL;
   int daysSinceIdeUse = -1;
   int daysSinceTxsAuthored = -1;
   boolean firstNotifySent = false;
   boolean secondNotifySent = false;
   boolean active = false;
   UserActivityStatus status = UserActivityStatus.Not_Set;
   UserActivityAction actionNeeded = UserActivityAction.Not_Set_Fix_This;
   AtsUser wssoUser = null;

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

   public int getDaysSinceLastUse() {
      if (daysSinceIdeUse == -1 && daysSinceTxsAuthored == -1) {
         status = UserActivityStatus.No_Use_Detected;
         return -1;
      }
      if ((daysSinceIdeUse > -1 && daysSinceTxsAuthored > -1) && daysSinceIdeUse < daysSinceTxsAuthored) {
         status = UserActivityStatus.Both_Days_Found_Ide_Is_Less;
         return daysSinceIdeUse;
      }
      if (daysSinceTxsAuthored > -1) {
         status = UserActivityStatus.Only_Days_Since_Txs_Found;
         return daysSinceTxsAuthored;
      }
      if (daysSinceIdeUse > -1) {
         status = UserActivityStatus.Only_Days_Since_Ide_Found;
         return daysSinceIdeUse;
      }
      status = UserActivityStatus.Both_Days_Found_Txs_Is_Less;
      return daysSinceTxsAuthored;
   }

   public UserActivityStatus getStatus() {
      return status;
   }

   public void setStatus(UserActivityStatus status) {
      this.status = status;
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

   public int getDaysSinceIdeUse() {
      return daysSinceIdeUse;
   }

   public void setDaysSinceIdeUse(int daysSinceIdeUse) {
      this.daysSinceIdeUse = daysSinceIdeUse;
   }

   @Override
   public String toString() {
      return "UAD [name=" + userTok.getName() + //
         ", id=" + userTok.getIdString() + //
         ", active=" + active + //
         ", daysSinceUse=" + getDaysSinceLastUse() + //
         ", status=" + status + //
         ", action=" + actionNeeded.getName() + //
         ", daysSinceIde=" + daysSinceIdeUse + //
         ", daysSinceTxs=" + daysSinceTxsAuthored + //
         ", firstNotifySent=" + firstNotifySent + //
         ", secondNotifySent=" + secondNotifySent + //
         "]";
   }

   public int getDaysSinceTxsAuthored() {
      return daysSinceTxsAuthored;
   }

   public void setDaysSinceTxsAuthored(int daysSinceTxsAuthored) {
      this.daysSinceTxsAuthored = daysSinceTxsAuthored;
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

   public boolean isIgnoreNameChange() {
      return actionNeeded.equals(UserActivityAction.Ignore_Name_Update);
   }

   public boolean isIgnoreDuplicateName() {
      return actionNeeded.equals(UserActivityAction.Ignore_Duplicate_Names);
   }

   public boolean isIgnoreByStaticId() {
      return actionNeeded.equals(UserActivityAction.Ignore_By_Static_Id);
   }

   public boolean isIgnoreCauseRecentUse() {
      return actionNeeded.equals(UserActivityAction.Ignore_Active_Cause_Recent_Use);
   }

   public boolean isIgnoreAlreadyActiveInOsee() {
      return actionNeeded.equals(UserActivityAction.Ignore_Already_InActive_In_OSEE);
   }

}
