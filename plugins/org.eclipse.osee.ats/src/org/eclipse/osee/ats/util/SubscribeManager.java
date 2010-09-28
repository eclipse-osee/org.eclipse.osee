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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class SubscribeManager {

   private final Collection<AbstractWorkflowArtifact> smas;

   public SubscribeManager(AbstractWorkflowArtifact sma) {
      this(Arrays.asList(sma));
   }

   public SubscribeManager(Collection<AbstractWorkflowArtifact> smas) {
      super();
      this.smas = smas;
   }

   public void toggleSubscribe() {
      toggleSubscribe(true);
   }

   public void toggleSubscribe(boolean prompt) {
      try {
         if (amISubscribed(smas.iterator().next())) {
            boolean result = true;
            if (prompt) {
               result =
                  MessageDialog.openQuestion(
                     PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                     "Un-Subscribe",
                     "You are currently subscribed to receive emails when this artifact transitions." + "\n\nAre You sure you wish to Un-Subscribe?");
            }
            if (result) {
               SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Toggle Subscribed");
               for (AbstractWorkflowArtifact sma : smas) {
                  removeSubscribed(sma, UserManager.getUser(), transaction);
               }
               transaction.execute();
            }
         } else {
            boolean result = true;
            if (prompt) {
               result =
                  MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                     "Subscribe",
                     "Are you sure you wish to subscribe to receive emails when this artifact transitions?");
            }
            if (result) {
               SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Toggle Subscribed");
               for (AbstractWorkflowArtifact sma : smas) {
                  addSubscribed(sma, UserManager.getUser(), transaction);
               }
               transaction.execute();
            }

         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static void addSubscribed(AbstractWorkflowArtifact workflow, User user, SkynetTransaction transaction) throws OseeCoreException {
      if (!workflow.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User).contains(user)) {
         workflow.addRelation(AtsRelationTypes.SubscribedUser_User, user);
         workflow.persist(transaction);
      }
   }

   public static void removeSubscribed(AbstractWorkflowArtifact workflow, User user, SkynetTransaction transaction) throws OseeCoreException {
      workflow.deleteRelation(AtsRelationTypes.SubscribedUser_User, user);
      workflow.persist(transaction);
   }

   public static boolean isSubscribed(AbstractWorkflowArtifact workflow, User user) throws OseeCoreException {
      return workflow.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User).contains(user);
   }

   public static List<User> getSubscribed(AbstractWorkflowArtifact workflow) throws OseeCoreException {
      ArrayList<User> arts = new ArrayList<User>();
      for (Artifact art : workflow.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User)) {
         arts.add((User) art);
      }
      return arts;
   }

   public static boolean amISubscribed(AbstractWorkflowArtifact workflow) {
      try {
         return isSubscribed(workflow, UserManager.getUser());
      } catch (OseeCoreException ex) {
         return false;
      }
   }

}
