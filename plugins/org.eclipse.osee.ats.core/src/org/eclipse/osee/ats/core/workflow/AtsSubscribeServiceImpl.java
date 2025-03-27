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

package org.eclipse.osee.ats.core.workflow;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.AtsSubcribeService;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsSubscribeServiceImpl implements AtsSubcribeService {

   private final AtsApi atsApi;

   protected AtsSubscribeServiceImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public void addSubscribed(IAtsWorkItem workItem, AtsUser user, IAtsChangeSet changes) {
      if (!atsApi.getRelationResolver().getRelated(workItem, AtsRelationTypes.SubscribedUser_User).contains(
         user.getStoreObject())) {
         changes.relate(workItem, AtsRelationTypes.SubscribedUser_User, user);
      }
   }

   @Override
   public void removeSubscribed(IAtsWorkItem workItem, AtsUser user, IAtsChangeSet changes) {
      changes.unrelate((IAtsObject) workItem, AtsRelationTypes.SubscribedUser_User, (IAtsObject) user);
   }

   @Override
   public boolean isSubscribed(IAtsWorkItem workItem, AtsUser user) {
      return atsApi.getRelationResolver().getRelated(workItem, AtsRelationTypes.SubscribedUser_User).contains(
         user.getStoreObject());
   }

   @Override
   public boolean amISubscribed(IAtsWorkItem workItem) {
      try {
         return isSubscribed(workItem, AtsApiService.get().getUserService().getCurrentUser());
      } catch (OseeCoreException ex) {
         return false;
      }
   }

   @Override
   public void toggleSubscribe(IAtsWorkItem awa) {
      toggleSubscribe(Arrays.asList(awa));
   }

   @Override
   public void toggleSubscribe(Collection<IAtsWorkItem> workItems) {
      if (amISubscribed(workItems.iterator().next())) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Toggle Subscribed");
         for (IAtsWorkItem workItem : workItems) {
            removeSubscribed(workItem, AtsApiService.get().getUserService().getCurrentUser(), changes);
         }
         changes.execute();
      } else {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Toggle Subscribed");
         for (IAtsWorkItem workItem : workItems) {
            addSubscribed(workItem, AtsApiService.get().getUserService().getCurrentUser(), changes);
         }
         changes.execute();
      }
   }

}
