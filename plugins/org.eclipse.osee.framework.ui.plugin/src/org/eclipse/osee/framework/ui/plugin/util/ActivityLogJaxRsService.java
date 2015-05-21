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
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.jaxrs.client.JaxRsClient;

/**
 * @author Donald G. Dunne
 */
public class ActivityLogJaxRsService {

   public static ActivityLogEndpoint get() {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      return JaxRsClient.newBuilder() //
         .createThreadSafeProxyClients(true) //  if the client needs to be shared between threads
         .build() //
         .targetProxy(appServer, ActivityLogEndpoint.class);
   }

   public static void createActivityType(ActivityType type) {
      get().createActivityType(type.getTypeId(), type.getLogLevel(), type.getModule(), type.getMessageFormat());
   }

   public static ActivityEntryId create(Long accountId, Long clientId, ActivityType type, long parent, Integer initialStatus, String message) {
      return get().createEntry(accountId, clientId, type.getTypeId(), parent, initialStatus, message);
   }

   public static void update(ActivityEntryId entryId, Integer statusId) {
      get().updateEntry(entryId.getGuid(), statusId);
   }

}
