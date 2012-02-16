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
package org.eclipse.osee.ats.core.client.notify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.role.Role;
import org.eclipse.osee.ats.core.client.review.role.UserRole;
import org.eclipse.osee.ats.core.client.review.role.UserRoleManager;
import org.eclipse.osee.ats.core.client.util.SubscribeManager;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.utility.EmailUtil;
import org.eclipse.osee.framework.skynet.core.utility.INotificationManager;
import org.eclipse.osee.framework.skynet.core.utility.OseeNotificationEvent;

/**
 * @author Donald G. Dunne
 */
public class AtsNotifyUsers {

   protected static void notify(INotificationManager oseeNotificationManager, AbstractWorkflowArtifact sma, AtsNotifyType... notifyTypes) throws OseeCoreException {
      notify(oseeNotificationManager, sma, null, notifyTypes);
   }

   /**
    * @param notifyUsers only valid for assignees notifyType. if null or any other type, the users will be computed
    */
   protected static void notify(INotificationManager oseeNotificationManager, AbstractWorkflowArtifact awa, Collection<IBasicUser> notifyUsers, AtsNotifyType... notifyTypes) throws OseeCoreException {
      List<AtsNotifyType> types = Collections.getAggregate(notifyTypes);

      if (types.contains(AtsNotifyType.Originator)) {
         IBasicUser originator = awa.getCreatedBy();
         if (originator.isActive()) {
            if (!EmailUtil.isEmailValid(originator) && !UserManager.isSystemUser(originator)) {
               OseeLog.logf(Activator.class, Level.INFO, "Email [%s] invalid for user [%s]",
                  UserManager.getUser(originator).getEmail(), originator.getName());
            } else if (!UserManager.getUser().equals(originator)) {
               oseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(Arrays.asList(originator),
                  AtsNotificationManager.getIdString(awa), AtsNotifyType.Originator.name(), String.format(
                     "You have been set as the originator of [%s] state [%s] titled [%s]", awa.getArtifactTypeName(),
                     awa.getStateMgr().getCurrentStateName(), awa.getName())));
            }
         }
      }
      if (types.contains(AtsNotifyType.Assigned)) {
         Collection<IBasicUser> assignees = new HashSet<IBasicUser>();
         if (notifyUsers != null) {
            assignees.addAll(notifyUsers);
         } else {
            assignees.addAll(awa.getStateMgr().getAssignees());
         }
         assignees.remove(UserManager.getUser());
         assignees = EmailUtil.getValidEmailUsers(assignees);
         assignees = EmailUtil.getActiveEmailUsers(assignees);
         if (assignees.size() > 0) {
            oseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(assignees,
               AtsNotificationManager.getIdString(awa), AtsNotifyType.Assigned.name(), String.format(
                  "You have been set as the assignee of [%s] in state [%s] titled [%s]", awa.getArtifactTypeName(),
                  awa.getStateMgr().getCurrentStateName(), awa.getName())));
         }
      }
      if (types.contains(AtsNotifyType.Subscribed)) {
         Collection<IBasicUser> subscribed = SubscribeManager.getSubscribed(awa);
         subscribed = EmailUtil.getValidEmailUsers(subscribed);
         subscribed = EmailUtil.getActiveEmailUsers(subscribed);
         if (subscribed.size() > 0) {
            oseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(subscribed,
               AtsNotificationManager.getIdString(awa), AtsNotifyType.Subscribed.name(), String.format(
                  "[%s] titled [%s] transitioned to [%s] and you subscribed for notification.",
                  awa.getArtifactTypeName(), awa.getName(), awa.getStateMgr().getCurrentStateName())));
         }
      }
      if (types.contains(AtsNotifyType.Cancelled) || types.contains(AtsNotifyType.Completed) && (!awa.isTask() && (awa.isCompleted() || awa.isCancelled()))) {
         IBasicUser originator = awa.getCreatedBy();
         if (originator.isActive()) {
            if (!EmailUtil.isEmailValid(originator)) {
               OseeLog.logf(Activator.class, Level.INFO, "Email [%s] invalid for user [%s]",
                  UserManager.getUser(originator).getEmail(), originator.getName());
            } else if (!UserManager.getUser().equals(originator)) {
               if (awa.isCompleted()) {
                  oseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(Arrays.asList(originator),
                     AtsNotificationManager.getIdString(awa), awa.getCurrentStateName(),
                     String.format("[%s] titled [%s] is [%s]", awa.getArtifactTypeName(), awa.getName(),
                        awa.getCurrentStateName())));
               }
               if (awa.isCancelled()) {
                  oseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(Arrays.asList(originator),
                     AtsNotificationManager.getIdString(awa), awa.getCurrentStateName(), String.format(
                        "[%s] titled [%s] was [%s] from the [%s] state on [%s].<br>Reason: [%s]",
                        awa.getArtifactTypeName(), awa.getName(), awa.getCurrentStateName(),
                        awa.getCancelledFromState(), DateUtil.getMMDDYYHHMM(awa.internalGetCancelledDate()),
                        awa.getCancelledReason())));
               }
            }
         }
      }
      if (types.contains(AtsNotifyType.Peer_Reviewers_Completed) && awa instanceof AbstractReviewArtifact) {
         UserRoleManager roleMgr = new UserRoleManager(awa);
         Collection<IBasicUser> authorModerator = new ArrayList<IBasicUser>();
         for (UserRole role : roleMgr.getUserRoles()) {
            if (role.getRole() == Role.Author || role.getRole() == Role.Moderator) {
               authorModerator.add(role.getUser());
            }
         }
         authorModerator = EmailUtil.getValidEmailUsers(authorModerator);
         authorModerator = EmailUtil.getActiveEmailUsers(authorModerator);
         if (authorModerator.size() > 0) {
            oseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(authorModerator,
               AtsNotificationManager.getIdString(awa), AtsNotifyType.Peer_Reviewers_Completed.name(), String.format(
                  "You are Author/Moderator of [%s] titled [%s] which has been reviewed by all reviewers",
                  awa.getArtifactTypeName(), awa.getName())));
         }
      }
   }

}
