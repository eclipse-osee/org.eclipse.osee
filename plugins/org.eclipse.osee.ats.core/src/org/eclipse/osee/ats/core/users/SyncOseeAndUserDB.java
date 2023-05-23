/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.core.users;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsConfigKey;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Donald G. Dunne
 */
public abstract class SyncOseeAndUserDB {

   public static String OSEE_AUTORUN_USER_RELATIONS_CHECKED = "osee.autorun.userRelationsChecked";
   private final String LAST_DATE_ACCOUNT_LOGGED_ACTIVITY =
      "SELECT start_timestamp FROM osee_activity WHERE account_id = ? ORDER BY START_TIMESTAMP desc";
   public static String NO_EMAIL_STATIC_ID = "noEmail";
   public static String FIRST_NOTIFICATION_STATIC_ID = "FirstInactiveNotification";
   public static String SECOND_NOTIFICATION_STATIC_ID = "SecondInactiveNotification";
   protected XResultData results = null;
   protected final AtsApi atsApi;
   protected final boolean persist;
   protected final boolean debug;
   protected final JdbcClient jdbcClient;
   protected final String IGNORE_DUP_NAMES = "IgnoreDupNames";
   protected final List<ArtifactReadable> ignoreDupNames = new ArrayList<>();
   protected IAtsChangeSet changes;
   protected List<String> ignoreStaticIds;

   public SyncOseeAndUserDB(boolean persist, boolean debug, AtsApi atsApi) {
      this.jdbcClient = atsApi.getJdbcService().getClient();
      this.atsApi = atsApi;
      this.persist = persist;
      this.debug = debug;
   }

   protected abstract List<String> getIgnoreStaticIds();

   protected String getTitle() {
      return "Sync OSEE and User DB";
   }

   public XResultData run() {
      try {
         ignoreStaticIds = getIgnoreStaticIds();
         results = new XResultData(false);
         results.log(getTitle() + "\n");
         List<UserToken> users = new ArrayList<>();

         ElapsedTime time = new ElapsedTime("Loading Users", debug);
         for (ArtifactToken art : atsApi.getQueryService().getArtifacts(CoreArtifactTypes.User)) {
            UserToken userToken = createUserToken(art);
            users.add(userToken);
         }
         time.end();

         time = new ElapsedTime("Filtering Users", debug);
         List<UserToken> regUsers = users //
            .stream() //
            .filter(u -> !SystemUser.values.contains(u)) //
            .filter(u -> Collections.setIntersection(getUserStaticIds(u), ignoreStaticIds).isEmpty()) //
            .collect(Collectors.toList());
         time.end();

         /////////////////////////////////////////////
         ///////////// REPORT ONLY
         /////////////////////////////////////////////

         // Test if there duplicate users with same name - REPORT ONLY
         time = new ElapsedTime("testForDuplicates", debug);
         testForDuplicates(regUsers);
         time.end();

         // If this test fails, no other checks are done cause these have to finish first
         if (results.isErrors()) {
            results.logf("\nError: Error: Error: Sync aborted cause duplicates found; <b>RESOLVE THESE FIRST!!</b>\n");
         } else {

            /////////////////////////////////////////////
            ///////////// FIX IF PERSIST
            /////////////////////////////////////////////

            if (persist) {
               changes = atsApi.createChangeSet(getTitle());
            }

            // Test that user is inactive in user db - FIX AVAILABLE
            time = new ElapsedTime("testCauseWentInactive", debug);
            testInactiveCauseWentInactive(regUsers);
            time.end();

            // Test that user has accessed in minimal time (180 days) - FIX AND EMAILS AVAILABLE
            time = new ElapsedTime("testCauseHaveNotAccessed", debug);
            testInactiveCauseHaveNotAccessed(regUsers);
            time.end();

            // Test that user is in appropriate UserGroups - FIX AVAILABLE
            time = new ElapsedTime("testUserGroups", debug);
            testUserGroups(regUsers);
            time.end();

            // Test that user has attributes updated from the User DB - FIX AVAILABLE
            time = new ElapsedTime("testUserAttributes", debug);
            testUserAttributes(regUsers);
            time.end();

         }

         if (changes != null && persist) {
            TransactionToken tx = changes.executeIfNeeded();
            if (tx != null) {
               results.logf("\nChanges persisted with Transaction %s\n", tx.getIdString());
            } else {
               results.logf("\nNo Changes to Persist\n");
            }
         } else {
            results.logf("\nReport Only - Changes NOT persisted\n");
         }

         results.log("\nProcessed " + users.size() + " users!\n");
      } catch (Exception ex) {
         results.errorf("\nException: %s", Lib.exceptionToString(ex));
      }
      return results;
   }

   protected abstract void testUserGroups(List<UserToken> regUsers);

   protected void addMember(IUserGroup everyoneGroup, UserToken user) {
      changes.relate(everyoneGroup.getArtifact(), CoreRelationTypes.Users_User, user);
   }

   /**
    * @return AtsUser as defined in company system such as wsso
    */
   protected abstract AtsUser getUserByUserId(String userId);

   protected abstract Date getTxDate(ArtifactToken art);

   protected List<String> getUserStaticIds(UserToken user) {
      return atsApi.getAttributeResolver().getAttributesToStringList(user.getArtifact(), CoreAttributeTypes.StaticId);
   }

   protected void testUserAttributes(List<UserToken> regUsers) throws Exception {

      for (UserToken user : regUsers) {
         if (!user.isActive()) {
            continue;
         }
         AtsUser atsUser = getUserByUserId(user.getUserId());
         if (atsUser != null) {
            String wssoBemsId = atsUser.getUserId();
            String wssoLoginId = atsUser.getLoginIds().get(0);
            String wssoUserName = atsUser.getName();
            String wssoMail = atsUser.getEmail();
            String wssoPhone = atsUser.getPhone();

            // No record returned, so nothing to update
            if (Strings.isInvalid(wssoUserName)) {
               continue;
            }

            // Verify/update loginId attribute
            if (debug && !user.getLoginIds().contains(wssoLoginId) && Strings.isValid(wssoLoginId)) {
               results.warningf("[%s] login ids [%s] appear invalid; should be [%s] - NO FIX\n", user.toStringWithId(),
                  user.getLoginIds(), wssoLoginId);
               /**
                * At this time, login ids will not be sync'd as they should have managers approval and manually fixed
                */
            }

            // If loginId == null, not a valid record/email, don't update or error
            if (wssoLoginId != null) {
               if (!EmailUtil.isEmailValid(wssoMail)) {
                  if (!atsApi.getAttributeResolver().getAttributesToStringList(user.getArtifact(),
                     CoreAttributeTypes.StaticId).contains(NO_EMAIL_STATIC_ID)) {
                     if (debug) {
                        results.errorf("[%s] WSSO User Email [%s] is invalid\n", user.toStringWithId(), wssoMail);
                     }
                  }
               } else if (!user.getEmail().equals(wssoMail)) {
                  /**
                   * Ignore situations where the user's email in already set and different
                   */
                  results.warningf("[%s] wsso.email [%s] != user.email [%s]\n", user.toStringWithId(), wssoMail,
                     user.getEmail());
                  if (persist) {
                     changes.setSoleAttributeValue(user.getArtifact(), CoreAttributeTypes.Email, wssoMail);
                     results.logf("Fixed Email to %s\n", wssoMail);
                  }
               }
            }

            String phone =
               atsApi.getAttributeResolver().getSoleAttributeValue(user.getArtifact(), CoreAttributeTypes.Phone, "");
            if (Strings.isValid(wssoPhone) && !wssoPhone.equals(phone)) {
               results.warningf("[%s] wsso.phone [%s] != user.phone [%s]\n", user.toStringWithId(), wssoPhone, phone);
               if (persist) {
                  changes.setSoleAttributeValue(user.getArtifact(), CoreAttributeTypes.Phone, wssoPhone);
                  results.log("Fixed");
               }
            }
            if (!wssoUserName.equals(user.getName())) {
               results.warningf("[%s] wsso.name [%s] != user.name [%s]\n", user.toStringWithId(), wssoUserName,
                  user.getName());
               if (persist) {
                  changes.setName(user.getArtifact(), wssoUserName);
                  results.log("Fixed");
               }
            }
            if (!wssoBemsId.equals(user.getUserId())) {
               if (!user.getUserId().matches("\\d{1,9}")) {
                  if (debug) {
                     results.warningf("[%s] wsso.bems [%s] invalid\n", user.toStringWithId(), wssoBemsId);
                  }
               } else {
                  results.warningf("[%s] wsso.bems [%s] != user.userId [%s]\n", wssoBemsId, user.getUserId(),
                     user.toStringWithId());
                  if (persist) {
                     changes.setSoleAttributeValue(user.getArtifact(), CoreAttributeTypes.UserId, wssoBemsId);
                     results.log("Fixed");
                  }
               }
            }
            if (!user.getName().contains(",") && debug) {
               results.errorf("[%s] name doesn't contain last, first??", user);
            }
         }
      }
   }

   private void testInactiveCauseWentInactive(List<UserToken> regUsers) throws Exception {
      for (UserToken user : regUsers) {
         // Where atsUser is record from company database
         AtsUser atsUser = getUserByUserId(user.getUserId());
         if (atsUser != null) {

            boolean inActive = false;
            if (user.isActive()) {

               // No record came back for this user, so set inactive
               if (Strings.isInvalid(atsUser.getName())) {
                  results.warningf("WssoUser record not found [%s]; Should set Inactive\n", user.toStringWithId());
                  inActive = true;
               }

               // Record came back but WssoUser = InActive and OSEE User == Active, so set inactive
               else if (!atsUser.isActive()) {
                  results.warningf("User [%s] User.active [%s] != WssoUser.active [%s]; Should set Inactive\n",
                     user.toStringWithId(), user.isActive(), atsUser.isActive());
                  inActive = true;
               }

               if (inActive && persist) {
                  changes.setSoleAttributeValue(user.getArtifact(), CoreAttributeTypes.Active, atsUser.isActive());
                  results.logf("Fixed Active to %s\n", atsUser.isActive());
               }
            }
         }
      }
   }

   private void testInactiveCauseHaveNotAccessed(List<UserToken> regUsers) throws Exception {
      results.logf("\ngetDaysTillFirstInActiveNotice %s\n", getDaysTillFirstInActiveNotice());
      results.logf("getDaysTillLastInActiveNotice %s\n", getDaysTillLastInActiveNotice());
      results.logf("getDaysTillInActive %s\n\n", getDaysTillInActive());
      for (UserToken user : regUsers) {
         if (user.isActive()) {
            // Handle users who still active but have not accessed OSEE in xxx days
            int leastDaysOfActivity = getLeastDaysLastActivity(user);

            if (leastDaysOfActivity > getDaysTillInActive()) {
               disableUserAccount(user, leastDaysOfActivity);
            } else if (leastDaysOfActivity > getDaysTillLastInActiveNotice()) {
               sendInactivityNotification(user, leastDaysOfActivity, changes, false);
            } else if (leastDaysOfActivity > getDaysTillFirstInActiveNotice()) {
               sendInactivityNotification(user, leastDaysOfActivity, changes, true);
            } else if (debug) {
               results.logf("Ignoring: %s days since login - userId %s - %s\n", leastDaysOfActivity, user.getUserId(),
                  user.toStringWithId());
            }
         }
      }
   }

   private void disableUserAccount(UserToken user, int leastDaysOfActivity) {
      results.warningf("De-Activate User not logged in for > %s days was %s for %s userId %s\n", getDaysTillInActive(),
         leastDaysOfActivity, user.toStringWithId(), user.getUserId());
      if (persist) {
         changes.setSoleAttributeValue(user.getArtifact(), CoreAttributeTypes.Active, false);
         results.log("Fixed");
      }
   }

   protected void sendInactivityNotification(UserToken user, int leastDaysOfActivity, IAtsChangeSet changes,
      boolean first) {

      if (EmailUtil.isEmailInValid(user.getEmail())) {
         return;
      }

      String firstOrLastStr = first ? "First" : "Last";
      int firstOrLastDaysInt = first ? getDaysTillFirstInActiveNotice() : getDaysTillLastInActiveNotice();
      String firstOrLastStaticId = first ? FIRST_NOTIFICATION_STATIC_ID : SECOND_NOTIFICATION_STATIC_ID;

      results.warningf("Email %s Notice to User not logged in for > %s days was %s for %s userId %s\n", firstOrLastStr,
         firstOrLastDaysInt, leastDaysOfActivity, user.toStringWithId(), user.getUserId());

      if (persist) {
         ArtifactReadable userArt = (ArtifactReadable) atsApi.getQueryService().getArtifact(user.getId());
         if (userArt.getTags().contains(firstOrLastStaticId)) {
            return;
         }
         String fromEmail = atsApi.getConfigValue(AtsConfigKey.NoReplyEmail, "");
         if (EmailUtil.isEmailInValid(fromEmail)) {
            results.errorf(AtsConfigKey.NoReplyEmail.name() + " is not set; notifications disabled\n");
            return;
         }

         String toEmail = user.getEmail();
         String subject =
            String.format("ACTION REQUIRED - OSEE Account Will Be Disabled - %s Notification", firstOrLastStr);
         atsApi.getNotificationService().sendNotifications(fromEmail, Collections.asList(toEmail), subject,
            "Your OSEE account has not been accessed in the past " //
               + firstOrLastDaysInt + " days.<br/><br/>" //
               + "<b>Please launch OSEE to continue account access.</b><br/><br/> " //
               + "Otherwise the account will be automatically disabled.<br/><br/>" //
               + "Account: " + user.toString());
         changes.addAttribute(userArt, CoreAttributeTypes.StaticId, firstOrLastStaticId);
         results.log("Sent");
      }
   }

   /**
    * @return least days on inactivity between activity log entries and user art last change
    */
   protected Integer getLeastDaysLastActivity(UserToken user) {

      // Check activity table based on account id (user artifact id)
      Integer lastActivityEntryDays = 0;
      JdbcStatement chStmt = jdbcClient.getStatement();
      Date lastActivityDate = null;
      try {
         chStmt.runPreparedQuery(LAST_DATE_ACCOUNT_LOGGED_ACTIVITY, user.getIdString());
         while (chStmt.next()) {
            Timestamp time = chStmt.getTimestamp("START_TIMESTAMP");
            if (time != null) {
               lastActivityDate = new Date(time.getTime());
               lastActivityEntryDays = DateUtil.getWorkingDaysBetween(lastActivityDate, new Date());
            }
            break;
         }
      } catch (OseeCoreException ex) {
         results.errorf("Exception %s", Lib.exceptionToString(ex));
      } finally {
         chStmt.close();
      }

      // Check last change to user artifact
      ArtifactToken art = atsApi.getQueryService().getArtifact(user);
      Date date = getTxDate(art);
      int userArtDaysInActive = DateUtil.getWorkingDaysBetween(date, new Date());

      if (lastActivityEntryDays > 0) {
         if (lastActivityEntryDays < userArtDaysInActive) {
            return lastActivityEntryDays;
         }
      }
      return userArtDaysInActive;
   }

   protected int getDaysTillInActive() {
      return 183;
   }

   protected int getDaysTillFirstInActiveNotice() {
      return 160;
   }

   protected int getDaysTillLastInActiveNotice() {
      return 175;
   }

   protected void testForDuplicates(List<UserToken> regUsers) throws Exception {
      Set<Long> duplicates = new HashSet<>();
      Map<String, UserToken> userIdMap = new TreeMap<>();
      Map<String, UserToken> nameMap = new TreeMap<>();
      boolean error = false;
      for (UserToken user : regUsers) {
         UserToken userIdInMap = userIdMap.get(user.getUserId());
         if (userIdMap.containsKey(user.getUserId())) {
            results.errorf("[%s] and [%s] have SAME USERIDs\n", user, userIdMap.get(user.getUserId()));
            duplicates.add(userIdInMap.getId());
            duplicates.add(user.getId());
            error = true;
         }
         UserToken nameInMap = nameMap.get(user.getName());
         if (nameMap.containsKey(user.getName()) && !ignoreDupNames.contains(user.getArtifact())) {
            results.errorf("[%s] and [%s] have SAME NAMES\n", user, nameMap.get(user.getName()));
            duplicates.add(nameInMap.getId());
            duplicates.add(user.getId());
            error = true;
         }
         nameMap.put(user.getName(), user);
         userIdMap.put(user.getUserId(), user);
      }
      if (error) {
         results.logf("\nDuplicates: %s\n", Collections.toString(",", duplicates));
      }
   }

   private UserToken createUserToken(ArtifactToken art) {
      String email = atsApi.getAttributeResolver().getSoleAttributeValue(art, CoreAttributeTypes.Email, "");
      String userId = atsApi.getAttributeResolver().getSoleAttributeValue(art, CoreAttributeTypes.UserId, "");
      boolean active = atsApi.getAttributeResolver().getSoleAttributeValue(art, CoreAttributeTypes.Active, false);
      List<String> loginIds = atsApi.getAttributeResolver().getAttributesToStringList(art, CoreAttributeTypes.LoginId);
      UserToken user = UserToken.create(art.getId(), art.getName(), email, userId, active, loginIds,
         java.util.Collections.emptyList());
      user.setArtifact(art);

      if (atsApi.getAttributeResolver().getAttributesToStringListFromArt(art, CoreAttributeTypes.StaticId).contains(
         IGNORE_DUP_NAMES)) {
         ignoreDupNames.add((ArtifactReadable) art);
      }
      return user;
   }

}
