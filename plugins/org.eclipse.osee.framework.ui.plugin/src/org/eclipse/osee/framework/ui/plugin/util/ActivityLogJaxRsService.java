/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.osee.activity.api.ActivityEntryId;
import org.eclipse.osee.activity.api.ActivityLogEndpoint;
import org.eclipse.osee.activity.api.ActivityType;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.client.JaxRsClient;

/**
 * @author Donald G. Dunne
 */
public class ActivityLogJaxRsService {

   public static ActivityLogEndpoint get() {
      try {
         String appServer = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER);
         if (Strings.isValid(appServer)) {
            return JaxRsClient.newBuilder() //
            .createThreadSafeProxyClients(true) //  if the client needs to be shared between threads
            .build() //
            .targetProxy(appServer, ActivityLogEndpoint.class);
         }
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public static void createActivityType(ActivityType type) {
      ActivityLogEndpoint activityEp = get();
      if (activityEp != null) {
         activityEp.createActivityType(type.getTypeId(), type.getLogLevel(), type.getModule(), type.getMessageFormat());
      }
   }

   public static ActivityEntryId create(ActivityType type, long parent, Integer initialStatus, String message) {
      ActivityLogEndpoint activityEp = get();
      if (activityEp != null) {
         return activityEp.createEntry(type.getTypeId(), parent, initialStatus, message);
      }
      return null;
   }

   public static void update(ActivityEntryId entryId, Integer statusId) {
      ActivityLogEndpoint activityEp = get();
      if (activityEp != null) {
         activityEp.updateEntry(entryId.getId(), statusId);
      }
   }

}
