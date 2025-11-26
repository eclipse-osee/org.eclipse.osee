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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsConfigKey;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.UserActivityAction;
import org.eclipse.osee.ats.api.user.UserActivityData;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Donald G. Dunne
 */
public abstract class SyncOseeAndUserDB {

   public static String OSEE_AUTORUN_USER_RELATIONS_CHECKED = "osee.autorun.userRelationsChecked";
   private final String DAYS_SINCE_LAST_ACTIVITY_LOG_ENTRY = "SELECT " //
      + "  ACCOUNT_ID as author_id, " //
      + "  FLOOR(CAST(SYSTIMESTAMP AS DATE) - CAST(MAX(\"START_TIMESTAMP\") AS DATE)) AS days_since_latest " //
      + "FROM OSEE_ACTIVITY act " //
      + "WHERE TYPE_ID = " //
      // IDE Client Session entry
      + CoreActivityTypes.IDE.getIdString() + " " //
      + "GROUP BY ACCOUNT_ID " //
      + "ORDER BY days_since_latest ASC";
   private final String DAYS_SINCE_LAST_TRANSACTION_AUTHOR_ENTRY = "SELECT" //
      + "  AUTHOR as author_id, " //
      + "  FLOOR(CAST(SYSTIMESTAMP AS DATE) - CAST(MAX(\"TIME\") AS DATE)) AS days_since_latest " //
      + "FROM OSEE_TX_DETAILS txs " //
      + "GROUP BY AUTHOR " //
      + "ORDER BY days_since_latest ASC";
   public static String NO_EMAIL_STATIC_ID = "noEmail";
   public static String FIRST_NOTIFICATION_STATIC_ID = "FirstInactiveNotification";
   public static String SECOND_NOTIFICATION_STATIC_ID = "SecondInactiveNotification";
   protected XResultData results = null;
   protected final AtsApi atsApi;
   protected final boolean persist;
   protected final boolean debug;
   protected final JdbcClient jdbcClient;
   protected final String IGNORE_NAME_CHANGE = "IgnoreNameChange";
   protected final String IGNORE_DUP_NAMES = "IgnoreDupNames";
   protected IAtsChangeSet changes;
   protected List<String> ignoreStaticIds;
   protected Map<ArtifactId, UserActivityData> userIdToUserAct = new HashMap<>(200);
   protected Date todayDate;
   private int activeCount;
   private final List<Long> INVALID_ACTIVITY_ACCOUNT_IDS = new ArrayList<>();
   private final List<Long> INVALID_TXS_AUTHORS = new ArrayList<>();

   public SyncOseeAndUserDB(boolean persist, boolean debug, AtsApi atsApi) {
      this.jdbcClient = atsApi.getJdbcService().getClient();
      this.atsApi = atsApi;
      this.persist = persist;
      this.debug = true;
      todayDate = new Date();
   }

   protected String getTitle() {
      return "Sync OSEE and User DB";
   }

   public XResultData run() {
      ElapsedTime fullTime = new ElapsedTime(getTitle(), debug);
      try {

         results = new XResultData(false);
         results.log(getTitle());
         results.log(DateUtil.getMMDDYYHHMM() + "\n");
         results.log("Search for \"Warning:\", \"Error:\" and \"Action Group:\" to review changes\n");

         loadUsers();
         if (results.isErrors()) {
            return results;
         }

         loadActivityLogData();
         loadTransactionAuthorData();

         ////////////////////////////////////////////////////////////////
         ///////////// THESE TESTS ONLY REPORT - NO AUTO-FIX AVAILABLE
         ////////////////////////////////////////////////////////////////

         // Test if there duplicate users with same name - REPORT ONLY
         ElapsedTime time = new ElapsedTime("testForDuplicates", debug);
         boolean duplicatesFound = testForDuplicates();
         time.end();

         // If this test fails, no other checks are done cause these have to finish first
         if (duplicatesFound) {
            results.logf(
               "\nError: Sync aborted because duplicate user IDs/NAMEs found; <b>RESOLVE THESE FIRST!!</b>\n");
         } else {

            ////////////////////////////////////////////////////////////
            ///////////// THESE CHECKS HAVE FIXES IF PERSIST == TRUE
            ////////////////////////////////////////////////////////////

            if (persist) {
               AtsUser user = atsApi.getUserService().getCurrentUserOrNull();
               if (user == null) {
                  user = atsApi.getUserService().getUserById(SystemUser.OseeSystem);
               }
               changes = atsApi.createChangeSet(getTitle(), user);
            }

            // Test that user is inactive in user db - FIX AVAILABLE
            time = new ElapsedTime("testCauseWentInactive", debug);
            testInactiveCauseWentInactive();
            time.end();

            // Test that user has accessed in minimal time (180 days) - FIX AND EMAILS AVAILABLE
            time = new ElapsedTime("testCauseHaveNotAccessed", debug);
            testInactiveCauseHaveNotAccessed();
            time.end();

            // Test that user is in appropriate UserGroups - FIX AVAILABLE
            time = new ElapsedTime("testUserGroups", debug);
            testUserGroups();
            time.end();

            // Test that user has attributes updated from the User DB - FIX AVAILABLE
            time = new ElapsedTime("testUserAttributes", debug);
            testUserAttributes();
            time.end();
         }

         printUads();

         results.logf("\nProcessed %s users %s active", getUserActivities().size(), activeCount);
      } catch (Exception ex) {
         results.errorf("\nException: %s", Lib.exceptionToString(ex));
      } finally {
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
      }
      fullTime.endSec();
      try {
         String html = AHTML.simplePage(results.toString());
         html = html.replaceAll("\n", "<br/>");
         saveResults(html);
      } catch (Exception ex) {
         results.logf("Error writing to file %s", Lib.exceptionToString(ex));
      }
      return results;
   }

   private void loadUsers() {

      ElapsedTime time = new ElapsedTime("Loading Users", debug);
      List<ArtifactToken> userArts = null;

      userArts = atsApi.getQueryService().getArtifacts(CoreArtifactTypes.User);

      // Debug single user only
      //      userArts = new ArrayList<>();
      //      userArts.add(atsApi.getQueryService().getArtifact(875L));

      for (ArtifactToken userArt : userArts) {

         UserToken userToken = atsApi.userService().create(userArt);
         UserActivityData uad = new UserActivityData();
         uad.setUserArt(userArt);
         uad.setUserTok(userToken);
         uad.setActive(userToken.isActive());

         if (!userToken.isActive()) {
            uad.setActionNeeded(UserActivityAction.Ignore_Already_InActive_In_OSEE);
         }
         activeCount += userToken.isActive() ? 1 : 0;
         userIdToUserAct.put(userToken.getArtifactId(), uad);

         List<String> tags =
            atsApi.getAttributeResolver().getAttributesToStringListFromArt(userArt, CoreAttributeTypes.StaticId);
         if (tags.contains(IGNORE_DUP_NAMES)) {
            uad.setActionNeeded(UserActivityAction.Ignore_Duplicate_Names);
         }
         if (tags.contains(IGNORE_NAME_CHANGE)) {
            uad.setActionNeeded(UserActivityAction.Ignore_Name_Update);
         }
         if (tags.contains(FIRST_NOTIFICATION_STATIC_ID)) {
            uad.setFirstNotifySent(true);
         }
         if (tags.contains(SECOND_NOTIFICATION_STATIC_ID)) {
            uad.setSecondNotifySent(true);
         }
         for (String ignoreTag : getIgnoreStaticIds()) {
            if (tags.contains(ignoreTag)) {
               uad.setActionNeeded(UserActivityAction.Ignore_By_Static_Id);
            }
         }
         if (SystemUser.values().contains(userArt)) {
            uad.setActionNeeded(UserActivityAction.Ignore_System_User);
         }
      }
      time.end();
   }

   protected void loadActivityLogData() {
      ElapsedTime time = new ElapsedTime("loadActivityLogData");
      JdbcStatement chStmt = jdbcClient.getStatement();
      try {
         chStmt.runPreparedQuery(DAYS_SINCE_LAST_ACTIVITY_LOG_ENTRY);
         while (chStmt.next()) {
            long authorId = chStmt.getLong("author_id");
            int days = chStmt.getInt("days_since_latest");
            UserActivityData uad = getUadOrError(authorId);
            if (uad.isIgnoreSystemUser()) {
               continue;
            }
            if (uad.isValid()) {
               uad.setDaysSinceIdeUse(days);
            } else {
               INVALID_ACTIVITY_ACCOUNT_IDS.add(authorId);
            }
         }
      } catch (OseeCoreException ex) {
         results.errorf("Activity: Exception %s\n", Lib.exceptionToString(ex));
      } finally {
         chStmt.close();
      }
      time.endMSec();
   }

   protected void loadTransactionAuthorData() {
      ElapsedTime time = new ElapsedTime("loadTransactionAuthorData");
      JdbcStatement chStmt = jdbcClient.getStatement();
      try {
         chStmt.runPreparedQuery(DAYS_SINCE_LAST_TRANSACTION_AUTHOR_ENTRY);
         while (chStmt.next()) {
            long authorId = chStmt.getLong("author_id");
            int days = chStmt.getInt("days_since_latest");
            UserActivityData uad = getUadOrError(authorId);
            if (uad.isIgnoreSystemUser()) {
               continue;
            }
            if (uad.isValid()) {
               uad.setDaysSinceTxsAuthored(days);
            } else {
               INVALID_TXS_AUTHORS.add(authorId);
            }
         }
      } catch (OseeCoreException ex) {
         results.errorf("Transaction: Exception %s\n", Lib.exceptionToString(ex));
      } finally {
         chStmt.close();
      }
      time.endMSec();
   }

   protected boolean testForDuplicates() throws Exception {
      Set<Long> duplicates = new HashSet<>();
      Map<String, UserToken> userIdMap = new TreeMap<>();
      Map<String, UserToken> nameMap = new TreeMap<>();
      boolean duplicateFound = false;
      for (UserActivityData uad : getUserActivities()) {
         UserToken userTok = uad.getUserTok();
         if (userIdMap.containsKey(userTok.getUserId())) {
            results.errorf("[%s] and [%s] have SAME USERIDs\n", uad, userIdMap.get(userTok.getUserId()));
            duplicates.add(userTok.getId());
            duplicateFound = true;
         }
         UserToken nameInMap = nameMap.get(userTok.getName());
         if (!uad.isIgnoreDuplicateName() && nameMap.containsKey(userTok.getName())) {
            results.errorf("[%s] and [%s] have SAME NAMES\n", userTok, nameMap.get(userTok.getName()));
            duplicates.add(nameInMap.getId());
            duplicates.add(userTok.getId());
            duplicateFound = true;
         }
         nameMap.put(userTok.getName(), userTok);
         userIdMap.put(userTok.getUserId(), userTok);
      }
      if (duplicateFound) {
         results.logf("\nDuplicates: %s\n", Collections.toString(",", duplicates));
      }
      return duplicateFound;
   }

   private void testInactiveCauseWentInactive() throws Exception {
      int count = 1;

      List<String> userIds = new ArrayList<>();
      for (UserActivityData uad : getUserActivities()) {
         if (uad.isActive()) {
            String userId = uad.getUserTok().getUserId();
            if (StringUtils.isNumeric(userId) && userId.length() <= 7) {
               userIds.add(userId);
            }
         }
      }
      int subDivideCount = 100;
      List<Collection<String>> subDivide = Collections.subDivide(userIds, subDivideCount);
      int x = 1, total = subDivide.size();
      for (Collection<String> userIdSet : subDivide) {
         String userIdsStr = Collections.toString(",", userIdSet);
         if (debug) {
            System.err.println(String.format("Processing set %s/%s", x++, total));
            System.err.println(String.format("--- UserIds [%s]", userIdsStr));
         }

         List<AtsUser> usersByUserIds = getUsersByUserIds(userIdsStr);
         if (debug) {
            System.err.println(String.format("--- Returned %s records", usersByUserIds.size()));
         }

         Map<String, AtsUser> userIdToWssoUser = new HashMap<>(usersByUserIds.size());
         for (AtsUser wssoUser : usersByUserIds) {
            userIdToWssoUser.put(wssoUser.getUserId(), wssoUser);
         }

         for (UserActivityData uad : getUserActivities()) {
            if (uad.isIgnoreByStaticId()) {
               continue;
            }

            UserToken user = uad.getUserTok();
            // Where wssoUser is record from company database
            AtsUser wssoUser = userIdToWssoUser.get(user.getUserId());
            if (wssoUser != null) {

               boolean inActive = false;
               if (user.isActive()) {
                  if (debug) {
                     System.err.println(
                        String.format("testInactive %s/%s - %s", count++, activeCount, user.toStringWithId()));
                  }

                  // No record came back for this user, so set inactive
                  if (Strings.isInvalid(wssoUser.getName())) {
                     results.warningf("WssoUser record not found [%s]; Should set Inactive\n", user.toStringWithId());
                     uad.setActionNeeded(UserActivityAction.Set_Inactive_Cause_Left);
                     inActive = true;
                  }

                  // Record came back but WssoUser = InActive and OSEE User == Active, so set inactive
                  else if (!wssoUser.isActive()) {
                     results.warningf("User [%s] User.active [%s] != WssoUser.active [%s]; Should set Inactive\n",
                        user.toStringWithId(), user.isActive(), wssoUser.isActive());
                     uad.setActionNeeded(UserActivityAction.Set_Inactive_Cause_Left);
                     inActive = true;
                  }

                  if (inActive && persist) {
                     changes.setSoleAttributeValue(getUser(user), CoreAttributeTypes.Active, wssoUser.isActive());
                     results.logf("Fixed Active to %s\n", wssoUser.isActive());
                  }
               }
            }
         }
      }
   }

   private void testInactiveCauseHaveNotAccessed() throws Exception {
      for (UserActivityData uad : getUserActivities()) {
         if (!uad.isActive() || uad.isIgnoreByStaticId() || uad.isIgnoreSystemUser()) {
            continue;
         }
         UserToken user = uad.getUserTok();
         if (user.isActive()) {
            // Handle users who still active but have not accessed OSEE in xxx days
            int daysSinceLastUse = uad.getDaysSinceLastUse();

            if (daysSinceLastUse == -1 || daysSinceLastUse > getDaysTillInActive()) {
               disableUserAccount(uad, user, daysSinceLastUse);
            } else if (daysSinceLastUse > getDaysTillLastInActiveNotice()) {
               sendInactivityNotification(uad, user, daysSinceLastUse, changes, false);
            } else if (daysSinceLastUse > getDaysTillFirstInActiveNotice()) {
               sendInactivityNotification(uad, user, daysSinceLastUse, changes, true);
            } else if (debug) {
               uad.setActionNeeded(UserActivityAction.Ignore_Active_Cause_Recent_Use);
            }
         }
      }
   }

   protected abstract void testUserGroups();

   protected void testUserAttributes() throws Exception {

      for (UserActivityData uad : getUserActivities()) {
         if (uad.isIgnoreByStaticId() || isNotActiveOrGoingInactive(uad)) {
            continue;
         }
         UserToken user = uad.getUserTok();
         AtsUser wssoUser = getWssoUser(uad);
         if (wssoUser != null) {
            String wssoUserId = wssoUser.getUserId();
            String wssoLoginId = wssoUser.getLoginIds().get(0);
            String wssoUserName = wssoUser.getName();
            String wssoMail = wssoUser.getEmail();
            String wssoPhone = wssoUser.getPhone();

            // No record returned, so nothing to update
            if (Strings.isInvalid(wssoUserName)) {
               continue;
            }

            // Verify/update loginId attribute
            if (debug && Strings.isValid(wssoLoginId) && !user.getLoginIds().contains(wssoLoginId)) {
               // By default, login ids will not be sync'd as they should have access approval and manually fixed
               results.warningf("%s login ids %s appear invalid; should be [%s] - NO FIX\n", user.toStringWithId(),
                  user.getLoginIds(), wssoLoginId);
            }

            // If loginId == null, not a valid record/email, don't update or error
            if (wssoLoginId != null) {
               if (!EmailUtil.isEmailValid(wssoMail)) {
                  if (!atsApi.getAttributeResolver().getAttributesToStringList(getUser(user),
                     CoreAttributeTypes.StaticId).contains(NO_EMAIL_STATIC_ID)) {
                     if (debug) {
                        results.errorf("[%s] WSSO User Email [%s] is invalid\n", user.toStringWithId(), wssoMail);
                     }
                  }
               } else if (!user.getEmail().equals(wssoMail)) {
                  // Ignore situations where the user's email in already set and different
                  results.warningf("(On Persist) - [%s] wsso.email [%s] != user.email [%s]\n", user.toStringWithId(),
                     wssoMail, user.getEmail());
                  if (persist) {
                     changes.setSoleAttributeValue(getUser(user), CoreAttributeTypes.Email, wssoMail);
                     results.logf("Fixed Email to %s\n", wssoMail);
                  }
               }
            }

            String phone =
               atsApi.getAttributeResolver().getSoleAttributeValue(getUser(user), CoreAttributeTypes.Phone, "");
            if (Strings.isValid(wssoPhone) && !wssoPhone.equals(phone)) {
               results.warningf("(On Persist) - [%s] wsso.phone [%s] != user.phone [%s]\n", user.toStringWithId(),
                  wssoPhone, phone);
               if (persist) {
                  changes.setSoleAttributeValue(getUser(user), CoreAttributeTypes.Phone, wssoPhone);
                  results.log("Fixed");
               }
            }

            if (!uad.isIgnoreNameChange() && !wssoUserName.equals(getUser(user).getName())) {
               results.warningf("(On Persist) - [%s] wsso.name [%s] != user.name [%s]\n", user.toStringWithId(),
                  wssoUserName, user.getName());
               if (persist) {
                  changes.setName(getUser(user), wssoUserName);
                  results.log("Fixed");
               }
            }

            if (!wssoUserId.equals(user.getUserId())) {
               if (!user.getUserId().matches("\\d{1,9}")) {
                  if (debug) {
                     results.warningf("[%s] wsso.userid [%s] invalid\n", user.toStringWithId(), wssoUserId);
                  }
               } else {
                  results.warningf("(On Persist) - [%s] wsso.userid [%s] != user.userId [%s]\n", wssoUserId,
                     user.getUserId(), user.toStringWithId());
                  if (persist) {
                     changes.setSoleAttributeValue(getUser(user), CoreAttributeTypes.UserId, wssoUserId);
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

   protected void printUads() {
      List<UserActivityAction> actions = Collections.castAll(UserActivityAction.Not_Set_Fix_This.values());
      for (UserActivityAction action : actions) {
         results.logf("\n============\n<b>%s</b>: %s\n============\n\n", AHTML.color("DARKBLUE", "Action Group"),
            action.name());
         if (action.equals(UserActivityAction.Invalid_Activity_Account_Id_Fix_This)) {
            results.log(Collections.toString(",", INVALID_ACTIVITY_ACCOUNT_IDS));
         } else if (action.equals(UserActivityAction.Invalid_Txs_Author_Fix_This)) {
            results.log(Collections.toString(",", INVALID_TXS_AUTHORS));
         } else {
            // Log days configured till notify or go inactive
            if (action.equals(UserActivityAction.Send_First_Unused_Notification)) {
               results.logf("daysTillFirstInActiveNotice %s\n\n", getDaysTillFirstInActiveNotice());
            } else if (action.equals(UserActivityAction.Send_Second_Unused_Notification)) {
               results.logf("daysTillLastInActiveNotice %s\n\n", getDaysTillLastInActiveNotice());
            } else if (action.equals(UserActivityAction.Set_Inactive_Cause_Unused)) {
               results.logf("daysTillInActive %s\n\n", getDaysTillInActive());
            }

            results.addRaw(AHTML.beginMultiColumnTable(98, 2));
            results.addRaw(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("User", "Active", "Days Since Use", //
               "Days Since IDE", "Days Since Txs", "Status", "Action", "First Notify", //
               "Second Notify")));
            for (UserActivityData uad : getUserActivities()) {
               if (uad.getActionNeeded().equals(action)) {
                  results.addRaw(AHTML.addRowMultiColumnTable( //
                     uad.getUserTok().toStringWithId(), //
                     String.valueOf(uad.isActive()), //
                     String.valueOf(uad.getDaysSinceLastUse()), //
                     String.valueOf(uad.getDaysSinceIdeUse()), //
                     String.valueOf(uad.getDaysSinceTxsAuthored()), //
                     uad.getStatus().getName(), //
                     uad.getActionNeeded().getName(), //
                     String.valueOf(uad.isFirstNotifySent()), //
                     String.valueOf(uad.isSecondNotifySent()) //
                  ));
               }
            }
            results.addRaw(AHTML.endMultiColumnTable());
         }
      }
   }

   private void saveResults(String html) {
      String serverData = System.getProperty("osee.application.server.data");
      if (!Strings.isValid(serverData)) {
         serverData = System.getProperty("user.home");
      }
      String outputDirName = serverData + File.separator + "userSync";
      File outDir = new File(outputDirName);
      outDir.mkdir();

      String outputFileName = outputDirName + //
         File.separator + Lib.getDateTimeString() + ".html";
      File outFile = new File(outputFileName);
      try {
         Lib.writeStringToFile(html, outFile);
      } catch (Exception ex) {
         String exStr = AHTML.simplePage(Lib.exceptionToString(ex));
         try {
            Lib.writeStringToFile(exStr, outFile);
         } catch (IOException ex1) {
            System.err.println(Lib.exceptionToString(ex1));
         }
      }
   }

   /**
    * @return List of strings stored in static id attribute of User artifact noting that a User artifact should be
    * ignored during sync. These would be general logins for things like labs where there is no single person
    * associated.
    */
   protected abstract List<String> getIgnoreStaticIds();

   private void disableUserAccount(UserActivityData uad, UserToken user, int leastDaysOfActivity) {
      uad.setActionNeeded(UserActivityAction.Set_Inactive_Cause_Unused);
      if (persist) {
         changes.setSoleAttributeValue(getUser(user), CoreAttributeTypes.Active, false);
         results.log("Fixed");
      }
   }

   protected void sendInactivityNotification(UserActivityData uad, UserToken user, int leastDaysOfActivity,
      IAtsChangeSet changes, boolean first) {

      if (EmailUtil.isEmailInValid(user.getEmail())) {
         return;
      }

      String oseeDbName = OseeProperties.getOseeDbName();
      String firstOrLastStr = first ? "First" : "Last";
      int firstOrLastDaysInt = first ? getDaysTillFirstInActiveNotice() : getDaysTillLastInActiveNotice();
      String firstOrLastStaticId = first ? FIRST_NOTIFICATION_STATIC_ID : SECOND_NOTIFICATION_STATIC_ID;

      if (first) {
         uad.setActionNeeded(UserActivityAction.Send_First_Unused_Notification);
      } else {
         uad.setActionNeeded(UserActivityAction.Send_Second_Unused_Notification);
      }

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

         List<String> emailList = new ArrayList<>();
         emailList.add(user.getEmail());
         String abridgedEmail = atsApi.getUserService().getAbridgedEmail(user, atsApi);
         if (EmailUtil.isEmailValid(abridgedEmail)) {
            emailList.add(abridgedEmail);
         }
         String subject = String.format("ACTION REQUIRED - %s OSEE Account Will Be Disabled - %s Notification",
            oseeDbName, firstOrLastStr);

         atsApi.getNotificationService().sendNotifications(fromEmail, emailList, subject,
            "Your OSEE account has not been accessed in the past " //
               + firstOrLastDaysInt + " days.<br/><br/>" //
               + "<b>Please launch OSEE to continue account access.</b><br/><br/> " //
               + "Otherwise the account will be automatically disabled.<br/><br/>" //
               + "Account: " + user.toString());

         changes.addAttribute(userArt, CoreAttributeTypes.StaticId, firstOrLastStaticId);
         results.log("Sent");
      }
   }

   protected UserActivityData getUadOrError(long authorId) {
      ArtifactId userArtId = ArtifactId.valueOf(authorId);
      UserActivityData uad = userIdToUserAct.get(userArtId);
      if (uad == null) {
         return UserActivityData.SENTINEL;
      }
      return uad;
   }

   protected void addMember(IUserGroup everyoneGroup, UserToken user) {
      changes.relate(everyoneGroup.getArtifact(), CoreRelationTypes.Users_User, user);
   }

   /**
    * @return AtsUser as defined in company system such as wsso
    */
   protected abstract AtsUser getWssoUserByUserId(String userId);

   protected abstract List<AtsUser> getUsersByUserIds(String userIds);

   protected abstract Date getTxDate(ArtifactToken art);

   protected List<String> getUserStaticIds(UserToken user) {
      return atsApi.getAttributeResolver().getAttributesToStringList(getUser(user), CoreAttributeTypes.StaticId);
   }

   protected boolean isNotActiveOrGoingInactive(UserActivityData uad) {
      return !uad.isActive() || //
      // Don't update user attributes if they are getting set inactive
         uad.getActionNeeded().equals(UserActivityAction.Set_Inactive_Cause_Left) || //
         uad.getActionNeeded().equals(UserActivityAction.Set_Inactive_Cause_Unused);
   }

   protected AtsUser getWssoUser(UserActivityData uad) {
      AtsUser wssoUser = uad.getWssoUser();
      if (wssoUser == null) {
         wssoUser = getWssoUserByUserId(uad.getUserTok().getUserId());
         uad.setWssoUser(wssoUser);
      }
      return wssoUser;
   }

   private ArtifactToken getUser(UserToken userTok) {
      ArtifactToken userArt = ArtifactToken.SENTINEL;
      UserActivityData uad = userIdToUserAct.get(userTok.getArtifactId());
      if (uad != null) {
         userArt = uad.getUserArt();
      }
      return userArt;
   }

   protected int getDaysTillFirstInActiveNotice() {
      return getDaysTillInActive() - 20; // 20 days before
   }

   protected int getDaysTillLastInActiveNotice() {
      return getDaysTillInActive() - 10; // 10 days before
   }

   protected int getDaysTillInActive() {
      return 183; // 6 Months and few days
   }

   public Map<ArtifactId, UserActivityData> getUserIdToUserAct() {
      return userIdToUserAct;
   }

   public void setUserIdToUserAct(Map<ArtifactId, UserActivityData> userIdToUserAct) {
      this.userIdToUserAct = userIdToUserAct;
   }

   protected Collection<UserActivityData> getUserActivities() {
      return userIdToUserAct.values();
   }

}
