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

package org.eclipse.osee.ats.rest.internal.util;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;

/**
 * @author Donald G. Dunne
 */
public class RestUtil {

   public static String simplePageHtml(String title, String message) throws Exception {
      String html = OseeInf.getResourceContents("templates/simple.html", RestUtil.class);
      html = html.replaceFirst("<\\?PUT_MESSAGE_HERE\\?>", message);
      html = html.replaceFirst("<\\?PUT_TITLE_HERE\\?>", title);
      return html;
   }

   public static String simplePageHtml(String message) throws Exception {
      return simplePageHtml("ATS", message);
   }

   public static Response simplePageResponse(String message) throws Exception {
      return simplePageResponse("ATS", message);
   }

   public static Response simplePageResponse(String title, String message) throws Exception {
      return Response.status(200).entity(simplePageHtml(title, message)).build();
   }

   public static ViewModel simplePage(String message) {
      return simplePage("ATS", message);
   }

   public static ViewModel simplePage(String title, String message) {
      return new ViewModel("simple.html") //
         .param("PUT_MESSAGE_HERE", message) //
         .param("PUT_TITLE_HERE", title);
   }

   public static Response redirect(IAtsWorkItem workItem, String defaultUrl, AtsApi atsApi) {
      return redirect(Arrays.asList(workItem), defaultUrl, atsApi);
   }

   public static Response redirect(Collection<? extends IAtsWorkItem> workItems, String defaultUrl, AtsApi atsApi) {
      String actionUrl = AtsUtil.getBaseActionUiUrl(defaultUrl, atsApi);
      String ids = "";
      for (IAtsWorkItem teamWf : workItems) {
         ids += teamWf.getAtsId() + ",";
      }
      ids = ids.replaceFirst(",$", "");
      actionUrl = actionUrl.replaceFirst("ID", ids);
      URI uri = UriBuilder.fromUri(actionUrl).build();
      return Response.seeOther(uri).build();
   }

   public static Response returnBadRequest(String message) {
      return Response.status(Status.BAD_REQUEST).entity(message).build();
   }

   public static Response returnInternalServerError(String message) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).build();
   }
}