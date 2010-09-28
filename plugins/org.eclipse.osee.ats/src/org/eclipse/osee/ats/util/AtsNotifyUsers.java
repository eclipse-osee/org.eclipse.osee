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
package org.eclipse.osee.ats.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.AbstractReviewArtifact;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.ui.skynet.notify.INotificationManager;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationEvent;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailUtil;

/**
 * @author Donald G. Dunne
 */
public class AtsNotifyUsers implements IArtifactEventListener {

   private static AtsNotifyUsers instance;
   private INotificationManager notificationManager;
   private boolean inTest = false;
   public static enum NotifyType {
      Subscribed,
      Cancelled,
      Completed,
      Assigned,
      Originator,
      Reviewed
   };

   public static AtsNotifyUsers getInstance() {
      if (instance == null) {
         instance = new AtsNotifyUsers(OseeNotificationManager.getInstance());
      }
      return instance;
   }

   public static void start() {
      getInstance();
   }

   private AtsNotifyUsers(INotificationManager notificationManager) {
      if (DbUtil.isDbInit()) {
         return;
      }
      OseeLog.log(AtsPlugin.class, Level.INFO, "Starting ATS Notification Handler");
      OseeEventManager.addListener(this);
      this.notificationManager = notificationManager;
   }

   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   public void notify(AbstractWorkflowArtifact sma, NotifyType... notifyTypes) throws OseeCoreException {
      notify(sma, null, notifyTypes);
   }

   /**
    * @param notifyUsers only valid for assignees notifyType. if null or any other type, the users will be computed
    */
   public void notify(AbstractWorkflowArtifact sma, Collection<User> notifyUsers, NotifyType... notifyTypes) throws OseeCoreException {
      if (!isInTest() && (!AtsUtil.isEmailEnabled() || !AtsUtil.isProductionDb() || sma.getName().startsWith("tt "))) {
         return;
      }
      List<NotifyType> types = Collections.getAggregate(notifyTypes);

      if (types.contains(NotifyType.Originator)) {
         User originator = sma.getOriginator();
         if (!EmailUtil.isEmailValid(originator)) {
            OseeLog.log(AtsPlugin.class, Level.INFO,
               String.format("Email [%s] invalid for user [%s]", originator.getEmail(), originator.getName()));
         } else if (!UserManager.getUser().equals(originator)) {
            notificationManager.addNotificationEvent(new OseeNotificationEvent(Arrays.asList(originator),
               getIdString(sma), NotifyType.Originator.name(), String.format(
                  "You have been set as the originator of [%s] state [%s] titled [%s]", sma.getArtifactTypeName(),
                  sma.getStateMgr().getCurrentStateName(), sma.getName())));
         }
      }
      if (types.contains(NotifyType.Assigned)) {
         Collection<User> assignees = notifyUsers != null ? notifyUsers : sma.getStateMgr().getAssignees();
         assignees.remove(UserManager.getUser());
         assignees = EmailUtil.getValidEmailUsers(assignees);
         if (assignees.size() > 0) {
            notificationManager.addNotificationEvent(new OseeNotificationEvent(assignees, getIdString(sma),
               NotifyType.Assigned.name(), String.format(
                  "You have been set as the assignee of [%s] in state [%s] titled [%s]", sma.getArtifactTypeName(),
                  sma.getStateMgr().getCurrentStateName(), sma.getName())));
         }
      }
      if (types.contains(NotifyType.Subscribed)) {
         Collection<User> subscribed = SubscribeManager.getSubscribed(sma);
         subscribed = EmailUtil.getValidEmailUsers(subscribed);
         if (subscribed.size() > 0) {
            notificationManager.addNotificationEvent(new OseeNotificationEvent(subscribed, getIdString(sma),
               NotifyType.Subscribed.name(), String.format(
                  "[%s] titled [%s] transitioned to [%s] and you subscribed for notification.",
                  sma.getArtifactTypeName(), sma.getName(), sma.getStateMgr().getCurrentStateName())));
         }
      }
      if (types.contains(NotifyType.Cancelled) || types.contains(NotifyType.Completed)) {
         if ((sma.isTeamWorkflow() || sma instanceof AbstractReviewArtifact) && (sma.isCompleted() || sma.isCancelled())) {
            User originator = sma.getOriginator();
            if (!EmailUtil.isEmailValid(originator)) {
               OseeLog.log(AtsPlugin.class, Level.INFO,
                  String.format("Email [%s] invalid for user [%s]", originator.getEmail(), originator.getName()));
            } else if (!UserManager.getUser().equals(originator)) {
               if (sma.isCompleted()) {
                  notificationManager.addNotificationEvent(new OseeNotificationEvent(Arrays.asList(originator),
                     getIdString(sma), NotifyType.Completed.name(), String.format("[%s] titled [%s] is Completed",
                        sma.getArtifactTypeName(), sma.getName())));
               }
               if (sma.isCancelled()) {
                  LogItem cancelledItem = sma.getLog().getStateEvent(LogType.StateCancelled);
                  notificationManager.addNotificationEvent(new OseeNotificationEvent(Arrays.asList(originator),
                     getIdString(sma), NotifyType.Cancelled.name(), String.format(
                        "[%s] titled [%s] was cancelled from the [%s] state on [%s].<br>Reason: [%s]",
                        sma.getArtifactTypeName(), sma.getName(), cancelledItem.getState(),
                        cancelledItem.getDate(DateUtil.MMDDYYHHMM), cancelledItem.getMsg())));
               }
            }
         }
      }
      if (types.contains(NotifyType.Reviewed)) {
         if (sma instanceof AbstractReviewArtifact) {
            if (((AbstractReviewArtifact) sma).getUserRoleManager() != null) {
               Collection<User> authorModerator =
                  ((AbstractReviewArtifact) sma).getUserRoleManager().getRoleUsersAuthorModerator();
               authorModerator = EmailUtil.getValidEmailUsers(authorModerator);
               if (authorModerator.size() > 0) {
                  for (UserRole role : ((AbstractReviewArtifact) sma).getUserRoleManager().getRoleUsersReviewComplete()) {
                     notificationManager.addNotificationEvent(new OseeNotificationEvent(authorModerator,
                        getIdString(sma), NotifyType.Reviewed.name(), String.format(
                           "[%s] titled [%s] has been Reviewed by [%s]", sma.getArtifactTypeName(), sma.getName(),
                           role.getUser().getName())));
                  }
               }
            }
         }
      }
   }

   private static String getIdString(AbstractWorkflowArtifact sma) {
      try {
         String legacyPcrId = sma.getWorldViewLegacyPCR();
         if (!legacyPcrId.equals("")) {
            return "HRID: " + sma.getHumanReadableId() + " / LegacyId: " + legacyPcrId;
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return "HRID: " + sma.getHumanReadableId();
   }

   public void setNotificationManager(INotificationManager notificationManager) {
      this.notificationManager = notificationManager;
   }

   public boolean isInTest() {
      return inTest;
   }

   public void setInTest(boolean inTest) {
      this.inTest = inTest;
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return Arrays.asList(OseeEventManager.getCommonBranchFilter(), AtsUtil.getTeamWorkflowArtifactTypeEventFilter());
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (DbUtil.isDbInit()) {
         return;
      }
      // Only process notifications if this client is sender
      if (sender.isRemote()) {
         return;
      }
      boolean notificationAdded = false;
      try {
         // Handle notifications for subscription by TeamDefinition and ActionableItem
         for (Artifact art : artifactEvent.getCacheArtifacts(EventModType.Added)) {
            try {
               if (art instanceof TeamWorkFlowArtifact) {
                  TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;

                  // Handle Team Definitions
                  Collection<User> subscribedUsers =
                     Collections.castAll(teamArt.getTeamDefinition().getRelatedArtifacts(
                        AtsRelationTypes.SubscribedUser_User));
                  if (subscribedUsers.size() > 0) {
                     notificationAdded = true;
                     notificationManager.addNotificationEvent(new OseeNotificationEvent(
                        subscribedUsers,
                        getIdString(teamArt),
                        "Workflow Creation",
                        "You have subscribed for email notification for Team \"" + teamArt.getTeamName() + "\"; New Team Workflow created with title \"" + teamArt.getName() + "\""));
                  }

                  // Handle Actionable Items
                  for (ActionableItemArtifact aia : teamArt.getActionableItemsDam().getActionableItems()) {
                     subscribedUsers =
                        Collections.castAll(aia.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User));
                     if (subscribedUsers.size() > 0) {
                        notificationAdded = true;
                        notificationManager.addNotificationEvent(new OseeNotificationEvent(
                           subscribedUsers,
                           getIdString(teamArt),
                           "Workflow Creation",
                           "You have subscribed for email notification for Actionable Item \"" + teamArt.getTeamName() + "\"; New Team Workflow created with title \"" + teamArt.getName() + "\""));
                     }
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      } finally {
         if (notificationAdded) {
            try {
               notificationManager.sendNotifications();
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      }
   }

}
