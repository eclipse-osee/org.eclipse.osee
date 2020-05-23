/*********************************************************************
 * Copyright (c) 2014 Boeing
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