/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.notify;

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
import org.eclipse.osee.ats.api.review.Role;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
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

   public WorkItemNotificationProcessor(Log logger, AtsApi atsApi, IAtsUserService userService, IAttributeResolver attrResolver) {
      this.logger = logger;
      this.atsApi = atsApi;
      this.userService = userService;
      this.attrResolver = attrResolver;
   }

   /**
    * @param notifyUsers only valid for assignees notifyType. if null or any other type, the users will be computed
    */
   public void run(AtsNotificationCollector notifications, AtsWorkItemNotificationEvent event) {
      Collection<AtsNotifyType> types = event.getNotifyTypes();
      List<IAtsUser> notifyUsers = new ArrayList<>();
      IAtsUser fromUser = AtsCoreUsers.SYSTEM_USER;
      if (Strings.isValid(event.getFromUserId())) {
         fromUser = userService.getUserById(event.getFromUserId());
      }
      for (String userId : event.getUserIds()) {
         notifyUsers.add(userService.getUserById(userId));
      }
      for (String atsId : event.getAtsIds()) {
         IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAtsId(atsId);

         if (types.contains(AtsNotifyType.Originator)) {
            try {
               IAtsUser originator = workItem.getCreatedBy();
               if (originator.isActive()) {
                  if (!EmailUtil.isEmailValid(originator.getEmail()) && !AtsCoreUsers.isAtsCoreUser(originator)) {
                     logger.info("Email [%s] invalid for user [%s]", originator.getEmail(), originator.getName());
                  } else if (fromUser.notEqual(originator)) {
                     String cancelUrl = getCancelUrl(notifications, workItem, atsApi);
                     String url = getUrl(workItem, atsApi);
                     notifications.addNotificationEvent(
                        AtsNotificationEventFactory.getNotificationEvent(getFromUser(event), Arrays.asList(originator),
                           getIdString(workItem), AtsNotifyType.Originator.name(), url, cancelUrl,
                           String.format("You have been set as the originator of [%s] state [%s] titled [%s]",
                              workItem.getArtifactTypeName(), workItem.getStateMgr().getCurrentStateName(),
                              workItem.getName())));
                  }
               }
            } catch (OseeCoreException ex) {
               logger.error(ex, "Error processing Originator for workItem [%s] and event [%s]",
                  workItem.toStringWithId(), event.toString());
            }
         }
         if (types.contains(AtsNotifyType.Assigned)) {
            try {
               Collection<IAtsUser> assignees = new HashSet<>();
               if (!notifyUsers.isEmpty()) {
                  assignees.addAll(notifyUsers);
               } else {
                  assignees.addAll(workItem.getStateMgr().getAssignees());
               }
               assignees.remove(fromUser);
               assignees = AtsUsersUtility.getValidEmailUsers(assignees);
               assignees = AtsUsersUtility.getActiveEmailUsers(assignees);
               if (assignees.size() > 0) {
                  notifications.addNotificationEvent(AtsNotificationEventFactory.getNotificationEvent(
                     getFromUser(event), assignees, getIdString(workItem), AtsNotifyType.Assigned.name(),
                     getUrl(workItem, atsApi), getCancelUrl(notifications, workItem, atsApi),
                     String.format("You have been set as the assignee of [%s] in state [%s] titled [%s]",
                        workItem.getArtifactTypeName(), workItem.getStateMgr().getCurrentStateName(),
                        workItem.getName())));
               }
            } catch (OseeCoreException ex) {
               logger.error(ex, "Error processing Assigned for workItem [%s] and event [%s]", workItem.toStringWithId(),
                  event.toString());
            }
         }
         if (types.contains(AtsNotifyType.Subscribed)) {
            try {
               Collection<IAtsUser> subscribed = new HashSet<>();
               subscribed.addAll(getSubscribed(workItem));
               subscribed = AtsUsersUtility.getValidEmailUsers(subscribed);
               subscribed = AtsUsersUtility.getActiveEmailUsers(subscribed);
               if (subscribed.size() > 0) {
                  notifications.addNotificationEvent(AtsNotificationEventFactory.getNotificationEvent(
                     getFromUser(event), subscribed, getIdString(workItem), AtsNotifyType.Subscribed.name(),
                     getUrl(workItem, atsApi), getCancelUrl(notifications, workItem, atsApi),
                     String.format("[%s] titled [%s] transitioned to [%s] and you subscribed for notification.",
                        workItem.getArtifactTypeName(), workItem.getName(),
                        workItem.getStateMgr().getCurrentStateName())));
               }
            } catch (OseeCoreException ex) {
               logger.error(ex, "Error processing Subscribed for workItem [%s] and event [%s]",
                  workItem.toStringWithId(), event.toString());
            }
         }
         try {
            IAtsStateDefinition stateDefinition = workItem.getStateDefinition();
            StateType stateType = stateDefinition.getStateType();
            boolean notificationTypeIsCompletedOrCancelled =
               types.contains(AtsNotifyType.Cancelled) || types.contains(AtsNotifyType.Completed);
            boolean stateTypeIsCompletedOrCancelled = stateType.isCompleted() || stateType.isCancelled();
            if (notificationTypeIsCompletedOrCancelled && !workItem.isTask() && stateTypeIsCompletedOrCancelled) {
               IAtsUser originator = workItem.getCreatedBy();
               if (originator.isActive()) {
                  if (!EmailUtil.isEmailValid(originator.getEmail())) {
                     logger.info("Email [%s] invalid for user [%s]", originator.getEmail(), originator.getName());
                  } else if (isOriginatorDifferentThanCancelledOrCompletedBy(workItem, fromUser, originator)) {
                     if (stateType.isCompleted()) {
                        notifications.addNotificationEvent(AtsNotificationEventFactory.getNotificationEvent(
                           getFromUser(event), Arrays.asList(originator), getIdString(workItem),
                           workItem.getStateMgr().getCurrentStateName(), getUrl(workItem, atsApi),
                           getCancelUrl(notifications, workItem, atsApi),
                           String.format("[%s] titled [%s] is [%s]", workItem.getArtifactTypeName(), workItem.getName(),
                              workItem.getStateMgr().getCurrentStateName())));
                     }
                     if (stateType.isCancelled()) {
                        notifications.addNotificationEvent(AtsNotificationEventFactory.getNotificationEvent(
                           getFromUser(event), Arrays.asList(originator), getIdString(workItem),
                           workItem.getStateMgr().getCurrentStateName(), getUrl(workItem, atsApi),
                           getCancelUrl(notifications, workItem, atsApi),
                           String.format("[%s] titled [%s] was [%s] from the [%s] state on [%s].<br>Reason: [%s]",
                              workItem.getArtifactTypeName(), workItem.getName(),
                              workItem.getStateMgr().getCurrentStateName(), workItem.getCancelledFromState(),
                              DateUtil.getMMDDYYHHMM(workItem.getCancelledDate()), workItem.getCancelledReason())));
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
               Collection<IAtsUser> authorModerator = new ArrayList<>();
               for (UserRole role : roleMgr.getUserRoles()) {
                  if (role.getRole() == Role.Author || role.getRole() == Role.Moderator) {
                     authorModerator.add(userService.getUserById(role.getUserId()));
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
            if (workItem.isTeamWorkflow()) {
               IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
               try {
                  Collection<IAtsUser> subscribedUsers = new HashSet<>();
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
               } catch (OseeCoreException ex) {
                  logger.error(ex, "Error processing SubscribedTeamOrAi for workItem [%s] and event [%s]",
                     workItem.toStringWithId(), event.toString());
               }
            }
         }
      }
   }

   public List<IAtsUser> getSubscribed(IAtsWorkItem workItem) {
      ArrayList<IAtsUser> arts = new ArrayList<>();
      for (ArtifactId art : atsApi.getRelationResolver().getRelated(workItem.getStoreObject(),
         AtsRelationTypes.SubscribedUser_User)) {
         arts.add(userService.getUserById(
            (String) atsApi.getAttributeResolver().getSoleAttributeValue(art, CoreAttributeTypes.UserId, null)));
      }
      return arts;
   }

   private boolean isOriginatorDifferentThanCancelledOrCompletedBy(IAtsWorkItem workItem, IAtsUser fromUser, IAtsUser originator) {
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

   private IAtsUser getFromUser(AtsWorkItemNotificationEvent event) {
      IAtsUser fromUser = AtsCoreUsers.SYSTEM_USER;
      if (Strings.isValid(event.getFromUserId())) {
         fromUser = userService.getUserById(event.getFromUserId());
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
