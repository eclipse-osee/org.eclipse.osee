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
package org.eclipse.osee.ats.notify;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class AtsNotification {

   private static Set<IAtsNotification> atsNotificationItems = new HashSet<IAtsNotification>();
   private static AtsNotification instance = new AtsNotification();
   private static Map<String, Collection<User>> preSaveStateAssignees = new HashMap<String, Collection<User>>();
   private static Map<String, User> preSaveOriginator = new HashMap<String, User>(500);

   private AtsNotification() {

      IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsNotification");
      if (point == null) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't access AtsNotification extension point");
         return;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("AtsNotification")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     atsNotificationItems.add((IAtsNotification) obj);
                  } catch (Exception ex) {
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Error loading AtsNotification extension", ex);
                  }
               }
            }
         }
      }
   }

   public static AtsNotification getInstance() {
      return instance;
   }

   public synchronized static void notifyNewAssigneesAndReset(AbstractWorkflowArtifact workflow, boolean resetOnly) throws OseeCoreException {
      if (preSaveStateAssignees.get(workflow.getGuid()) == null || resetOnly) {
         preSaveStateAssignees.put(workflow.getGuid(), workflow.getStateMgr().getAssignees());
         return;
      }
      Set<User> newAssignees = new HashSet<User>();
      for (User user : workflow.getStateMgr().getAssignees()) {
         if (!preSaveStateAssignees.get(workflow.getGuid()).contains(user)) {
            newAssignees.add(user);
         }
      }
      preSaveStateAssignees.put(workflow.getGuid(), workflow.getStateMgr().getAssignees());
      if (newAssignees.isEmpty()) {
         return;
      }
      try {
         // These will be processed upon save
         AtsNotifyUsers.getInstance().notify(workflow, newAssignees, AtsNotifyUsers.NotifyType.Assigned);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static void notifyOriginatorAndReset(AbstractWorkflowArtifact workflow, boolean resetOnly) throws OseeCoreException {
      if (preSaveOriginator.get(workflow.getGuid()) == null || resetOnly) {
         User orig = workflow.getOriginator();
         if (orig == null) {
            orig = UserManager.getUser();
         }
         preSaveOriginator.put(workflow.getGuid(), orig);
         return;
      }
      if (preSaveOriginator.get(workflow.getGuid()) != null && workflow.getOriginator() != null && !workflow.getOriginator().equals(
         preSaveOriginator)) {
         AtsNotifyUsers.getInstance().notify(workflow, AtsNotifyUsers.NotifyType.Originator);
      }
      preSaveOriginator.put(workflow.getGuid(), workflow.getOriginator());
   }

   public static Set<IAtsNotification> getAtsNotificationItems() {
      return atsNotificationItems;
   }

}
