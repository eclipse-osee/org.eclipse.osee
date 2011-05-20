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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.core.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class AtsNotification {

   private final static Set<IAtsNotification> atsNotificationItems = new HashSet<IAtsNotification>();
   private final static AtsNotification instance = new AtsNotification();
   private final static Map<String, Collection<? extends IBasicUser>> preSaveStateAssignees =
      new HashMap<String, Collection<? extends IBasicUser>>();
   private final static Map<String, IBasicUser> guidToOriginatorMap = new HashMap<String, IBasicUser>(500);

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

      OseeEventManager.addListener(new IArtifactEventListener() {

         @Override
         public List<? extends IEventFilter> getEventFilters() {
            ArtifactTypeEventFilter filter = new ArtifactTypeEventFilter(AtsArtifactTypes.AbstractWorkflowArtifact);
            return Arrays.asList(filter);
         }

         @Override
         public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
            // Since multiple ways exist to change the assignees, notification is performed on the persist
            if (sender.isLocal()) {
               Collection<Artifact> artifacts =
                  artifactEvent.getCacheArtifacts(EventModType.Modified, EventModType.Added);
               for (Artifact artifact : artifacts) {
                  if (!artifact.isDeleted()) {
                     try {
                        if (artifact instanceof AbstractWorkflowArtifact) {
                           AbstractWorkflowArtifact workFlow = (AbstractWorkflowArtifact) artifact;
                           // TODO Add this back in
                           // AtsNotification.notifyNewAssigneesAndReset(workFlow, false);
                           // AtsNotification.notifyOriginatorAndReset(workFlow, false);
                        }
                     } catch (Exception ex) {
                        OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                     }

                     if (artifact.isOfType(AtsArtifactTypes.ReviewArtifact)) {
                        try {
                           if (artifact instanceof AbstractReviewArtifact) {
                              AbstractReviewArtifact review = (AbstractReviewArtifact) artifact;
                              review.notifyReviewersComplete();
                           }
                        } catch (Exception ex) {
                           OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                        }
                     }
                  }
               }
            }
         }

      });
   }

   public static AtsNotification getInstance() {
      return instance;
   }

   public synchronized static void notifyNewAssigneesAndReset(AbstractWorkflowArtifact workflow, boolean resetOnly) throws OseeCoreException {
      if (preSaveStateAssignees.get(workflow.getGuid()) == null || resetOnly) {
         preSaveStateAssignees.put(workflow.getGuid(), workflow.getStateMgr().getAssignees());
         return;
      }
      Set<IBasicUser> newAssignees = new HashSet<IBasicUser>();
      for (IBasicUser user : workflow.getStateMgr().getAssignees()) {
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
      IBasicUser preSaveOriginator = guidToOriginatorMap.get(workflow.getGuid());
      if (preSaveOriginator == null || resetOnly) {
         IBasicUser orig = workflow.getCreatedBy();
         if (orig == null) {
            orig = UserManager.getUser();
            guidToOriginatorMap.put(workflow.getGuid(), orig);
         }
         return;
      }
      if (workflow.getCreatedBy() != null && !workflow.getCreatedBy().equals(preSaveOriginator)) {
         AtsNotifyUsers.getInstance().notify(workflow, AtsNotifyUsers.NotifyType.Originator);
      }
      guidToOriginatorMap.put(workflow.getGuid(), workflow.getCreatedBy());
   }

   public static Set<IAtsNotification> getAtsNotificationItems() {
      return atsNotificationItems;
   }

}
