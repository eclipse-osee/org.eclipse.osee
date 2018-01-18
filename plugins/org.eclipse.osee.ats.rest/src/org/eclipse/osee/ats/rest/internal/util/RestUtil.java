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
package org.eclipse.osee.ats.rest.internal.util;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.rest.internal.AtsApplication;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Donald G. Dunne
 */
public class RestUtil {

   /**
    * @return Contents of resource starting at path org.eclipse.osee.ats.rest/OSEE-INF
    */
   public static String getResource(String path) throws Exception {
      Bundle bundle = FrameworkUtil.getBundle(AtsApplication.class);
      URL url = bundle.getEntry("OSEE-INF/" + path);
      return Lib.inputStreamToString(url.openStream());
   }

   public static File getResourceAsFile(String path) {
      Bundle bundle = FrameworkUtil.getBundle(AtsApplication.class);
      URL url = bundle.getEntry("OSEE-INF/" + path);
      try {
         URL fileUrl = FileLocator.toFileURL(url);
         return new File(fileUrl.toURI().getPath());
      } catch (Exception ex) {
         throw new OseeCoreException(ex, "Error getting resource [%s] as file", path);
      }
   }

   public static String simplePageHtml(String title, String message) throws Exception {
      String html = getResource("templates/simple.html");
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
      String actionUrl = AtsUtilCore.getBaseActionUiUrl(defaultUrl, atsApi);
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