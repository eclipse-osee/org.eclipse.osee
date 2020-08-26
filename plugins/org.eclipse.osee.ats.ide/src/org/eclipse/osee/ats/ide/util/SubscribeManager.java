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

package org.eclipse.osee.ats.ide.util;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class SubscribeManager {

   public static void addSubscribed(AbstractWorkflowArtifact workflow, AtsUser user, IAtsChangeSet changes) {
      if (!workflow.getRelatedArtifactsUnSorted(AtsRelationTypes.SubscribedUser_User).contains(user.getStoreObject())) {
         changes.relate(workflow, AtsRelationTypes.SubscribedUser_User, user);
      }
   }

   public static void removeSubscribed(AbstractWorkflowArtifact workflow, AtsUser user, IAtsChangeSet changes) {
      changes.unrelate((IAtsObject) workflow, AtsRelationTypes.SubscribedUser_User, (IAtsObject) user);
   }

   public static boolean isSubscribed(AbstractWorkflowArtifact workflow, AtsUser user) {
      return workflow.getRelatedArtifactsUnSorted(AtsRelationTypes.SubscribedUser_User).contains(user.getStoreObject());
   }

   public static boolean amISubscribed(AbstractWorkflowArtifact workflow) {
      try {
         return isSubscribed(workflow, AtsApiService.get().getUserService().getCurrentUser());
      } catch (OseeCoreException ex) {
         return false;
      }
   }

   public static void toggleSubscribe(AbstractWorkflowArtifact awa) {
      toggleSubscribe(Arrays.asList(awa));
   }

   public static void toggleSubscribe(Collection<AbstractWorkflowArtifact> awas) {
      if (SubscribeManager.amISubscribed(awas.iterator().next())) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Toggle Subscribed");
         for (AbstractWorkflowArtifact awa : awas) {
            SubscribeManager.removeSubscribed(awa, AtsApiService.get().getUserService().getCurrentUser(), changes);
         }
         changes.execute();
      } else {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Toggle Subscribed");
         for (AbstractWorkflowArtifact awa : awas) {
            SubscribeManager.addSubscribed(awa, AtsApiService.get().getUserService().getCurrentUser(), changes);
         }
         changes.execute();
      }
   }

}
