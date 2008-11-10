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

package org.eclipse.osee.ats.util;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.core.client.server.HttpRequest;
import org.eclipse.osee.framework.core.client.server.HttpResponse;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.client.server.IHttpServerRequest;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class AtsHttpServerRequest implements IHttpServerRequest {

   private static AtsHttpServerRequest instance = new AtsHttpServerRequest();

   public AtsHttpServerRequest() {
      super();
   }

   public static AtsHttpServerRequest getInstance() {
      return instance;
   }

   public String getUrl(Artifact artifact) {
      Map<String, String> keyValues = new HashMap<String, String>();
      String guid = artifact.getGuid();
      if (Strings.isValid(guid)) {
         keyValues.put("guid", guid);
      }
      return HttpUrlBuilder.getInstance().getUrlForLocalSkynetHttpServer(getRequestType(), keyValues);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest#processRequest(org.eclipse.osee.framework.skynet.core.linking.HttpRequest,
    *      org.eclipse.osee.framework.skynet.core.linking.HttpResponse)
    */
   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
      String guid = httpRequest.getParameter("guid");
      try {
         final Artifact artifact = ArtifactQuery.getArtifactFromId(guid, AtsPlugin.getAtsBranch());
         if (artifact instanceof IATSArtifact) {
            Display.getDefault().asyncExec(new Runnable() {

               public void run() {
                  AtsLib.openAtsAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
               }
            });
         }
         String html =
               AHTML.simplePage("Action has been opened in OSEE ATS<br><br>" + "<form><input type=button onClick='window.opener=self;window.close()' value='Close'></form>");
         httpResponse.getPrintStream().println(html);
      } catch (Exception ex) {
         httpResponse.outputStandardError(400, "Exception handling request", ex);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest#getRequestType()
    */
   public String getRequestType() {
      return "ATS";
   }
}
