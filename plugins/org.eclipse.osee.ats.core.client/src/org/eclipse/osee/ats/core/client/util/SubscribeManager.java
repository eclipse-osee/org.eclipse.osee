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
package org.eclipse.osee.ats.core.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class SubscribeManager {

   public static void addSubscribed(AbstractWorkflowArtifact workflow, IAtsUser user, SkynetTransaction transaction) throws OseeCoreException {
      if (!workflow.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User).contains(user)) {
         workflow.addRelation(AtsRelationTypes.SubscribedUser_User,
            AtsClientService.get().getUserAdmin().getOseeUser(user));
         workflow.persist(transaction);
      }
   }

   public static void removeSubscribed(AbstractWorkflowArtifact workflow, IAtsUser user, SkynetTransaction transaction) throws OseeCoreException {
      workflow.deleteRelation(AtsRelationTypes.SubscribedUser_User,
         AtsClientService.get().getUserAdmin().getOseeUser(user));
      workflow.persist(transaction);
   }

   public static boolean isSubscribed(AbstractWorkflowArtifact workflow, IAtsUser user) throws OseeCoreException {
      return workflow.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User).contains(user);
   }

   public static List<IAtsUser> getSubscribed(AbstractWorkflowArtifact workflow) throws OseeCoreException {
      ArrayList<IAtsUser> arts = new ArrayList<IAtsUser>();
      for (Artifact art : workflow.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User)) {
         arts.add(AtsClientService.get().getUserAdmin().getUserFromOseeUser((User) art));
      }
      return arts;
   }

   public static boolean amISubscribed(AbstractWorkflowArtifact workflow) {
      try {
         return isSubscribed(workflow, AtsClientService.get().getUserAdmin().getCurrentUser());
      } catch (OseeCoreException ex) {
         return false;
      }
   }

   public static void toggleSubscribe(AbstractWorkflowArtifact awa) throws OseeCoreException {
      toggleSubscribe(Arrays.asList(awa));
   }

   public static void toggleSubscribe(Collection<AbstractWorkflowArtifact> awas) throws OseeCoreException {
      if (SubscribeManager.amISubscribed(awas.iterator().next())) {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Toggle Subscribed");
         for (AbstractWorkflowArtifact awa : awas) {
            SubscribeManager.removeSubscribed(awa, AtsClientService.get().getUserAdmin().getCurrentUser(),
               transaction);
         }
         transaction.execute();
      } else {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Toggle Subscribed");
         for (AbstractWorkflowArtifact awa : awas) {
            SubscribeManager.addSubscribed(awa, AtsClientService.get().getUserAdmin().getCurrentUser(), transaction);
         }
         transaction.execute();
      }
   }

}
