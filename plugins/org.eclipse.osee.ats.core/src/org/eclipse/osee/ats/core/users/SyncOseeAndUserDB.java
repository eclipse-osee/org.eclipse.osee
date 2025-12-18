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
import java.util.Comparator;
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
import org.eclipse.osee.ats.api.user.UserUsageStatus;
import org.eclipse.osee.ats.api.user.UserUsageType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionDetails;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.DatabaseType;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.data.ArtifactReadableImpl;

/**
 * @author Donald G. Dunne
 */
public abstract class SyncOseeAndUserDB {

   private static final String DAYS_SINCE_SET_ACTIVE = "Days Since Set Active";
   private static final String DAYS_SINCE_TXS = "Days Since Txs";
   private static final String DAYS_SINCE_IDE = "Days Since IDE";
   private static final String DAYS_SINCE_USE = "Days Since Use";
   public static String OSEE_AUTORUN_USER_RELATIONS_CHECKED = "osee.autorun.userRelationsChecked";
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
   protected Map<ArtifactId, UserActivityData> userArtIdToUserAct = new HashMap<>(200);
   protected Date todayDate;
   private int activeCount;
   private final List<Long> INVALID_ACTIVITY_ACCOUNT_IDS = new ArrayList<>();
   private final List<Long> INVALID_TXS_AUTHORS = new ArrayList<>();
   private List<UserActivityData> sortedUads;
   private Date runDate;
   private TransactionToken tx = TransactionToken.SENTINEL;

   public SyncOseeAndUserDB(boolean persist, boolean debug, AtsApi atsApi) {
      this.jdbcClient = atsApi.getJdbcService().getClient();
      this.atsApi = atsApi;
      this.persist = persist;
      this.debug = debug;
      todayDate = new Date();
   }

   protected String getTitle() {
      return "Sync OSEE and User DB";
   }

   public XResultData run() {
      ElapsedTime fullTime = new ElapsedTime(getTitle(), debug);
      try {

         runDate = new Date();

         results = new XResultData(false);
         results.log(getTitle());
         results.log(DateUtil.getMMDDYYHHMM(runDate) + "\n");
         results.log("Search for \"Warning:\", \"Error:\", \"-1\" and \"Action Group:\" to review changes\n");
         results.log(DAYS_SINCE_IDE + ": Days since last launch of Windows OSEE Client");
         results.log(DAYS_SINCE_TXS + ": Days Since User authored a transaction (saved something) via any client");
         results.log(DAYS_SINCE_SET_ACTIVE + ": Days Since User active attribute was set to true or created");
         results.log(DAYS_SINCE_USE + ": Lesser number of the above days");
         results.log("NOTE : Not Found means no use detected, 0 means usage detected today\n");

         ElapsedTime time = new ElapsedTime("Loading Users", debug);
         loadUsers();
         time.endMSec();

         if (results.isErrors()) {
            return results;
         }

         time = new ElapsedTime("loadReactivateData", debug);
         loadReactivateData();
         time.endMSec();

         time = new ElapsedTime("loadActivityLogData", debug);
         loadActivityLogData();
         time.endMSec();

         time = new ElapsedTime("loadTransactionAuthorData", debug);
         loadTransactionAuthorData();
         time.endMSec();

         sortedUads = new ArrayList<>(userArtIdToUserAct.size());
         sortedUads.addAll(userArtIdToUserAct.values());
         sortedUads.sort(Comparator.comparingInt(UserActivityData::getEarliestActivityStatusDaysSince));

         ////////////////////////////////////////////////////////////////
         ///////////// THESE TESTS ONLY REPORT - NO AUTO-FIX AVAILABLE
         ////////////////////////////////////////////////////////////////

         // Test if there duplicate users with same name - REPORT ONLY
         time = new ElapsedTime("testForDuplicates", debug);
         boolean duplicatesFound = testForDuplicates();
         time.end();

         // If this test fails, no other checks are done cause these have to finish first
         if (duplicatesFound) {
            results.logf(
               "\nError: Sync aborted because duplicate user IDs/NAMEs found; <b>RESOLVE THESE FIRST!!</b>\n");
            return results;
         }

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

         /**
          * The following operations are in the order where the status can be set and thus they will not continue to be
          * checked by the other operations. This order should not be changed. If any users are not in any "Action
          * Group" or Not_Set_Fix_This, then there's a case that is not being handled and needs to be.
          */
         time = new ElapsedTime("ignoreSystemUser", debug);
         ignoreSystemUser();
         time.end();

         time = new ElapsedTime("ignoreByStaticId", debug);
         ignoreByStaticId();
         time.end();

         time = new ElapsedTime("ignoreAlreadyInactive", debug);
         ignoreAlreadyInactive();
         time.end();

         // Test that user is inactive in user db - FIX AVAILABLE
         time = new ElapsedTime("setInactiveCauseLeftCompany", debug);
         setInactiveCauseLeftCompany();
         time.end();

         // Test that user has accessed in minimal time (180 days) - FIX AND EMAILS AVAILABLE
         time = new ElapsedTime("setInactiveOrNotifyCauseHaveNotAccessed", debug);
         setInactiveOrNotifyCauseHaveNotAccessed();
         time.end();

         // Test that user is in appropriate UserGroups - FIX AVAILABLE
         time = new ElapsedTime("updateUserGroups", debug);
         updateUserGroups();
         time.end();

         // Test that user has attributes updated from the User DB - FIX AVAILABLE
         time = new ElapsedTime("updateUserAttributes", debug);
         updateUserAttributes();
         time.end();

         // Test that recent use or re-activated and clear notification flags
         time = new ElapsedTime("updateNotificationsTags", debug);
         updateNotificationsTags();
         time.end();

         printUads();
         printActiveWithDept();

         results.logf("\nProcessed %s users %s active\n", getUserActivities().size(), activeCount);
      } catch (Exception ex) {
         results.errorf("\nException: %s\n", Lib.exceptionToString(ex));
      } finally {
         if (changes != null && persist) {
            tx = changes.executeIfNeeded();
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
         saveResultsToFile(html);
      } catch (Exception ex) {
         results.logf("Error writing to file %s\n", Lib.exceptionToString(ex));
      }
      return results;
   }

   protected void loadUsers() {
      List<ArtifactToken> userArts = null;
      userArts = queryUsersWithTxsDetails();

      // Debug single user only
      //      userArts = new ArrayList<>();
      //      userArts.add(atsApi.getQueryService().getArtifact(875L));

      for (ArtifactToken userArt : userArts) {
         UserToken userToken = atsApi.userService().create(userArt);
         UserActivityData uad = new UserActivityData();
         uad.setUserArt(userArt);
         uad.setUserTok(userToken);
         uad.setActive(userToken.isActive());
         activeCount += userToken.isActive() ? 1 : 0;
         userArtIdToUserAct.put(userToken.getArtifactId(), uad);

         List<String> tags =
            atsApi.getAttributeResolver().getAttributesToStringListFromArt(userArt, CoreAttributeTypes.StaticId);
         uad.setTags(tags);
         if (tags.contains(IGNORE_DUP_NAMES)) {
            results.warningf("Ignoring Duplicate Names for %s\n", userToken.toStringWithId());
            uad.setIgnoreDuplicateNames(true);
         }
         if (tags.contains(IGNORE_NAME_CHANGE)) {
            uad.setIgnoreNameUpdate(true);
         }
         if (tags.contains(FIRST_NOTIFICATION_STATIC_ID)) {
            uad.setFirstNotifySent(true);
         }
         if (tags.contains(SECOND_NOTIFICATION_STATIC_ID)) {
            uad.setSecondNotifySent(true);
         }
      }
   }

   protected abstract List<ArtifactToken> queryUsersWithTxsDetails();

   // Calculate if user was just created or re-activated
   private void loadReactivateData() {
      for (UserActivityData uad : userArtIdToUserAct.values()) {
         ArtifactToken userArt = uad.getUserArt();
         if (userArt instanceof ArtifactReadableImpl) {
            ArtifactReadableImpl userRead = (ArtifactReadableImpl) userArt;
            IAttribute<Object> activeAttr = userRead.getSoleAttribute(CoreAttributeTypes.Active, null);
            if (activeAttr != null) {
               TransactionDetails latestTxDetails = activeAttr.getLatestTxDetails();
               if (latestTxDetails != null && latestTxDetails.getTxId().isValid()) {
                  Date activeDate = latestTxDetails.getTime();
                  int daysSinceActive = DateUtil.getDifference(activeDate, runDate);
                  uad.addUsageType(UserUsageType.USER_REACTIVATED, daysSinceActive);
               }
            }
         }
      }
   }

   protected void loadActivityLogData() {
      JdbcStatement chStmt = jdbcClient.getStatement();
      try {
         boolean postgresql = atsApi.getJdbcService().getClient().getDbType().equals(DatabaseType.postgresql);
         String sql = OseeSql.getDaysSinceLastActivityLogEntrySql(postgresql);
         chStmt.runPreparedQuery(sql);
         while (chStmt.next()) {
            long authorId = chStmt.getLong("author_id");
            int days = chStmt.getInt("days_since_latest");
            UserActivityData uad = getUadOrError(authorId);
            if (uad.isIgnoreSystemUser()) {
               continue;
            }
            if (uad.isValid()) {
               uad.addUsageType(UserUsageType.IDE_CLIENT_USE, days);
            } else {
               INVALID_ACTIVITY_ACCOUNT_IDS.add(authorId);
            }
         }
      } catch (OseeCoreException ex) {
         results.errorf("Activity: Exception %s\n", Lib.exceptionToString(ex));
      } finally {
         chStmt.close();
      }
   }

   protected void loadTransactionAuthorData() {
      JdbcStatement chStmt = jdbcClient.getStatement();
      try {
         boolean postgresql = atsApi.getJdbcService().getClient().getDbType().equals(DatabaseType.postgresql);
         String sql = OseeSql.getDaysSinceTransactionAuthorEntrySql(postgresql);
         chStmt.runPreparedQuery(sql);
         while (chStmt.next()) {
            long authorId = chStmt.getLong("author_id");
            int days = chStmt.getInt("days_since_latest");
            UserActivityData uad = getUadOrError(authorId);
            if (uad.isIgnoreSystemUser()) {
               continue;
            }
            if (uad.isValid()) {
               uad.addUsageType(UserUsageType.AUTHOR_TX_ENTRY, days);
            } else {
               INVALID_TXS_AUTHORS.add(authorId);
            }
         }
      } catch (OseeCoreException ex) {
         results.errorf("Transaction: Exception %s\n", Lib.exceptionToString(ex));
      } finally {
         chStmt.close();
      }
   }

   protected void ignoreSystemUser() {
      for (UserActivityData uad : getUserActivities()) {
         if (uad.isNotSet()) {
            if (SystemUser.values().contains(uad.getUserTok())) {
               validateAndSetAction(uad, UserActivityAction.Ignore_System_User);
            }
         }
      }
   }

   protected void ignoreByStaticId() {
      for (UserActivityData uad : getUserActivities()) {
         if (uad.isNotSet()) {
            for (String ignoreStaticId : getIgnoreStaticIds()) {
               if (uad.getTags().contains(ignoreStaticId)) {
                  validateAndSetAction(uad, UserActivityAction.Ignore_By_Static_Id);
               }
            }
         }
      }
   }

   protected void ignoreAlreadyInactive() {
      for (UserActivityData uad : getUserActivities()) {
         if (uad.isNotSet()) {
            if (!uad.isActive()) {
               validateAndSetAction(uad, UserActivityAction.Ignore_Already_InActive_In_OSEE);
            }
         }
      }
   }

   protected boolean testForDuplicates() {
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
         if (!uad.isIgnoreDuplicateNames() && nameMap.containsKey(userTok.getName())) {
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

   private void setInactiveCauseLeftCompany() {
      List<String> userIds = new ArrayList<>();
      for (UserActivityData uad : getUserActivities()) {
         if (uad.isActive()) {
            String userId = uad.getUserTok().getUserId();
            if (StringUtils.isNumeric(userId)) {
               userIds.add(userId);
            }
         }
      }
      int subDivideCount = 100;
      List<Collection<String>> subDivide = Collections.subDivide(userIds, subDivideCount);
      int x = 1, total = subDivide.size();
      Map<String, AtsUser> userIdToWssoUser = new HashMap<>(userIds.size());
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

         for (AtsUser wssoUser : usersByUserIds) {
            userIdToWssoUser.put(wssoUser.getUserId(), wssoUser);
         }
      }

      for (UserActivityData uad : getUserActivities()) {
         if (uad.isNotSet()) {
            UserToken user = uad.getUserTok();
            if (user.isActive()) {
               // Where wssoUser is record from company database
               AtsUser wssoUser = userIdToWssoUser.get(user.getUserId());
               boolean setInActive = false;
               // Query may return invalid record regardless w/ Active=N, so, check that name is valid
               if (wssoRecordIsValid(uad, wssoUser)) {
                  uad.setWssoUser(wssoUser);
                  if (user.isActive()) {
                     // Record came back but WssoUser = InActive and OSEE User == Active, so set inactive
                     if (!wssoUser.isActive()) {
                        validateAndSetAction(uad, UserActivityAction.Set_Inactive_Cause_Left_Company_Record_Inactive);
                        setInActive = true;
                     }
                  }
               }
               if (setInActive) {
                  uad.setDeactivating(true);
                  if (persist) {
                     changes.setSoleAttributeValue(getUser(user), CoreAttributeTypes.Active, false);
                     results.logf("Fixed Active to false\n");
                  }
               }
            }
         }
      }
   }

   protected boolean wssoRecordIsValid(UserActivityData uad, AtsUser wssoUser) {
      return wssoUser != null && Strings.isValid(wssoUser.getName());
   }

   private void setInactiveOrNotifyCauseHaveNotAccessed() {
      for (UserActivityData uad : getUserActivities()) {
         if (uad.isNotSet() && uad.isActive()) {
            UserToken user = uad.getUserTok();
            // Handle users who still active but have not accessed OSEE in xxx days
            UserUsageStatus daysSinceLastUseStatus = uad.getEarliestActivityStatus();
            int daysSinceLastUse = daysSinceLastUseStatus.getDaysSince();

            if (daysSinceLastUse == -1 || daysSinceLastUse > getDaysTillInActive()) {
               disableUserAccount(uad, user, daysSinceLastUse);
            } else if (daysSinceLastUse > getDaysTillLastInActiveNotice()) {
               sendInactivityNotification(uad, user, daysSinceLastUse, changes, false);
            } else if (daysSinceLastUse > getDaysTillFirstInActiveNotice()) {
               sendInactivityNotification(uad, user, daysSinceLastUse, changes, true);
            } else {
               validateAndSetAction(uad, UserActivityAction.Ignore_Active_Cause_Recent_Use_Or_Reactivated);
            }
         }
      }
   }

   // For subclass implementation
   protected void updateUserGroups() {

      List<IUserGroupArtifactToken> userGroups = new ArrayList<>();
      userGroups.add(CoreUserGroups.Everyone);
      userGroups.addAll(getAdditionalUserGroups());

      Map<IUserGroupArtifactToken, Collection<UserToken>> tokToMembers = new HashMap<>();
      Map<IUserGroupArtifactToken, IUserGroup> tokToUserGroup = new HashMap<>();
      for (IUserGroupArtifactToken userGroupTok : userGroups) {
         IUserGroup userGroup = atsApi.userService().getUserGroup(userGroupTok);
         Collection<UserToken> users = userGroup.getMembers();
         tokToMembers.put(userGroupTok, users);
         tokToUserGroup.put(userGroupTok, userGroup);
      }

      for (UserActivityData uad : getUserActivities()) {
         if (!uad.isDeactivating() && !uad.isIgnoreByStaticId() && !uad.isIgnoreSystemUser() //
            && (uad.isActive())) {
            UserToken user = uad.getUserTok();
            for (IUserGroupArtifactToken tok : userGroups) {
               if (!tokToMembers.get(tok).contains(user)) {
                  results.warningf(" %s not in [%s] group.\n", user.toStringWithId(), tok.getName());
                  if (persist) {
                     addMember(tokToUserGroup.get(tok), user);
                     results.log("Fixed - Added");
                  }
               }
            }
         }
      }
   }

   protected Collection<IUserGroupArtifactToken> getAdditionalUserGroups() {
      return java.util.Collections.emptyList();
   }

   protected void updateUserAttributes() {

      for (UserActivityData uad : getUserActivities()) {
         if (!uad.isDeactivating() && !uad.isIgnoreByStaticId() && !uad.isIgnoreSystemUser() //
            && (uad.isActive())) {
            UserToken user = uad.getUserTok();
            AtsUser wssoUser = getWssoUser(uad);
            if (wssoUser != null) {
               String wssoUserId = wssoUser.getUserId();
               String wssoLoginId = wssoUser.getLoginIds().size() == 0 ? "" : wssoUser.getLoginIds().get(0);
               String wssoUserName = wssoUser.getName();
               String wssoMail = wssoUser.getEmail();

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
                     if (!uad.getTags().contains(NO_EMAIL_STATIC_ID)) {
                        if (debug) {
                           results.warningf("[%s] WSSO User Email [%s] is invalid: %s\n", user.toStringWithId(),
                              wssoMail, wssoUser.getJson());
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

               if (!uad.isIgnoreNameUpdate() && !wssoUserName.equals(getUser(user).getName())) {
                  results.warningf("(On Persist) - [%s] wsso.name [%s] != user.name [%s]\n", user.toStringWithId(),
                     wssoUserName, user.getName());
                  if (persist) {
                     changes.setName(getUser(user), wssoUserName);
                     results.warningf("Fixed Name to wsso.name\n", wssoUserName);
                  }
               }

               if (!wssoUserId.equals(user.getUserId())) {
                  if (!user.getUserId().matches("\\d{1,9}")) {
                     results.errorf("[%s] wsso.userid [%s] invalid\n", user.toStringWithId(), wssoUserId);
                  } else {
                     results.warningf("(On Persist) - [%s] wsso.userid [%s] != user.userId [%s]\n", wssoUserId,
                        user.getUserId(), user.toStringWithId());
                     if (persist) {
                        changes.setSoleAttributeValue(getUser(user), CoreAttributeTypes.UserId, wssoUserId);
                        results.logf("Fixed UserId to %s\n", wssoUserId);
                     }
                  }
               }

               if (!user.getName().contains(",") && debug) {
                  results.warningf("[%s] name doesn't contain last, first\n", user);
               }
            }
         }
      }
   }

   private void updateNotificationsTags() {
      for (UserActivityData uad : getUserActivities()) {
         UserToken user = uad.getUserTok();
         if (user.isActive()) {
            // Handle users who were inactive but toggled back to active and have used
            UserUsageStatus daysSinceLastUse = uad.getEarliestActivityStatus();
            if (daysSinceLastUse.getDaysSince() > 0 && daysSinceLastUse.getDaysSince() < getDaysTillFirstInActiveNotice()) {
               if (uad.isFirstNotifySent()) {
                  results.warningf(
                     "(On Persist) - Active user %s found with [%s] and First Notification flag, remove\n",
                     uad.getUserTok().toStringWithId(), daysSinceLastUse);
                  if (persist) {
                     changes.deleteAttribute(getUser(user), CoreAttributeTypes.StaticId, FIRST_NOTIFICATION_STATIC_ID);
                     results.logf("Fixed - First Notification Flag Cleared\n");
                  }
               }
               if (uad.isSecondNotifySent()) {
                  results.warningf(
                     "(On Persist) - Active user %s found with [%s] and Second Notification flag, remove\n",
                     uad.getUserTok().toStringWithId(), daysSinceLastUse);
                  if (persist) {
                     changes.deleteAttribute(getUser(user), CoreAttributeTypes.StaticId, SECOND_NOTIFICATION_STATIC_ID);
                     results.logf("Fixed - Second Notification Flag Cleared\n");
                  }
               }
            }
         }
      }
   }

   private void validateAndSetAction(UserActivityData uad, UserActivityAction action) {
      UserActivityAction actionNeeded = uad.getActionNeeded();
      if (!actionNeeded.equals(UserActivityAction.Not_Set_Fix_This)) {
         results.errorf("UAD Action Neeeded %s Already Set to [%s]; Attempting to re-set to [%s]\n",
            uad.getUserTok().toStringWithId(), actionNeeded, action);
      }
      uad.setActionNeeded(action);
   }

   protected void printUads() {
      List<UserActivityAction> actions = Collections.castAll(UserActivityAction.Not_Set_Fix_This.values());
      for (UserActivityAction action : actions) {
         results.logf("\n=======================================================================" //
            + "\n<b>%s</b>: %s\nDescription: %s\n\n", AHTML.color("DARKBLUE", "Action Group"), action.name(),
            action.getDescription());
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
            results.addRaw(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("User", "Active", DAYS_SINCE_USE, //
               DAYS_SINCE_IDE, DAYS_SINCE_TXS, DAYS_SINCE_SET_ACTIVE, "Action", "First Notify", //
               "Second Notify")));
            for (UserActivityData uad : getUserActivities()) {
               if (uad.getActionNeeded().equals(action)) {
                  int daysSinceUse = uad.getEarliestActivityStatusDaysSince();
                  String daysSinceIdeUse = "Not Found";
                  String daysSinceSetActive = "Not Set";
                  String daysSinceTxAuthored = "Not Found";
                  for (UserUsageStatus uas : uad.getUsageStatus()) {
                     if (uas.getActType().equals(UserUsageType.USER_REACTIVATED) && uas.getDaysSince() > -1) {
                        daysSinceSetActive = String.valueOf(uas.getDaysSince());
                     }
                     if (uas.getActType().equals(UserUsageType.AUTHOR_TX_ENTRY) && uas.getDaysSince() > -1) {
                        daysSinceTxAuthored = String.valueOf(uas.getDaysSince());
                     }
                     if (uas.getActType().equals(UserUsageType.IDE_CLIENT_USE) && uas.getDaysSince() > -1) {
                        daysSinceIdeUse = String.valueOf(uas.getDaysSince());
                     }
                  }
                  results.addRaw(AHTML.addRowMultiColumnTable( //
                     uad.getUserTok().toStringWithId(), //
                     String.valueOf(uad.isActive()), //
                     String.valueOf(daysSinceUse), //
                     String.valueOf(daysSinceIdeUse), //
                     String.valueOf(daysSinceTxAuthored), //
                     String.valueOf(daysSinceSetActive), //
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

   private void printActiveWithDept() {
      List<String> userUse = new ArrayList<>();
      for (UserActivityData uad : getUserActivities()) {
         if (uad.getWssoUser() != null && Strings.isValid(uad.getWssoUser().getManager())) {
            int daysSinceUse = uad.getEarliestActivityStatusDaysSince();
            String dept = uad.getWssoUser().getDept();
            if (Strings.isInvalid(dept)) {
               dept = "No Dept";
            }
            if (daysSinceUse >= 0 && daysSinceUse < 31) {
               userUse.add(String.format("%s - %s - %s - %s", dept, uad.getWssoUser().getManager(),
                  uad.getUserTok().getName(), daysSinceUse));
            }
         }
      }
      results.log("\n<b>Active last 30 days</b><br/>Dept - Manager - User - DaysSinceUse\n");
      java.util.Collections.sort(userUse);
      for (String use : userUse) {
         results.log(use);
      }
   }

   private void saveResultsToFile(String html) {
      String serverData = System.getProperty("osee.application.server.data");
      if (!Strings.isValid(serverData)) {
         serverData = System.getProperty("user.home");
      }
      String outputDirName = serverData + File.separator + "userSync";
      File outDir = new File(outputDirName);
      outDir.mkdir();

      String outputFileName = String.format("%s%s%s_%s.html", //
         outputDirName, //
         File.separator, //
         Lib.getDateTimeString(), //
         (tx.isValid() ? "persist_" + tx.getIdString() : "report_only"));
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
      validateAndSetAction(uad, UserActivityAction.Set_Inactive_Cause_Unused);
      uad.setDeactivating(true);
      if (persist) {
         changes.setSoleAttributeValue(getUser(user), CoreAttributeTypes.Active, false);
         results.logf("Fixed - Account Disabled %s\n", user.toStringWithId());
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
         validateAndSetAction(uad, UserActivityAction.Send_First_Unused_Notification);
      } else {
         validateAndSetAction(uad, UserActivityAction.Send_Second_Unused_Notification);
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
      UserActivityData uad = userArtIdToUserAct.get(userArtId);
      if (uad == null) {
         return UserActivityData.SENTINEL;
      }
      return uad;
   }

   protected void addMember(IUserGroup everyoneGroup, UserToken user) {
      // TBD fix this to use loaded art from asArtifacts and not everyoneGroup?
      //      changes.relate(everyoneGroup.getArtifact(), CoreRelationTypes.Users_User, user);
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
      UserActivityData uad = userArtIdToUserAct.get(userTok.getArtifactId());
      if (uad != null) {
         userArt = uad.getUserArt();
      }
      Conditions.assertNotSentinel(userArt);
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

   protected Collection<UserActivityData> getUserActivities() {
      return sortedUads;
   }

}
