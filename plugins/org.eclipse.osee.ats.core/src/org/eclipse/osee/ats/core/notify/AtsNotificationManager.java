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
package org.eclipse.osee.ats.core.notify;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.utility.INotificationManager;
import org.eclipse.osee.framework.skynet.core.utility.OseeNotificationEvent;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationManager {

   private static List<IAtsNotification> atsNotificationItems;
   private static AtsNotificationManager instance;
   private static INotificationManager notificationManager;
   private static boolean inTest = false;
   private static boolean isProduction;

   private AtsNotificationManager(INotificationManager notificationManager, boolean isProduction) {
      AtsNotificationManager.isProduction = isProduction;
      AtsNotificationManager.notificationManager = notificationManager;
      ExtensionDefinedObjects<IAtsNotification> objects =
         new ExtensionDefinedObjects<IAtsNotification>("org.eclipse.osee.ats.AtsNotification", "AtsNotification",
            "classname", true);
      atsNotificationItems = objects.getObjects();
      OseeLog.log(Activator.class, Level.INFO, "Starting ATS Notification Handler");
   }

   public static void start(INotificationManager oseeNotificationManager, boolean isProduction) {
      instance = new AtsNotificationManager(oseeNotificationManager, isProduction);
   }

   public static AtsNotificationManager getInstafnce() {
      return instance;
   }

   /**
    * Handle notifications for subscription by TeamDefinition and ActionableItem
    */
   public static void notifySubscribedByTeamOrActionableItem(TeamWorkFlowArtifact teamArt) {
      if (inTest || !AtsUtilCore.isEmailEnabled() || !isProduction) {
         return;
      }
      boolean notificationAdded = false;
      try {
         // Handle Team Definitions
         Collection<IBasicUser> subscribedUsers =
            Collections.castAll(teamArt.getTeamDefinition().getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User));
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
            subscribedUsers = Collections.castAll(aia.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User));
            if (subscribedUsers.size() > 0) {
               notificationAdded = true;
               notificationManager.addNotificationEvent(new OseeNotificationEvent(
                  subscribedUsers,
                  getIdString(teamArt),
                  "Workflow Creation",
                  "You have subscribed for email notification for Actionable Item \"" + teamArt.getTeamName() + "\"; New Team Workflow created with title \"" + teamArt.getName() + "\""));
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      } finally {
         if (notificationAdded) {
            try {
               notificationManager.sendNotifications();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
   }

   public static List<IAtsNotification> getAtsNotificationItems() {
      return atsNotificationItems;
   }

   protected static String getIdString(AbstractWorkflowArtifact sma) {
      try {
         String legacyPcrId = sma.getSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, "");
         if (!legacyPcrId.equals("")) {
            return "HRID: " + sma.getHumanReadableId() + " / LegacyId: " + legacyPcrId;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return "HRID: " + sma.getHumanReadableId();
   }

   protected static boolean isInTest() {
      return inTest;
   }

   protected static void setInTest(boolean inTest) {
      AtsNotificationManager.inTest = inTest;
   }

   protected static void setNotificationManager(INotificationManager notificationManager) {
      AtsNotificationManager.notificationManager = notificationManager;
   }

   public static void notify(AbstractWorkflowArtifact sma, AtsNotifyType... notifyTypes) throws OseeCoreException {
      notify(sma, null, notifyTypes);
   }

   public static void notify(AbstractWorkflowArtifact awa, Collection<IBasicUser> notifyUsers, AtsNotifyType... notifyTypes) throws OseeCoreException {
      if (inTest || !AtsUtilCore.isEmailEnabled() || !isProduction || awa.getName().startsWith("tt ")) {
         return;
      }
      AtsNotifyUsers.notify(notificationManager, awa, notifyUsers, notifyTypes);
   }

   public static void setIsProduction(boolean isProduction) {
      AtsNotificationManager.isProduction = isProduction;
   }

}
