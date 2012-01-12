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
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class SubscribeManager {

   public static void addSubscribed(AbstractWorkflowArtifact workflow, IBasicUser user, SkynetTransaction transaction) throws OseeCoreException {
      if (!workflow.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User).contains(user)) {
         workflow.addRelation(AtsRelationTypes.SubscribedUser_User, UserManager.getUser(user));
         workflow.persist(transaction);
      }
   }

   public static void removeSubscribed(AbstractWorkflowArtifact workflow, IBasicUser user, SkynetTransaction transaction) throws OseeCoreException {
      workflow.deleteRelation(AtsRelationTypes.SubscribedUser_User, UserManager.getUser(user));
      workflow.persist(transaction);
   }

   public static boolean isSubscribed(AbstractWorkflowArtifact workflow, IBasicUser user) throws OseeCoreException {
      return workflow.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User).contains(user);
   }

   public static List<IBasicUser> getSubscribed(AbstractWorkflowArtifact workflow) throws OseeCoreException {
      ArrayList<IBasicUser> arts = new ArrayList<IBasicUser>();
      for (Artifact art : workflow.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User)) {
         arts.add((IBasicUser) art);
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

   public static void toggleSubscribe(AbstractWorkflowArtifact awa) throws OseeCoreException {
      toggleSubscribe(Arrays.asList(awa));
   }

   public static void toggleSubscribe(Collection<AbstractWorkflowArtifact> awas) throws OseeCoreException {
      if (SubscribeManager.amISubscribed(awas.iterator().next())) {
         SkynetTransaction transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Toggle Subscribed");
         for (AbstractWorkflowArtifact awa : awas) {
            SubscribeManager.removeSubscribed(awa, UserManager.getUser(), transaction);
         }
         transaction.execute();
      } else {
         SkynetTransaction transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Toggle Subscribed");
         for (AbstractWorkflowArtifact awa : awas) {
            SubscribeManager.addSubscribed(awa, UserManager.getUser(), transaction);
         }
         transaction.execute();
      }
   }

}
