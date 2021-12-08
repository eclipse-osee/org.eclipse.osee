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

import java.util.Arrays;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotifyEndpointApi;
import org.eclipse.osee.ats.rest.internal.notify.OseeEmail.BodyType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;

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

      System.err.println(
         String.format("server: [%s] - [%s]", AtsNotificationServiceImpl.class.getSimpleName(), notifications));

      atsApi.getNotificationService().sendNotifications(notifications);
      return Response.ok().build();
   }

   @Override
   public XResultData sendEmail() {
      XResultData rd = new XResultData();
      rd.log("Send Test Email - Server");
      try {
         OseeEmail emailMessage = new OseeEmail(Arrays.asList("donald.g.dunne@boeing.com"), "donald.g.dunne@boeing.com",
            "donald.g.dunne@boeing.com", "Test Email - Server",
            AHTML.simplePage(AHTML.bold("Hello World - this should be bold")), BodyType.Html, atsApi);
         emailMessage.send();
      } catch (Exception ex) {
         rd.error(Lib.exceptionToString(ex));
      }
      return rd;
   }
}