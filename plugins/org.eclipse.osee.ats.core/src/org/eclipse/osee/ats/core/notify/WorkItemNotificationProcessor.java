/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.core.notify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.notify.AtsWorkItemNotificationEvent;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.ReviewRoleType;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.users.AtsUsersUtility;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class WorkItemNotificationProcessor {

   private final Log logger;
   private final IAtsUserService userService;
   private final IAttributeResolver attrResolver;
   private final AtsApi atsApi;

   public WorkItemNotificationProcessor(AtsApi atsApi) {
      this.logger = atsApi.getLogger();
      this.atsApi = atsApi;
      this.userService = atsApi.getUserService();
      this.attrResolver = atsApi.getAttributeResolver();
   }

   /**
    * @param notifyUsers only valid for assignees notifyType. if null or any other type, the users will be computed
    */
   public void run(AtsNotificationCollector notifications, AtsWorkItemNotificationEvent event) {
      Collection<AtsNotifyType> types = event.getNotifyTypes();
      List<AtsUser> notifyUsers = new ArrayList<>();
      AtsUser fromUser = AtsCoreUsers.SYSTEM_USER;
      if (Strings.isValid(event.getFromUserId())) {
         fromUser = userService.getUserByUserId(event.getFromUserId());
      }
      for (String userId : event.getUserIds()) {
         notifyUsers.add(userService.getUserByUserId(userId));
      }
      for (Long id : event.getIds()) {
         IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(id);

         if (workItem == null) {
            logger.error(
               "In WorkItemNotificationProcessor.run, the local variable \\\"workItem\\\" is null which is dereferenced");
            continue;
         }

         if (types.contains(AtsNotifyType.Originator)) {
            try {
               AtsUser originator = workItem.getCreatedBy();
               if (originator != null && originator.isActive()) {
                  if (!EmailUtil.isEmailValid(originator.getEmail()) && !AtsCoreUsers.isAtsCoreUser(originator)) {
                     logger.info("Email [%s] invalid for user [%s]", originator.getEmail(), originator.getName());
                  } else if (fromUser.notEqual(originator)) {
                     String cancelUrl = getCancelUrl(notifications, workItem, atsApi);
                     String url = getUrl(workItem, atsApi);
                     notifications.addNotificationEvent(
                        AtsNotificationEventFactory.getNotificationEvent(getFromUser(event), Arrays.asList(originator),
                           getIdString(workItem), AtsNotifyType.Originator.name(), url, cancelUrl,
                           String.format("You have been set as the originator of [%s] state [%s] titled [%s]",
                              workItem.getArtifactTypeName(), workItem.getCurrentStateName(), workItem.getName())));
                  }
               }
            } catch (OseeCoreException ex) {
               logger.error(ex, "Error processing Originator for workItem [%s] and event [%s]",
                  workItem.toStringWithId(), event.toString());
            }
         }
         if (types.contains(AtsNotifyType.Assigned)) {
            try {
               Collection<AtsUser> assignees = new HashSet<>();
               if (!notifyUsers.isEmpty()) {
                  assignees.addAll(notifyUsers);
               } else {
                  assignees.addAll(workItem.getAssignees());
               }
               assignees.remove(fromUser);
               assignees = AtsUsersUtility.getValidEmailUsers(assignees);
               assignees = AtsUsersUtility.getActiveEmailUsers(assignees);
               if (assignees.size() > 0) {
                  notifications.addNotificationEvent(AtsNotificationEventFactory.getNotificationEvent(
                     getFromUser(event), assignees, getIdString(workItem), AtsNotifyType.Assigned.name(),
                     getUrl(workItem, atsApi), getCancelUrl(notifications, workItem, atsApi),
                     String.format("You have been set as the assignee of [%s] in state [%s] titled [%s]",
                        workItem.getArtifactTypeName(), workItem.getCurrentStateName(), workItem.getName())));
               }
            } catch (OseeCoreException ex) {
               logger.error(ex, "Error processing Assigned for workItem [%s] and event [%s]", workItem.toStringWithId(),
                  event.toString());
            }
         }
         if (types.contains(AtsNotifyType.Subscribed)) {
            try {
               Collection<AtsUser> subscribed = new HashSet<>();
               subscribed.addAll(getSubscribed(workItem));
               subscribed = AtsUsersUtility.getValidEmailUsers(subscribed);
               subscribed = AtsUsersUtility.getActiveEmailUsers(subscribed);
               if (subscribed.size() > 0) {
                  notifications.addNotificationEvent(AtsNotificationEventFactory.getNotificationEvent(
                     getFromUser(event), subscribed, getIdString(workItem), AtsNotifyType.Subscribed.name(),
                     getUrl(workItem, atsApi), getCancelUrl(notifications, workItem, atsApi),
                     String.format("[%s] titled [%s] transitioned to [%s] and you subscribed for notification.",
                        workItem.getArtifactTypeName(), workItem.getName(), workItem.getCurrentStateName())));
               }
            } catch (OseeCoreException ex) {
               logger.error(ex, "Error processing Subscribed for workItem [%s] and event [%s]",
                  workItem.toStringWithId(), event.toString());
            }
         }
         try {
            StateDefinition stateDefinition = workItem.getStateDefinition();
            StateType stateType = stateDefinition.getStateType();
            boolean notificationTypeIsCompletedOrCancelled =
               types.contains(AtsNotifyType.Cancelled) || types.contains(AtsNotifyType.Completed);
            boolean stateTypeIsCompletedOrCancelled = stateType.isCompleted() || stateType.isCancelled();
            if (notificationTypeIsCompletedOrCancelled && !workItem.isTask() && stateTypeIsCompletedOrCancelled) {
               AtsUser originator = workItem.getCreatedBy();
               if (originator.isActive()) {
                  if (!EmailUtil.isEmailValid(originator.getEmail())) {
                     logger.info("Email [%s] invalid for user [%s]", originator.getEmail(), originator.getName());
                  } else if (isOriginatorDifferentThanCancelledOrCompletedBy(workItem, fromUser, originator)) {
                     if (stateType.isCompleted()) {
                        notifications.addNotificationEvent(AtsNotificationEventFactory.getNotificationEvent(
                           getFromUser(event), Arrays.asList(originator), getIdString(workItem),
                           workItem.getCurrentStateName(), getUrl(workItem, atsApi),
                           getCancelUrl(notifications, workItem, atsApi), String.format("[%s] titled [%s] is [%s]",
                              workItem.getArtifactTypeName(), workItem.getName(), workItem.getCurrentStateName())));
                     }
                     if (stateType.isCancelled()) {
                        notifications.addNotificationEvent(
                           AtsNotificationEventFactory.getNotificationEvent(getFromUser(event),
                              Arrays.asList(originator), getIdString(workItem), workItem.getCurrentStateName(),
                              getUrl(workItem, atsApi), getCancelUrl(notifications, workItem, atsApi),
                              String.format("[%s] titled [%s] was [%s] from the [%s] state on [%s].<br>Reason: [%s]",
                                 workItem.getArtifactTypeName(), workItem.getName(), workItem.getCurrentStateName(),
                                 workItem.getCancelledFromState(), DateUtil.getMMDDYYHHMM(workItem.getCancelledDate()),
                                 workItem.getCancelledReason())));
                     }
                  }
               }
            }
         } catch (Exception ex) {
            logger.error(ex, "Error processing Completed or Cancelled for workItem [%s] and event [%s]",
               workItem.toStringWithId(), event.toString());
         }
         if (types.contains(AtsNotifyType.Peer_Reviewers_Completed) && workItem instanceof IAtsPeerToPeerReview) {
            try {
               IAtsPeerReviewRoleManager roleMgr = ((IAtsPeerToPeerReview) workItem).getRoleManager();
               Collection<AtsUser> authorModerator = new ArrayList<>();
               for (UserRole role : roleMgr.getUserRoles()) {
                  if (role.getRole().getReviewRoleType().matches(ReviewRoleType.Author, ReviewRoleType.Moderator)) {
                     authorModerator.add(userService.getUserByUserId(role.getUserId()));
                  }
               }
               authorModerator = AtsUsersUtility.getValidEmailUsers(authorModerator);
               authorModerator = AtsUsersUtility.getActiveEmailUsers(authorModerator);
               if (authorModerator.size() > 0) {
                  notifications.addNotificationEvent(
                     AtsNotificationEventFactory.getNotificationEvent(getFromUser(event), authorModerator,
                        getIdString(workItem), AtsNotifyType.Peer_Reviewers_Completed.name(),
                        String.format(
                           "You are Author/Moderator of [%s] titled [%s] which has been reviewed by all reviewers",
                           workItem.getArtifactTypeName(), workItem.getName())));
               }
            } catch (OseeCoreException ex) {
               logger.error(ex, "Error processing Peer_Reviewers_Completed for workItem [%s] and event [%s]",
                  workItem.toStringWithId(), event.toString());
            }
         }
         if (types.contains(AtsNotifyType.SubscribedTeamOrAi)) {
            try {
               if (workItem.isTeamWorkflow()) {
                  IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
                  Collection<AtsUser> subscribedUsers = new HashSet<>();
                  // Handle Team Definitions
                  IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
                  subscribedUsers.addAll(atsApi.getTeamDefinitionService().getSubscribed(teamDef));
                  if (subscribedUsers.size() > 0) {
                     notifications.addNotificationEvent(AtsNotificationEventFactory.getNotificationEvent(
                        AtsCoreUsers.SYSTEM_USER, subscribedUsers, getIdString(teamWf), "Workflow Creation",
                        getUrl(workItem, atsApi), getCancelUrl(notifications, workItem, atsApi),
                        "You have subscribed for email notification for Team \"" + teamWf.getTeamDefinition().getName() + "\"; New Team Workflow created with title \"" + teamWf.getName() + "\""));
                  }

                  // Handle Actionable Items
                  for (IAtsActionableItem aia : teamWf.getActionableItems()) {
                     subscribedUsers = atsApi.getActionableItemService().getSubscribed(aia);
                     if (subscribedUsers.size() > 0) {
                        notifications.addNotificationEvent(AtsNotificationEventFactory.getNotificationEvent(
                           AtsCoreUsers.SYSTEM_USER, subscribedUsers, getIdString(teamWf), "Workflow Creation",
                           getUrl(workItem, atsApi), getCancelUrl(notifications, workItem, atsApi),
                           "You have subscribed for email notification for Actionable Item \"" + teamWf.getTeamDefinition().getName() + "\"; New Team Workflow created with title \"" + teamWf.getName() + "\""));
                     }
                  }
               }
            } catch (OseeCoreException ex) {
               logger.error(ex, "Error processing SubscribedTeamOrAi for workItem [%s] and event [%s]",
                  workItem.toStringWithId(), event.toString());
            }
         }
      }
   }

   public List<AtsUser> getSubscribed(IAtsWorkItem workItem) {
      ArrayList<AtsUser> arts = new ArrayList<>();
      for (ArtifactId art : atsApi.getRelationResolver().getRelated(workItem.getStoreObject(),
         AtsRelationTypes.SubscribedUser_User)) {
         arts.add(userService.getUserByUserId(
            (String) atsApi.getAttributeResolver().getSoleAttributeValue(art, CoreAttributeTypes.UserId, null)));
      }
      return arts;
   }

   private boolean isOriginatorDifferentThanCancelledOrCompletedBy(IAtsWorkItem workItem, AtsUser fromUser,
      AtsUser originator) {
      boolean different = true;
      if (fromUser.equals(originator)) {
         different = false;
      } else if (workItem.getCancelledBy() != null && originator.equals(workItem.getCancelledBy())) {
         different = false;
      } else if (workItem.getCompletedBy() != null && originator.equals(workItem.getCompletedBy())) {
         different = false;
      }
      return different;
   }

   private String getCancelUrl(AtsNotificationCollector notifications, IAtsWorkItem workItem, AtsApi atsApi) {
      if (notifications.isIncludeCancelHyperlink()) {
         return atsApi.getWorkItemService().getCancelUrl(workItem, atsApi);
      }
      return "";
   }

   private String getUrl(IAtsWorkItem workItem, AtsApi atsApi) {
      String url = atsApi.getWorkItemService().getHtmlUrl(workItem, atsApi);
      if (Strings.isInValid(url)) {
         return "Not Configured";
      }
      return url;
   }

   private AtsUser getFromUser(AtsWorkItemNotificationEvent event) {
      AtsUser fromUser = AtsCoreUsers.SYSTEM_USER;
      if (Strings.isValid(event.getFromUserId())) {
         fromUser = userService.getUserByUserId(event.getFromUserId());
      }
      return fromUser;
   }

   private String getIdString(IAtsWorkItem workItem) {
      try {
         String legacyPcrId = attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.LegacyPcrId, "");
         if (!legacyPcrId.equals("")) {
            return "ID: " + workItem.getAtsId() + " / LegacyId: " + legacyPcrId;
         }
      } catch (Exception ex) {
         logger.error(ex, "Error getting legacyId pcr for workItem [%s]", workItem);
      }
      return "ID: " + workItem.getAtsId();
   }

}
