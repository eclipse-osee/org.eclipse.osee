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
package org.eclipse.osee.ats.rest.internal.notify;

import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotifyEndpointApi;

/**
 * @author Donald G. Dunne
 */
public class AtsNotifyEndpointImpl implements AtsNotifyEndpointApi {
   private final AtsApi atsApi;

   public AtsNotifyEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public Response sendNotifications(AtsNotificationCollector notifications) {
      atsApi.sendNotifications(notifications);
      return Response.ok().build();
   }
}